const common = require('./webpack.common');
const {
	createOnProxyReq,
	createOnProxyRes,
	onAIHubProxyRes,
} = require('./webpack.dev.proxy');
const {merge} = require('webpack-merge');
const webpack = require('webpack');

require('dotenv').config();

const TARGET = (process.env.FARO_URL || 'http://0.0.0.0:8080').replace(
	/\/$/,
	''
);

const COOKIE = process.env.FARO_COOKIE || '';

// Mirrors AI_HUB_PROXY_PATH in src/main/js/external-scripts.js, which points the
// chatbot widget here instead of at the AI Hub directly so its configuration
// fetch is same-origin. See webpack.dev.proxy.js for why.

const AI_HUB_PROXY_PATH = '/__aihub__';

const AI_HUB_TARGET =
	process.env.FARO_AI_HUB_URL || 'https://na1.hub.liferay.com';

module.exports = merge(common.config, {
	devServer: {
		client: {
			overlay: false,
		},
		host: '0.0.0.0',
		port: 3000,

		// An array rather than an object so the AI Hub rule is guaranteed to be
		// matched before the catch-all that sends everything else upstream.

		proxy: [
			{
				changeOrigin: true,
				context: [AI_HUB_PROXY_PATH],
				onProxyRes: onAIHubProxyRes,
				pathRewrite: {[`^${AI_HUB_PROXY_PATH}`]: ''},
				target: AI_HUB_TARGET,
			},
			{
				changeOrigin: true,
				context: ['**'],
				onProxyReq: createOnProxyReq(COOKIE, TARGET),
				onProxyRes: createOnProxyRes(TARGET),
				selfHandleResponse: true,
				target: TARGET,
			},
		],
	},

	// Non-eval source map: React Router v7's ESM build uses `import.meta.hot`,
	// which throws "Cannot use 'import.meta' outside a module" when a module is
	// wrapped in eval() (as `eval-*` devtools do). A non-eval devtool keeps the
	// real ES module context (output.module is true) so `import.meta` is valid.

	devtool: 'cheap-module-source-map',
	mode: 'development',
	module: {
		rules: [
			{
				include: common.include,
				loader: 'liferay-lang-key-dev-loader',
				test: /\.(js|ts)x?$/,
			},
		],
	},
	output: {
		chunkFilename: '[name].[chunkhash:8].js',
		publicPath: common.PUBLIC_PATH,
	},
	plugins: [
		new webpack.DefinePlugin({
			FARO_DEV_MODE: true,
		}),
	],
});
