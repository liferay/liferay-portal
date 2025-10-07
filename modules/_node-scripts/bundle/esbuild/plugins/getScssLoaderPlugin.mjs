/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs/promises';
import path from 'path';

import {BUILD_SASS_CACHE_PATH, SRC_PATH} from '../../../util/constants.mjs';
import extractFileHash from '../../util/extractFileHash.mjs';

/**
 * This plugin transforms `import from` statements for .scss files into JavaScript code that inserts
 * a link to the actual file in the HTML at rutime.
 *
 * This technique is only used for liferay-portal internal code (ie: it is not applied to external
 * npm packages).
 */
export default function getScssLoaderPlugin(projectWebContextPath) {
	return {
		name: 'scss-loader-plugin',

		setup(build) {
			build.onLoad(
				{
					filter: /\.scss$/,
				},
				async (args) => {
					const projectPath = path.relative(SRC_PATH, args.path);

					const cssFiles = await fs.readdir(
						path.join(
							BUILD_SASS_CACHE_PATH,
							path.dirname(projectPath)
						)
					);

					const projectBasePath = projectPath.replace(/\.scss$/, '');

					const cssBasename = cssFiles.find((cssFile) =>
						cssFile.startsWith(
							`${path.basename(projectBasePath)}.(`
						)
					);

					const hash = extractFileHash(cssBasename);

					const cssBaseURI = projectPath
						.split(path.sep)
						.join(path.posix.sep)
						.replace(/\.scss$/, '');

					const contents = `
const link = document.createElement('link');
link.setAttribute('rel', 'stylesheet');
link.setAttribute('type', 'text/css');
link.setAttribute(
	'href', 
	Liferay.ThemeDisplay.getPathContext() +
		'/o${projectWebContextPath}/${cssBaseURI}' +
		(document.dir === 'rtl' ? '_rtl' : '') +
		'.(${hash}).css'
);
if (Liferay.CSP) {
	link.setAttribute('nonce', Liferay.CSP.nonce);
}
document.querySelector('head').appendChild(link);
`;

					return {
						contents,
						loader: 'js',
					};
				}
			);
		},
	};
}
