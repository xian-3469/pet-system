<template>
  <div class="pet-public-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title-row">
        <i class="el-icon-user-solid page-title-icon"></i>
        <span class="page-title">宠物广场</span>
      </div>
      <span class="page-subtitle">看看大家分享的毛孩子们~</span>
    </div>

    <!-- 搜索筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-input
          size="medium"
          v-model="petName"
          placeholder="搜索宠物名称"
          prefix-icon="el-icon-search"
          clearable
          style="width: 200px"
          @keyup.enter.native="load"
        ></el-input>
        <el-select size="medium" v-model="petType" placeholder="宠物类型" style="width: 130px" clearable @change="load">
          <el-option label="全部" value="all"></el-option>
          <el-option label="猫咪" value="cat"></el-option>
          <el-option label="狗狗" value="dog"></el-option>
          <el-option label="其他" value="other"></el-option>
        </el-select>
      </div>
      <div class="filter-right">
        <el-select size="medium" v-model="orderBy" style="width: 150px" @change="load">
          <el-option label="最新发布" value="latest"></el-option>
          <el-option label="最多点赞" value="most_liked"></el-option>
          <el-option label="最多评论" value="most_commented"></el-option>
        </el-select>
      </div>
    </div>

    <!-- 话题标签栏 -->
    <div class="topic-bar" v-if="topics.length > 0">
      <span
        v-for="topic in topics"
        :key="topic.id"
        :class="['topic-tag', { active: selectedTopic === topic.name }]"
        @click="toggleTopic(topic.name)"
      >
        # {{ topic.name }}
      </span>
    </div>

    <!-- 加载状态 -->
    <div v-loading="loading" class="loading-wrapper">
      <!-- 空状态 -->
      <div v-if="!loading && tableData.length === 0" class="empty-state">
        <i class="el-icon-picture-outline" style="font-size: 64px; color: #ddd; display: block; margin-bottom: 16px;"></i>
        <p style="color: #999; font-size: 15px; margin: 0 0 8px 0;">还没有人分享宠物，快来当第一个吧~</p>
        <el-button type="warning" size="small" @click="$router.push('/front/petProfile')">去分享</el-button>
      </div>

      <!-- 宠物卡片网格 -->
      <div v-else class="pet-grid">
        <div
          v-for="item in tableData"
          :key="item.id"
          class="pet-card"
        >
          <!-- 左侧文字信息区域 65% -->
          <div class="card-info-area">
            <!-- 发布者信息 -->
            <div class="owner-info" @click.stop="goUserProfile(item.ownerId)">
              <img
                :src="item.ownerAvatar ? (baseApi + item.ownerAvatar) : require('../../assets/front-logo.jpg')"
                class="owner-avatar"
                alt="用户头像"
              />
              <span class="owner-name">{{ item.ownerNickname || '匿名用户' }}</span>
            </div>
            <div class="owner-time">{{ item.timeAgo }}</div>

            <!-- 宠物名字 + 性别 -->
            <div class="pet-name-row">
              <span class="pet-name">{{ item.petName }}</span>
              <span class="pet-gender" :class="getGenderClass(item.gender)">
                {{ item.genderText || formatGender(item.gender) }}
              </span>
            </div>

            <!-- 话题标签 -->
            <div class="tag-row" v-if="item.tags">
              <span
                v-for="(tag, idx) in getTagList(item.tags).slice(0, 2)"
                :key="idx"
                class="pet-tag"
                @click.stop="toggleTopic(tag)"
              >
                # {{ tag }}
              </span>
            </div>

            <!-- 宠物简介 -->
            <p class="pet-intro" v-if="item.intro">{{ item.intro }}</p>

            <!-- 基本信息 -->
            <div class="pet-info-grid">
              <div class="info-item" v-if="item.breed">
                <i class="el-icon-guide info-icon"></i>
                <span>{{ item.breed }}</span>
              </div>
              <div class="info-item" v-if="item.age">
                <i class="el-icon-date info-icon"></i>
                <span>{{ item.age }} 月</span>
              </div>
              <div class="info-item" v-if="item.neutered !== null && item.neutered !== undefined">
                <i class="el-icon-magic-stick info-icon"></i>
                <span>{{ item.neutered === 1 ? '已绝育' : '未绝育' }}</span>
              </div>
              <div class="info-item" v-if="item.adoptDateText && item.adoptDateText !== '-'">
                <i class="el-icon-house info-icon"></i>
                <span>{{ item.adoptDateText }}</span>
              </div>
            </div>

            <!-- 互动按钮 -->
            <div class="interaction-bar" @click.stop>
              <div class="interaction-btn" :class="{ active: item.liked }" @click="toggleLike(item)">
                <i class="el-icon-like"></i>
                <span>{{ item.likeCount || 0 }}</span>
              </div>
              <div class="interaction-btn" @click="openCommentDialog(item)">
                <i class="el-icon-chat-dot-square"></i>
                <span>{{ item.commentCount || 0 }}</span>
              </div>
              <div class="interaction-btn" :class="{ active: item.collected }" @click="toggleCollect(item)">
                <i :class="item.collected ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                <span>{{ item.collectCount || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- 右侧正方形图片区域 35% -->
          <div class="card-image-area">
            <div class="card-favorite" @click.stop="toggleCollect(item)">
              <i :class="item.collected ? 'el-icon-star-on favorited' : 'el-icon-star-off'"></i>
            </div>
            <img
              :src="item.avatar ? (baseApi + item.avatar) : require('../../assets/front-logo.jpg')"
              class="card-image"
              alt="宠物头像"
            />
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="tableData.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[6, 12, 18, 24]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          background
        ></el-pagination>
      </div>
    </div>

    <!-- 评论对话框 -->
    <el-dialog title="评论" :visible.sync="commentDialogVisible" width="500px" :close-on-click-modal="false">
      <div class="comment-pet-info" v-if="commentTarget">
        <img :src="commentTarget.avatar ? (baseApi + commentTarget.avatar) : require('../../assets/front-logo.jpg')" class="comment-pet-avatar" />
        <span class="comment-pet-name">{{ commentTarget.petName }}</span>
      </div>
      <el-input
        type="textarea"
        v-model="commentContent"
        placeholder="说点什么吧..."
        :rows="3"
        maxlength="500"
        show-word-limit
        style="margin-top: 12px"
      ></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="commentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComment" :disabled="!commentContent.trim()">发表评论</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'PetPublic',
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 12,
      petName: '',
      petType: 'all',
      orderBy: 'latest',
      selectedTopic: '',
      topics: [],
      loading: false,
      baseApi: this.$store.state.baseApi,
      user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null,
      commentDialogVisible: false,
      commentTarget: null,
      commentContent: ''
    }
  },
  created() {
    this.loadTopics()
    this.load()
  },
  methods: {
    load() {
      this.loading = true
      const params = {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        orderBy: this.orderBy,
        petType: this.petType
      }
      if (this.petName) params.petName = this.petName
      if (this.selectedTopic) params.topic = this.selectedTopic

      this.request.get('/pet-profile/public/page', { params }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data.records || []
          this.total = res.data.total || 0
        }
      }).catch(() => {
        this.$message.error('加载失败，请稍后重试')
      }).finally(() => {
        this.loading = false
      })
    },
    loadTopics() {
      this.request.get('/pet-profile/public/topics').then(res => {
        if (res.code === '200') {
          this.topics = res.data || []
        }
      }).catch(() => {})
    },
    reset() {
      this.petName = ''
      this.petType = 'all'
      this.orderBy = 'latest'
      this.selectedTopic = ''
      this.pageNum = 1
      this.load()
    },
    toggleTopic(tag) {
      if (this.selectedTopic === tag) {
        this.selectedTopic = ''
      } else {
        this.selectedTopic = tag
      }
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
    goUserProfile(ownerId) {
      if (ownerId) {
        this.$router.push({ path: '/front/person', query: { userId: ownerId } })
      }
    },
    toggleLike(item) {
      if (!this.user) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.request.post('/pet-profile/public/like/' + item.id).then(res => {
        if (res.code === '200') {
          const action = res.data
          if (action === 'like') {
            item.liked = true
            item.likeCount = (item.likeCount || 0) + 1
            this.$message.success('点赞成功')
          } else {
            item.liked = false
            item.likeCount = Math.max((item.likeCount || 0) - 1, 0)
            this.$message.success('取消点赞')
          }
        }
      }).catch(() => {})
    },
    toggleCollect(item) {
      if (!this.user) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.request.post('/pet-profile/public/collect/' + item.id).then(res => {
        if (res.code === '200') {
          const action = res.data
          if (action === 'collect') {
            item.collected = true
            item.collectCount = (item.collectCount || 0) + 1
            this.$message.success('收藏成功')
          } else {
            item.collected = false
            item.collectCount = Math.max((item.collectCount || 0) - 1, 0)
            this.$message.success('取消收藏')
          }
        }
      }).catch(() => {})
    },
    openCommentDialog(item) {
      if (!this.user) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.commentTarget = item
      this.commentContent = ''
      this.commentDialogVisible = true
    },
    submitComment() {
      if (!this.commentContent.trim()) return
      this.request.post('/pet-profile/public/comment', {
        petProfileId: this.commentTarget.id,
        content: this.commentContent.trim()
      }).then(res => {
        if (res.code === '200') {
          this.$message.success('评论成功')
          this.commentTarget.commentCount = (this.commentTarget.commentCount || 0) + 1
          this.commentDialogVisible = false
        }
      }).catch(() => {
        this.$message.error('评论失败，请稍后重试')
      })
    },
    getTagList(tags) {
      if (!tags) return []
      return tags.split(',').map(t => t.trim()).filter(t => t)
    },
    formatGender(gender) {
      if (gender === 'FEMALE' || gender === '母') return '♀ 母'
      if (gender === 'MALE' || gender === '公') return '♂ 公'
      return gender || ''
    },
    getGenderClass(gender) {
      if (gender === 'FEMALE' || gender === '母') return 'gender-female'
      if (gender === 'MALE' || gender === '公') return 'gender-male'
      return ''
    }
  }
}
</script>

