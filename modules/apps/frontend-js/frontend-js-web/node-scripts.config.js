/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const path = require('path');

module.exports = {
	customBuild: {
		esbuild: {
			bundle: true,
			entryNames: 'Liferay.([hash])',
			entryPoints: [
				path.resolve(
					'src',
					'main',
					'resources',
					'META-INF',
					'resources',
					'liferay',
					'index.js'
				),
			],
			loader: {
				'.js': 'jsx',
			},
			outdir: path.resolve(
				'build',
				'node',
				'packageRunBuild',
				'resources'
			),
			sourcemap: true,
			target: ['es2020'],
		},
	},
	main: './src/main/resources/META-INF/resources/main/index.js',
	submodules: {
		auto_fields:
			'./src/main/resources/META-INF/resources/auto_fields/index.js',
		legacy: './src/main/resources/META-INF/resources/legacy/index.ts',
	},
	typescript: {
		main: './src/main/resources/META-INF/resources/main/index.d.ts',
	},
};
