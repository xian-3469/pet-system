<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" size="large"  placeholder="请输入名称" suffix-icon="iconfont icon-r-find" v-model="name"></el-input>
      <el-select clearable v-model="adopt" size="large" placeholder="请选择" class="ml-5">
        <el-option v-for="item in ['不可领养', '可领养']" :key="item" :label="item" :value="item"></el-option>
      </el-select>
      <el-select clearable v-model="bodyType" size="large" placeholder="体型" class="ml-5" style="width: 120px">
        <el-option v-for="item in ['小型', '中型', '大型']" :key="item" :label="item" :value="item"></el-option>
      </el-select>
      <el-select clearable v-model="clusterTagFilter" size="large" placeholder="聚类标签" class="ml-5" style="width: 150px">
        <el-option v-for="(count, tag) in clusterStats" :key="tag" :label="tag + '(' + count + ')'" :value="tag"></el-option>
      </el-select>
    </div>

    <div style="margin: 10px 0">
      <el-button class="ml-5" type="primary" @click="load"> 搜索</el-button>
      <el-button type="warning" @click="reset"> 重置</el-button>
      <el-button type="primary" @click="handleAdd"> 新增 </el-button>
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
      <el-button type="success" class="ml-5" @click="generateClusterTags" :loading="generating"> 一键生成聚类标签 </el-button>
      <!-- <el-upload :action="$store.state.baseApi+'/animal/import'" :show-file-list="false" accept="xlsx" :on-success="handleExcelImportSuccess" style="display: inline-block">
        <el-button type="primary" class="ml-5"> 导入 <i class="el-icon-bottom"></i></el-button>
      </el-upload>
      <el-button type="primary" @click="exp" class="ml-5"> 导出 <i class="el-icon-top"></i></el-button> -->
    </div>

    <!-- 聚类标签说明 -->
    <div style="margin: 10px 0; padding: 12px 15px; background: #f0f9eb; border-radius: 8px; border-left: 4px solid #67c23a;">
      <span style="color: #67c23a; font-weight: bold;">聚类标签说明：</span>
      <span style="color: #666;">
        格式为 <el-tag size="mini" style="margin: 0 4px;">体型+年龄阶段+品种</el-tag>，如：
        <el-tag size="mini" type="success" style="margin: 0 4px;">小型幼猫</el-tag>
        <el-tag size="mini" type="warning" style="margin: 0 4px;">中型成犬</el-tag>
        <el-tag size="mini" type="info" style="margin: 0 4px;">大型老年犬</el-tag>
      </span>
    </div>

    <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
      <el-table-column prop="nickname" label="动物名字"></el-table-column>
      <el-table-column prop="sex" label="动物性别"></el-table-column>
      <el-table-column prop="type" label="种类"></el-table-column>
      <el-table-column prop="age" label="年龄"></el-table-column>
      <el-table-column label="图片" width="140"><template slot-scope="scope"><el-image style="width: 100px" :src="$store.state.baseApi+scope.row.img" :preview-src-list="[$store.state.baseApi + scope.row.img]"></el-image></template></el-table-column>
      <el-table-column prop="bodyType" label="体型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.bodyType" size="mini" :type="getBodyTypeTag(scope.row.bodyType)">{{ scope.row.bodyType }}</el-tag>
          <span v-else style="color: #999;">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="clusterTag" label="聚类标签" width="140">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.clusterTag" size="mini" type="success">{{ scope.row.clusterTag }}</el-tag>
          <span v-else style="color: #999;">待生成</span>
        </template>
      </el-table-column>
      <el-table-column prop="adopt" label="可领养状态"></el-table-column>
      <el-table-column prop="isAdopt" label="是否被领养">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isAdopt === '是'" type="danger" effect="dark">已领养</el-tag>
          <el-tag v-else type="success">未领养</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作"  fixed="right"   width="280" align="center">
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
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="40%" :close-on-click-modal="false">
      <el-form label-width="100px"  style="width: 90%">
        <el-form-item label="动物名字">
          <el-input v-model="form.nickname" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="动物性别">
          <el-radio v-model="form.sex" label="公">公</el-radio>
          <el-radio v-model="form.sex" label="母">母</el-radio>
        </el-form-item>
        <el-form-item label="种类">
          <el-input v-model="form.type" autocomplete="off" placeholder="如：猫、狗、柴犬、柯基"></el-input>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="form.age" autocomplete="off" placeholder="如：6个月、2周岁、8岁"></el-input>
        </el-form-item>
        <el-form-item label="体型">
          <el-select v-model="form.bodyType" placeholder="请选择体型" style="width: 100%">
            <el-option label="小型" value="小型"></el-option>
            <el-option label="中型" value="中型"></el-option>
            <el-option label="大型" value="大型"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="性格">
          <el-select v-model="form.personality" placeholder="请选择性格" style="width: 100%">
            <el-option label="温顺" value="温顺"></el-option>
            <el-option label="活泼" value="活泼"></el-option>
            <el-option label="独立" value="独立"></el-option>
            <el-option label="粘人" value="粘人"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="动物照片">
          <el-upload :action="$store.state.baseApi+'/file/upload'" ref="img" :on-success="handleImgUploadSuccess">
            <el-button  type="primary"> 点击上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="活动范围">
          <el-input v-model="form.address" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="身体状态">
          <el-input v-model="form.status" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="是否绝育">
          <el-radio v-model="form.sterilization" label="是">是</el-radio>
          <el-radio v-model="form.sterilization" label="否">否</el-radio>
        </el-form-item>
        <el-form-item label="疫苗接种">
          <el-radio v-model="form.vaccine" label="未接种">未接种</el-radio>
          <el-radio v-model="form.vaccine" label="已接种">已接种</el-radio>
        </el-form-item>
        <el-form-item label="可领养状态">
          <el-radio v-model="form.adopt" label="不可领养">不可领养</el-radio>
          <el-radio v-model="form.adopt" label="可领养">可领养</el-radio>
        </el-form-item>
        <el-form-item label="是否被领养">
          <el-radio v-model="form.isAdopt" label="是">是</el-radio>
          <el-radio v-model="form.isAdopt" label="否">否</el-radio>
        </el-form-item>
        <el-form-item label="其他描述">
          <el-input type="textarea" v-model="form.information" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="聚类标签" v-if="form.clusterTag">
          <el-tag type="success">{{ form.clusterTag }}</el-tag>
          <span style="color: #999; margin-left: 10px; font-size: 12px;">保存后将自动更新</span>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">  取 消</el-button>
        <el-button type="primary" @click="save"> 确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Animal",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      adopt: '',
      bodyType: '',
      clusterTagFilter: '',
      clusterStats: {},
      generating: false
    }
  },
  created() {
    this.load()
    this.loadClusterStats()
  },
  methods: {
    load() {
      this.request.get("/animal/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          adopt: this.adopt,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },

    // 加载聚类标签统计
    loadClusterStats() {
      this.request.get("/animal/cluster/stats").then(res => {
        if (res.code === '200') {
          this.clusterStats = res.data || {}
        }
      })
    },

    // 生成聚类标签
    generateClusterTags() {
      this.generating = true
      this.request.post("/animal/cluster/generate").then(res => {
        this.generating = false
        if (res.code === '200') {
          this.$message.success(res.data || "聚类标签生成成功")
          this.load()
          this.loadClusterStats()
        } else {
          this.$message.error("生成失败")
        }
      }).catch(() => {
        this.generating = false
      })
    },

    // 获取体型标签类型
    getBodyTypeTag(type) {
      if (type === '小型') return 'success'
      if (type === '中型') return 'warning'
      if (type === '大型') return 'danger'
      return 'info'
    },

    save() {
      this.request.post("/animal", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
          this.loadClusterStats()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = { sex: '公', adopt: '可领养', isAdopt: '否', sterilization: '否', vaccine: '未接种' }
      this.$nextTick(() => {
        if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
       this.$nextTick(() => {
         if(this.$refs.img) {
           this.$refs.img.clearFiles();
         }
         if(this.$refs.file) {
          this.$refs.file.clearFiles();
         }
       })
    },
    del(id) {
      this.request.delete("/animal/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/animal/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.adopt = ""
      this.bodyType = ""
      this.clusterTagFilter = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    handleFileUploadSuccess(res) {
      this.form.file = res
    },
    handleImgUploadSuccess(res) {
      this.form.img = res
    },
    download(url) {
      window.open(url)
    },
    exp() {
      window.open(this.$store.state.baseApi+"/animal/export")
    },
    handleExcelImportSuccess() {
      this.$message.success("导入成功")
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
