/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {readdirSync, statSync} from 'fs';
import path from 'path';

import {checkInZip} from '../../../../utils/zip';

export function getTomcatTempDir(liferayHomeDir: string): string {
	const tomcatDirs = readdirSync(liferayHomeDir)
		.filter((file) => file.startsWith('tomcat-'))
		.sort((a, b) =>
			a.localeCompare(b, undefined, {numeric: true, sensitivity: 'base'})
		);

	return path.join(liferayHomeDir, tomcatDirs[tomcatDirs.length - 1], 'temp');
}

export async function unzipAndCheckFolder(
	tempDir: string,
	folderName: string = 'adaptive-media'
): Promise<boolean | null> {
	const files = readdirSync(tempDir)
		.filter((file) => file.endsWith('.lar'))
		.map((file) => ({
			file,
			time: statSync(path.join(tempDir, file)).mtime.getTime(),
		}))
		.sort((a, b) => b.time - a.time);

	if (!files.length) {
		return null;
	}

	const mostRecentFilePath = path.join(tempDir, files[0].file);

	try {
		const hasFolder = await checkInZip(mostRecentFilePath, folderName);

		return hasFolder;
	}
	catch (error) {
		console.error(`Error reading file ${files[0].file}: ${error}`);
	}

	return null;
}
