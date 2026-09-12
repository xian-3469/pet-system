<template>
  <div class="ai-assistant">
    <!-- 悬浮球 -->
    <div class="ai-fab" @click="openDrawer" :class="{ 'ai-fab-pulse': unreadTip }">
      <span class="ai-fab-icon">🐾</span>
      <span class="ai-fab-text">AI 助手</span>
    </div>

    <!-- 聊天抽屉 -->
    <el-drawer
      :visible.sync="visible"
      direction="rtl"
      :size="drawerWidth"
      :with-header="false"
      :wrapperClosable="true"
      custom-class="ai-drawer"
    >
      <div class="ai-chat">
        <!-- 左缘拖拽条：拖动向左拉大 / 双击恢复默认 -->
        <div
          class="ai-resize-handle"
          :class="{ 'ai-resize-active': dragging }"
          title="拖动调整宽度，双击恢复默认"
          @mousedown.prevent="startDrag"
          @dblclick="resetWidth"
        ></div>
        <!-- 头部 -->
        <div class="ai-header">
          <div class="ai-header-info">
            <span class="ai-avatar">🐾</span>
            <div>
              <div class="ai-title">小宠 AI 助手</div>
              <div class="ai-subtitle">领养 · 健康 · 服务，有事随时问我</div>
            </div>
          </div>
          <div class="ai-header-actions">
            <el-tooltip content="清空会话" placement="top">
              <i class="el-icon-delete" @click="clearHistory"></i>
            </el-tooltip>
            <i class="el-icon-close" @click="visible = false"></i>
          </div>
        </div>

        <!-- 未登录提示 -->
        <div v-if="!user.id" class="ai-login-tip">
          <p>🐾 登录后即可与小宠 AI 助手对话</p>
          <el-button type="primary" size="small" round @click="$router.push('/login')">去登录</el-button>
        </div>

        <!-- 消息区 -->
        <div v-else class="ai-messages" ref="messages">
          <div v-for="(msg, index) in messages" :key="index" :class="['ai-msg', msg.role]">
            <template v-if="msg.role === 'assistant'">
              <div class="ai-msg-avatar">🐾</div>
              <div class="ai-msg-body">
                <div v-if="msg.trace && msg.trace.length" class="ai-trace">
                  <span v-for="(t, ti) in msg.trace" :key="ti" class="ai-trace-tag">
                    <i class="el-icon-search"></i> {{ t.brief }}
                  </span>
                </div>
                <div class="ai-bubble" v-html="renderMarkdown(msg.content)"></div>
              </div>
            </template>
            <template v-else>
              <div class="ai-bubble user-bubble">{{ msg.content }}</div>
            </template>
          </div>

          <!-- 正在思考 -->
          <div v-if="loading" class="ai-msg assistant">
            <div class="ai-msg-avatar">🐾</div>
            <div class="ai-msg-body">
              <div class="ai-bubble typing">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>

          <!-- 空状态 + 快捷问题 -->
          <div v-if="messages.length === 0 && !loading" class="ai-welcome">
            <div class="ai-welcome-emoji">🐶</div>
            <p>你好呀，我是小宠！可以帮你查领养信息、解答养宠问题～</p>
            <div class="ai-chips">
              <span class="ai-chip" v-for="(q, i) in quickQuestions" :key="i" @click="send(q)">{{ q }}</span>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div v-if="user.id" class="ai-input-area">
          <textarea
            v-model="input"
            class="ai-input"
            placeholder="输入你的问题，Enter 发送，Shift+Enter 换行"
            rows="2"
            @keydown.enter.exact.prevent="onEnter"
          ></textarea>
          <el-button
            class="ai-send-btn"
            type="primary"
            icon="el-icon-s-promotion"
            :disabled="loading || !input.trim()"
            @click="onEnter"
          >发送</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
