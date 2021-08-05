module.exports = {
  publicPath: "./",
  outputDir: "../../backend/auth-client/src/main/resources/static",
  assetsDir: "./",
  devServer: {
    proxy: {
      // '/login': {
      //   target: 'http://auth-server:9000',
      //   ws: true,
      //   changeOrigin: true
      // },
      "/messages": {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      }
    }
  }
}