<style scoped>
/* ============ 页面整体布局 ============ */
.pet-public-page {
  padding: 0 20px 20px;
  min-height: calc(100vh - 60px);
}

/* ============ 页面标题 ============ */
.page-header {
  background: #fff;
  border-radius: 10px;
  padding: 20px 24px 16px;
  margin: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.page-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.page-title-icon {
  font-size: 24px;
  color: #ff7d00;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.page-subtitle {
  font-size: 13px;
  color: #999;
  margin-left: 34px;
}

/* ============ 筛选栏 ============ */
.filter-bar {
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.filter-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ============ 话题标签栏 ============ */
.topic-bar {
  background: #fff;
  border-radius: 10px;
  padding: 12px 20px;
  margin-bottom: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.topic-tag {
  font-size: 12px;
  color: #ff7d00;
  background: #fff3e6;
  border: 1px solid #ffd4b0;
  border-radius: 20px;
  padding: 4px 14px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.topic-tag:hover,
.topic-tag.active {
  background: #ff7d00;
  color: #fff;
  border-color: #ff7d00;
}

/* ============ 加载状态 ============ */
.loading-wrapper {
  min-height: 400px;
}

/* ============ 空状态 ============ */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

/* ============ 宠物网格 ============ */
.pet-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  grid-auto-rows: 220px;
}

@media (max-width: 900px) {
  .pet-grid {
    grid-template-columns: 1fr;
  }
}

/* ============ 宠物卡片 ============ */
.pet-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0e8e4;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: stretch;
  overflow: hidden;
  padding: 16px;
  gap: 16px;
  height: 220px;
}

.pet-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 125, 0, 0.15);
  border-color: #ffd4b0;
}

