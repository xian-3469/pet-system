<template>
  <div style="padding: 20px; min-height: calc(100vh - 60px)">
    <div style="margin: 10px 0">
      <el-input size="large" v-model="searchName" style="width: 260px" placeholder="请输入服务名称" suffix-icon="el-icon-search"></el-input>
      <el-select v-model="searchType" placeholder="服务类型" style="width: 160px; margin-left: 10px" clearable>
        <el-option label="洗澡" value="BATH"></el-option>
        <el-option label="美容" value="BEAUTY"></el-option>
        <el-option label="寄养" value="FOSTER"></el-option>
        <el-option label="医疗" value="MEDICAL"></el-option>
      </el-select>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
      <el-button type="primary" @click="handleAdd">新增服务</el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference" :disabled="!multipleSelection.length">批量删除</el-button>
      </el-popconfirm>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50"></el-table-column>
      <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
      <el-table-column prop="name" label="服务名称" width="160"></el-table-column>
      <el-table-column prop="type" label="服务类型" width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="getTypeTag(scope.row.type)">{{ getTypeName(scope.row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template slot-scope="scope">
          <span style="color: #ff6b6b; font-weight: bold;">¥{{ scope.row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(分钟)" width="100"></el-table-column>
      <el-table-column prop="storeName" label="门店名称" width="150">
        <template slot-scope="scope">
          {{ scope.row.storeName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="storePhone" label="联系电话" width="140">
        <template slot-scope="scope">
          {{ scope.row.storePhone || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="服务描述" min-width="200">
        <template slot-scope="scope">
          {{ scope.row.description || '暂无描述' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200" align="center">
        <template slot-scope="scope">
          <el-button class="edit-btn" size="small" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="确定删除该服务项目吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button class="delete-btn" size="small" slot="reference" icon="el-icon-delete">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="padding: 15px 0">
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

    <!-- 新增/编辑服务弹窗 -->
    <el-dialog :title="form.id ? '编辑服务项目' : '新增服务项目'" :visible.sync="dialogFormVisible" width="550px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="服务名称">
          <el-input v-model="form.name" placeholder="请输入服务名称"></el-input>
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="form.type" placeholder="请选择服务类型" style="width: 100%">
            <el-option label="洗澡" value="BATH"></el-option>
            <el-option label="美容" value="BEAUTY"></el-option>
            <el-option label="寄养" value="FOSTER"></el-option>
            <el-option label="医疗" value="MEDICAL"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="服务价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="服务时长">
          <el-input-number v-model="form.duration" :min="10" style="width: 100%"></el-input-number>
          <span style="color: #999; margin-left: 10px">分钟</span>
        </el-form-item>
        <el-form-item label="门店名称">
          <el-input v-model="form.storeName" placeholder="请输入门店名称"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.storePhone" placeholder="请输入门店联系电话"></el-input>
        </el-form-item>
        <el-form-item label="服务描述">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入服务描述"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
  name: "ServiceItemAdmin",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchName: "",
      searchType: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/service-item/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.searchName,
          type: this.searchType
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        }
      })
    },
    handleAdd() {
      this.form = { status: 1 }
      this.dialogFormVisible = true
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    save() {
      if (!this.form.name || !this.form.type) {
        this.$message.warning("请填写完整信息")
        return
      }
      this.request.post("/service-item", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error(res.msg || "保存失败")
        }
      })
    },
    del(id) {
      this.request.delete("/service-item/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error(res.msg || "删除失败")
        }
      })
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.warning("请先选择要删除的数据")
        return
      }
      this.request.delete("/service-item/del/batch", { data: this.multipleSelection.map(v => v.id) }).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error(res.msg || "删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    getTypeName(type) {
      const map = { 'BATH': '洗澡', 'BEAUTY': '美容', 'FOSTER': '寄养', 'MEDICAL': '医疗' }
      return map[type] || type
    },
    getTypeTag(type) {
      const map = { 'BATH': '', 'BEAUTY': 'success', 'FOSTER': 'warning', 'MEDICAL': 'danger' }
      return map[type] || ''
    },
    reset() {
      this.searchName = ""
      this.searchType = ""
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
    }
  }
}
</script>

<style scoped>
</style>
