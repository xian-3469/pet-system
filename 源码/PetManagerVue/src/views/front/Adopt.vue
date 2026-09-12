<template>
  <div style="padding-bottom: 20px; min-height: calc(100vh - 60px)">
    <!-- 搜索区域 -->
    <div style="padding: 20px; background-color: #fff; border-radius: 10px; margin: 10px 0">
      <div style="display: flex; align-items: center; gap: 10px; flex-wrap: wrap;">
        <el-input size="large" v-model="nickname" style="width: 200px" placeholder="搜索宠物名称"></el-input>
        <el-select v-model="filterType" placeholder="宠物类型" style="width: 100px" clearable>
          <el-option label="全部" value=""></el-option>
          <el-option label="猫" value="猫"></el-option>
          <el-option label="狗" value="狗"></el-option>
        </el-select>
        <el-select v-model="filterClusterTag" placeholder="聚类标签" style="width: 140px" clearable>
          <el-option v-for="(count, tag) in clusterStats" :key="tag" :label="tag + '(' + count + ')'" :value="tag"></el-option>
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="load()">搜 索</el-button>
        <el-button type="success" icon="el-icon-document" @click="$router.push('/front/myAdopt')">我的领养</el-button>
        <el-button v-if="hasApplication" type="warning" icon="el-icon-star-off" @click="loadRecommend">
          智能推荐
        </el-button>
      </div>
      <!-- 聚类标签快捷筛选 -->
      <div style="margin-top: 12px; display: flex; gap: 8px; flex-wrap: wrap;">
        <span style="color: #666; line-height: 32px;">快速筛选：</span>
        <el-tag
            v-for="tag in quickFilterTags"
            :key="tag"
            :type="activeQuickFilter === tag ? 'primary' : 'info'"
            style="cursor: pointer;"
            @click="handleQuickFilter(tag)">{{ tag }}</el-tag>
      </div>
    </div>

    <!-- 匹配度说明提示 -->
    <div v-if="showRecommendTip" style="padding: 15px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 10px; margin: 10px 0; display: flex; align-items: center;">
      <i class="el-icon-info" style="margin-right: 10px; font-size: 18px;"></i>
      <span style="font-size: 14px;">根据您的领养申请信息，我们为您智能匹配了以下宠物，优先推荐高匹配度的宠物哦！</span>
    </div>

    <!-- 宠物列表 -->
    <div style="margin: 10px 0">
      <el-card v-for="item in tableData" :key="item.id" style="margin: 10px 0; position: relative; transition: all 0.3s;" class="animal-card">
        <!-- 匹配度标签 -->
        <div v-if="item.matchScore !== undefined"
             :class="['match-badge', getMatchBadgeClass(item.matchScore)]"
             style="position: absolute; top: 15px; right: 15px; padding: 10px 18px; border-radius: 20px; text-align: center; z-index: 10;">
          <div style="font-size: 22px; font-weight: bold;">{{ item.matchScore }}</div>
          <div style="font-size: 12px;">{{ item.matchLevel || '待评估' }}</div>
        </div>

        <div style="display: flex;" class="animal-content">
          <div style="width: 160px; flex-shrink: 0; cursor: pointer;" @click="$router.push('/front/homeDetail?id=' + item.id)">
            <img :src="$store.state.baseApi+item.img" alt="" style="width: 100%; height: 180px; border-radius: 10px; object-fit: cover;">
          </div>
          <div style="padding-left: 40px; flex: 1; min-width: 0;">
            <div style="border-bottom: 1px solid #eee; width: 100%; padding-bottom: 12px; margin-bottom: 10px;">
              <span style="font-size: 24px; font-weight: bold;">{{ item.nickname }}</span>
              <el-tag v-if="item.clusterTag" size="small" type="success" style="margin-left: 10px;">{{ item.clusterTag }}</el-tag>
              <el-tag v-else-if="item.bodyType" size="small" style="margin-left: 10px;">{{ item.bodyType }}</el-tag>
              <el-tag v-if="item.personality" size="small" type="info" style="margin-left: 5px;">{{ item.personality }}</el-tag>
              <span style="margin-left: 15px; color: #666;">
                <i class="el-icon-male" v-if="item.sex === '公'" style="color: #409EFF;"></i>
                <i class="el-icon-female" v-else style="color: #F56C6C;"></i>
                {{ item.sex }}
              </span>
              <span style="margin-left: 15px; color: #666;">{{ item.age }}</span>
            </div>
            <div style="line-height: 32px; color: #555;">
              <div><b style="margin-right: 8px; color: #333;">体型：</b>{{ item.bodyType || '未知' }}</div>
              <div><b style="margin-right: 8px; color: #333;">性格：</b>{{ item.personality || item.information || '未知' }}</div>
              <div>
                <b style="margin-right: 8px; color: #333;">健康状况：</b>
                <el-tag size="small" type="success" v-if="item.status === '健康' || item.status === '良好'">{{ item.status }}</el-tag>
                <el-tag size="small" v-else>{{ item.status }}</el-tag>
                <span style="margin-left: 10px;">{{ item.sterilization === '是' ? '已绝育' : '未绝育' }}</span>
                <span style="margin-left: 10px;">{{ item.vaccine === '已接种' ? '已打疫苗' : '未打疫苗' }}</span>
              </div>
              <div style="text-align: right; margin-top: 10px;">
                <el-button type="primary" size="medium" icon="el-icon-edit" @click="handleApply(item.id)">申请领养</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 无数据提示 -->
      <div v-if="!tableData.length" style="text-align: center; padding: 60px 20px; color: #999; background: #fff; border-radius: 10px;">
        <i class="el-icon-folder-delete" style="font-size: 60px; margin-bottom: 20px;"></i>
        <p style="font-size: 16px;">暂无待领养的宠物</p>
      </div>
    </div>

    <!-- 分页 -->
    <div style="padding: 15px; background-color: #fff; border-radius: 10px">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next"
          :total="total">
      </el-pagination>
    </div>

    <!-- 领养申请弹窗 -->
    <el-dialog title="领养申请" :visible.sync="dialogFormVisible" width="550px" :close-on-click-modal="false">
      <el-form label-width="100px" style="width: 90%">
        <el-form-item label="姓名">
          <el-input v-model="form.name" placeholder="请输入您的姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio v-model="form.sex" label="男">男</el-radio>
          <el-radio v-model="form.sex" label="女">女</el-radio>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="form.age" placeholder="请输入您的年龄"></el-input>
        </el-form-item>
        <el-form-item label="养宠经验">
          <el-radio v-model="form.experience" label="无经验">无经验</el-radio>
          <el-radio v-model="form.experience" label="有经验">有经验</el-radio>
        </el-form-item>
        <el-form-item label="住房条件">
          <el-select v-model="form.housing" placeholder="请选择住房条件" style="width: 100%">
            <el-option label="公寓" value="公寓"></el-option>
            <el-option label="别墅" value="别墅"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="家庭结构">
          <el-select v-model="form.familyStructure" placeholder="请选择家庭结构" style="width: 100%">
            <el-option label="单身" value="单身"></el-option>
            <el-option label="二人世界" value="二人世界"></el-option>
            <el-option label="三口之家" value="三口之家"></el-option>
            <el-option label="大家庭" value="大家庭"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="收入水平">
          <el-input v-model="form.income" placeholder="请输入月收入（元）"></el-input>
        </el-form-item>
        <el-form-item label="职业">
          <el-input v-model="form.profession" placeholder="请输入职业"></el-input>
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="form.address" placeholder="请输入详细住址"></el-input>
        </el-form-item>
        <el-form-item label="领养理由">
          <el-input type="textarea" v-model="form.reason" :rows="3" placeholder="请简要说明您想领养的原因"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Adopt",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      nickname: "",
      filterType: "",
      filterClusterTag: "",
      activeQuickFilter: "",
      showRecommendTip: false,
      hasApplication: false,
      dialogFormVisible: false,
      submitLoading: false,
      form: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      clusterStats: {},
      quickFilterTags: ["小型幼猫", "中型成犬", "大型成犬", "小型成年猫", "中型成猫", "小型成犬"]
    }
  },
  created() {
    this.checkUserApplication()
    this.loadClusterStats()
    this.load()
  },
  methods: {
    // 加载聚类标签统计
    loadClusterStats() {
      this.request.get("/animal/cluster/stats").then(res => {
        if (res.code === '200') {
          this.clusterStats = res.data || {}
          // 更新快捷筛选标签
          this.quickFilterTags = Object.keys(res.data)
        }
      })
    },

    // 快捷筛选
    handleQuickFilter(tag) {
      if (this.activeQuickFilter === tag) {
        this.activeQuickFilter = ""
        this.filterClusterTag = ""
      } else {
        this.activeQuickFilter = tag
        this.filterClusterTag = tag
      }
      this.load()
    },

    // 检查用户是否有申请记录
    async checkUserApplication() {
      if (!this.user.id) return
      try {
        const res = await this.request.get("/applcation/user/" + this.user.id)
        this.hasApplication = res.data && res.data.length > 0
      } catch (e) {
        this.hasApplication = false
      }
    },

    // 加载智能推荐列表
    loadRecommend() {
      const user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null
      if (!user || !user.token) {
        this.$message.warning("请先登录后再使用智能推荐")
        this.$router.push("/login")
        return
      }
      if (!this.hasApplication) {
        this.$message.warning("请先提交领养申请后再使用智能推荐")
        return
      }
      this.$message.info("正在分析您的申请信息，计算宠物匹配度...")
      this.request.get("/animal/recommend").then(res => {
        if (res.code === '200') {
          this.showRecommendTip = true
          this.tableData = res.data.map(item => ({
            ...item.animal,
            matchScore: item.matchScore,
            matchLevel: item.matchLevel
          }))
          this.total = this.tableData.length
          this.$message.success("为您找到了 " + this.tableData.length + " 只匹配度较高的宠物")
        } else {
          this.$message.warning(res.msg || "获取推荐失败，请重试")
        }
      })
    },

    // 获取匹配度标签样式
    getMatchBadgeClass(score) {
      if (score >= 90) return 'match-excellent'
      if (score >= 75) return 'match-good'
      if (score >= 60) return 'match-normal'
      return 'match-low'
    },

    load() {
      this.showRecommendTip = false
      this.request.get("/animal/cluster/search", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.nickname,
          type: this.filterType,
          clusterTag: this.filterClusterTag
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        } else {
          // 降级到普通接口
          this.request.get("/animal/page/user", {
            params: {
              pageNum: this.pageNum,
              pageSize: this.pageSize,
              name: this.nickname,
            }
          }).then(r => {
            this.tableData = r.data.records || []
            this.total = r.data.total || 0
          })
        }
      })
    },

    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },

    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },

    handleApply(animalId) {
      const user = localStorage.getItem("user")
      if (!user) {
        this.$message.warning("请先登录后再申请领养")
        this.$router.push("/login")
        return
      }
      // 检查是否已有待审核的申请
      this.request.get("/applcation/user/" + this.user.id).then(res => {
        if (res.data) {
          const pendingApplication = res.data.find(app => 
            app.animalId === animalId && app.state === '待审核'
          )
          if (pendingApplication) {
            this.$message.warning("您已经申请过领养该宠物，请等待审核结果")
            return
          }
        }
        this.form = { animalId: animalId }
        this.dialogFormVisible = true
      })
    },

    save() {
      if (!this.form.name || !this.form.phone || !this.form.experience) {
        this.$message.warning("请填写必填信息（姓名、联系方式、养宠经验）")
        return
      }
      this.submitLoading = true
      this.form.userId = this.user.id
      this.request.post("/applcation", this.form).then(res => {
        this.submitLoading = false
        if (res.code === '200') {
          this.$message.success("申请提交成功！")
          this.dialogFormVisible = false
          this.hasApplication = true
        } else {
          this.$message.error(res.msg || "提交失败")
        }
      }).catch(() => {
        this.submitLoading = false
      })
    },
  }
}
</script>

<style scoped>
.animal-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.animal-content {
  transition: all 0.3s;
}

@media (max-width: 768px) {
  .animal-content {
    flex-direction: column;
  }
  .animal-content > div:first-child {
    width: 100% !important;
  }
  .animal-content > div:last-child {
    padding-left: 0 !important;
    padding-top: 15px;
  }
}

/* 匹配度标签样式 */
.match-badge {
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.2);
  min-width: 70px;
}
.match-excellent {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}
.match-good {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}
.match-normal {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
}
.match-low {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #666;
}
</style>
