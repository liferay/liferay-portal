/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fastGlob from 'fast-glob';
import fs from 'fs';
import path from 'path';
import {pipeline} from 'stream/promises';
import yazl from 'yazl';

import project from '../project.mjs';

/**
 * plugin:war
 */
export default async function pluginWar() {
	const {distName, pathDist, rootDir} = project.options;

	const entries = await fastGlob('**/*', {
		cwd: rootDir,
		markDirectories: true,
		onlyFiles: false,
	});

	const zipFile = new yazl.ZipFile();

	for (const entry of entries.sort()) {
		const filePath = path.join(rootDir, entry);

		const stat = fs.statSync(filePath);

		const options = {
			mode: stat.mode,
			mtime: stat.mtime,
		};

		if (entry.endsWith('/')) {
			zipFile.addEmptyDirectory(entry, options);
		}
		else {
			zipFile.addBuffer(fs.readFileSync(filePath), entry, options);
		}
	}

	zipFile.end();

	fs.mkdirSync(pathDist, {recursive: true});

	await pipeline(
		zipFile.outputStream,
		fs.createWriteStream(path.join(pathDist, distName + '.war'))
	);
}
