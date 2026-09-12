<template>
  <div style="padding-bottom: 20px;">

    <!-- 个性化服务推荐模块 -->
    <div class="recommend-module">
      <div class="recommend-header">
        <div class="recommend-title-row">
          <i class="el-icon-magic-stick recommend-title-icon"></i>
          <span class="recommend-title">为您推荐</span>
        </div>
        <el-button type="text" class="view-all-btn" @click="goToServiceReservation">
          查看全部 <i class="el-icon-arrow-right"></i>
        </el-button>
      </div>

      <!-- 未登录提示 -->
      <div v-if="!isLoggedIn" class="recommend-no-login">
        <i class="el-icon-user" style="font-size: 32px; margin-bottom: 10px; display: block; color: #ff7d00;"></i>
        <p style="color: #999;">登录后获取个性化服务推荐</p>
        <el-button size="small" type="warning" @click="$router.push('/login')" style="margin-top: 10px;">立即登录</el-button>
      </div>

      <!-- 推荐列表轮播 -->
      <div v-else v-loading="loadingRecommendations" @mouseenter="pauseAutoplay" @mouseleave="startAutoplay">
        <div class="carousel-container">
          <!-- 左箭头 -->
          <div class="carousel-arrow carousel-arrow-left" @click="prevCarousel" v-if="carouselData.length > 1">
            <i class="el-icon-arrow-left"></i>
          </div>

          <el-carousel
            ref="carousel"
            :interval="4000"
            :initial-index="0"
            trigger="click"
            arrow="never"
            height="280px"
            indicator-position="none"
            @change="handleCarouselChange"
            v-if="carouselData.length > 0">
            <el-carousel-item v-for="(group, index) in carouselData" :key="index">
              <div class="carousel-item-wrapper">
                <div v-for="item in group" :key="item.serviceId" class="recommend-card">
                  <!-- 收藏按钮（右上角） -->
                  <div class="card-favorite" @click.stop="toggleFavorite(item)">
                    <i :class="isFavorited(item) ? 'el-icon-star-on favorited' : 'el-icon-star-off'"></i>
                  </div>
                  <!-- 卡片主体（点击跳转详情） -->
                  <div class="card-main" @click="goToDetail(item)">
                    <!-- 第一行：图标 + 标题 + 类型标签 -->
                    <div class="card-header">
                      <div class="service-title-row">
                        <span class="service-icon" :class="getServiceIcon(item.type)"></span>
                        <span class="service-title">{{ item.name }}</span>
                      </div>
                      <el-tag size="small" class="type-tag" :class="'type-' + item.type">
                        {{ getTypeName(item.type) }}
                      </el-tag>
                    </div>

                    <!-- 热度/新品标签 + 推荐理由 -->
                    <div class="heat-tag-row">
                      <span class="heat-tag" :class="'heat-' + item.recommendType">
                        {{ getHeatLabel(item) }}
                      </span>
                      <span class="sales-count" v-if="item.salesCount">
                        <i class="el-icon-shopping-cart-2 sales-icon"></i>
                        已售{{ item.salesCount }}
                      </span>
                      <span class="reason-tag" v-if="item.reason">
                        <i class="el-icon-magic-stick"></i> {{ item.reason }}
                      </span>
                    </div>

                    <!-- 服务描述 -->
                    <p class="service-desc">{{ item.description || '暂无描述' }}</p>

                    <!-- 服务时长、门店、电话 -->
                    <div class="service-meta">
                      <p class="meta-item" v-if="item.duration">
                        <i class="el-icon-time meta-icon"></i>
                        <span>服务时长：{{ item.duration }}分钟</span>
                      </p>
                      <p class="meta-item" v-if="item.storeName">
                        <i class="el-icon-office-building meta-icon"></i>
                        <span>门店：{{ item.storeName }}</span>
                      </p>
                      <p class="meta-item" v-if="item.storePhone">
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
                      <span class="review-link">
                        {{ item.reviewCount > 0 ? item.reviewCount + '条评价' : '暂无评价' }}
                      </span>
                    </div>

                  </div>

                  <!-- 价格和预约按钮（固定底部，点击不跳转） -->
                  <div class="card-footer">
                    <span class="price">¥{{ item.price }}</span>
                    <el-button type="primary" class="reserve-btn" @click.stop="handleReserve(item)">
                      立即预约
                    </el-button>
                  </div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>

          <!-- 右箭头 -->
          <div class="carousel-arrow carousel-arrow-right" @click="nextCarousel" v-if="carouselData.length > 1">
            <i class="el-icon-arrow-right"></i>
          </div>

          <!-- 底部圆点指示器 -->
          <div class="carousel-indicators" v-if="carouselData.length > 1">
            <span
              v-for="(item, index) in carouselData"
              :key="index"
              :class="['indicator-dot', { active: carouselIndex === index }]"
              @click="goToSlide(index)">
            </span>
          </div>

          <!-- 无推荐时显示 -->
          <div v-if="carouselData.length === 0 && !loadingRecommendations" class="recommend-empty">
            <p>暂无个性化推荐，试试其他服务吧~</p>
          </div>
        </div>
      </div>
    </div>

    <div style="margin: 15px 0">
      <article-kp></article-kp>
    </div>

    <div style="margin: 10px 0">
      <activity></activity>
    </div>
  </div>
