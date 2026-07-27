const AutoprefixerPlugin = require('autoprefixer');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const fs = require('fs');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const NormalizeCharsetPlugin = require('postcss-normalize-charset');
const path = require('path');
const SpriteLoaderPlugin = require('svg-sprite-loader/plugin');
const webpack = require('webpack');

const PUBLIC_PATH = '/o/osb-faro-web/dist/';

function resolveModule(name = '') {
	return path.resolve(__dirname, 'src', 'main', 'js', name);
}

const include = [resolveModule()];

const FAVICONS_DIR = path.resolve(
	__dirname,
	'src',
	'main',
	'images',
	'favicons'
);

const config = {
	entry: [
		'core-js/fn/array/fill',
		'core-js/fn/string/code-point-at',
		'core-js/fn/typed',
		'custom-event-polyfill',
		'unorm',
		'whatwg-fetch',
		resolveModule('main.jsx'),
	],
	experiments: {
		outputModule: true,
	},
	externals: [
		{

			// Bundle @clayui/icon like the rest of Clay, but expose the DXP's
			// runtime @clayui/icon under this synthetic specifier so we can feed
			// its ClayIconSpriteContext (the one FrontendDataSet reads).

			'@clayui/icon-runtime': '@clayui/icon',
			'@liferay/frontend-data-set-web':
				'/o/frontend-data-set-web/__liferay__/index.js',
			react: 'react',
			'react-dom': 'react-dom',
		},
	],
	externalsType: 'module',
	module: {
		rules: [
			{
				include,
				loader: 'ts-loader',
				options: {
					transpileOnly: true,
				},
				resolve: {
					alias: {
						assets: resolveModule('assets'),
						'cerebro-shared': resolveModule('cerebro-shared'),
						commerce: resolveModule('commerce'),
						contacts: resolveModule('contacts'),
						'custom-types': resolveModule('custom-types'),
						'event-analysis': resolveModule('event-analysis'),
						experiments: resolveModule('experiments'),
						individual: resolveModule('individual'),
						lifecycle: resolveModule('lifecycle'),
						'route-middleware': resolveModule('route-middleware'),
						segment: resolveModule('segment'),
						settings: resolveModule('settings'),
						shared: resolveModule('shared'),
						sites: resolveModule('sites'),
						test: resolveModule('test'),
						touchpoints: resolveModule('touchpoints'),
						'ui-kit': resolveModule('ui-kit'),
					},
					extensions: ['.js', '.jsx', '.ts', '.tsx'],
				},
				test: /\.(js|ts)x?$/,
			},
			{
				loader: 'graphql-tag/loader',
				test: /\.graphql$/,
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
									AutoprefixerPlugin,
								],
								sourceMap: true,
							},
						},
					},
				],
			},
			{
				include: path.resolve(__dirname, 'src', 'main', 'css'),
				test: /\.scss$/,
				use: [
					MiniCssExtractPlugin.loader,
					{
						loader: 'css-loader',
						options: {
							importLoaders: 2,
						},
					},
					{
						loader: 'postcss-loader',
						options: {
							postcssOptions: {
								ident: 'postcss',
								plugins: () => [AutoprefixerPlugin],
								sourceMap: true,
							},
						},
					},
					{
						loader: 'sass-loader',
						options: {
							api: 'modern',
							implementation: require('sass'),
							sassOptions: {
								loadPaths: [
									path.resolve(__dirname, 'node_modules'),
								],
								quietDeps: true,
								silenceDeprecations: [
									'import',
									'global-builtin',
								],
							},
							sourceMap: true,
						},
					},
				],
			},
			{

				// Favicons are referenced by URL from a link element, so they
				// are emitted as standalone files instead of sprite symbols.

				include: FAVICONS_DIR,
				test: /\.svg$/,
				type: 'asset/resource',
			},
			{
				exclude: FAVICONS_DIR,
				test: /\.svg$/,
				use: [
					{
						loader: 'svg-sprite-loader',
						options: {
							extract: true,
							spriteFilename: 'sprite.svg',
						},
					},
					{
						loader: 'svgo-loader',
						options: {
							plugins: [
								{removeDimensions: true},
								{removeUselessStrokeAndFill: false},
								{removeViewBox: false},
							],
						},
					},
				],
			},
			{
				test: /\.(eot|ttf|woff|woff2)(\?v=\d+\.\d+\.\d+)?$/,
				use: 'file-loader',
			},
		],
	},
	output: {
		filename: 'main.js',
		module: true,
		path: path.resolve('src/main/resources/META-INF/resources/dist'),
		pathinfo: false,
		publicPath: PUBLIC_PATH,
	},
	plugins: [
		new MiniCssExtractPlugin({
			filename: 'main.css',
		}),
		new ForkTsCheckerWebpackPlugin({
			issue: {
				include: [{file: '**/src/main/js/**/*'}],
			},
			logger: 'webpack-infrastructure',
		}),
		new SpriteLoaderPlugin(),
		new webpack.DefinePlugin({
			FARO_ENV: JSON.stringify(process.env.FARO_ENVIRONMENT_NAME || ''),
		}),
		new webpack.DefinePlugin({
			FARO_PENDO_API_KEY: JSON.stringify(
				process.env.FARO_PENDO_API_KEY || ''
			),
		}),
		new webpack.IgnorePlugin({
			contextRegExp: /moment$/,
			resourceRegExp: /^\.\/locale$/,
		}),
	],
	target: 'web',
};

