/**
 * 创建代理配置（用于 Vite 的 server.proxy）
 * 用于解析 .env 中的 VITE_PROXY 配置，例如：
 * VITE_PROXY='[["/api","http://localhost:8080"],["/admin","http://localhost:9000"]]'
 *
 * @param list 代理配置列表，格式为二维数组：
 *   [
 *     ["/api", "http://localhost:8080"],
 *     ["/admin", "http://localhost:9000"]
 *   ]
 *
 * @returns 返回符合 Vite server.proxy 要求的代理对象
 */
export function createProxy(list) {
  const ret = {};

  for (const [prefix, target] of list) {
    /**
     * prefix:   代理前缀，例如 "/api"
     * target:   代理目标地址，如 "http://localhost:8080"
     *
     * ret[prefix] 将生成如下结构：
     * {
     *   "/api": {
     *     target: "http://localhost:8080",
     *     changeOrigin: true,  // 修改请求头中的 origin
     *     ws: true,            // 是否代理 WebSocket
     *     rewrite: path => path.replace(/^\/api/, "")  // 去除前缀
     *   }
     * }
     */
    ret[prefix] = {
      // 目标地址
      target: target,
      // 开启跨域：代理时修改 origin
      changeOrigin: true,
      // 支持 WebSocket 代理
      ws: true,
      /**
       * rewrite: 重写路径，例如：
       * 输入:  /api/users
       * 输出:  /users
       */
      rewrite: path => path.replace(new RegExp(`^${prefix}`), "")
    };
  }

  return ret;
}