</template>

<script>
import ArticleKp from '@/components/front/ArticleKp.vue'
import Activity from '@/components/front/Activity.vue'

export default {
  name: "FrontHome",
  data() {
    return {
      recommendations: [],
      loadingRecommendations: false,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null,
      carouselIndex: 0,
      autoplayTimer: null,
      favoriteIds: []
    }
  },
  components: {
    ArticleKp,
    Activity
  },
  computed: {
    isLoggedIn() {
      return !!this.user
    },
    carouselData() {
      const itemsPerSlide = 3
      const result = []
      for (let i = 0; i < this.recommendations.length; i += itemsPerSlide) {
        result.push(this.recommendations.slice(i, i + itemsPerSlide))
      }
      return result
    }
  },
  mounted() {
    this.startAutoplay()
  },
  beforeDestroy() {
    this.stopAutoplay()
  },
  created() {
    if (this.isLoggedIn) {
      this.loadRecommendations()
      this.loadFavorites()
    }
  },
  methods: {
    loadRecommendations() {
      this.loadingRecommendations = true
      this.request.get("/service-recommendation/comprehensive", {
        params: { personalizedLimit: 6, hotLimit: 6, newLimit: 6 }
      }).then(res => {
        if (res.code === '200') {
          const allServices = []
          // 个性化推荐
          if (res.data.personalized && res.data.personalized.length > 0) {
            res.data.personalized.forEach(item => {
              allServices.push({ ...item, recommendType: 'PERSONALIZED' })
            })
          }
          // 热门推荐
          if (res.data.hot && res.data.hot.length > 0) {
            res.data.hot.forEach(item => {
              allServices.push({ ...item, recommendType: 'HOT' })
            })
          }
          // 新服务推荐
          if (res.data.newServices && res.data.newServices.length > 0) {
            res.data.newServices.forEach(item => {
              allServices.push({ ...item, recommendType: 'NEW' })
            })
          }
          // 去除重复（根据serviceId）
          const uniqueMap = new Map()
          allServices.forEach(item => {
            if (!uniqueMap.has(item.serviceId)) {
              uniqueMap.set(item.serviceId, item)
            }
          })
          this.recommendations = Array.from(uniqueMap.values()).map(item => ({
            ...item,
            avgRating: item.avgRating ? parseFloat(item.avgRating) : 0,
            displayRating: item.avgRating ? Math.round(parseFloat(item.avgRating) * 2) / 2 : 0
          }))
        }
      }).catch(() => {
        // 加载失败，不显示推荐
      }).finally(() => {
        this.loadingRecommendations = false
      })
    },
    getTypeName(type) {
      const map = { 'BATH': '洗澡', 'BEAUTY': '美容', 'FOSTER': '寄养', 'MEDICAL': '医疗' }
      return map[type] || type
    },
    getServiceIcon(type) {
      const icons = { 'BATH': 'el-icon-bath', 'BEAUTY': 'el-icon-scissors', 'FOSTER': 'el-icon-house', 'MEDICAL': 'el-icon-first-aid-kit' }
      return icons[type] || 'el-icon-service'
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
    getHeatLabel(item) {
      const type = item.recommendType
      if (type === 'HOT') return '🔥 热销'
      if (type === 'NEW') return '🆕 新品'
      if (type === 'PERSONALIZED') return '⭐ 推荐'
      return '⭐ 推荐'
    },
    isFavorited(item) {
      const id = item.serviceId || item.id
      return this.favoriteIds.includes(id)
    },
    toggleFavorite(item) {
      if (!this.isLoggedIn) {
        this.$message.warning("请先登录后再收藏服务")
        this.$router.push("/login")
        return
      }
      const id = item.serviceId || item.id
      if (this.isFavorited(item)) {
        this.favoriteIds = this.favoriteIds.filter(fid => fid !== id)
        this.request.delete("/service-favorite/" + id).catch(() => {})
        this.$message.success("已取消收藏")
      } else {
        this.favoriteIds.push(id)
        this.request.post("/service-favorite", { serviceId: id }).catch(() => {})
        this.$message.success("收藏成功")
      }
    },
    loadFavorites() {
      this.request.get("/service-favorite/user").then(res => {
        if (res.code === '200') {
          this.favoriteIds = (res.data || []).map(item => item.serviceId)
        }
      }).catch(() => {})
    },
    goToDetail(item) {
      this.$router.push({
        path: '/front/serviceReservation',
        query: {
          serviceId: item.serviceId || item.id,
          serviceName: item.name,
          price: item.price
        }
      })
    },
    getRecommendTagName(type) {
      const map = { 'PERSONALIZED': '个性推荐', 'HOT': '热门', 'SIMILAR': '相似', 'NEW': '新品' }
      return map[type] || '推荐'
    },
    getRecommendTagType(type) {
      const map = { 'PERSONALIZED': 'success', 'HOT': 'warning', 'SIMILAR': 'info', 'NEW': 'primary' }
      return map[type] || 'info'
    },
    handleReserve(item) {
      if (!this.isLoggedIn) {
        this.$message.warning("请先登录后再预约服务")
        this.$router.push("/login")
        return
      }
      // 通过路由query传递服务信息
      this.$router.push({
        path: '/front/serviceReservation',
        query: {
          serviceId: item.serviceId || item.id,
          serviceName: item.name,
          price: item.price
        }
      })
    },
    goToServiceReservation() {
      this.$router.push('/front/serviceReservation')
    },
    handleCarouselChange(index) {
      this.carouselIndex = index
    },
    prevCarousel() {
      if (this.$refs.carousel && this.carouselData.length > 1) {
        this.$refs.carousel.prev()
      }
    },
    nextCarousel() {
      if (this.$refs.carousel && this.carouselData.length > 1) {
        this.$refs.carousel.next()
      }
    },
    goToSlide(index) {
      if (this.$refs.carousel && this.carouselData.length > 1) {
        this.$refs.carousel.setActiveItem(index)
        this.carouselIndex = index
      }
    },
    startAutoplay() {
      if (this.autoplayTimer) return
      this.autoplayTimer = setInterval(() => {
        if (this.$refs.carousel && this.carouselData.length > 1) {
          this.$refs.carousel.next()
        }
      }, 3000)
    },
    stopAutoplay() {
      if (this.autoplayTimer) {
        clearInterval(this.autoplayTimer)
      }
      this.autoplayTimer = null
    },
    pauseAutoplay() {
      this.stopAutoplay()
    }
  }
}
</script>

<style>
/* ============ 推荐模块整体容器 ============ */
.recommend-module {
  margin: 15px 0;
  padding: 20px 24px;
  background: #faf8f7;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* ============ 标题栏 ============ */
.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.recommend-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.recommend-title-icon {
  font-size: 22px;
  color: #ff7d00;
}

.recommend-title {
  font-size: 20px;
  color: #333333;
  font-weight: bold;
}

.view-all-btn {
  color: #ff7d00 !important;
  font-size: 14px;
  padding: 5px 12px;
  border-radius: 20px;
  background: rgba(255, 125, 0, 0.06);
  transition: all 0.2s;
}

.view-all-btn:hover {
  background: rgba(255, 125, 0, 0.12) !important;
  color: #e06a00 !important;
}

.view-all-btn:focus,
.view-all-btn:active {
  background: rgba(255, 125, 0, 0.06) !important;
  color: #ff7d00 !important;
}

/* ============ 轮播容器 ============ */
.carousel-container {
  position: relative;
  padding: 0 35px;
}

/* ============ 轮播箭头 ============ */
.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.carousel-arrow:hover {
  background: white;
  box-shadow: 0 4px 12px rgba(255, 125, 0, 0.2);
  transform: translateY(-50%) scale(1.1);
}

.carousel-arrow-left {
  left: 5px;
}

.carousel-arrow-right {
  right: 5px;
}

.carousel-arrow i {
  font-size: 16px;
  color: #ff7d00;
}

/* ============ 底部指示器 ============ */
.carousel-indicators {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 15px;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e0dbd8;
  cursor: pointer;
  transition: all 0.3s;
}

.indicator-dot.active {
  background: #ff7d00;
  transform: scale(1.3);
  box-shadow: 0 2px 6px rgba(255, 125, 0, 0.3);
}

.indicator-dot:hover {
  background: #ffaa44;
}

/* ============ 轮播卡片组 ============ */
.carousel-item-wrapper {
  display: flex;
  justify-content: flex-start;
  gap: 20px;
  padding: 0 10px;
  height: 100%;
}

.carousel-item-wrapper .recommend-card {
  flex: 1;
  max-width: calc(33.333% - 14px);
  min-width: 260px;
}

/* ============ 推荐卡片（和服务预约页完全一致） ============ */
.recommend-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0e8e4;
  display: flex;
  flex-direction: column;
  height: 100%;
  position: relative;
  overflow: hidden;
}

.recommend-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 卡片主体（点击区域） */
.card-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  min-height: 0;
  overflow: hidden;
}

