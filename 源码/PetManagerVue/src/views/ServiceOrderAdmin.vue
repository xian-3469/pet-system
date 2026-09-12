<template>
  <div style="padding: 20px; min-height: calc(100vh - 60px)">
    <div style="margin: 10px 0">
      <span style="font-size: 20px; font-weight: bold; margin-right: 20px">预约审核管理</span>
      <el-badge :value="pendingCount" :hidden="pendingCount === 0" type="warning" style="margin-right: 20px">
        <el-tag type="warning">待审核</el-tag>
      </el-badge>
      <el-input size="large" v-model="searchOrderNo" style="width: 220px; margin-left: 20px" placeholder="请输入订单号" suffix-icon="el-icon-search"></el-input>
      <el-select v-model="searchStatus" placeholder="订单状态" style="width: 160px; margin-left: 10px">
        <el-option label="全部" value=""></el-option>
        <el-option label="待审核" value="PENDING"></el-option>
        <el-option label="已确认" value="CONFIRMED"></el-option>
        <el-option label="已完成" value="COMPLETED"></el-option>
        <el-option label="已取消" value="CANCELLED"></el-option>
      </el-select>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 统计卡片 -->
    <div style="display: flex; gap: 15px; margin: 10px 0">
      <el-card shadow="hover" style="flex: 1; text-align: center">
        <div style="font-size: 24px; font-weight: bold; color: #E6A23C">{{ pendingCount }}</div>
        <div style="color: #666">待审核</div>
      </el-card>
      <el-card shadow="hover" style="flex: 1; text-align: center">
        <div style="font-size: 24px; font-weight: bold; color: #409EFF">{{ confirmedCount }}</div>
        <div style="color: #666">已确认</div>
      </el-card>
      <el-card shadow="hover" style="flex: 1; text-align: center">
        <div style="font-size: 24px; font-weight: bold; color: #67C23A">{{ completedCount }}</div>
        <div style="color: #666">已完成</div>
      </el-card>
      <el-card shadow="hover" style="flex: 1; text-align: center">
        <div style="font-size: 24px; font-weight: bold; color: #909399">{{ cancelledCount }}</div>
        <div style="color: #666">已取消</div>
      </el-card>
    </div>

    <!-- 订单列表 -->
    <el-card style="margin: 10px 0; border-radius: 10px">
      <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'">
        <el-table-column prop="orderNo" label="订单号" width="180"></el-table-column>
        <el-table-column label="服务项目" width="150">
          <template slot-scope="scope">
            {{ getServiceName(scope.row.serviceId) }}
          </template>
        </el-table-column>
        <el-table-column label="宠物" width="120">
          <template slot-scope="scope">
            {{ getPetName(scope.row.petId) }}
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="160"></el-table-column>
        <el-table-column prop="storeName" label="门店名称" width="150"></el-table-column>
        <el-table-column prop="storePhone" label="联系电话" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160"></el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button
                v-if="scope.row.status === 'PENDING'"
                type="success"
                size="small"
                @click="handleConfirmWithStore(scope.row)">
              确认并设置门店
            </el-button>
            <el-button
                v-if="scope.row.status === 'CONFIRMED'"
                type="primary"
                size="small"
                @click="handleComplete(scope.row)">
              完成
            </el-button>
            <el-popconfirm
                v-if="scope.row.status === 'CANCELLED'"
                class="ml-5"
                confirm-button-text="确定"
                cancel-button-text="取消"
                title="确定删除该订单吗？"
                @confirm="handleDelete(scope.row.id)">
              <el-button class="delete-btn" slot="reference" size="small" icon="el-icon-delete">删除</el-button>
            </el-popconfirm>
            <el-button size="small" @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!tableData.length" style="text-align: center; padding: 40px; color: #999">
        暂无订单记录
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

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="550px" :close-on-click-modal="false">
      <el-descriptions :column="2" border v-if="detailData.id">
        <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)" size="small">
            {{ getStatusName(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ getServiceName(detailData.serviceId) }}</el-descriptions-item>
        <el-descriptions-item label="服务价格">¥{{ getServicePrice(detailData.serviceId) }}</el-descriptions-item>
        <el-descriptions-item label="宠物">{{ getPetName(detailData.petId) }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ detailData.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="门店名称">{{ detailData.storeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.storePhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="确认时间">{{ detailData.confirmTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detailData.completeTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="取消时间">{{ detailData.cancelTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 确认并设置门店弹窗 -->
    <el-dialog title="确认订单并设置门店" :visible.sync="storeDialogVisible" width="450px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="storeForm.orderNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="服务项目">
          <el-input :value="getServiceName(storeForm.serviceId)" disabled></el-input>
        </el-form-item>
        <el-form-item label="预约时间">
          <el-input v-model="storeForm.appointmentTime" disabled></el-input>
        </el-form-item>
        <el-form-item label="门店名称" required>
          <el-input v-model="storeForm.storeName" placeholder="请输入门店名称"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" required>
          <el-input v-model="storeForm.storePhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="storeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmWithStore" :loading="storeLoading">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "ServiceOrderAdmin",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchOrderNo: "",
      searchStatus: "",
      services: [],
      pets: [],
      detailDialogVisible: false,
      detailData: {},
      pendingCount: 0,
      confirmedCount: 0,
      completedCount: 0,
      cancelledCount: 0,
      storeDialogVisible: false,
      storeForm: {
        id: null,
        orderNo: "",
        serviceId: null,
        appointmentTime: "",
        storeName: "",
        storePhone: ""
      },
      storeLoading: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
    this.loadServices()
    this.loadAllPets()
    this.loadStatistics()
  },
  methods: {
    load() {
      this.request.get("/service-order/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          orderNo: this.searchOrderNo,
          status: this.searchStatus
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        }
      })
    },
    loadStatistics() {
      this.request.get("/service-order/statistics").then(res => {
        if (res.code === '200') {
          this.pendingCount = res.data.PENDING || 0
          this.confirmedCount = res.data.CONFIRMED || 0
          this.completedCount = res.data.COMPLETED || 0
          this.cancelledCount = res.data.CANCELLED || 0
        }
      })
    },
    loadServices() {
      this.request.get("/service-item/list").then(res => {
        if (res.code === '200') {
          this.services = res.data || []
        }
      })
    },
    loadAllPets() {
      this.request.get("/pet-profile/page", {
        params: { pageNum: 1, pageSize: 1000 }
      }).then(res => {
        if (res.code === '200') {
          this.pets = res.data.records || []
        }
      })
    },
    getServiceName(serviceId) {
      const service = this.services.find(s => s.id === serviceId)
      return service ? service.name : '-'
    },
    getServicePrice(serviceId) {
      const service = this.services.find(s => s.id === serviceId)
      return service ? service.price : '-'
    },
    getPetName(petId) {
      const pet = this.pets.find(p => p.id === petId)
      return pet ? pet.petName : '-'
    },
    getStatusName(status) {
      const map = {
        'PENDING': '待审核',
        'CONFIRMED': '已确认',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return map[status] || status
    },
    getStatusType(status) {
      const map = {
        'PENDING': 'warning',
        'CONFIRMED': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'info'
      }
      return map[status] || ''
    },
    handleConfirm(row) {
      this.$confirm('确认该预约订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.request.put("/service-order/confirm/" + row.id).then(res => {
          if (res.code === '200') {
            this.$message.success("订单已确认")
            this.load()
            this.loadStatistics()
          } else {
            this.$message.error(res.msg || "操作失败")
          }
        })
      }).catch(() => {})
    },
    handleConfirmWithStore(row) {
      this.storeForm = {
        id: row.id,
        orderNo: row.orderNo,
        serviceId: row.serviceId,
        appointmentTime: row.appointmentTime,
        storeName: row.storeName || '',
        storePhone: row.storePhone || ''
      }
      this.storeDialogVisible = true
    },
    confirmWithStore() {
      if (!this.storeForm.storeName) {
        this.$message.warning("请输入门店名称")
        return
      }
      if (!this.storeForm.storePhone) {
        this.$message.warning("请输入联系电话")
        return
      }
      this.storeLoading = true
      this.request.put("/service-order/confirmWithStore/" + this.storeForm.id, {
        storeName: this.storeForm.storeName,
        storePhone: this.storeForm.storePhone
      }).then(res => {
        this.storeLoading = false
        if (res.code === '200') {
          this.$message.success("订单已确认并设置门店信息")
          this.storeDialogVisible = false
          this.load()
          this.loadStatistics()
        } else {
          this.$message.error(res.msg || "操作失败")
        }
      }).catch(() => {
        this.storeLoading = false
      })
    },
    handleComplete(row) {
      this.$confirm('确认完成该预约订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.request.put("/service-order/complete/" + row.id).then(res => {
          if (res.code === '200') {
            this.$message.success("订单已完成")
            this.load()
            this.loadStatistics()
          } else {
            this.$message.error(res.msg || "操作失败")
          }
        })
      }).catch(() => {})
    },
    handleDelete(id) {
      this.request.delete("/service-order/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
          this.loadStatistics()
        } else {
          this.$message.error(res.msg || "删除失败")
        }
      })
    },
    handleDetail(row) {
      this.detailData = row
      this.detailDialogVisible = true
    },
    reset() {
      this.searchOrderNo = ""
      this.searchStatus = ""
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

<style>
.headerBg {
  background: #eee !important;
}
</style>
