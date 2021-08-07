module.exports = {
  publicPath: "/",
  outputDir: "../../../custom-workspace/auth-server-demo/src/main/resources/dist",
  devServer: {
    proxy: {
      // '/login': {
      //   target: 'http://auth-server:9000',
      //   ws: true,
      //   changeOrigin: true
      // },
      "/getCsrfToken": {
        target: 'http://auth-server:9000',
        changeOrigin: true
      }
    }
  }
}