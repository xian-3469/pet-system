<template>
  <div style="padding-bottom: 20px; min-height: calc(100vh - 60px)">
    <div style="padding: 25px; background: linear-gradient(135deg, #FFFFFF 0%, #FFF9F5 100%); border-radius: 16px; margin: 15px 0; box-shadow: 0 2px 12px rgba(255, 154, 86, 0.1);">
      <div style="display: flex; align-items: center; gap: 15px;">
        <el-input size="large" v-model="petName" style="width: 260px" placeholder="请输入宠物名称" prefix-icon="el-icon-search"></el-input>
        <el-button class="ml-5" type="primary" icon="el-icon-search" @click="load">搜索</el-button>
        <el-button type="info" icon="el-icon-refresh" @click="reset">重置</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增档案</el-button>
      </div>
    </div>

    <el-card style="margin: 15px 0;" class="pet-card">
      <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'">
        <el-table-column prop="id" label="宠物ID" width="80"></el-table-column>
        <el-table-column label="头像" width="120">
          <template slot-scope="scope">
            <el-image
                v-if="scope.row.avatar"
                style="width: 70px; height: 70px; border-radius: 50%; border: 3px solid var(--pet-border);"
                :src="$store.state.baseApi + scope.row.avatar"
                :preview-src-list="[$store.state.baseApi + scope.row.avatar]">
            </el-image>
            <div v-else style="width: 70px; height: 70px; border-radius: 50%; background: var(--pet-bg); display: flex; align-items: center; justify-content: center; border: 3px solid var(--pet-border);">
              <i class="el-icon-picture-outline" style="font-size: 24px; color: var(--pet-text-light);"></i>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="petName" label="宠物名称">
          <template slot-scope="scope">
            <span style="font-weight: bold; color: var(--pet-primary-dark);">{{ scope.row.petName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="breed" label="品种"></el-table-column>
        <el-table-column prop="age" label="年龄(月)"></el-table-column>
        <el-table-column prop="gender" label="性别">
          <template slot-scope="scope">
            <el-tag size="small" :type="scope.row.gender === '公' ? 'primary' : 'danger'" style="border-radius: 12px;">{{ scope.row.gender }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="体重(kg)"></el-table-column>
        <el-table-column label="绝育状态">
          <template slot-scope="scope">
            <el-tag size="small" :type="scope.row.neutered === 1 ? 'success' : 'warning'" style="border-radius: 12px;">{{ scope.row.neutered === 1 ? '已绝育' : '未绝育' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="adoptDate" label="领养日期"></el-table-column>
        <el-table-column label="公开" width="80">
          <template slot-scope="scope">
            <el-tag size="small" :type="scope.row.isPublic === 1 ? 'success' : 'info'" style="border-radius: 12px;">{{ scope.row.isPublic === 1 ? '公开' : '私密' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button class="edit-btn" size="mini" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
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

      <div style="padding: 15px 0">
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

    <el-dialog title="宠物档案" :visible.sync="dialogFormVisible" width="45%" :close-on-click-modal="false" class="pet-dialog">
      <el-form label-width="100px" class="pet-form">
        <el-form-item label="宠物名称">
          <el-input v-model="form.petName" placeholder="请输入宠物名称"></el-input>
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="form.breed" placeholder="如：中华田园猫、泰迪等"></el-input>
        </el-form-item>
        <el-form-item label="年龄(月)">
          <el-input-number v-model="form.age" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio v-model="form.gender" label="公">公</el-radio>
          <el-radio v-model="form.gender" label="母">母</el-radio>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :precision="2" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="绝育状态">
          <el-radio :label="0" v-model="form.neutered">未绝育</el-radio>
          <el-radio :label="1" v-model="form.neutered">已绝育</el-radio>
        </el-form-item>
        <el-form-item label="领养日期">
          <el-date-picker v-model="form.adoptDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload :action="$store.state.baseApi + '/file/upload'" :on-success="handleAvatarUploadSuccess" :show-file-list="false">
            <el-button size="small" type="primary" icon="el-icon-upload">上传头像</el-button>
          </el-upload>
          <div v-if="form.avatar" style="margin-top: 10px">
            <el-image style="width: 80px; height: 80px; border-radius: 50%; border: 3px solid var(--pet-border);" :src="$store.state.baseApi + form.avatar"></el-image>
          </div>
          <div v-if="form.avatar" style="margin-top: 8px">
            <el-button size="mini" type="success" plain icon="el-icon-magic-stick" :loading="recognizing" @click="recognizePet">
              {{ recognizing ? 'AI 识别中…' : 'AI 识别填充品种' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人"></el-input>
        </el-form-item>
        <el-form-item label="是否公开">
          <el-radio :label="1" v-model="form.isPublic">公开</el-radio>
          <el-radio :label="0" v-model="form.isPublic">私密</el-radio>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "PetProfile",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      petName: "",
      form: {},
      dialogFormVisible: false,
      recognizing: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/pet-profile/my/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          petName: this.petName
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
      })
    },
    handleAdd() {
      this.form = {isPublic: 1, neutered: 0}
      this.dialogFormVisible = true
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    save() {
      if (!this.form.petName || !this.form.breed) {
        this.$message.warning("请填写宠物名称和品种")
        return
      }
      if (this.form.id) {
        this.request.put("/pet-profile/" + this.form.id, this.form).then(res => {
          if (res.code === '200') {
            this.$message.success("更新成功")
            this.dialogFormVisible = false
            this.load()
          } else {
            this.$message.error(res.msg || "更新失败")
          }
        })
      } else {
        this.request.post("/pet-profile", this.form).then(res => {
          if (res.code === '200') {
            this.$message.success("新增成功")
            this.dialogFormVisible = false
            this.load()
          } else {
            this.$message.error(res.msg || "新增失败")
          }
        })
      }
    },
    del(id) {
      this.request.delete("/pet-profile/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error(res.msg || "删除失败")
        }
      })
    },
    reset() {
      this.petName = ""
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
    handleAvatarUploadSuccess(res) {
      this.form.avatar = res
      this.$message.info("已上传，可点击“AI 识别填充品种”自动补全")
    },
    recognizePet() {
      if (!this.form.avatar || this.recognizing) return
      this.recognizing = true
      this.request.post("/ai/pet-profile/recognize", { imageUrl: this.form.avatar }, { timeout: 120000 }).then(res => {
        this.recognizing = false
        if (res.code === '200') {
          const d = res.data || {}
          const invalidBreeds = ['', '未知', '不知道', '无法确定', '无法识别']
          const breed = (d.breed || '').replace(/\?$/, '').trim()
          if (d.petType && ['cat', 'dog', 'other'].includes(d.petType)) this.$set(this.form, 'petType', d.petType)
          if (d.description && !this.form.intro) this.$set(this.form, 'intro', d.description)
          if (d.tags && !this.form.tags) this.$set(this.form, 'tags', d.tags)
          if (breed && !invalidBreeds.includes(breed)) {
            this.$set(this.form, 'breed', breed)
            this.$message.success("AI 识别完成，已自动填充（可手动修改）")
          } else {
            this.$message.warning("照片中未能认出具体品种，已尽量填充其他信息，品种请手动选择")
          }
        } else {
          this.$message.warning(res.msg || "识别失败，请手动填写")
        }
      }).catch(() => {
        this.recognizing = false
        this.$message.warning("AI 服务连接失败，请手动填写")
      })
    }
  }
}
</script>

<style>
.headerBg {
  background: #FFF5EE !important;
}

/* 宠物档案对话框样式 */
.pet-dialog .el-dialog__header {
  background: linear-gradient(135deg, var(--pet-primary) 0%, var(--pet-primary-dark) 100%);
  border-radius: 16px 16px 0 0;
}

.pet-dialog .el-dialog__title {
  color: white !important;
  font-weight: bold;
  font-size: 18px;
}

.pet-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white !important;
}

.pet-form .el-form-item__label {
  color: var(--pet-text) !important;
  font-weight: 500 !important;
}
</style>
