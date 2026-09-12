<template>
  <div style="padding-bottom: 20px; min-height: calc(100vh - 60px)">
    <!-- 选择宠物区域 -->
    <div style="padding: 25px; background: linear-gradient(135deg, #FFFFFF 0%, #FFF9F5 100%); border-radius: 16px; margin: 15px 0; box-shadow: 0 2px 12px rgba(255, 154, 86, 0.1);">
      <div style="display: flex; align-items: center; gap: 15px;">
        <el-select v-model="selectedPetId" placeholder="请选择宠物" size="large" style="width: 260px" @change="onPetChange" prefix-icon="el-icon-paw">
          <el-option v-for="pet in myPets" :key="pet.id" :label="pet.petName + ' (' + pet.breed + ')'" :value="pet.id">
            <span style="float: left">{{ pet.petName }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">{{ pet.breed }}</span>
          </el-option>
        </el-select>
        <el-button class="ml-5" type="primary" icon="el-icon-data-line" @click="showDashboard">健康看板</el-button>
        <el-button type="success" icon="el-icon-plus" @click="handleAdd">新增记录</el-button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <div v-if="selectedPetId" style="padding: 20px; background: linear-gradient(135deg, #62C88A 0%, #4AB877 100%); border-radius: 16px; margin: 15px 0;">
      <el-select clearable v-model="recordType" placeholder="筛选类型" size="large" style="width: 200px; background: white; border-radius: 8px;" @change="loadRecords">
        <el-option label="疫苗" value="疫苗"></el-option>
        <el-option label="驱虫" value="驱虫"></el-option>
        <el-option label="体检" value="体检"></el-option>
        <el-option label="治疗" value="治疗"></el-option>
      </el-select>
    </div>

    <!-- 健康记录列表 -->
    <el-card v-if="selectedPetId" style="margin: 15px 0;" class="pet-card">
      <el-table :data="tableData" border stripe :header-cell-class-name="'headerBg'">
        <el-table-column prop="recordType" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.recordType)" style="border-radius: 12px;">{{ scope.row.recordType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="itemName" label="项目名称">
          <template slot-scope="scope">
            <span style="font-weight: 500; color: var(--pet-primary-dark);">{{ scope.row.itemName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="recordDate" label="记录时间" width="120"></el-table-column>
        <el-table-column prop="hospital" label="医院/机构"></el-table-column>
        <el-table-column prop="doctor" label="医生" width="100"></el-table-column>
        <el-table-column prop="weight" label="体重(kg)" width="100"></el-table-column>
        <el-table-column prop="nextDate" label="下次时间" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.nextDate" style="color: var(--pet-secondary); font-weight: 500;">{{ scope.row.nextDate }}</span>
            <span v-else style="color: var(--pet-text-light);">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button class="edit-btn" size="mini" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm
                class="ml-5"
                confirm-button-text="确定"
                cancel-button-text="取消"
                title="确定删除该记录吗？"
                @confirm="del(scope.row.id)">
              <el-button class="delete-btn" slot="reference" size="mini" icon="el-icon-delete">删除</el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

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

    <el-empty v-else description="请先选择宠物"></el-empty>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="form.id ? '编辑健康记录' : '新增健康记录'" :visible.sync="dialogFormVisible" width="500px" :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="宠物">
          <el-select v-model="form.petId" placeholder="请选择宠物" style="width: 100%">
            <el-option v-for="pet in myPets" :key="pet.id" :label="pet.petName" :value="pet.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="记录类型">
          <el-select v-model="form.recordType" placeholder="请选择类型" style="width: 100%">
            <el-option label="疫苗" value="疫苗"></el-option>
            <el-option label="驱虫" value="驱虫"></el-option>
            <el-option label="体检" value="体检"></el-option>
            <el-option label="治疗" value="治疗"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="form.itemName" placeholder="如：狂犬疫苗、妙三多等"></el-input>
        </el-form-item>
        <el-form-item label="记录时间">
          <el-date-picker v-model="form.recordDate" type="date" placeholder="选择日期" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :precision="2" :min="0" style="width: 100%"></el-input-number>
        </el-form-item>
        <el-form-item label="医院/机构">
          <el-input v-model="form.hospital" placeholder="请输入医院或机构名称"></el-input>
        </el-form-item>
        <el-form-item label="医生">
          <el-input v-model="form.doctor" placeholder="请输入医生姓名"></el-input>
        </el-form-item>
        <el-form-item label="下次提醒时间">
          <el-date-picker v-model="form.nextDate" type="date" placeholder="选择日期（可选）" style="width: 100%" value-format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注信息"></el-input>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload :action="$store.state.baseApi + '/file/upload'" :on-success="handleAttachmentSuccess" :show-file-list="false" multiple>
            <el-button size="small" type="primary">上传附件</el-button>
          </el-upload>
          <div v-if="form.attachment" style="margin-top: 8px">
            <el-tag v-for="(url, index) in form.attachment.split(',')" :key="index" style="margin-right: 5px">
              <a :href="$store.state.baseApi + url" target="_blank" style="color: inherit">附件{{ index + 1 }}</a>
            </el-tag>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </el-dialog>

    <!-- 健康看板弹窗 -->
    <el-dialog title="健康数据看板" :visible.sync="dashboardVisible" width="1100px" :close-on-click-modal="false">
      <div v-if="selectedPetId" style="padding: 0 5px;">
        <div style="margin-bottom: 20px; display: flex; gap: 15px; align-items: center;">
          <el-select v-model="dashboardPetId" placeholder="选择宠物" style="width: 200px" @change="loadDashboardData">
            <el-option v-for="pet in myPets" :key="pet.id" :label="pet.petName" :value="pet.id"></el-option>
          </el-select>
          <el-tag type="info">最后更新：{{ lastUpdateTime }}</el-tag>
        </div>

        <!-- 四个模块：2x2 网格 -->
        <div style="display: flex; gap: 20px; flex-wrap: wrap;">

          <!-- 模块一：体征监测（左上） -->
          <div style="width: calc(50% - 10px); background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.06);">
            <div style="font-size: 16px; font-weight: bold; margin-bottom: 16px; color: #333;">体征监测</div>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
              <div style="text-align: center; padding: 16px; background: #fff7f0; border-radius: 8px;">
                <div style="font-size: 24px; font-weight: bold; color: #ff7d00;">{{ dashSummary.totalRecords || 0 }}</div>
                <div style="font-size: 12px; color: #666; margin-top: 4px;">健康记录</div>
              </div>
              <div style="text-align: center; padding: 16px; background: #f0f7ff; border-radius: 8px;">
                <div style="font-size: 24px; font-weight: bold; color: #409EFF;">{{ dashSummary.vaccineCount || 0 }}</div>
                <div style="font-size: 12px; color: #666; margin-top: 4px;">疫苗接种</div>
              </div>
              <div style="text-align: center; padding: 16px; background: #f0fff0; border-radius: 8px;">
                <div style="font-size: 24px; font-weight: bold; color: #62C88A;">{{ dashSummary.dewormCount || 0 }}</div>
                <div style="font-size: 12px; color: #666; margin-top: 4px;">驱虫记录</div>
              </div>
              <div style="text-align: center; padding: 16px; background: #fff5f5; border-radius: 8px;">
                <div style="font-size: 24px; font-weight: bold; color: #F56C6C;">{{ dashSummary.overdueCount || 0 }}</div>
                <div style="font-size: 12px; color: #666; margin-top: 4px;">已过期</div>
              </div>
            </div>
            <div v-if="dashSummary.dueSoonCount" style="margin-top: 14px; padding: 10px 12px; background: #fff7f0; border-radius: 6px; border-left: 3px solid #ff7d00; font-size: 13px; color: #666;">
              <i class="el-icon-bell" style="color: #ff7d00; margin-right: 6px;"></i>
              还有 <span style="color: #ff7d00; font-weight: bold;">{{ dashSummary.dueSoonCount }}</span> 项记录将在3天内到期
            </div>
          </div>

          <!-- 模块二：疫苗接种状态（右上） -->
          <div style="width: calc(50% - 10px); background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.06);">
            <div style="font-size: 16px; font-weight: bold; margin-bottom: 16px; color: #333;">疫苗接种状态</div>
            <div style="margin-bottom: 16px;">
              <div style="display: flex; justify-content: space-between; font-size: 13px; color: #666; margin-bottom: 6px;">
                <span>接种完成率</span>
                <span style="color: #ff7d00; font-weight: bold;">{{ vaccineProgress }}%</span>
              </div>
              <el-progress :percentage="vaccineProgress" :stroke-width="10" :show-text="false" :stroke-color="'#ff7d00'"></el-progress>
            </div>
            <div style="display: flex; gap: 12px; font-size: 13px; color: #666; margin-bottom: 16px;">
              <span><i class="el-icon-circle-check" style="color: #62C88A; margin-right: 4px;"></i>已接种 {{ vaccineDone }} 种</span>
              <span><i class="el-icon-remove" style="color: #ccc; margin-right: 4px;"></i>未接种 {{ vaccineTotal - vaccineDone }} 种</span>
            </div>
            <div style="margin-bottom: 12px;">
              <el-tag size="mini" type="success" v-for="item in vaccineDoneList" :key="item" style="margin: 3px 3px 3px 0;">
                <i class="el-icon-circle-check" style="margin-right: 3px;"></i>{{ item }}
              </el-tag>
              <el-tag size="mini" v-for="item in vaccineTodoList" :key="item" style="margin: 3px 3px 3px 0; background: #f5f5f5; border-color: #ddd; color: #999;">
                <i class="el-icon-remove" style="margin-right: 3px;"></i>{{ item }}
              </el-tag>
            </div>
            <div v-if="vaccineNextDate" style="font-size: 13px; color: #666; margin-top: 8px; padding: 8px 12px; background: #fff7f0; border-radius: 6px; border-left: 3px solid #ff7d00;">
              <span style="color: #ff7d00; font-weight: bold;">下次接种：</span>
              <span>{{ vaccineNextDate }} {{ vaccineNextName }}</span>
            </div>
            <div v-if="!vaccineHasData && vaccineTotal === 0" style="text-align: center; color: #999; padding: 20px 0;">
              <i class="el-icon-medicine-box" style="font-size: 24px; color: #ddd; display: block; margin-bottom: 6px;"></i>
              <span>暂无疫苗接种记录</span>
            </div>
          </div>

          <!-- 模块三：体重变化趋势（左下） -->
          <div style="width: calc(50% - 10px); background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.06);">
            <div style="font-size: 16px; font-weight: bold; margin-bottom: 16px; color: #333;">体重变化趋势</div>
            <div v-if="weightChartData.length > 0" ref="weightChartRef" style="width: 100%; height: 260px;"></div>
            <div v-else style="text-align: center; color: #999; padding: 60px 0;">
              <i class="el-icon-data-analysis" style="font-size: 40px; color: #ddd; display: block; margin-bottom: 10px;"></i>
              <span>暂无体重记录</span>
            </div>
          </div>

          <!-- 模块四：健康小贴士（右下） -->
          <div style="width: calc(50% - 10px); background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.06);">
            <div style="font-size: 16px; font-weight: bold; margin-bottom: 16px; color: #333;">健康小贴士</div>
            <div style="display: flex; flex-direction: column; gap: 12px;">
              <div v-for="(tip, index) in healthTips" :key="index"
                style="display: flex; align-items: flex-start; gap: 10px; padding: 10px; background: #f9f9f9; border-radius: 6px;">
                <div style="width: 22px; height: 22px; background: #ff7d00; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; font-weight: bold; flex-shrink: 0;">{{ index + 1 }}</div>
                <div>
                  <div style="font-size: 13px; color: #333; font-weight: 500;">{{ tip.title }}</div>
                  <div style="font-size: 12px; color: #999; margin-top: 3px; line-height: 1.5;">{{ tip.desc }}</div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: "HealthRecord",
  data() {
    return {
      myPets: [],
      selectedPetId: null,
      dashboardPetId: null,
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      recordType: "",
      dialogFormVisible: false,
      dashboardVisible: false,
      form: {},
      dashSummary: { totalRecords: 0, vaccineCount: 0, dewormCount: 0, overdueCount: 0, dueSoonCount: 0 },
      lastUpdateTime: '-',

      // 疫苗模块变量
      vaccineHasData: false,
      vaccineProgress: 80,
      vaccineTotal: 5,
      vaccineDone: 4,
      vaccineDoneList: ['妙三多', '猫三联', '狂犬疫苗', '弓形虫'],
      vaccineTodoList: ['钩端螺旋体'],
      vaccineNextDate: '2025-07-15',
      vaccineNextName: '狂犬疫苗',
      // 体重变化趋势
      weightChartData: [],
      weightChart: null,
      // 健康小贴士
      healthTips: [
        { title: '定期疫苗接种', desc: '幼年宠物需在6-8周开始接种疫苗，成年后每年加强一次，有效预防传染病。' },
        { title: '合理饮食控制', desc: '根据宠物年龄、体重、活动量选择狗粮/猫粮，避免随意喂食人类食物，保持营养均衡。' },
        { title: '定期体内外驱虫', desc: '每月一次体外驱虫，每3个月一次体内驱虫，外出后注意检查有无蜱虫、跳蚤。' },
        { title: '保持清洁卫生', desc: '定期洗澡、梳毛、清洁耳道、修剪指甲，窝垫常晒洗，预防皮肤病和寄生虫。' }
      ]
    }
  },
  created() {
    this.loadMyPets()
  },
  methods: {
    loadMyPets() {
      this.request.get("/pet-profile/my/page", {
        params: { pageNum: 1, pageSize: 100, petName: "" }
      }).then(res => {
        this.myPets = res.data.records || []
        if (this.myPets.length > 0) {
          this.selectedPetId = this.myPets[0].id
          this.loadRecords()
        }
      })
    },
    onPetChange() {
      this.pageNum = 1
      this.loadRecords()
    },
    loadRecords() {
      if (!this.selectedPetId) return
      this.request.get("/health-record/my/page", {
        params: {
          petId: this.selectedPetId,
          recordType: this.recordType,
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
      }).then(res => {
        this.tableData = res.data.records || []
        this.total = res.data.total || 0
      })
    },
    handleAdd() {
      if (!this.selectedPetId) {
        this.$message.warning("请先选择宠物")
        return
      }
      this.form = { petId: this.selectedPetId, recordType: "疫苗", weight: null }
      this.dialogFormVisible = true
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      // 确保日期格式正确显示
      if (this.form.recordDate && !(this.form.recordDate instanceof Date)) {
        this.form.recordDate = new Date(this.form.recordDate)
      }
      if (this.form.nextDate && !(this.form.nextDate instanceof Date)) {
        this.form.nextDate = new Date(this.form.nextDate)
      }
      this.dialogFormVisible = true
    },
    save() {
      if (!this.form.petId) {
        this.$message.warning("请选择宠物")
        return
      }
      if (!this.form.recordType) {
        this.$message.warning("请选择记录类型")
        return
      }
      if (!this.form.itemName) {
        this.$message.warning("请填写项目名称")
        return
      }
      if (!this.form.recordDate) {
        this.$message.warning("请选择记录时间")
        return
      }
      if (this.form.id) {
        this.request.put("/health-record/" + this.form.id, this.form).then(res => {
          if (res.code === '200') {
            this.$message.success("更新成功")
            this.dialogFormVisible = false
            this.loadRecords()
          } else {
            this.$message.error(res.msg || "更新失败")
          }
        })
      } else {
        this.request.post("/health-record", this.form).then(res => {
          if (res.code === '200') {
            this.$message.success("新增成功")
            this.dialogFormVisible = false
            this.loadRecords()
          } else {
            this.$message.error(res.msg || "新增失败")
          }
        })
      }
    },
    del(id) {
      this.request.delete("/health-record/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.loadRecords()
        } else {
          this.$message.error(res.msg || "删除失败")
        }
      })
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.loadRecords()
    },
    handleCurrentChange(num) {
      this.pageNum = num
      this.loadRecords()
    },
    handleAttachmentSuccess(res) {
      if (!this.form.attachment) {
        this.form.attachment = res
      } else {
        this.form.attachment += "," + res
      }
    },
    getTypeColor(type) {
      const colors = { "疫苗": "success", "驱虫": "warning", "体检": "primary", "治疗": "danger" }
      return colors[type] || "info"
    },
    showDashboard() {
      if (!this.selectedPetId) {
        this.$message.warning("请先选择宠物")
        return
      }
      this.dashboardPetId = this.selectedPetId
      this.dashboardVisible = true
      this.loadDashboardData()
    },
    loadDashboardData() {
      if (!this.dashboardPetId) return
      this.vaccineHasData = false
      this.vaccineProgress = 80
      this.vaccineTotal = 5
      this.vaccineDone = 4
      this.vaccineDoneList = ['妙三多', '猫三联', '狂犬疫苗', '弓形虫']
      this.vaccineTodoList = ['钩端螺旋体']
      this.vaccineNextDate = '2025-07-15'
      this.vaccineNextName = '狂犬疫苗'
      this.weightChartData = []
      this.loadSummary()
      this.loadVaccineData()
      this.loadWeightData()
    },
    loadSummary() {
      this.request.get("/health-record/dashboard/summary", {
        params: { petId: this.dashboardPetId }
      }).then(res => {
        this.dashSummary = res.data || {}
        this.lastUpdateTime = new Date().toLocaleString('zh-CN')
      }).catch(() => {})
    },
    loadWeightData() {
      this.request.get("/health-record/dashboard/weight", {
        params: { petId: this.dashboardPetId }
      }).then(res => {
        const data = res.data || []
        this.weightChartData = data
        this.$nextTick(() => {
          this.initWeightChart()
        })
      }).catch(() => {})
    },
    initWeightChart() {
      if (!this.$refs.weightChartRef) return
      if (this.weightChart) {
        this.weightChart.dispose()
      }
      this.weightChart = echarts.init(this.$refs.weightChartRef)

      const dates = this.weightChartData.map(d => {
        const date = new Date(d.date)
        const month = date.getMonth() + 1
        const day = date.getDate()
        return `${month}月${day}日`
      })
      const weights = this.weightChartData.map(d => d.weight)
      const items = this.weightChartData.map(d => d.itemName || '')
      const maxWeight = Math.max(...weights)
      const yMax = Math.ceil(maxWeight * 1.15 * 10) / 10

      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255,255,255,0.95)',
          borderColor: '#eee',
          borderWidth: 1,
          textStyle: { color: '#333', fontSize: 12 },
          formatter: (params) => {
            if (!params || !params.length) return ''
            const p = params[0]
            const idx = p.dataIndex
            const itemName = items[idx] || ''
            return `<div style="font-weight:bold;color:#ff7d00;">${p.name}</div>` +
                   `<div style="margin-top:4px;">体重：<span style="color:#ff7d00;font-weight:bold;">${p.value} kg</span></div>` +
                   (itemName ? `<div style="color:#999;margin-top:2px;">${itemName}</div>` : '')
          }
        },
        grid: {
          top: 45,
          right: 20,
          bottom: 30,
          left: 50,
          containLabel: false
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLine: { lineStyle: { color: '#eee' } },
          axisLabel: { color: '#999', fontSize: 11 }
        },
        yAxis: {
          type: 'value',
          name: 'kg',
          nameTextStyle: { color: '#999', fontSize: 11 },
          max: yMax,
          axisLine: { show: false },
          axisTick: { show: false },
          splitLine: { lineStyle: { color: '#f5f5f5', type: 'dashed' } },
          axisLabel: { color: '#999', fontSize: 11 }
        },
        series: [{
          type: 'line',
          data: weights,
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { color: '#ff7d00', width: 3 },
          itemStyle: { color: '#ff7d00', borderColor: '#fff', borderWidth: 2 },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(255,125,0,0.25)' },
                { offset: 1, color: 'rgba(255,125,0,0.02)' }
              ]
            }
          },
          label: {
            show: true,
            position: 'top',
            distance: 6,
            color: '#ff7d00',
            fontSize: 11,
            fontWeight: 'bold',
            formatter: '{c}'
          }
        }]
      }

      this.weightChart.setOption(option)
    },
    loadVaccineData() {
      this.request.get("/health-record/my/page", {
        params: { petId: this.dashboardPetId, recordType: '疫苗', pageNum: 1, pageSize: 100 }
      }).then(res => {
        const records = res.data?.records || []
        if (records.length > 0) {
          this.vaccineHasData = true
          this.vaccineDone = records.length
          this.vaccineTotal = Math.max(records.length, 5)
          this.vaccineProgress = Math.round(this.vaccineDone / this.vaccineTotal * 100)
          this.vaccineDoneList = records.map(rec => rec.itemName)
          const upcoming = records
            .filter(rec => rec.nextDate && new Date(rec.nextDate) >= new Date())
            .sort((a, b) => new Date(a.nextDate) - new Date(b.nextDate))
          if (upcoming.length > 0) {
            this.vaccineNextDate = upcoming[0].nextDate
            this.vaccineNextName = upcoming[0].itemName
          }
        }
      }).catch(() => {})
    }
  }
}
</script>

<style>
.headerBg {
  background: #eee !important;
}
</style>
