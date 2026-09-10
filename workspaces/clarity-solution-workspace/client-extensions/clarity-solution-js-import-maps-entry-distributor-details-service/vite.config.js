/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {resolve} from 'path';
import {defineConfig} from 'vite';

export default defineConfig({
	build: {
		lib: {
			entry: {
				index: resolve(__dirname, 'src/index.js'),
			},
			formats: ['es'],
		},
		outDir: 'build/vite',
		rollupOptions: {
			output: {
				entryFileNames: '[name].js',
				format: 'es',
			},
		},
	},
	define: {
		'process.env.NODE_ENV': '"production"',
	},
});