// Warn when this app's bundled @clayui/icon diverges from the portal's
// (liferay-portal/modules/node_modules). FrontendDataSet renders with the
// portal's runtime @clayui/icon, and this app bridges the portal's
// ClayIconSpriteContext (see the @clayui/icon-runtime external), so a divergent
// @clayui/icon can break FrontendDataSet icons. Only @clayui/icon crosses that
// boundary -- the rest of Clay is bundled and self-contained, and React is
// external.

function warnOnClayIconVersionMismatch() {
	const readVersion = (...segments) => {
		try {
			return JSON.parse(
				fs.readFileSync(path.resolve(__dirname, ...segments), 'utf8')
			).version;
		}
		catch (error) {
			return null;
		}
	};

	const bundledVersion =
		readVersion('node_modules', '@clayui', 'icon', 'package.json') ||
		readVersion(
			'..',
			'..',
			'node_modules',
			'@clayui',
			'icon',
			'package.json'
		);
	const portalVersion = readVersion(
		'..',
		'..',
		'..',
		'..',
		'modules',
		'node_modules',
		'@clayui',
		'icon',
		'package.json'
	);

	// The portal node_modules is absent in checkouts that install only the
	// workspace (e.g. CI); skip the check but report why it did not run.

	if (!portalVersion) {

		// eslint-disable-next-line no-console
		console.warn(
			`\n[osb-faro-web] Unable to verify @clayui/icon parity: the portal node_modules (liferay-portal/modules/node_modules) is not installed, so the portal @clayui/icon version could not be read. FrontendDataSet uses the portal's @clayui/icon at runtime; verify it matches this app's bundled version (${bundledVersion || 'unknown'}) manually.\n`
		);

		return;
	}

	if (bundledVersion && bundledVersion !== portalVersion) {

		// eslint-disable-next-line no-console
		console.warn(
			`\n[osb-faro-web] @clayui/icon version mismatch: this app bundles ${bundledVersion} but the portal provides ${portalVersion}. FrontendDataSet uses the portal's @clayui/icon at runtime and this app bridges its ClayIconSpriteContext, so a divergent version can break FrontendDataSet icons. Align this app's @clayui/icon with the portal.\n`
		);
	}
}

warnOnClayIconVersionMismatch();

module.exports = {
	config,
	include,
	publicPath: PUBLIC_PATH,
	resolve: {
		extensions: ['', '.js', '.jsx', '.ts', '.tsx'],
		root: [resolveModule()],
	},
};
