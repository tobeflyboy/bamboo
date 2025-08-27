import axios from "axios";
import { showFullScreenLoading, tryHideFullScreenLoading } from "@/components/Loading/fullScreen";
import { LOGIN_URL } from "@/config";
import { ElMessage } from "element-plus";
import { checkStatus } from "./helper/checkStatus";
import { AxiosCanceler } from "./helper/axiosCancel";
import { useUserStore } from "@/stores/modules/user";
import router from "@/routers";

// 默认配置
const config = {
  // 基础请求地址，从环境变量获取
  baseURL: import.meta.env.VITE_API_URL,
  // 请求超时时间（毫秒）
  timeout: 30000,
  // 跨域请求时是否携带凭证
  withCredentials: true
};

// 创建重复请求取消器实例
const axiosCanceler = new AxiosCanceler();

class RequestHttp {
  // 构造函数，初始化axios实例
  constructor(config) {
    // 创建axios实例
    this.service = axios.create(config);

    // 请求拦截器：发送请求前的处理
    this.service.interceptors.request.use(
      config => {
        const userStore = useUserStore();
        // 配置是否取消重复请求（默认取消）
        config.cancel = config.cancel ? config.cancel : true;
        if (config.cancel) {
          axiosCanceler.addPending(config);
        }

        // 配置是否显示加载状态（默认显示）
        config.loading = config.loading ? config.loading : true;
        if (config.loading) {
          showFullScreenLoading();
        }

        // 添加认证令牌
        if (config.headers && typeof config.headers.set === "function") {
          config.headers.set("Authorization", userStore.token);
        }
        return config;
      },
      // 请求错误处理
      error => {
        return Promise.reject(error);
      }
    );

    // 响应拦截器：接收响应后的处理
    this.service.interceptors.response.use(
      response => {
        const { config } = response;
        // 移除已完成的请求（防止重复取消）
        axiosCanceler.removePending(config);
        // 隐藏加载状态
        config.loading && tryHideFullScreenLoading();

        // 关键修复：对于二进制文件类型，直接返回完整响应对象
        const responseType = config.responseType || "";
        if (["blob", "arraybuffer"].includes(responseType)) {
          return response; // 返回完整响应，包含headers和data
        }

        // 处理JSON类型响应
        const data = response.data;
        const userStore = useUserStore();

        // 处理登录失效（A0230为登录失效状态码）
        if (data.code === "A0230") {
          userStore.setToken(""); // 清空令牌
          router.replace(LOGIN_URL); // 跳转到登录页
          ElMessage.error(data.msg); // 显示错误信息
          return Promise.reject(data); // 拒绝该 promise
        }

        // 全局错误信息拦截，处理业务错误（非00000为错误状态码）
        if (data.code && "00000" !== data.code) {
          ElMessage.error(data.msg);
          return Promise.reject(data); // 拒绝该 promise
        }
        // 成功响应，返回数据
        return data;
      },
      // 响应错误处理
      async error => {
        // 隐藏加载状态
        tryHideFullScreenLoading();
        // 处理超时错误
        if (error.message.indexOf("timeout") !== -1) ElMessage.error("请求超时！请您稍后重试");
        // 处理网络错误
        if (error.message.indexOf("Network Error") !== -1) ElMessage.error("网络错误！请您稍后重试");

        // 处理服务器返回的错误状态码
        if (error.response) {
          // 处理二进制响应的错误（后端返回JSON错误信息）
          const { config, data, headers } = error.response;
          if (["blob", "arraybuffer"].includes(config.responseType)) {
            // 尝试将错误的blob转换为JSON
            const text = await new Response(data).text();
            try {
              const jsonData = JSON.parse(text);
              ElMessage.error(jsonData.msg || "下载失败");
              return Promise.reject(jsonData);
            } catch (e) {
              ElMessage.error("下载文件格式错误");
              return Promise.reject(e);
            }
          }
          checkStatus(error.response.status);
        }
        // 处理断网情况
        if (!window.navigator.onLine) {
          // 跳转到错误页
          router.replace("/500");
        }
        return Promise.reject(error);
      }
    );
  }

  // GET请求方法
  get(url, params, _object = {}) {
    return this.service.get(url, { params, ..._object });
  }

  // POST请求方法
  post(url, params, _object = {}) {
    return this.service.post(url, params, _object);
  }