/* 收藏按钮 */
.card-favorite {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 5;
  border-radius: 50%;
  transition: background 0.2s;
}

.card-favorite:hover {
  background: rgba(255, 125, 0, 0.08);
}

.card-favorite i {
  font-size: 20px;
  color: #ccc;
  transition: color 0.2s;
}

.card-favorite i.favorited {
  color: #ff7d00;
}

.card-favorite:hover i {
  color: #ff7d00;
}

/* ============ 卡片头部：标题 + 类型标签 ============ */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  gap: 8px;
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

/* 热度/新品标签行 */
.heat-tag-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-shrink: 0;
  flex-wrap: wrap;
}

.heat-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: bold;
  white-space: nowrap;
}

.heat-tag.heat-HOT {
  background: #fff1e6;
  color: #e64a19;
  border: 1px solid #ffccbc;
}

.heat-tag.heat-NEW {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
}

.heat-tag.heat-PERSONALIZED {
  background: #fff3e0;
  color: #e65100;
  border: 1px solid #ffe0b2;
}

.sales-count {
  font-size: 11px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 3px;
}

.sales-icon {
  font-size: 12px;
  color: #c8c0ba;
}

/* ============ 服务描述 ============ */
.service-desc {
  font-size: 14px;
  color: #666666;
  margin: 0 0 10px 0;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-shrink: 0;
}

