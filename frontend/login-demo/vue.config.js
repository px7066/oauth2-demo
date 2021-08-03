module.exports = {
  publicPath: "/",
  outputDir: "../../backend/auth-server/src/main/resources/dist",
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