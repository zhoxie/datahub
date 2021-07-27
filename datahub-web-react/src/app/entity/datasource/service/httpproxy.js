import HttpProxyAgent from 'http-proxy-agent';

export const proxy = () => new HttpProxyAgent({ host: 'localhost', port: '3002' });


