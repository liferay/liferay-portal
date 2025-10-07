const AutoprefixerPlugin = require('autoprefixer');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const NormalizeCharsetPlugin = require('postcss-normalize-charset');
const path = require('path');
const SpriteLoaderPlugin = require('svg-sprite-loader/plugin');
const webpack = require('webpack');

const PUBLIC_PATH = '/o/osb-faro-web/dist/';

function resolveModule(name = '') {
	return path.resolve(__dirname, 'src', 'main', 'js', name);
}

const include = [resolveModule(), path.resolve(__dirname, 'node_modules')];

const config = {
	entry: [
		'core-js/fn/array/fill',
		'core-js/fn/string/code-point-at',
		'core-js/fn/typed',
		'custom-event-polyfill',
		'unorm',
		'whatwg-fetch',
		resolveModule('main.jsx')
	],
	module: {
		rules: [
			{
				include,
				loader: 'ts-loader',
				options: {
					transpileOnly: true
				},
				resolve: {
					alias: {
						assets: resolveModule('assets'),
						'cerebro-shared': resolveModule('cerebro-shared'),
						'clay-charts-react': resolveModule('clay-charts-react'),
						commerce: resolveModule('commerce'),
						contacts: resolveModule('contacts'),
						'custom-types': resolveModule('custom-types'),
						'event-analysis': resolveModule('event-analysis'),
						experiments: resolveModule('experiments'),
						home: resolveModule('home'),
						individual: resolveModule('individual'),
						'route-middleware': resolveModule('route-middleware'),
						segment: resolveModule('segment'),
						settings: resolveModule('settings'),
						shared: resolveModule('shared'),
						sites: resolveModule('sites'),
						test: resolveModule('test'),
						touchpoints: resolveModule('touchpoints'),
						'ui-kit': resolveModule('ui-kit')
					},
					extensions: ['.js', '.jsx', '.ts', '.tsx']
				},
				test: /\.(js|ts)x?$/
			},
			{
				loader: 'graphql-tag/loader',
				test: /\.graphql$/
			},
			{
				test: /\.css$/i,
				use: [
					MiniCssExtractPlugin.loader,
					'css-loader',
					{
						loader: 'postcss-loader',
						options: {
							postcssOptions: {
								ident: 'postcss',
								plugins: () => [
									NormalizeCharsetPlugin,
									AutoprefixerPlugin
								],
								sourceMap: true
							}
						}
					}
				]
			},
			{
				include: path.resolve(__dirname, 'src', 'main', 'css'),
				test: /\.scss$/,
				use: [
					MiniCssExtractPlugin.loader,
					{
						loader: 'css-loader',
						options: {
							importLoaders: 2
						}
					},
					{
						loader: 'postcss-loader',
						options: {
							postcssOptions: {
								ident: 'postcss',
								plugins: () => [AutoprefixerPlugin],
								sourceMap: true
							}
						}
					},
					{
						loader: 'sass-loader',
						options: {
							implementation: require('sass'),
							sourceMap: true
						}
					}
				]
			},
			{
				test: /\.svg$/,
				use: [
					{
						loader: 'svg-sprite-loader',
						options: {
							extract: true,
							spriteFilename: 'sprite.svg'
						}
					},
					{
						loader: 'svgo-loader',
						options: {
							plugins: [
								{removeDimensions: true},
								{removeUselessStrokeAndFill: false},
								{removeViewBox: false}
							]
						}
					}
				]
			},
			{
				test: /\.(eot|ttf|woff|woff2)(\?v=\d+\.\d+\.\d+)?$/,
				use: 'file-loader'
			}
		]
	},
	output: {
		filename: 'main.js',
		path: path.resolve('src/main/resources/META-INF/resources/dist'),
		pathinfo: false,
		publicPath: PUBLIC_PATH
	},
	plugins: [
		new MiniCssExtractPlugin({
			filename: 'main.css'
		}),
		new ForkTsCheckerWebpackPlugin({
			eslint: {
				files: 'src/main/js/**/*.+(js|ts)?(x)'
			}
		}),
		new SpriteLoaderPlugin(),
		new webpack.DefinePlugin({
			FARO_ENV: JSON.stringify(process.env.FARO_ENVIRONMENT_NAME || '')
		}),
		new webpack.DefinePlugin({
			FARO_PENDO_API_KEY: JSON.stringify(
				process.env.FARO_PENDO_API_KEY || ''
			)
		}),
		new webpack.IgnorePlugin({
			contextRegExp: /moment$/,
			resourceRegExp: /^\.\/locale$/
		})
	],
	target: 'web'
};

module.exports = {
	config,
	include,
	publicPath: PUBLIC_PATH,
	resolve: {
		extensions: ['', '.js', '.jsx', '.ts', '.tsx'],
		root: [resolveModule()]
	}
};
