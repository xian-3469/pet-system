<template>
  <div style="padding: 20px; min-height: calc(100vh - 60px)">
    <div style="margin: 10px 0">
      <el-input size="large" v-model="searchName" style="width: 260px" placeholder="请输入服务名称" suffix-icon="el-icon-search"></el-input>
      <el-select v-model="searchType" placeholder="服务类型" style="width: 160px; margin-left: 10px" @change="handleTypeChange" clearable>
        <el-option label="全部" value=""></el-option>
        <el-option label="洗澡" value="BATH"></el-option>
        <el-option label="美容" value="BEAUTY"></el-option>
        <el-option label="寄养" value="FOSTER"></el-option>
        <el-option label="医疗" value="MEDICAL"></el-option>
      </el-select>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 服务项目列表 -->
    <el-card style="margin: 10px 0; border-radius: 10px; overflow: hidden;">
      <div class="service-grid">
        <div v-for="item in tableData" :key="item.id" class="service-item">
          <div class="service-card" @click="handleReserve(item)">
            <!-- 服务标题 + 类型标签行 -->
            <div class="card-header">
              <div class="service-title-row">
                <span class="service-icon" :class="getServiceIcon(item.type)"></span>
                <span class="service-title">{{ item.name }}</span>
              </div>
              <el-tag size="small" class="type-tag" :class="'type-' + item.type">
                {{ getTypeName(item.type) }}
              </el-tag>
            </div>

            <!-- 服务描述 -->
            <p class="service-desc">{{ item.description || '暂无描述' }}</p>

            <!-- 服务时长、门店、电话 -->
            <div class="service-meta">
              <p class="meta-item">
                <i class="el-icon-time meta-icon"></i>
                <span>服务时长：{{ item.duration }}分钟</span>
              </p>
              <p v-if="item.storeName" class="meta-item">
                <i class="el-icon-office-building meta-icon"></i>
                <span>门店：{{ item.storeName }}</span>
              </p>
              <p v-if="item.storePhone" class="meta-item">
                <i class="el-icon-phone meta-icon"></i>
                <span>电话：{{ item.storePhone }}</span>
              </p>
            </div>

            <!-- 评分区域 -->
            <div class="rating-area">
              <div class="rating-with-score">
                <span v-html="renderStarRating(item.displayRating)"></span>
                <span class="rating-score">{{ getDisplayRating(item.avgRating) }}</span>
              </div>
              <span class="review-link" @click.stop="showReviews(item)">
                {{ item.reviewCount > 0 ? item.reviewCount + '条评价 >' : '暂无评价，快来抢首评吧～' }}
              </span>
            </div>

            <!-- 可滑动的评价窗口 -->
            <div v-if="item.reviewCount > 0" class="review-scroll-container" @click.stop>
              <div class="review-scroll" ref="reviewScroll"
                @mousedown="onScrollMouseDown"
                @mousemove="onScrollMouseMove"
                @mouseup="onScrollMouseUp"
                @mouseleave="onScrollMouseUp">
                <div v-for="review in item.reviews" :key="review.id" class="review-card">
                  <div class="review-header">
                    <span class="reviewer-name">{{ review.nickname || '匿名用户' }}</span>
                    <span v-html="renderStarRating(5)" class="review-stars"></span>
                  </div>
                  <p class="review-content">{{ review.content || '该用户未留下评价内容' }}</p>
                  <span class="review-time">{{ formatDate(review.createTime) }}</span>
                </div>
              </div>
              <div v-if="item.reviewCount > 2" class="scroll-indicator">
                <span class="scroll-hint">左右滑动查看更多</span>
              </div>
            </div>

            <!-- 价格和预约按钮 -->
            <div class="card-footer">
              <span class="price">¥{{ item.price }}</span>
              <el-button type="primary" class="reserve-btn" @click.stop="handleReserve(item)">
                立即预约
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <div v-if="!tableData.length" style="text-align: center; padding: 40px; color: #999">
        暂无服务项目
      </div>
    </el-card>

    <!-- 分页 -->
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[8, 16, 24]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 预约下单弹窗 -->
    <el-dialog title="预约服务" :visible.sync="reserveDialogVisible" width="550px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="服务项目">
          <el-input v-model="reserveForm.serviceName" disabled></el-input>
        </el-form-item>
        <el-form-item label="服务价格">
          <el-input v-model="reserveForm.price" disabled>
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="选择宠物">
          <el-select v-model="reserveForm.petId" placeholder="请选择要预约的宠物" style="width: 100%">
            <el-option
                v-for="pet in myPets"
                :key="pet.id"
                :label="pet.petName + ' (' + pet.breed + ')'"
                :value="pet.id">
            </el-option>
          </el-select>
          <div v-if="!myPets.length" style="color: #999; font-size: 12px; margin-top: 5px">
            暂无宠物档案，请先 <router-link to="/front/petProfile" style="color: #409EFF">添加宠物</router-link>
          </div>
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker
              v-model="reserveForm.appointmentTime"
              type="datetime"
              placeholder="选择预约时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              :picker-options="pickerOptions"
              style="width: 100%"
              @change="checkPeakHour">
          </el-date-picker>
          <div v-if="isPeakHour" style="color: #E6A23C; font-size: 13px; margin-top: 5px;">
            <i class="el-icon-warning"></i> 该时段预约较多，建议选择其他时段
          </div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="reserveForm.remark" :rows="2" placeholder="如有特殊需求请备注"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="showConfirmDialog">确认预约</el-button>
      </div>
    </el-dialog>

    <!-- 预约确认弹窗 -->
    <el-dialog title="确认预约信息" :visible.sync="confirmDialogVisible" width="450px" :close-on-click-modal="false">
      <div style="padding: 20px;">
        <el-alert type="info" :closable="false" show-icon style="margin-bottom: 20px">
          <template slot="title">
            请确认以下预约信息，提交后将锁定预约时段
          </template>
        </el-alert>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="服务项目">{{ reserveForm.serviceName }}</el-descriptions-item>
          <el-descriptions-item label="服务价格">¥{{ reserveForm.price }}</el-descriptions-item>
          <el-descriptions-item label="预约宠物">{{ getPetName(reserveForm.petId) }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ reserveForm.appointmentTime }}</el-descriptions-item>
          <el-descriptions-item label="门店名称">{{ reserveForm.storeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ reserveForm.storePhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ reserveForm.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="confirmDialogVisible = false">返回修改</el-button>
        <el-button type="primary" @click="submitOrder" :loading="submitLoading">确认提交</el-button>
      </div>
    </el-dialog>

    <!-- 查看评价弹窗 -->
    <el-dialog title="用户评价" :visible.sync="reviewDialogVisible" width="600px">
      <div style="margin-bottom: 15px;">
        <div class="rating-with-score review-dialog-rate">
          <span v-html="renderStarRating(currentDisplayRating)"></span>
          <span class="rating-score">{{ getDisplayRating(currentServiceRating) }}</span>
        </div>
        <span style="margin-left: 10px; color: #999">({{ reviewTotal }}条评价)</span>
      </div>
      <el-divider></el-divider>
      <div v-if="reviewList.length" style="max-height: 400px; overflow-y: auto;">
        <div v-for="review in reviewList" :key="review.id" style="padding: 15px 0; border-bottom: 1px solid #eee;">
          <div style="display: flex; justify-content: space-between;">
            <span style="font-weight: bold;">{{ review.nickname || '匿名用户' }}</span>
            <span v-html="renderStarRating(5)" class="review-stars"></span>
          </div>
          <p style="color: #666; margin: 8px 0;">{{ review.content || '该用户未留下评价内容' }}</p>
          <div style="display: flex; justify-content: space-between; font-size: 12px; color: #999;">
            <span>{{ formatDate(review.createTime) }}</span>
            <span v-if="review.reply" style="color: #67C23A;">商家回复：{{ review.reply }}</span>
          </div>
        </div>
      </div>
      <div v-else style="text-align: center; padding: 30px; color: #999;">
        暂无评价
      </div>
      <div v-if="reviewTotal > reviewList.length" style="text-align: center; margin-top: 10px;">
        <el-button type="text" @click="loadMoreReviews">加载更多</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "ServiceReservation",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 8,
      searchName: "",
      searchType: "",
      reserveDialogVisible: false,
      confirmDialogVisible: false,
      reviewDialogVisible: false,
      reviewList: [],
      reviewTotal: 0,
      reviewPage: 1,
      currentServiceId: null,
      currentServiceRating: 0,
      currentDisplayRating: 0,
      reserveForm: {
        serviceId: null,
        serviceName: "",
        price: 0,
        petId: null,
        appointmentTime: "",
        remark: "",
        storeName: "",
        storePhone: ""
      },
      myPets: [],
      pickerOptions: {
        disabledDate(date) {
          return date < new Date();
        }
      },
      submitLoading: false,
      peakHours: [],
      isPeakHour: false,
      scrollState: {
        isDragging: false,
        el: null,
        startX: 0,
        scrollLeft: 0
      }
    }
  },
  created() {
    this.loadMyPets().then(() => {
      this.load()
      this.checkPassedService()
    })
    this.loadPeakHours()
  },
  watch: {
    '$route.query.serviceId': function() {
      this.checkPassedService()
    }
  },
  methods: {
    checkPassedService() {
      const { serviceId, serviceName, price } = this.$route.query
      if (serviceId && serviceName) {
        if (!this.myPets.length) {
          this.loadMyPets().then(() => {
            this.loadServiceAndReserve(serviceId, serviceName, price)
          })
        } else {
          this.loadServiceAndReserve(serviceId, serviceName, price)
        }
      }
    },
    loadServiceAndReserve(serviceId, serviceName, price) {
      this.request.get("/service-item/" + serviceId).then(res => {
        if (res.code === '200' && res.data) {
          this.handleReserve(res.data)
        } else {
          this.handleReserve({ id: serviceId, name: serviceName, price: price, storeName: '', storePhone: '' })
        }
        this.$router.replace({ path: '/front/serviceReservation' })
      }).catch(() => {
        this.handleReserve({ id: serviceId, name: serviceName, price: price, storeName: '', storePhone: '' })
        this.$router.replace({ path: '/front/serviceReservation' })
      })
    },
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
          this.tableData.forEach(item => {
            this.loadServiceRating(item)
            this.loadServiceReviews(item)
          })
        }
      })
    },
    loadServiceRating(item) {
      this.request.get("/service-review/rating/" + item.id).then(res => {
        if (res.code === '200') {
          this.$set(item, 'avgRating', res.data || 0)
          this.$set(item, 'displayRating', Math.round((res.data || 0) * 2) / 2)
        }
      })
      this.request.get("/service-review/service/" + item.id + "?pageNum=1&pageSize=1").then(res => {
        if (res.code === '200') {
          this.$set(item, 'reviewCount', res.data.total || 0)
        }
      })
    },
    loadServiceReviews(item) {
      this.$set(item, 'reviews', [])
      this.request.get("/service-review/service/preview/" + item.id, {
        params: { limit: 10 }
      }).then(res => {
        if (res.code === '200') {
          this.$set(item, 'reviews', res.data || [])
        }
      })
    },
    loadMyPets() {
      return this.request.get("/pet-profile/my/page", {
        params: { pageNum: 1, pageSize: 100 }
      }).then(res => {
        if (res.code === '200') {
          this.myPets = res.data.records || []
        }
      })
    },
    getTypeName(type) {
      const map = { 'BATH': '洗澡', 'BEAUTY': '美容', 'FOSTER': '寄养', 'MEDICAL': '医疗' }
      return map[type] || type
    },
    getServiceIcon(type) {
      const iconMap = {
        'MEDICAL': 'el-icon-first-aid-kit',
        'BATH': 'el-icon-bath',
        'BEAUTY': 'el-icon-magic-stick',
        'FOSTER': 'el-icon-home',
        'DEFAULT': 'el-icon-goods'
      }
      return iconMap[type] || iconMap['DEFAULT']
    },
    handleReserve(item) {
      const user = localStorage.getItem("user")
      if (!user) {
        this.$message.warning("请先登录后再预约服务")
        this.$router.push("/login")
        return
      }
      if (!this.myPets.length) {
        this.$message.warning("请先添加宠物档案后再预约服务")
        this.$router.push("/front/petProfile")
        return
      }
      this.reserveForm = {
        serviceId: item.id,
        serviceName: item.name,
        price: item.price,
        petId: null,
        appointmentTime: "",
        remark: "",
        storeName: item.storeName || '',
        storePhone: item.storePhone || ''
      }
      this.reserveDialogVisible = true
    },
    showReviews(item) {
      this.currentServiceId = item.id
      this.currentServiceRating = item.avgRating || 0
      this.currentDisplayRating = Math.round((item.avgRating || 0) * 2) / 2
      this.reviewPage = 1
      this.reviewList = []
      this.loadReviews()
      this.reviewDialogVisible = true
    },
    loadReviews() {
      this.request.get("/service-review/service/" + this.currentServiceId, {
        params: { pageNum: this.reviewPage, pageSize: 10 }
      }).then(res => {
        if (res.code === '200') {
          if (this.reviewPage === 1) {
            this.reviewList = res.data.records || []
          } else {
            this.reviewList = [...this.reviewList, ...(res.data.records || [])]
          }
          this.reviewTotal = res.data.total || 0
        }
      })
    },
    loadMoreReviews() {
      this.reviewPage++
      this.loadReviews()
    },
    formatDate(time) {
      if (!time) return ''
      const d = new Date(time)
      if (isNaN(d.getTime())) return ''
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    },
    getDisplayRating(rating) {
      if (!rating && rating !== 0) return '0.0'
      return Number(rating).toFixed(1)
    },
    renderStarRating(rating) {
      if (!rating && rating !== 0) return ''
      const rounded = Math.round(rating * 2) / 2
      let stars = ''
      for (let i = 1; i <= 5; i++) {
        if (rounded >= i) {
          stars += '<span class="star-full">&#9733;</span>'
        } else if (rounded >= i - 0.5) {
          const fill = Math.round((rounded - (i - 1)) * 100)
          stars += `<span class="star-partial" style="--fill:${fill}">&#9733;</span>`
        } else {
          stars += '<span class="star-empty">&#9734;</span>'
        }
      }
      return stars
    },
    showConfirmDialog() {
      if (!this.reserveForm.petId) {
        this.$message.warning("请选择预约的宠物")
        return
      }
      if (!this.reserveForm.appointmentTime) {
        this.$message.warning("请选择预约时间")
        return
      }
      this.confirmDialogVisible = true
    },
    getPetName(petId) {
      const pet = this.myPets.find(p => p.id === petId)
      return pet ? pet.petName : ''
    },
    submitOrder() {
      this.submitLoading = true
      this.request.post("/service-order", {
        serviceId: this.reserveForm.serviceId,
        petId: this.reserveForm.petId,
        appointmentTime: this.reserveForm.appointmentTime,
        remark: this.reserveForm.remark
      }).then(res => {
        this.submitLoading = false
        if (res.code === '200') {
          this.$message.success("预约成功！订单号：" + res.data)
          this.confirmDialogVisible = false
          this.reserveDialogVisible = false
          this.$router.push("/front/myServiceOrder")
        } else {
          this.$message.error(res.msg || "预约失败，请重试")
        }
      }).catch(() => {
        this.submitLoading = false
      })
    },
    reset() {
      this.searchName = ""
      this.searchType = ""
      this.pageNum = 1
      this.load()
    },
    handleTypeChange() {
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
    loadPeakHours() {
      this.request.get("/service-order/peak-hours").then(res => {
        if (res.code === '200') {
          this.peakHours = res.data || []
        }
      })
    },
    checkPeakHour() {
      if (!this.reserveForm.appointmentTime) {
        this.isPeakHour = false
        return
      }
      const selectedDate = new Date(this.reserveForm.appointmentTime)
      const selectedHour = selectedDate.getHours()
      this.isPeakHour = this.peakHours.includes(selectedHour)
    },
    onScrollMouseDown(e) {
      this.scrollState.el = e.currentTarget
      this.scrollState.isDragging = true
      this.scrollState.startX = e.pageX
      this.scrollState.scrollLeft = this.scrollState.el.scrollLeft
      this.scrollState.el.style.cursor = 'grabbing'
      window.addEventListener('mousemove', this.onScrollMouseMove)
      window.addEventListener('mouseup', this.onScrollMouseUp)
    },
    onScrollMouseMove(e) {
      if (!this.scrollState.isDragging || !this.scrollState.el) return
      e.preventDefault()
      const walk = e.pageX - this.scrollState.startX
      this.scrollState.el.scrollLeft = this.scrollState.scrollLeft - walk
    },
    onScrollMouseUp(e) {
      this.scrollState.isDragging = false
      if (this.scrollState.el) {
        this.scrollState.el.style.cursor = 'grab'
      }
      window.removeEventListener('mousemove', this.onScrollMouseMove)
      window.removeEventListener('mouseup', this.onScrollMouseUp)
    }
  }
}
</script>

