/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

module.exports = {
	exports: [
		'@liferay/js-api',
		'@liferay/js-api/data-set',
		'cropperjs/dist/cropper.css',
		'graphql-hooks-memcache',
		'graphql-hooks',
		'highlight.js/styles/monokai-sublime.css',
		'qrcode',
		'react-dropzone',
		'react-transition-group',
		'uuid',
		'react-flow-renderer',
		'react-helmet',
		'axe-core',
		'clipboard',
		'cropperjs',
		'dagre',
		'date-fns',
		'dom-align',
		'fuzzy',
		'highlight.js',
		'highlight.js/lib/core',
		'highlight.js/lib/languages/java',
		'highlight.js/lib/languages/javascript',
		'highlight.js/lib/languages/plaintext',
		'libphonenumber-js',
		'liferay-ckeditor',
		'moment',
		'moment/min/moment-with-locales',
		'numeral',
		'object-hash',
		'qs',
		'react-router',
		'react-text-mask',
		'text-mask-addons',
		'text-mask-core',
		'ua-parser-js',
	],
	symbols: {
		qrcode: [

			// Need to explicitly list exports because the package differs in browser and server

			'create',
			'toCanvas',
			'toString',
			'toDataURL',
		],
	},
};
