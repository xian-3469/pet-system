<template>
  <div style="padding: 20px; min-height: calc(100vh - 60px)">
    <div style="margin: 10px 0">
      <el-select v-model="searchType" placeholder="建议类型" style="width: 160px">
        <el-option label="全部" value=""></el-option>
        <el-option label="饮食建议" value="DIET"></el-option>
        <el-option label="运动建议" value="EXERCISE"></el-option>
        <el-option label="疫苗提醒" value="VACCINE"></el-option>
        <el-option label="体检建议" value="CHECKUP"></el-option>
        <el-option label="牙齿护理" value="DENTAL"></el-option>
        <el-option label="美容建议" value="GROOMING"></el-option>
        <el-option label="其他" value="OTHER"></el-option>
      </el-select>
      <el-select v-model="searchRead" placeholder="阅读状态" style="width: 140px; margin-left: 10px">
        <el-option label="全部" value=""></el-option>
        <el-option label="未读" value="0"></el-option>
        <el-option label="已读" value="1"></el-option>
      </el-select>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 宠物选择卡片 -->
    <div v-if="!selectedPetId && myPets.length" style="margin: 15px 0;">
      <span style="margin-right: 16px; font-weight: bold">选择宠物：</span>
      <span
          v-for="pet in myPets"
          :key="pet.id"
          :class="['pet-tag', { 'pet-tag-selected': selectedPetId === pet.id }]"
          @click="selectPet(pet.id)">
        {{ pet.petName }} ({{ pet.breed }})
      </span>
    </div>

    <!-- 健康建议列表 -->
    <el-card style="margin: 10px 0; border-radius: 10px">
      <div v-if="selectedPetId" style="margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center;">
        <span style="font-weight: bold">{{ selectedPetName }} 的健康建议</span>
        <div>
          <el-button type="primary" size="small" icon="el-icon-magic-stick" :loading="aiGenerating" @click="generateAiAdvice">
            {{ aiGenerating ? 'AI 分析中…' : 'AI 深度生成建议' }}
          </el-button>
          <el-button type="text" @click="selectedPetId = null">返回选择宠物</el-button>
        </div>
      </div>

      <el-timeline v-if="tableData.length">
        <el-timeline-item
            v-for="item in tableData"
            :key="item.id"
            :timestamp="formatTime(item.createTime)"
            :type="getPriorityType(item.priority)"
            :hollow="item.isRead === 0"
            placement="top">
          <el-card shadow="hover" :class="{'unread-card': item.isRead === 0}">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div style="flex: 1;">
                <div style="display: flex; align-items: center; margin-bottom: 8px;">
                  <el-tag size="small" :type="getTypeTagType(item.adviceType)">
                    {{ getTypeName(item.adviceType) }}
                  </el-tag>
                  <el-tag size="small" :type="getPriorityType(item.priority)" style="margin-left: 8px;">
                    {{ getPriorityName(item.priority) }}
                  </el-tag>
                  <el-tag size="small" :type="item.source === 'AI' ? 'success' : 'info'" style="margin-left: 8px;">
                    {{ item.source === 'AI' ? 'AI 生成' : '规则生成' }}
                  </el-tag>
                  <span v-if="item.isRead === 0" style="color: #409EFF; font-size: 12px; margin-left: 8px;">新</span>
                </div>
                <h4 style="margin: 5px 0;">{{ item.title }}</h4>
                <p style="color: #666; margin: 8px 0; font-size: 14px;">{{ item.content }}</p>
                <div style="color: #999; font-size: 12px;">
                  宠物：{{ item.petName }} | 品种：{{ item.breed }} | 月龄：{{ item.age }}个月
                </div>
              </div>
              <div style="margin-left: 20px;">
                <el-button v-if="item.isRead === 0" size="small" @click="markRead(item.id)">标为已读</el-button>
                <el-button v-if="item.isHandled === 0" size="small" type="success" @click="markHandled(item.id)">已处理</el-button>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <div v-else style="text-align: center; padding: 40px; color: #999">
        暂无健康建议，请联系管理员为您生成个性化养护建议
      </div>

      <div style="padding: 10px 0">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "HealthAdvice",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchType: "",
      searchRead: "",
      myPets: [],
      selectedPetId: null,
      selectedPetName: "",
      aiGenerating: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.loadMyPets()
  },
  methods: {
    loadMyPets() {
      this.request.get("/pet-profile/my/page", {
        params: { pageNum: 1, pageSize: 100 }
      }).then(res => {
        if (res.code === '200') {
          this.myPets = res.data.records || []
          if (this.myPets.length > 0) {
            this.selectedPetId = this.myPets[0].id
            this.selectedPetName = this.myPets[0].petName
            this.load()
          }
        }
      })
    },
    selectPet(petId) {
      const pet = this.myPets.find(p => p.id === petId)
      this.selectedPetId = petId
      this.selectedPetName = pet ? pet.petName : ''
      this.load()
    },
    generateAiAdvice() {
      if (!this.selectedPetId || this.aiGenerating) return
      this.aiGenerating = true
      this.request.post("/ai/health-advice/generate/" + this.selectedPetId, {}, { timeout: 120000 }).then(res => {
        this.aiGenerating = false
        if (res.code === '200') {
          if (res.data && res.data.source === 'AI') {
            this.$message.success("AI 已为 " + this.selectedPetName + " 生成 " + res.data.count + " 条个性化健康建议")
          } else {
            this.$message.warning("AI 服务暂不可用，已使用规则引擎生成建议")
          }
          this.pageNum = 1
          this.load()
        } else {
          this.$message.error(res.msg || "生成失败，请稍后再试")
        }
      }).catch(() => {
        this.aiGenerating = false
        this.$message.error("AI 服务连接失败，请稍后再试")
      })
    },
    load() {
      if (!this.selectedPetId) {
        this.tableData = []
        this.total = 0
        return
      }

      this.request.get("/health-advice/my", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          adviceType: this.searchType,
          isRead: this.searchRead
        }
      }).then(res => {
        if (res.code === '200') {
          const records = res.data.records || []
          // 过滤出当前选中宠物的建议
          this.tableData = records.filter(item => item.petId === this.selectedPetId)
          this.total = res.data.total || 0
        }
      })
    },
    markRead(id) {
      this.request.put("/health-advice/read/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("已标记为已读")
          this.load()
        }
      })
    },
    markHandled(id) {
      this.request.put("/health-advice/handle/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("已标记为已处理")
          this.load()
        }
      })
    },
    getTypeName(type) {
      const map = {
        'DIET': '饮食建议',
        'EXERCISE': '运动建议',
        'VACCINE': '疫苗提醒',
        'CHECKUP': '体检建议',
        'DENTAL': '牙齿护理',
        'GROOMING': '美容建议',
        'OTHER': '其他'
      }
      return map[type] || type
    },
    getTypeTagType(type) {
      const map = {
        'DIET': 'success',
        'EXERCISE': 'warning',
        'VACCINE': 'danger',
        'CHECKUP': 'primary',
        'DENTAL': 'info',
        'GROOMING': '',
        'OTHER': 'warning'
      }
      return map[type] || ''
    },
    getPriorityName(priority) {
      const map = { 'LOW': '普通', 'MEDIUM': '重要', 'HIGH': '紧急' }
      return map[priority] || priority
    },
    getPriorityType(priority) {
      const map = { 'LOW': 'info', 'MEDIUM': 'warning', 'HIGH': 'danger' }
      return map[priority] || 'info'
    },
    formatTime(time) {
      if (!time) return ''
      return new Date(time).toLocaleString()
    },
    reset() {
      this.searchType = ""
      this.searchRead = ""
      this.load()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.load()
    },
    handleCurrentChange(num) {
      this.pageNum = num
      this.load()
    }
  }
}
</script>

<style scoped>
.unread-card {
  border-left: 3px solid #409EFF;
}
.pet-tag {
  display: inline-block;
  margin-right: 12px;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: normal;
  line-height: 1;
  cursor: pointer;
  color: #666666;
  background-color: #f5f5f5;
  border: 1px solid transparent;
  vertical-align: middle;
  transition: background-color 0.2s, color 0.2s;
  user-select: none;
}
.pet-tag:hover {
  background-color: #eeeeee;
}
.pet-tag-selected {
  background-color: #ff7d00;
  color: #ffffff;
}
.pet-tag-selected:hover {
  background-color: #ff7d00;
}
</style>
