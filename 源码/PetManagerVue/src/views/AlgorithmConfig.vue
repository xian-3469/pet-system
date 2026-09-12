<template>
  <div>
    <div style="margin: 10px 0">
      <el-select v-model="algorithmType" placeholder="选择算法类型" style="width: 200px" size="large" @change="load">
        <el-option label="全部" value=""></el-option>
        <el-option label="聚类算法" value="CLUSTERING"></el-option>
        <el-option label="领养匹配算法" value="ADOPT_MATCH"></el-option>
        <el-option label="服务推荐算法" value="SERVICE_RECOMMENDATION"></el-option>
      </el-select>
      <el-input style="width: 200px; margin-left: 10px" size="large" placeholder="请输入配置键" suffix-icon="el-icon-search" v-model="configKey"></el-input>
    </div>

    <div style="margin: 10px 0">
      <el-button class="ml-5" type="primary" @click="load"> 搜索</el-button>
      <el-button type="warning" @click="reset"> 重置</el-button>
      <el-button type="primary" @click="handleAdd"> 新增配置 </el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference"> 批量删除 </el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="algorithmType" label="算法类型" width="200">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.algorithmType === 'CLUSTERING'" type="success">聚类算法</el-tag>
          <el-tag v-else-if="scope.row.algorithmType === 'ADOPT_MATCH'" type="warning">领养匹配算法</el-tag>
          <el-tag v-else-if="scope.row.algorithmType === 'SERVICE_RECOMMENDATION'" type="primary">服务推荐算法</el-tag>
          <el-tag v-else>{{ scope.row.algorithmType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="configKey" label="配置键" width="200"></el-table-column>
      <el-table-column prop="configValue" label="配置值" width="150"></el-table-column>
      <el-table-column prop="configType" label="配置类型" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.configType === 'STRING'" type="info">字符串</el-tag>
          <el-tag v-else-if="scope.row.configType === 'INTEGER'" type="success">整数</el-tag>
          <el-tag v-else-if="scope.row.configType === 'DOUBLE'" type="warning">浮点数</el-tag>
          <el-tag v-else>{{ scope.row.configType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column label="操作" fixed="right" width="200" align="center">
        <template slot-scope="scope">
          <el-button class="edit-btn" @click="handleEdit(scope.row)"> 编辑 </el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button class="delete-btn" slot="reference" icon="el-icon-delete"> 删除 </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="算法配置信息" :visible.sync="dialogFormVisible" width="40%">
      <el-form label-width="120px" :model="form" :rules="rules" ref="formRef">
        <el-form-item label="算法类型" prop="algorithmType">
          <el-select v-model="form.algorithmType" placeholder="请选择算法类型" style="width: 100%">
            <el-option label="聚类算法" value="CLUSTERING"></el-option>
            <el-option label="领养匹配算法" value="ADOPT_MATCH"></el-option>
            <el-option label="服务推荐算法" value="SERVICE_RECOMMENDATION"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" autocomplete="off" placeholder="请输入配置键，如：weight_experience"></el-input>
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" autocomplete="off" placeholder="请输入配置值"></el-input>
        </el-form-item>
        <el-form-item label="配置类型" prop="configType">
          <el-select v-model="form.configType" placeholder="请选择配置类型" style="width: 100%">
            <el-option label="字符串" value="STRING"></el-option>
            <el-option label="整数" value="INTEGER"></el-option>
            <el-option label="浮点数" value="DOUBLE"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" autocomplete="off" type="textarea" :rows="3" placeholder="请输入配置项描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false"> 取 消</el-button>
        <el-button type="primary" @click="save"> 确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "AlgorithmConfig",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      algorithmType: "",
      configKey: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      rules: {
        algorithmType: [{ required: true, message: '请选择算法类型', trigger: 'change' }],
        configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
        configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }],
        configType: [{ required: true, message: '请选择配置类型', trigger: 'change' }],
      }
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/algorithm-config/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          algorithmType: this.algorithmType,
          configKey: this.configKey
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.request.post("/algorithm-config", this.form).then(res => {
            if (res.code === '200') {
              this.$message.success("保存成功")
              this.dialogFormVisible = false
              this.load()
            } else {
              this.$message.error("保存失败")
            }
          })
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.form.algorithmType = this.algorithmType || "CLUSTERING"
      this.form.configType = "STRING"
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/algorithm-config/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)
      if (ids.length === 0) {
        this.$message.warning("请选择要删除的数据")
        return
      }
      this.request.post("/algorithm-config/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.algorithmType = ""
      this.configKey = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    }
  }
}
</script>

<style>
.headerBg {
  background: #eee!important;
}
</style>
