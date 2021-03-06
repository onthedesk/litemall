const path = require('path');

function resolve(dir = '') {
  return path.join(__dirname, './src', dir);
}

module.exports = {
  publicPath: './',
  outputDir: 'dist',
  assetsDir: 'static',
  productionSourceMap: false,
  devServer: {
    //九键输入法的 「mall」= 「6255」
    port: 6255,
    proxy: {
      "/": {
        target: 'http://localhost:8080/',
        changeOrigin: true,
        onProxyRes: function (proxyRes, req, res) {
          proxyRes.headers['Access-Control-Allow-Origin'] = '*'
          proxyRes.headers['Access-Control-Allow-Headers'] = 'Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, sessionid, cross-request-open-sign'
          proxyRes.headers['Access-Control-Allow-Methods'] = 'GET,HEAD,OPTIONS,POST,PUT'
        }
      }
    }
  },
  chainWebpack: config => {
    config.plugins.delete('prefetch');
    config.plugins.delete('preload');
  },
  configureWebpack: {
    resolve: {
      alias: {
        core: resolve('core')
      }
    },
    optimization: {
      runtimeChunk: {
        name: entrypoint => `runtime~${entrypoint.name}`
      },
      splitChunks: {
        minChunks: 2,
        minSize: 20000,
        maxAsyncRequests: 20,
        maxInitialRequests: 30,
        name: false
      }
    }
  },
  css: {
    loaderOptions: {
      sass: {
        data:
          '@import "@/assets/scss/_var.scss";@import "@/assets/scss/_mixin.scss";'
      }
    }
  }
};
