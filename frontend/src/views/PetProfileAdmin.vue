<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" size="large" placeholder="宠物名称" v-model="petName"></el-input>
      <el-input style="width: 200px" size="large" class="ml-5" placeholder="品种" v-model="breed"></el-input>
      <el-select clearable v-model="isPublic" size="large" placeholder="公开状态" class="ml-5">
        <el-option label="公开" :value="1"></el-option>
        <el-option label="私密" :value="0"></el-option>
      </el-select>
    </div>

    <div style="margin: 10px 0">
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
      <el-button type="primary" icon="el-icon-magic-stick" @click="generateAllAdvice" :loading="generatingAll" style="margin-left: 10px">
        一键智能生成建议
      </el-button>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'">
      <el-table-column prop="id" label="宠物ID" width="80" sortable></el-table-column>
      <el-table-column prop="ownerId" label="主人ID" width="90"></el-table-column>
      <el-table-column label="头像" width="120">
        <template slot-scope="scope">
          <el-image
              v-if="scope.row.avatar"
              style="width: 70px; height: 70px; border-radius: 8px"
              :src="$store.state.baseApi + scope.row.avatar"
              :preview-src-list="[$store.state.baseApi + scope.row.avatar]">
          </el-image>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column prop="petName" label="宠物名称"></el-table-column>
      <el-table-column prop="breed" label="品种"></el-table-column>
      <el-table-column prop="age" label="年龄(月)"></el-table-column>
      <el-table-column prop="gender" label="性别"></el-table-column>
      <el-table-column prop="weight" label="体重(kg)"></el-table-column>
      <el-table-column prop="adoptDate" label="领养日期"></el-table-column>
      <el-table-column label="公开状态">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isPublic === 1 ? 'success' : 'info'">
            {{ scope.row.isPublic === 1 ? '公开' : '私密' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="380" fixed="right">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="showDetail(scope.row)">详情</el-button>
          <el-button type="success" size="mini" @click="audit(scope.row, 1)" :disabled="scope.row.isPublic === 1">审核公开</el-button>
          <el-button type="warning" size="mini" @click="audit(scope.row, 0)" :disabled="scope.row.isPublic === 0">设为私密</el-button>
          <el-button type="info" size="mini" @click="openAddAdviceDialog(scope.row)">添加建议</el-button>
          <el-button type="success" size="mini" @click="generateAdvice(scope.row)" :loading="scope.row.generating">生成建议</el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text="确定"
              cancel-button-text="取消"
              title="确定删除该档案吗？"
              @confirm="del(scope.row.id)">
            <el-button class="delete-btn" slot="reference" size="mini" icon="el-icon-delete">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

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

    <el-dialog title="宠物档案详情" :visible.sync="detailVisible" width="720px" :close-on-click-modal="false">
      <div v-if="detailData.id">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="宠物ID">{{ detailData.id }}</el-descriptions-item>
          <el-descriptions-item label="主人ID">{{ detailData.ownerId }}</el-descriptions-item>
          <el-descriptions-item label="宠物名称">{{ detailData.petName }}</el-descriptions-item>
          <el-descriptions-item label="品种">{{ detailData.breed }}</el-descriptions-item>
          <el-descriptions-item label="年龄(月)">{{ detailData.age }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ detailData.gender }}</el-descriptions-item>
          <el-descriptions-item label="体重(kg)">{{ detailData.weight }}</el-descriptions-item>
          <el-descriptions-item label="绝育">{{ detailData.neutered === 1 ? '已绝育' : '未绝育' }}</el-descriptions-item>
          <el-descriptions-item label="领养日期">{{ detailData.adoptDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="公开状态">{{ detailData.isPublic === 1 ? '公开' : '私密' }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人">{{ detailData.emergencyContact || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 18px">
          <div style="font-weight: bold; margin-bottom: 10px">操作日志（谁审核了哪条）</div>
          <el-table :data="logTableData" border stripe size="mini">
            <el-table-column prop="id" label="日志ID" width="80"></el-table-column>
            <el-table-column prop="operatorId" label="操作人ID" width="90"></el-table-column>
            <el-table-column prop="operatorName" label="操作人"></el-table-column>
            <el-table-column label="审核前">
              <template slot-scope="scope">{{ scope.row.beforePublic === 1 ? '公开' : '私密' }}</template>
            </el-table-column>
            <el-table-column label="审核后">
              <template slot-scope="scope">{{ scope.row.afterPublic === 1 ? '公开' : '私密' }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注"></el-table-column>
            <el-table-column prop="createTime" label="操作时间" width="170"></el-table-column>
          </el-table>
          <div style="padding-top: 10px">
            <el-pagination
                @current-change="handleLogCurrentChange"
                :current-page="logPageNum"
                :page-size="logPageSize"
                layout="total, prev, pager, next"
                :total="logTotal">
            </el-pagination>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 添加健康建议对话框 -->
    <el-dialog title="添加健康建议" :visible.sync="adviceDialogVisible" width="500px" :close-on-click-modal="false">
      <el-form :model="adviceForm" :rules="adviceRules" ref="adviceForm" label-width="100px">
        <el-form-item label="宠物信息">
          <el-input :value="adviceForm.petName + ' (' + adviceForm.breed + ')'" disabled></el-input>
        </el-form-item>
        <el-form-item label="建议类型" prop="adviceType">
          <el-select v-model="adviceForm.adviceType" placeholder="请选择建议类型" style="width: 100%">
            <el-option label="饮食建议" value="DIET"></el-option>
            <el-option label="运动建议" value="EXERCISE"></el-option>
            <el-option label="疫苗提醒" value="VACCINE"></el-option>
            <el-option label="体检建议" value="CHECKUP"></el-option>
            <el-option label="牙齿护理" value="DENTAL"></el-option>
            <el-option label="美容建议" value="GROOMING"></el-option>
            <el-option label="其他" value="OTHER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="adviceForm.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="普通" value="LOW"></el-option>
            <el-option label="重要" value="MEDIUM"></el-option>
            <el-option label="紧急" value="HIGH"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="建议标题" prop="title">
          <el-input v-model="adviceForm.title" placeholder="请输入建议标题" maxlength="100" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="建议内容" prop="content">
          <el-input type="textarea" v-model="adviceForm.content" placeholder="请输入建议内容" :rows="4" maxlength="500" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="adviceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAdvice">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "PetProfileAdmin",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      petName: "",
      breed: "",
      isPublic: null,
      detailVisible: false,
      detailData: {},
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      logTableData: [],
      logTotal: 0,
      logPageNum: 1,
      logPageSize: 5,
      adviceDialogVisible: false,
      adviceForm: {
        petId: null,
        petName: '',
        breed: '',
        adviceType: '',
        priority: 'MEDIUM',
        title: '',
        content: ''
      },
      adviceRules: {
        adviceType: [{ required: true, message: '请选择建议类型', trigger: 'change' }],
        priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
        title: [{ required: true, message: '请输入建议标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入建议内容', trigger: 'blur' }]
      },
      generatingAll: false
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/pet-profile/admin/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          petName: this.petName,
          breed: this.breed,
          isPublic: this.isPublic
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
      })
    },
    generateAdvice(row) {
      this.$set(row, 'generating', true)
      this.request.post("/health-advice/generate/" + row.id).then(res => {
        if (res.code === '200') {
          this.$message.success("生成建议成功，该宠物已重新生成健康建议")
        } else {
          this.$message.error(res.msg || "生成建议失败")
        }
      }).finally(() => {
        this.$set(row, 'generating', false)
      })
    },
    generateAllAdvice() {
      this.generatingAll = true
      this.request.post("/health-advice/generate-all").then(res => {
        if (res.code === '200') {
          this.$message.success("一键生成成功，已为所有宠物生成健康建议")
        } else {
          this.$message.error(res.msg || "一键生成失败")
        }
      }).finally(() => {
        this.generatingAll = false
      })
    },
    audit(row, isPublic) {
      this.request.put("/pet-profile/admin/audit/" + row.id + "/" + isPublic).then(res => {
        if (res.code === '200') {
          this.$message.success("审核操作成功")
          this.load()
          if (this.detailVisible && this.detailData.id === row.id) {
            this.loadDetail(row.id)
            this.loadLogs()
          }
        } else {
          this.$message.error(res.msg || "审核失败")
        }
      })
    },
    del(id) {
      this.request.delete("/pet-profile/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          if (this.detailVisible && this.detailData.id === id) {
            this.detailVisible = false
          }
          this.load()
        } else {
          this.$message.error(res.msg || "删除失败")
        }
      })
    },
    reset() {
      this.petName = ""
      this.breed = ""
      this.isPublic = null
      this.pageNum = 1
      this.load()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.load()
    },
    handleCurrentChange(num) {
      this.pageNum = num
      this.load()
    },
    showDetail(row) {
      this.detailVisible = true
      this.logPageNum = 1
      this.loadDetail(row.id)
      this.loadLogs(row.id)
    },
    loadDetail(id) {
      this.request.get("/pet-profile/" + id).then(res => {
        this.detailData = res.data || {}
      })
    },
    loadLogs(id) {
      const petProfileId = id || this.detailData.id
      if (!petProfileId) return
      this.request.get("/pet-profile/admin/log/page", {
        params: {
          petProfileId: petProfileId,
          pageNum: this.logPageNum,
          pageSize: this.logPageSize
        }
      }).then(res => {
        this.logTableData = (res.data && res.data.records) || []
        this.logTotal = (res.data && res.data.total) || 0
      })
    },
    handleLogCurrentChange(pageNum) {
      this.logPageNum = pageNum
      this.loadLogs()
    },
    openAddAdviceDialog(row) {
      this.adviceForm = {
        petId: row.id,
        petName: row.petName,
        breed: row.breed,
        adviceType: '',
        priority: 'MEDIUM',
        title: '',
        content: ''
      }
      this.adviceDialogVisible = true
    },
    saveAdvice() {
      this.$refs.adviceForm.validate(valid => {
        if (valid) {
          this.request.post("/health-advice/add", this.adviceForm).then(res => {
            if (res.code === '200') {
              this.$message.success("健康建议已添加")
              this.adviceDialogVisible = false
            } else {
              this.$message.error(res.msg || "添加失败")
            }
          })
        }
      })
    }
  }
}
</script>

<style>
.headerBg {
  background: #eee !important;
}
</style>
