<template>
  <div>
    <div style="padding: 20px">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/front/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>站内信</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div style="margin: 20px 0; display: flex; justify-content: space-between; align-items: center;">
      <div style="font-size: 18px; font-weight: bold; color: var(--pet-primary-dark);">
        📬 我的站内信
      </div>
      <div style="color: #999; font-size: 14px;">
        共 {{ tableData.length }} 条消息
      </div>
    </div>

    <div v-if="tableData.length === 0" style="text-align: center; padding: 60px 0; color: #999;">
      <i class="el-icon-message" style="font-size: 60px; margin-bottom: 20px;"></i>
      <p>暂无站内信消息</p>
    </div>

    <div v-else>
      <el-card v-for="item in tableData" :key="item.id"
               style="margin-bottom: 15px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);"
               :class="{ 'unread-card': !item.isRead }">
        <div style="display: flex; justify-content: space-between; align-items: flex-start;">
          <div style="flex: 1;">
            <div style="display: flex; align-items: center; margin-bottom: 8px;">
              <el-tag v-if="!item.isRead" type="danger" size="small" style="margin-right: 8px;">未读</el-tag>
              <el-tag v-else type="info" size="small" style="margin-right: 8px;">已读</el-tag>
              <span style="font-weight: bold; color: var(--pet-text);">{{ item.title }}</span>
            </div>
            <div style="color: #666; margin-bottom: 8px; line-height: 1.6;">
              {{ item.content }}
            </div>
            <div style="color: #999; font-size: 14px; display: flex; align-items: center;">
              <i class="el-icon-time" style="margin-right: 5px;"></i>
              <span>{{ formatTime(item.createTime) }}</span>
            </div>
          </div>
          <div>
            <el-button type="text" size="small" @click.stop="del(item.id)">删除</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: "Notice",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      tableData: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    load() {
      this.request.get("/health-record/notice/my").then(res => {
        if (res.code === '200') {
          this.tableData = res.data || []
        }
      })
    },
    del(id) {
      this.$confirm("确定删除该消息吗？", "提示", {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.request.delete("/health-record/notice/" + id).then(res => {
          if (res.code === '200') {
            this.$message.success("删除成功")
            this.load()
          } else {
            this.$message.error(res.msg || "删除失败")
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.unread-card {
  border-left: 4px solid var(--pet-primary);
  background: linear-gradient(to right, rgba(255, 154, 86, 0.05), white);
}
</style>