/* ============ 左侧文字区域 65% ============ */
.card-info-area {
  flex: 0 0 65%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
  overflow: hidden;
}

/* ============ 右侧正方形图片区域 35% ============ */
.card-image-area {
  flex: 0 0 35%;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: #faf8f7;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* ============ 收藏按钮（图片右上角） ============ */
.card-favorite {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 5;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.card-favorite:hover {
  background: #fff;
  transform: scale(1.1);
}

.card-favorite i {
  font-size: 16px;
  color: #ccc;
  transition: color 0.2s;
}

.card-favorite i.favorited {
  color: #ff7d00;
}

.pet-card:hover .card-favorite:hover i {
  color: #ff7d00;
}

/* ============ 发布者信息 ============ */
.owner-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.owner-info:hover {
  opacity: 0.75;
}

.owner-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid #ffd4b0;
  flex-shrink: 0;
}

.owner-name {
  font-size: 13px;
  color: #666;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.owner-time {
  font-size: 11px;
  color: #bbb;
  margin-top: 2px;
}

/* ============ 宠物名字行 ============ */
.pet-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pet-name {
  font-size: 17px;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pet-gender {
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 4px;
  white-space: nowrap;
  flex-shrink: 0;
}

.gender-male {
  color: #ff7d00;
  background: #fff3e6;
  border: 1px solid #ffd4b0;
}

.gender-female {
  color: #ff7d00;
  background: #fff3e6;
  border: 1px solid #ffd4b0;
}

/* ============ 话题标签 ============ */
.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.pet-tag {
  font-size: 11px;
  color: #ff7d00;
  background: #fff3e6;
  border: 1px solid #ffd4b0;
  border-radius: 20px;
  padding: 2px 8px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.pet-tag:hover {
  background: #ff7d00;
  color: #fff;
  border-color: #ff7d00;
}

/* ============ 宠物简介 ============ */
.pet-intro {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin: 4px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* ============ 宠物信息网格 ============ */
.pet-info-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #888;
  background: #faf8f7;
  padding: 3px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.info-icon {
  color: #c8c0ba;
  font-size: 12px;
}

/* ============ 互动按钮 ============ */
.interaction-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.interaction-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
  cursor: pointer;
  transition: all 0.2s;
  padding: 4px 6px;
  border-radius: 6px;
  user-select: none;
}

.interaction-btn:hover {
  color: #ff7d00;
  background: #fff3e6;
}

.interaction-btn i {
  font-size: 16px;
}

.interaction-btn.active {
  color: #ff7d00;
}

.interaction-btn.active i {
  color: #ff7d00;
}

/* ============ 分页 ============ */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-bottom: 20px;
}

/* ============ 评论对话框 ============ */
.comment-pet-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  background: #faf8f7;
  border-radius: 8px;
}

.comment-pet-avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  object-fit: cover;
}

.comment-pet-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}
</style>
