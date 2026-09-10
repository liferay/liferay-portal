/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {resolve} from 'path';
import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [react()],
	build: {
		lib: {
			entry: {
				index: resolve(__dirname, 'src/index.jsx'),
			},
			formats: ['es'],
		},
		outDir: 'build/vite',
		rollupOptions: {
			external: ['react', 'react-dom'],
			output: {
				entryFileNames: '[name].js',
				format: 'es', // Ensured format is consistent
			},
		},
	},
	define: {
		'process.env.NODE_ENV': '"production"',
	},
	server: {
		origin: 'http://localhost:5173',
	},
});
