<template>
  <div style="padding: 20px; min-height: calc(100vh - 60px)">
    <div style="margin: 10px 0">
      <el-select v-model="searchStatus" placeholder="订单状态" style="width: 160px">
        <el-option label="全部" value=""></el-option>
        <el-option label="待审核" value="PENDING"></el-option>
        <el-option label="已确认" value="CONFIRMED"></el-option>
        <el-option label="已完成" value="COMPLETED"></el-option>
        <el-option label="已取消" value="CANCELLED"></el-option>
      </el-select>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 我的订单列表 -->
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
        <el-table-column prop="storeName" label="门店名称" width="150">
          <template slot-scope="scope">
            {{ scope.row.storeName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="storePhone" label="联系电话" width="120">
          <template slot-scope="scope">
            {{ scope.row.storePhone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row)" size="small">
              {{ getStatusName(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button
                v-if="scope.row.status === 'COMPLETED'"
                type="success"
                size="small"
                @click="handleReview(scope.row)">
              评价
            </el-button>
            <el-button
                v-if="scope.row.status === 'PENDING' || scope.row.status === 'CONFIRMED'"
                type="danger"
                size="small"
                @click="handleCancel(scope.row)">
              取消
            </el-button>
            <span v-if="scope.row.status === 'CANCELLED'" style="color: #999; font-size: 12px">已取消</span>
            <!-- 已到期但未自动完成的订单也可以评价 -->
            <el-button
                v-if="(scope.row.status === 'PENDING' || scope.row.status === 'CONFIRMED') && isExpired(scope.row)"
                type="success"
                size="small"
                @click="handleReview(scope.row)">
              评价
            </el-button>
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

    <!-- 取消订单弹窗 -->
    <el-dialog title="取消订单" :visible.sync="cancelDialogVisible" width="400px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="订单号">
          <el-input v-model="cancelForm.orderNo" disabled></el-input>
        </el-form-item>
        <el-form-item label="取消原因">
          <el-input type="textarea" v-model="cancelForm.reason" :rows="3" placeholder="请输入取消原因"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelDialogVisible = false">返回</el-button>
        <el-button type="danger" @click="confirmCancel">确认取消</el-button>
      </div>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog title="服务评价" :visible.sync="reviewDialogVisible" width="500px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="服务项目">
          <el-input v-model="reviewForm.serviceName" disabled></el-input>
        </el-form-item>
        <el-form-item label="服务评分">
          <el-rate v-model="reviewForm.rating" show-text :texts="['很差', '较差', '一般', '满意', '非常满意']"></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input type="textarea" v-model="reviewForm.content" :rows="4" placeholder="请分享您的服务体验..."></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submitLoading">提交评价</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "MyServiceOrder",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      searchStatus: "",
      services: [],
      pets: [],
      cancelDialogVisible: false,
      reviewDialogVisible: false,
      cancelForm: {
        id: null,
        orderNo: "",
        reason: ""
      },
      reviewForm: {
        orderId: null,
        serviceId: null,
        serviceName: "",
        rating: 5,
        content: ""
      },
      submitLoading: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      currentTime: new Date()
    }
  },
  created() {
    this.load()
    this.loadServices()
    this.loadMyPets()
    // 每分钟更新当前时间，用于动态显示状态
    this.timer = setInterval(() => {
      this.currentTime = new Date()
    }, 60000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    load() {
      this.request.get("/service-order/my", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          status: this.searchStatus
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
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
    loadMyPets() {
      this.request.get("/pet-profile/my/page", {
        params: { pageNum: 1, pageSize: 100 }
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
    getPetName(petId) {
      const pet = this.pets.find(p => p.id === petId)
      return pet ? pet.petName : '-'
    },
    isExpired(row) {
      if (!row.appointmentTime) return false
      const appointment = new Date(row.appointmentTime)
      return appointment < this.currentTime
    },
    getStatusName(row) {
      const status = row.status
      const appointmentTime = row.appointmentTime

      // 对于待审核和已确认的订单，根据预约时间动态显示状态
      if (status === 'PENDING' || status === 'CONFIRMED') {
        return this.getDynamicStatus(appointmentTime, status)
      }
      const map = {
        'PENDING': '待审核',
        'CONFIRMED': '已确认',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return map[status] || status
    },
    getDynamicStatus(appointmentTime, originalStatus) {
      if (!appointmentTime) {
        return originalStatus === 'PENDING' ? '待审核' : '已确认'
      }
      const appointment = new Date(appointmentTime)
      const now = this.currentTime
      const diff = appointment - now
      const oneHour = 60 * 60 * 1000
      const oneDay = 24 * oneHour

      // 如果预约时间已过，返回"已到期"
      if (diff < 0) {
        return '已到期'
      }
      // 如果1小时内，返回"即将到期"
      if (diff < oneHour) {
        return '即将到期'
      }
      // 如果24小时内，返回"明日到期"
      if (diff < oneDay) {
        return '明日到期'
      }
      // 否则返回原状态
      return originalStatus === 'PENDING' ? '待审核' : '已确认'
    },
    getStatusType(row) {
      const status = row.status
      const appointmentTime = row.appointmentTime

      // 对于待审核和已确认的订单，根据预约时间动态显示标签颜色
      if (status === 'PENDING' || status === 'CONFIRMED') {
        const dynamicStatus = this.getDynamicStatus(appointmentTime, status)
        if (dynamicStatus === '已到期') {
          return 'info'
        }
        if (dynamicStatus === '即将到期' || dynamicStatus === '明日到期') {
          return 'danger'
        }
        return status === 'PENDING' ? 'warning' : 'primary'
      }
      const map = {
        'PENDING': 'warning',
        'CONFIRMED': 'primary',
        'COMPLETED': 'success',
        'CANCELLED': 'info'
      }
      return map[status] || ''
    },
    handleCancel(row) {
      this.cancelForm = {
        id: row.id,
        orderNo: row.orderNo,
        reason: ""
      }
      this.cancelDialogVisible = true
    },
    confirmCancel() {
      this.request.put("/service-order/cancel/" + this.cancelForm.id, null, {
        params: { reason: this.cancelForm.reason }
      }).then(res => {
        if (res.code === '200') {
          this.$message.success("订单已取消")
          this.cancelDialogVisible = false
          this.load()
        } else {
          this.$message.error(res.msg || "取消失败")
        }
      })
    },
    handleReview(row) {
      this.reviewForm = {
        orderId: row.id,
        serviceId: row.serviceId,
        serviceName: this.getServiceName(row.serviceId),
        rating: 5,
        content: ""
      }
      this.reviewDialogVisible = true
    },
    submitReview() {
      if (!this.reviewForm.rating) {
        this.$message.warning("请选择评分")
        return
      }
      this.submitLoading = true
      this.request.post("/service-review", {
        orderId: this.reviewForm.orderId,
        serviceId: this.reviewForm.serviceId,
        rating: this.reviewForm.rating,
        content: this.reviewForm.content
      }).then(res => {
        this.submitLoading = false
        if (res.code === '200') {
          this.$message.success("评价成功，感谢您的反馈！")
          this.reviewDialogVisible = false
          this.load()
        } else {
          this.$message.error(res.msg || "评价失败")
        }
      }).catch(() => {
        this.submitLoading = false
      })
    },
    reset() {
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
