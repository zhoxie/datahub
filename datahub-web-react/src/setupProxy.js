// eslint-disable-next-line @typescript-eslint/no-var-requires
const { createProxyMiddleware } = require('http-proxy-middleware');

// eslint-disable-next-line func-names
module.exports = function (app) {
    // ...You can now register proxies as you wish!
    app.use(
        createProxyMiddleware('/api', {
            target: 'http://localhost:9002/',
            changeOrigin: true,
        }),
    );
    app.use(
        createProxyMiddleware('/entities', {
            target: 'http://mtta2stm017.webex.com:8080/',
            changeOrigin: true,
        }),
    );
};
