import path from "path";

/**
 * 判断当前模式是否为开发环境
 * @param mode 当前模式字符串（例如：dev / pro / test）
 */
export function isDevFn(mode) {
  return mode === "dev";
}

/**
 * 判断当前模式是否为生产环境
 * @param mode 当前模式字符串
 */
export function isProdFn(mode) {
  return mode === "pro";
}

/**
 * 判断当前模式是否为测试环境
 * @param mode 当前模式字符串
 */
export function isTestFn(mode) {
  return mode === "test";
}

/**
 * 是否开启打包报告模式
 * 例如：VITE_REPORT=true 时启用
 */
export function isReportMode() {
  return process.env.VITE_REPORT === "true";
}

/**
 * 解析 .env 文件内容，将环境变量统一转换为正确的类型
 * @param envConf Vite 传入的环境变量对象（字符串形式）
 * @returns 处理后的环境配置对象
 */
export function wrapperEnv(envConf) {
  const ret = {};

  for (const envName of Object.keys(envConf)) {
    // 替换换行符 "\n"
    let realName = envConf[envName].replace(/\\n/g, "\n");

    // 将 "true"/"false" 字符串转换为布尔值
    realName = realName === "true" ? true : realName === "false" ? false : realName;

    // 若为端口号，则强制转换为数字类型
    if (envName === "VITE_PORT") {
      realName = Number(realName);
    }

    /**
     * 处理代理配置 VITE_PROXY
     * VITE_PROXY 应为一个 JSON 字符串数组，如：
     * VITE_PROXY='[["/api","http://localhost:3000"]]'
     */
    if (envName === "VITE_PROXY") {
      try {
        realName = JSON.parse(realName);

        // 如果不是数组，则报错
        if (!Array.isArray(realName)) {
          throw new Error("VITE_PROXY is not an array");
        }
      } catch (error) {
        console.error(`Failed to parse VITE_PROXY: ${error.message}. Falling back to an empty array.`);
        realName = []; // 解析失败时使用空数组作为默认值
      }
    }

    // 将处理后的变量写入返回对象
    ret[envName] = realName;
  }

  return ret;
}

/**
 * 获取项目根目录下的绝对路径
 * @param dir 路径片段
 * @returns 拼接后的绝对路径
 */
export function getRootPath(...dir) {
  return path.resolve(process.cwd(), ...dir);
}