/* ============ 元信息区：时长、门店、电话 ============ */
.service-meta {
  margin-bottom: 10px;
  flex-shrink: 0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666666;
  margin: 0 0 4px 0;
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
  margin-bottom: 10px;
  flex-wrap: wrap;
  gap: 6px;
  flex-shrink: 0;
}

.rating-with-score {
  display: flex;
  align-items: center;
  gap: 4px;
}

.rating-score {
  font-size: 14px;
  color: #ff7d00;
  font-weight: bold;
  line-height: 1;
  min-width: 30px;
}

.review-link {
  font-size: 13px;
  color: #999999;
  white-space: nowrap;
}

/* ============ 推荐理由标签 ============ */
.reason-tag {
  font-size: 11px;
  color: #ff7d00;
  background: #fff3e6;
  padding: 2px 10px;
  border-radius: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
  border: 1px solid #ffd4b0;
}

.reason-tag i {
  margin-right: 4px;
}

/* ============ 卡片底部：价格 + 按钮 ============ */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 4px;
}

.price {
  font-size: 20px;
  color: #ff7d00;
  font-weight: bold;
  line-height: 1;
}

.reserve-btn {
  width: 100px !important;
  height: 34px !important;
  border-radius: 8px !important;
  font-size: 13px !important;
  background-color: #ff7d00 !important;
  border-color: #ff7d00 !important;
  transition: background-color 0.2s ease, transform 0.15s ease, box-shadow 0.2s ease !important;
  box-shadow: 0 2px 8px rgba(255, 125, 0, 0.25) !important;
  padding: 0 12px !important;
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

/* ============ 未登录 & 空状态 ============ */
.recommend-no-login,
.recommend-empty {
  text-align: center;
  padding: 30px;
  color: #999;
}

.recommend-no-login p,
.recommend-empty p {
  margin: 0;
  font-size: 14px;
}

/* ============ Element UI 基础覆盖 ============ */
.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  margin: 0;
  text-align: center;
}

.el-carousel__item:nth-child(2n) {
  background-color: transparent;
}

.el-carousel__item:nth-child(2n+1) {
  background-color: transparent;
}

.el-carousel__arrow {
  display: none !important;
}

.el-carousel__indicators {
  display: none !important;
}
</style>
