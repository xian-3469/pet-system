<template>
  <div style="padding-bottom: 20px; min-height: calc(100vh - 60px)">
    <div v-if="!tableData.length" style=" margin: 10px 0">
      <el-card>
        <div style="text-align: center; color: #999;">暂无申请领养记录</div>
      </el-card>
    </div>
<!--    <div style="padding: 20px; background-color: #fff; border-radius: 10px; margin: 10px 0">-->
<!--      <el-input v-model="nickname" style="width: 400px" ></el-input> <el-button type="primary" > 搜 索</el-button>-->
<!--    </div>-->
    <div v-else style="margin: 10px 0">
      <el-card  v-for="item in tableData" :key="item.id" style="margin: 10px 0">
        <div style="display: flex">
          <div style="width: 140px"> <img :src="$store.state.baseApi+item.animal.img" alt="" style="width: 100%; height: 160px; border-radius: 10px"></div>
          <div style="padding-left: 50px; flex: 1">
            <div style="border-bottom: 1px solid #ddd; width: 100%; padding-bottom: 10px">
              <span style="font-size: 24px">{{ item.animal.nickname }}</span>
              <span style="margin-left: 20px">{{ item.animal.sex }}</span>
              <span style="margin-left: 20px">{{ item.animal.age }}</span>
              <b style="margin-left: 30px">
                <span v-if="item.state === '审核通过'" style="color: green">审核通过</span>
                <span v-if="item.state === '审核不通过'" style="color: red">审核不通过</span>
                <span v-if="item.state === '待审核'" style="color: #E6A23C">申请审核中</span>
              </b>
            </div>
            <div style="line-height: 30px">
              <div><b style="margin-right: 10px">是否绝育：</b>{{ item.animal.sterilization }}</div>
              <div><b style="margin-right: 10px">疫苗接种：</b>{{ item.animal.vaccine }}</div>
              <div><b style="margin-right: 10px">身体状态：</b>{{ item.animal.status }}</div>
              <div><b style="margin-right: 10px">其他描述：</b>{{ item.animal.information }}</div>
            </div>
          </div>
        </div>
        <div style="padding: 10px 0">
          <el-descriptions title="申请详情">
            <el-descriptions-item label="姓名">{{ item.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ item.sex }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ item.age }}</el-descriptions-item>
            <el-descriptions-item label="养宠经验">{{ item.experience }}</el-descriptions-item>
            <el-descriptions-item label="联系方式">{{ item.phone }}</el-descriptions-item>
            <el-descriptions-item label="婚姻">{{ item.married }}</el-descriptions-item>
            <el-descriptions-item label="收入">{{ item.income }}</el-descriptions-item>
            <el-descriptions-item label="职业">{{ item.profession }}</el-descriptions-item>
            <el-descriptions-item label="住址">{{ item.address }}</el-descriptions-item>
            <el-descriptions-item label="领养理由">{{ item.reason }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div style="padding-bottom: 10px">
          <el-button type="primary" plain size="small" icon="el-icon-magic-stick" :loading="analyzingId === item.id" @click="showAiAnalysis(item)">
            {{ analyzingId === item.id ? 'AI 分析中…' : 'AI 匹配分析' }}
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- AI 匹配分析弹窗 -->
    <el-dialog :title="analysisTitle" :visible.sync="analysisVisible" width="560px" :close-on-click-modal="false">
      <div v-if="analysis">
        <el-row :gutter="20" style="margin-bottom: 16px">
          <el-col :span="12">
            <el-card shadow="never" style="text-align: center; border-radius: 10px">
              <div style="font-size: 34px; font-weight: bold; color: #FF7E3D">{{ analysis.matchScore }}<span style="font-size: 14px">分</span></div>
              <div style="color: #999; font-size: 13px">算法匹配得分</div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" style="text-align: center; border-radius: 10px">
              <div style="font-size: 22px; font-weight: bold; color: #67C23A; line-height: 56px">{{ analysis.matchLevel }}</div>
            </el-card>
          </el-col>
        </el-row>
        <p style="color: #666; line-height: 1.7; background: #FFF7F0; border-radius: 8px; padding: 10px 14px">{{ analysis.conclusion }}</p>
        <div v-if="analysis.highlights && analysis.highlights.length" style="margin-top: 12px">
          <b style="color: #67C23A">✨ 匹配亮点</b>
          <ul style="padding-left: 20px; margin: 6px 0; color: #555; line-height: 1.8">
            <li v-for="(h, i) in analysis.highlights" :key="'h'+i">{{ h }}</li>
          </ul>
        </div>
        <div v-if="analysis.risks && analysis.risks.length" style="margin-top: 12px">
          <b style="color: #E6A23C">⚠️ 潜在风险</b>
          <ul style="padding-left: 20px; margin: 6px 0; color: #555; line-height: 1.8">
            <li v-for="(r, i) in analysis.risks" :key="'r'+i">{{ r }}</li>
          </ul>
        </div>
        <div v-if="analysis.suggestions && analysis.suggestions.length" style="margin-top: 12px">
          <b style="color: #409EFF">💡 改善建议</b>
          <ul style="padding-left: 20px; margin: 6px 0; color: #555; line-height: 1.8">
            <li v-for="(s, i) in analysis.suggestions" :key="'s'+i">{{ s }}</li>
          </ul>
        </div>
        <div style="text-align: center; color: #bbb; font-size: 12px; margin-top: 10px">由 AI 生成的分析仅供参考，最终以平台审核结果为准</div>
      </div>
    </el-dialog>


    <el-dialog title="信息" :visible.sync="dialogFormVisible" width="30%" :close-on-click-modal="false">
      <el-form label-width="100px"  style="width: 90%">
        <el-form-item label="姓名">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio v-model="form.sex" label="男">男</el-radio>
          <el-radio v-model="form.sex" label="女">女</el-radio>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="form.age" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="养宠经验">
          <el-radio v-model="form.experience" label="无经验">无经验</el-radio>
          <el-radio v-model="form.experience" label="有经验">有经验</el-radio>
        </el-form-item>
<!--        <el-form-item label="宠物">-->
<!--          <el-input v-model="form.pet" autocomplete="off"></el-input>-->
<!--        </el-form-item>-->
        <el-form-item label="联系方式">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="婚姻">
          <el-radio v-model="form.married" label="未婚">未婚</el-radio>
          <el-radio v-model="form.married" label="已婚">已婚</el-radio>
        </el-form-item>
        <el-form-item label="收入">
          <el-input v-model="form.income" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="职业">
          <el-input v-model="form.profession" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="form.address" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="领养理由">
          <el-input v-model="form.reason" autocomplete="off"></el-input>
        </el-form-item>
<!--        <el-form-item label="状态">-->
<!--          <el-input v-model="form.state" autocomplete="off"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="动物id">-->
<!--          <el-input v-model="form.animalId" autocomplete="off"></el-input>-->
<!--        </el-form-item>-->
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
  name: "FrontHome",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      nickname: "",
      dialogFormVisible: false,
      analyzingId: null,
      analysisVisible: false,
      analysisTitle: "",
      analysis: null,
      form: {}
    }
  },
  created() {
    this.load()
  },
  methods: {
    save() {
      this.request.post("/applcation", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleApply(animalId) {
      this.form = {animalId: animalId}
      this.dialogFormVisible = true
    },
    showAiAnalysis(item) {
      if (this.analyzingId) return
      this.analyzingId = item.id
      this.analysis = null
      this.analysisTitle = "「" + (item.animal ? item.animal.nickname : '') + "」领养匹配分析"
      this.request.get("/ai/adopt-analysis/" + item.id, { timeout: 120000 }).then(res => {
        this.analyzingId = null
        if (res.code === '200') {
          this.analysis = res.data
          this.analysisVisible = true
        } else {
          this.$message.error(res.msg || "AI 分析失败，请稍后再试")
        }
      }).catch(() => {
        this.analyzingId = null
        this.$message.error("AI 服务连接失败，请稍后再试")
      })
    },
    load() {
      this.request.get("/applcation/my").then(res => {
        this.tableData = res.data
      })
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
  }
}
</script>

<style>

</style>
