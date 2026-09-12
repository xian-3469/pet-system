<template>
  <div style="min-height: calc(100vh - 70px)">

    <div style="margin: 10px 0">
      <div class="rescueFind"> 救助站</div>
      <!-- 救助站模块-->
      <rescue></rescue>
    </div>

    <div style="margin: 10px 0">
      <div class="feed"> 宠物喂养点</div>
      <!-- 宠物喂养点模块-->
      <feed></feed>
    </div>
  </div>
</template>

<script>
import rescue from '@/components/front/Rescue.vue'
import feed from '@/components/front/Feed.vue'
export default {
  name: "Salvation",
  components: {
    rescue,
    feed
  },
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  }
}
</script>


<style>
.headerBg {
  background: #eee !important;
}

.rescueFind {
  padding: 20px 10px;
  font-size: 28px;
  background-color: #fff;
  border-radius: 10px;
  color: rgb(30, 144, 255);
}

.feed {
  padding: 20px 10px;
  font-size: 28px;
  background-color: #fff;
  border-radius: 10px;
  color: green;
}
</style>
