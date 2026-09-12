import Vue from 'vue'
import Vuex from 'vuex'
import router, {resetRouter} from "@/router";

Vue.use(Vuex)

const store = new Vuex.Store({
    state: {
        currentPathName: '',
        // 动态取当前访问的主机名，保证从其他机器访问时图片/上传等地址仍然可用
        baseApi: 'http://' + window.location.hostname + ':9311'
    },
    mutations: {
        setPath (state) {
            state.currentPathName = localStorage.getItem("currentPathName")
        },
        logout() {
            // 清空缓存
            localStorage.removeItem("user")
            localStorage.removeItem("menus")
            router.push("/login")
            // 重置路由
            resetRouter()
        }
    }
})

export default store
