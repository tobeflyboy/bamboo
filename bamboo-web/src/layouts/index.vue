<!-- 💥 这里是一次性加载 LayoutComponents -->
<template>
  <el-watermark id="watermark" :font="font" :content="watermark ? watermarkContent : ''">
    <component :is="LayoutComponents[layout]" />
    <ThemeDrawer />
  </el-watermark>
</template>

<script setup name="layout">
import { computed, reactive, watch } from "vue";
import ThemeDrawer from "./components/ThemeDrawer/index.vue";
import LayoutVertical from "./LayoutVertical/index.vue";
import LayoutClassic from "./LayoutClassic/index.vue";
import LayoutTransverse from "./LayoutTransverse/index.vue";
import LayoutColumns from "./LayoutColumns/index.vue";
import { useGlobalStore } from "@/stores/modules/global.js";
import { useUserStore } from "@/stores/modules/user";

const LayoutComponents = {
  vertical: LayoutVertical,
  classic: LayoutClassic,
  transverse: LayoutTransverse,
  columns: LayoutColumns
};

const globalStore = useGlobalStore();
const userStore = useUserStore();
const watermarkContent = ["Bamboo System", userStore.onlineUser.realName, userStore.onlineUser.username];
const isDark = computed(() => globalStore.isDark);
const layout = computed(() => globalStore.layout);
const watermark = computed(() => globalStore.watermark);

const font = reactive({ color: "rgba(0, 0, 0, .15)" });
watch(isDark, () => (font.color = isDark.value ? "rgba(255, 255, 255, .15)" : "rgba(0, 0, 0, .15)"), {
  immediate: true
});
</script>

<style scoped lang="scss">
.layout {
  min-width: 600px;
}
</style>