<style scoped>
/* ============ 卡片基础样式 ============ */
.service-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  cursor: pointer;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0e8e4;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 260px;
}

.service-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* ============ 网格布局 ============ */
.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  padding: 5px;
}

.service-item {
  min-height: 200px;
  display: flex;
  flex-direction: column;
}

/* ============ 卡片头部：标题 + 类型标签 ============ */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 10px;
}

.service-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.service-icon {
  font-size: 18px;
  color: #ff7d00;
  flex-shrink: 0;
  line-height: 1;
}

.service-title {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
  line-height: 1.3;
  word-break: break-word;
}

/* 类型标签统一橙色风格 */
.type-tag {
  border-radius: 20px !important;
  flex-shrink: 0;
  font-size: 12px;
  padding: 0 10px;
  height: 24px;
  line-height: 22px;
  border: 1px solid transparent;
  background-color: #fff3e6 !important;
  border-color: #ffd4b0 !important;
  color: #ff7d00 !important;
}

/* ============ 服务描述 ============ */
.service-desc {
  font-size: 14px;
  color: #666666;
  margin: 0 0 12px 0;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ============ 元信息区：时长、门店、电话 ============ */
.service-meta {
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666666;
  margin: 0 0 5px 0;
  line-height: 1;
}

.meta-icon {
  color: #c8c0ba;
  font-size: 13px;
  flex-shrink: 0;
}

/* ============ 评分区域 ============ */
.rating-area {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 8px;
}

.rating-with-score {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating-score {
  font-size: 14px;
  color: #ff7d00;
  font-weight: bold;
  line-height: 1;
  min-width: 30px;
}

.star-full, .star-half, .star-empty {
  font-size: 14px;
  line-height: 1;
  display: inline-block;
}

.review-link {
  font-size: 13px;
  color: #ff7d00;
  cursor: pointer;
  transition: color 0.2s ease;
  white-space: nowrap;
}

.review-link:hover {
  color: #e06a00;
  text-decoration: underline;
}

/* ============ 评价滑动区 ============ */
.review-scroll-container {
  margin-bottom: 14px;
  position: relative;
  background: #faf8f7;
  border-radius: 8px;
  padding: 8px;
  overflow: hidden;
}

.review-scroll {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding-bottom: 5px;
  cursor: grab;
  user-select: none;
}

.review-scroll::-webkit-scrollbar {
  display: none;
}

.review-scroll:active {
  cursor: grabbing;
}

.review-card {
  flex: 0 0 180px;
  background: white;
  border-radius: 6px;
  padding: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0ebe8;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.reviewer-name {
  font-weight: bold;
  font-size: 13px;
  color: #555;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.review-content {
  font-size: 12px;
  color: #666;
  margin: 0 0 5px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.review-time {
  font-size: 11px;
  color: #999;
}

.scroll-indicator {
  position: absolute;
  bottom: 2px;
  right: 8px;
  pointer-events: none;
}

.scroll-hint {
  font-size: 10px;
  color: #999;
  background: rgba(255, 255, 255, 0.75);
  padding: 2px 6px;
  border-radius: 4px;
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

/* ============ 底部：价格 + 按钮 ============ */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f5ede8;
}

.price {
  font-size: 20px;
  color: #ff7d00;
  font-weight: bold;
  letter-spacing: -0.5px;
}

.reserve-btn {
  width: 100px !important;
  height: 36px !important;
  border-radius: 8px !important;
  font-size: 14px !important;
  background-color: #ff7d00 !important;
  border-color: #ff7d00 !important;
  transition: background-color 0.2s ease, transform 0.15s ease, box-shadow 0.2s ease !important;
  box-shadow: 0 2px 8px rgba(255, 125, 0, 0.25) !important;
}

.reserve-btn:hover {
  background-color: #ff9500 !important;
  border-color: #ff9500 !important;
  transform: scale(0.98) !important;
  box-shadow: 0 4px 12px rgba(255, 125, 0, 0.35) !important;
}

.reserve-btn:active {
  transform: scale(0.96) !important;
}

/* ============ Element UI 基础覆盖 ============ */
.el-card {
  border-radius: 10px;
}

/* ============ 响应式适配 ============ */
@media (max-width: 768px) {
  .service-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
  }

  .service-card {
    padding: 18px;
  }

  .service-title {
    font-size: 16px;
  }
}

@media (hover: none) {
  .review-scroll {
    overflow-x: auto;
  }
}
</style>
