/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/no-extraneous-dependencies
const {polyfillNode} = require('esbuild-plugin-polyfill-node');
const fs = require('fs/promises');
const path = require('path');

const outdir = path.resolve('build', 'node', 'packageRunBuild', 'resources');

/* doHover function known issue: Expected Parser stream to be available
https://github.com/graphql/graphiql/issues/4157 */

const monacoEditorPath = path.dirname(
	require.resolve('monaco-editor/package.json')
);

const monacoGraphqlPath = path.dirname(
	require.resolve('monaco-graphql/package.json')
);

module.exports = {
	customBuild: {
		esbuild: {
			bundle: true,
			entryNames: '[name]',
			entryPoints: {
				'editor.worker': path.resolve(
					monacoEditorPath,
					'esm/vs/editor/editor.worker.js'
				),
				'graphql.worker': path.resolve(
					monacoGraphqlPath,
					'esm/graphql.worker.js'
				),
				'headless-discovery-web-min': path.resolve('src', 'index.js'),
				'json.worker': path.resolve(
					monacoEditorPath,
					'esm/vs/language/json/json.worker.js'
				),
			},
			loader: {
				'.js': 'jsx',
				'.ttf': 'file',
			},
			outdir,
			plugins: [polyfillNode({})],
			sourcemap: true,
			target: ['es2020'],
		},
		other: async () => {
			await fs.mkdir(outdir, {recursive: true});

			await Promise.all([
				fs.copyFile(
					path.resolve('src', 'index.html'),
					path.resolve(outdir, 'index.html')
				),
				fs.copyFile(
					path.resolve('src', 'css', 'main.css'),
					path.resolve(outdir, 'main.css')
				),
			]);
		},
	},
};
