import { resolve } from "path";
import { VitePWA } from "vite-plugin-pwa";
import { createHtmlPlugin } from "vite-plugin-html";
import { visualizer } from "rollup-plugin-visualizer";
import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";
import eslintPlugin from "vite-plugin-eslint";
import viteCompression from "vite-plugin-compression";
import vueSetupExtend from "unplugin-vue-setup-extend-plus/vite";
import NextDevTools from "vite-plugin-vue-devtools";

/**
 * 创建 Vite 插件集合
 * @param viteEnv 环境变量对象（已由 wrapperEnv 处理）
 * @returns Array<VitePlugin> 返回插件数组
 */
export const createVitePlugins = viteEnv => {
  const { VITE_GLOB_APP_TITLE, VITE_REPORT, VITE_DEVTOOLS, VITE_PWA } = viteEnv;
  return [
    /**
     * Vue 单文件组件支持
     */
    vue(),

    /**
     * Vue JSX/TSX 支持
     */
    vueJsx(),
    // VITE_DEVTOOLS && NextDevTools({ launchEditor: "code" }),

    /**
     * ESLint 插件：构建或运行时自动进行 ESLint 检查
     */
    eslintPlugin(),

    /**
     * 允许在 <script setup> 中直接使用 name
     */
    vueSetupExtend({}),

    /**
     * 注入 HTML 内容（如标题、变量）
     * 用于自动修改 index.html 中的 <title>
     */
    createHtmlPlugin({
      inject: {
        data: { title: VITE_GLOB_APP_TITLE }
      }
    }),

    /**
     * 自动生成 SVG Icon，支持 svg-sprite
     * 使用示例：<svg><use href="#icon-folder-name"/></svg>
     */
    createSvgIconsPlugin({
      // svg 文件目录
      iconDirs: [resolve(process.cwd(), "src/assets/icons")],
      // symbol id 格式
      symbolId: "icon-[dir]-[name]"
    }),

    /**
     * PWA（渐进式应用）支持
     * 通过 VITE_PWA 控制是否启用
     */
    VITE_PWA && createVitePwa(viteEnv),

    /**
     * 打包分析工具（生成 stats.html）
     * 通过 VITE_REPORT 控制是否启用
     */
    VITE_REPORT && visualizer({ filename: "stats.html" })
  ];
};

/**
 * 根据环境变量生成打包压缩插件（gzip / brotli）
 * @param viteEnv 环境变量对象
 */
const createCompression = viteEnv => {
  const { VITE_BUILD_COMPRESS = "none", VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE } = viteEnv;
  // 支持多种格式，例如：gzip,brotli
  const compressList = VITE_BUILD_COMPRESS.split(",");
  const plugins = [];

  /**
   * 开启 gzip 压缩
   */
  if (compressList.includes("gzip")) {
    plugins.push(
      viteCompression({
        ext: ".gz",
        algorithm: "gzip",
        deleteOriginFile: VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE
      })
    );
  }

  /**
   * 开启 brotli 压缩
   */
  if (compressList.includes("brotli")) {
    plugins.push(
      viteCompression({
        ext: ".br",
        algorithm: "brotliCompress",
        deleteOriginFile: VITE_BUILD_COMPRESS_DELETE_ORIGIN_FILE
      })
    );
  }
  return plugins;
};

/**
 * 创建 PWA 插件配置
 * @param viteEnv 环境变量
 */
const createVitePwa = viteEnv => {
  const { VITE_GLOB_APP_TITLE } = viteEnv;
  return VitePWA({
    /**
     * PWA 缓存策略：自动更新 service worker
     */
    registerType: "autoUpdate",

    /**
     * PWA Manifest 配置
     */
    manifest: {
      // 应用名称
      name: VITE_GLOB_APP_TITLE,
      // 应用短名称
      short_name: VITE_GLOB_APP_TITLE,
      // 主题颜色
      theme_color: "#ffffff",
      icons: [
        {
          src: "/logo.png",
          sizes: "192x192",
          type: "image/png"
        },
        {
          src: "/logo.png",
          sizes: "512x512",
          type: "image/png"
        },
        {
          src: "/logo.png",
          sizes: "512x512",
          type: "image/png",
          // 支持圆角遮罩图标
          purpose: "any maskable"
        }
      ]
    }
  });
};