export default {
  name: "AiAssistant",
  data() {
    return {
      visible: false,
      loading: false,
      input: "",
      drawerWidth: 420,
      dragging: false,
      dragStartX: 0,
      dragStartWidth: 0,
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      messages: [],
      quickQuestions: [
        "现在有哪些可领养的猫咪？",
        "我的宠物最近该做什么健康护理？",
        "推荐几个宠物洗护服务",
        "附近的救助站在哪里？"
      ]
    }
  },
  computed: {
    unreadTip() {
      return !this.visible && this.user.id && this.messages.length === 0
    }
  },
  created() {
    // 恢复上次的抽屉宽度（360 ~ 窗口85% 之间才有效）
    try {
      const saved = parseInt(localStorage.getItem("ai_drawer_width"))
      const max = Math.floor(window.innerWidth * 0.85)
      if (!isNaN(saved) && saved >= 360 && saved <= max) {
        this.drawerWidth = saved
      }
    } catch (e) { /* 忽略 */ }
    this.loadHistory()
  },
  beforeDestroy() {
    // 组件销毁时清理全局事件，防止泄漏
    document.removeEventListener('mousemove', this.handleMove)
    document.removeEventListener('mouseup', this.stopDrag)
  },
  methods: {
    // ===== 抽屉宽度拖拽 =====
    startDrag(e) {
      this.dragging = true
      this.dragStartX = e.clientX
      this.dragStartWidth = this.drawerWidth
      document.body.style.userSelect = 'none'
      document.addEventListener('mousemove', this.handleMove)
      document.addEventListener('mouseup', this.stopDrag)
    },
    handleMove(e) {
      if (!this.dragging) return
      // 抽屉靠右，向左拖 = 宽度增大；限制在 360px ~ 窗口85%
      const delta = this.dragStartX - e.clientX
      const max = Math.floor(window.innerWidth * 0.85)
      this.drawerWidth = Math.min(max, Math.max(360, this.dragStartWidth + delta))
    },
    stopDrag() {
      if (!this.dragging) return
      this.dragging = false
      document.body.style.userSelect = ''
      document.removeEventListener('mousemove', this.handleMove)
      document.removeEventListener('mouseup', this.stopDrag)
      try { localStorage.setItem('ai_drawer_width', String(this.drawerWidth)) } catch (e) { /* 忽略 */ }
    },
    resetWidth() {
      this.drawerWidth = 420
      try { localStorage.setItem('ai_drawer_width', '420') } catch (e) { /* 忽略 */ }
    },
    openDrawer() {
      this.user = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
      this.visible = true
      this.scrollToBottom()
    },
    storageKey() {
      return "ai_chat_history_" + (this.user.id || "guest")
    },
    loadHistory() {
      try {
        const raw = localStorage.getItem(this.storageKey())
        this.messages = raw ? JSON.parse(raw) : []
      } catch (e) {
        this.messages = []
      }
    },
    saveHistory() {
      try {
        // 只保留最近 40 条，避免超出 localStorage 容量
        const trimmed = this.messages.slice(-40)
        localStorage.setItem(this.storageKey(), JSON.stringify(trimmed))
      } catch (e) { /* 忽略存储失败 */ }
    },
    clearHistory() {
      this.$confirm("确定要清空本次会话吗？", "提示", { type: "warning" }).then(() => {
        this.messages = []
        this.saveHistory()
      }).catch(() => {})
    },
    onEnter() {
      if (this.loading || !this.input.trim()) return
      this.send(this.input.trim())
      this.input = ""
    },
    send(text) {
      if (this.loading) return
      const question = (text || "").trim()
      if (!question) return
      if (!this.user.id) {
        this.$message.warning("请先登录")
        return
      }
      this.messages.push({ role: "user", content: question })
      this.loading = true
      this.scrollToBottom()

      // 组装历史（不含轨迹，只保留 user/assistant 文本，最多最近 12 条）
      const history = this.messages
        .filter(m => (m.role === "user" || m.role === "assistant") && m.content && !m.isError)
        .slice(-13, -1)
        .map(m => ({ role: m.role, content: m.content }))

      this.request.post("/ai/chat", { history: history, message: question }, { timeout: 120000 })
        .then(res => {
          this.loading = false
          if (res.code === "200") {
            this.messages.push({
              role: "assistant",
              content: res.data.reply || "（AI 没有返回内容）",
              trace: res.data.trace || []
            })
          } else {
            this.messages.push({ role: "assistant", content: "😅 " + (res.msg || "AI 服务暂时不可用，请稍后再试"), isError: true })
          }
          this.saveHistory()
          this.scrollToBottom()
        })
        .catch(() => {
          this.loading = false
          this.messages.push({ role: "assistant", content: "😅 AI 服务连接失败，请稍后再试", isError: true })
          this.saveHistory()
          this.scrollToBottom()
        })
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const box = this.$refs.messages
        if (box) {
          box.scrollTop = box.scrollHeight
        }
      })
    },
    // 轻量 Markdown 渲染（加粗/行内代码/代码块/标题/列表/链接/换行）
    renderMarkdown(text) {
      if (!text) return ""
      let html = text
        .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
      // 代码块
      html = html.replace(/```([\s\S]*?)```/g, (m, code) => "<pre class='ai-code'>" + code.trim() + "</pre>")
      // 标题
      html = html.replace(/^### (.*)$/gm, "<h4>$1</h4>")
        .replace(/^## (.*)$/gm, "<h3>$1</h3>")
        .replace(/^# (.*)$/gm, "<h3>$1</h3>")
      // 加粗 / 斜体 / 行内代码
      html = html.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>")
        .replace(/(^|[^*])\*([^*\n]+)\*/g, "$1<em>$2</em>")
        .replace(/`([^`]+)`/g, "<code class='ai-inline-code'>$1</code>")
      // 链接
      html = html.replace(/\[([^\]]+)\]\((https?:[^)]+)\)/g, "<a href='$2' target='_blank'>$1</a>")
      // 列表
      html = html.replace(/^\s*[-*] (.*)$/gm, "<li>$1</li>")
      html = html.replace(/(<li>[\s\S]*?<\/li>)(?!\s*<li>)/g, "<ul class='ai-list'>$1</ul>")
      // 换行
      html = html.replace(/\n/g, "<br>")
      // 修正块级元素间多余的 br
      html = html.replace(/(<\/h3>|<\/h4>|<\/ul>|<\/pre>)<br>/g, "$1").replace(/<br>(<h3>|<h4>|<ul class='ai-list'>|<pre class='ai-code'>)/g, "$1")
      return html
    }
  }
}
</script>

<style scoped>
/* 悬浮球 */
.ai-fab {
  position: fixed;
  right: 28px;
  bottom: 32px;
  z-index: 2000;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 32px;
  background: linear-gradient(135deg, #FF9A56 0%, #FF7E3D 100%);
  color: #fff;
  font-size: 15px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(255, 126, 61, 0.45);
  transition: transform .25s, box-shadow .25s;
  user-select: none;
}
.ai-fab:hover {
  transform: translateY(-3px) scale(1.03);
  box-shadow: 0 10px 26px rgba(255, 126, 61, 0.55);
}
.ai-fab-icon { font-size: 20px; }
.ai-fab-pulse { animation: aiPulse 2.4s ease-in-out infinite; }
@keyframes aiPulse {
  0%, 100% { box-shadow: 0 6px 20px rgba(255, 126, 61, 0.45); }
  50% { box-shadow: 0 6px 30px rgba(255, 126, 61, 0.85); }
}

/* 抽屉整体 */
.ai-drawer { border-radius: 16px 0 0 16px; }
.ai-chat {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--pet-bg, #FFFAF6);
}

/* 左缘拖拽条 */
.ai-resize-handle {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  cursor: col-resize;
  z-index: 20;
  background: transparent;
  transition: background .2s;
}
.ai-resize-handle:hover,
.ai-resize-handle.ai-resize-active {
  background: rgba(255, 154, 86, .45);
}

/* 头部 */
.ai-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: linear-gradient(135deg, #FF9A56 0%, #FF7E3D 100%);
  color: #fff;
}
.ai-header-info { display: flex; align-items: center; gap: 10px; }
.ai-avatar {
  width: 40px; height: 40px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(255,255,255,0.25);
  border-radius: 50%;
  font-size: 22px;
}
.ai-title { font-size: 16px; font-weight: bold; }
.ai-subtitle { font-size: 12px; opacity: .9; }
.ai-header-actions { display: flex; align-items: center; gap: 14px; font-size: 18px; }
.ai-header-actions i { cursor: pointer; opacity: .9; }
.ai-header-actions i:hover { opacity: 1; }

/* 未登录 */
.ai-login-tip {
  flex: 1;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  gap: 14px; color: var(--pet-text, #333);
}

/* 消息区 */
.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 14px;
}
.ai-msg { display: flex; margin-bottom: 14px; }
.ai-msg.user { justify-content: flex-end; }
.ai-msg-avatar {
  width: 32px; height: 32px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  background: #fff; border: 1px solid var(--pet-border, #FFD9C2);
  border-radius: 50%; font-size: 17px;
  margin-right: 8px;
}
.ai-msg-body { max-width: 82%; }
.ai-bubble {
  background: #fff;
  border: 1px solid var(--pet-border, #FFD9C2);
  border-radius: 4px 14px 14px 14px;
  padding: 10px 12px;
  font-size: 14px;
  line-height: 1.65;
  color: var(--pet-text, #333);
  word-break: break-word;
}
.user-bubble {
  background: linear-gradient(135deg, #FF9A56 0%, #FF7E3D 100%);
  color: #fff;
  border: none;
  border-radius: 14px 4px 14px 14px;
}
.ai-bubble ::v-deep h3, .ai-bubble ::v-deep h4 { margin: 6px 0 4px; font-size: 14px; }
.ai-bubble ::v-deep ul.ai-list { margin: 4px 0; padding-left: 18px; }
.ai-bubble ::v-deep pre.ai-code {
  background: #f6f6f6; border-radius: 8px;
  padding: 8px; margin: 6px 0;
  font-size: 12px; overflow-x: auto; white-space: pre-wrap;
}
.ai-bubble ::v-deep code.ai-inline-code {
  background: #f6f6f6; border-radius: 4px; padding: 1px 5px; font-size: 12px;
}
.ai-bubble ::v-deep a { color: var(--pet-primary, #FF7E3D); }

/* 工具调用轨迹 */
.ai-trace { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 6px; }
.ai-trace-tag {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: 11px; color: #b06a2c;
  background: #FFF1E5;
  border: 1px solid #FFD9C2;
  border-radius: 10px;
  padding: 2px 8px;
}

/* 打字动画 */
.typing { display: flex; align-items: center; gap: 4px; padding: 14px 16px; }
.typing span {
  width: 7px; height: 7px; border-radius: 50%;
  background: #FF9A56; display: inline-block;
  animation: aiTyping 1.2s infinite ease-in-out;
}
.typing span:nth-child(2) { animation-delay: .2s; }
.typing span:nth-child(3) { animation-delay: .4s; }
@keyframes aiTyping {
  0%, 60%, 100% { transform: translateY(0); opacity: .5; }
  30% { transform: translateY(-5px); opacity: 1; }
}

/* 欢迎语 + 快捷问题 */
.ai-welcome { text-align: center; padding: 30px 10px; color: var(--pet-text, #333); }
.ai-welcome-emoji { font-size: 44px; margin-bottom: 10px; }
.ai-welcome p { font-size: 13px; color: #999; margin-bottom: 16px; }
.ai-chips { display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; }
.ai-chip {
  font-size: 12px;
  color: #b06a2c;
  background: #fff;
  border: 1px solid var(--pet-border, #FFD9C2);
  border-radius: 14px;
  padding: 6px 12px;
  cursor: pointer;
  transition: all .2s;
}
.ai-chip:hover { background: #FFF1E5; border-color: var(--pet-primary, #FF7E3D); }

/* 输入区 */
.ai-input-area {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 12px;
  background: #fff;
  border-top: 1px solid var(--pet-border, #FFD9C2);
}
.ai-input {
  flex: 1;
  resize: none;
  border: 1px solid var(--pet-border, #FFD9C2);
  border-radius: 10px;
  padding: 8px 10px;
  font-size: 13px;
  font-family: inherit;
  outline: none;
  background: var(--pet-bg, #FFFAF6);
}
.ai-input:focus { border-color: var(--pet-primary, #FF7E3D); }
.ai-send-btn { border-radius: 10px; }
</style>