  // PUT请求方法
  put(url, params, _object = {}) {
    return this.service.put(url, params, _object);
  }

  // DELETE请求方法
  delete(url, params, _object = {}) {
    return this.service.delete(url, { params, ..._object });
  }

  // 下载请求方法（基础版）
  download(url, params, _object = {}) {
    return this.service.post(url, params, { ..._object, responseType: "blob" });
  }

  /**
   * 新增：文件下载通用方法（增强版）
   * 封装了从请求到文件保存的完整流程，支持自定义配置
   * @param {string} url - 请求地址
   * @param {object} params - 请求参数（与get/post方法参数一致）
   * @param {object} options - 额外配置选项
   * @param {string} options.defaultFileName - 默认文件名（当响应头没有指定时使用）
   * @param {string} options.method - 请求方法，默认'post'，可选'get'
   * @param {boolean} options.loading - 是否显示全屏加载状态，默认true
   * @returns {Promise} - 返回包含下载结果的Promise对象
   */
  async downloadFile(url, params, options = {}) {
    // 解析配置选项，设置默认值
    const {
      defaultFileName, // 默认文件名
      method = "post", // 默认请求方法为POST
      loading = true // 默认显示加载状态
    } = options;

    // 显示全屏加载状态（如果配置了需要显示）
    if (loading) {
      showFullScreenLoading();
    }

    try {
      // 根据请求方法选择对应的请求方式
      let result;
      if (method.toLowerCase() === "get") {
        // GET请求：参数通过params传递
        result = await this.get(url, params, {
          ...options,
          responseType: "blob", // 强制响应类型为blob（二进制）
          loading // 传递加载状态配置
        });
      } else {
        // POST请求：参数通过请求体传递
        result = await this.post(url, params, {
          ...options,
          responseType: "blob", // 强制响应类型为blob（二进制）
          loading // 传递加载状态配置
        });
      }

      // 处理文件名：优先使用默认文件名，格式如"下载文件_2023-10-01.xlsx"
      let fileName = defaultFileName || `下载文件_${new Date().toLocaleDateString().replace(/\//g, "-")}.xlsx`;

      // 尝试从响应头中获取文件名（Content-Disposition可能有大小写差异）
      let contentDisposition = result.headers?.["content-disposition"] || result.headers?.["Content-Disposition"];

      if (contentDisposition) {
        // 正则匹配文件名（支持带引号和不带引号的格式）
        const match = contentDisposition.match(/filename="?([^"]+)"?/);
        if (match && match[1]) {
          // 解码文件名（处理URL编码的情况）
          fileName = decodeURIComponent(match[1]);
        }
      }

      // 验证下载数据的有效性
      if (!result.data || (result.data.size && result.data.size === 0)) {
        throw new Error("下载的数据为空");
      }

      // 创建Blob对象（二进制数据容器）
      // 优先使用响应头中的Content-Type，默认使用Excel类型
      const blob = new Blob([result.data], {
        type: result.headers?.["content-type"] || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
      });

      // 创建下载链接并触发下载
      const urlObj = window.URL.createObjectURL(blob); // 创建指向blob的URL
      const link = document.createElement("a"); // 创建a标签
      link.href = urlObj; // 设置下载链接
      link.download = fileName; // 设置文件名
      document.body.appendChild(link); // 将a标签添加到页面
      link.click(); // 触发点击下载

      // 延迟清理资源，确保下载完成
      setTimeout(() => {
        document.body.removeChild(link); // 移除a标签
        window.URL.revokeObjectURL(urlObj); // 释放blob URL
      }, 100);

      // 显示下载成功提示
      ElMessage.success("导出成功");
      return { success: true, fileName }; // 返回成功结果
    } catch (error) {
      // 捕获并处理所有可能的错误
      console.error("文件下载失败:", error);
      ElMessage.error(`导出失败: ${error.message || "未知错误"}`);
      return { success: false, error }; // 返回失败结果
    } finally {
      // 无论成功失败，最终都要隐藏加载状态
      if (loading) {
        // 延迟隐藏，避免界面闪烁
        setTimeout(() => tryHideFullScreenLoading(), 100);
      }
    }
  }
}

// 导出实例化的请求对象
export default new RequestHttp(config);
