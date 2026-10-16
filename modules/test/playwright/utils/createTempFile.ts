/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as fs from 'fs';
import * as os from 'node:os'; // eslint-disable-line @liferay/no-extraneous-dependencies
import * as path from 'path';
import {zip} from 'zip-a-folder';

import onExit from './onExit';

const TMP_DIR = `tmp/${process.pid}`;

onExit(() => {
	if (fs.existsSync(TMP_DIR)) {
		fs.rmdirSync(TMP_DIR, {recursive: true});
	}
});

export class TempFileMissingError extends Error {
	constructor(fileName: string) {
		super(`Temporary file ${fileName} does not exist`);
	}
}

export default function createTempFile(
	name: string,
	content: Buffer | string = ''
): string {
	fs.mkdirSync(TMP_DIR, {recursive: true});

	const filePath = path.join(TMP_DIR, name);

	if (fs.existsSync(filePath)) {
		throw new Error(`Temporary file ${name} already exists`);
	}

	fs.writeFileSync(filePath, content);

	return filePath;
}

export function createTempDir(): string {
	fs.mkdirSync(TMP_DIR, {recursive: true});

	return fs.mkdtempSync(path.join(TMP_DIR, 'dir-'));
}

export function readTempFile(name: string): string {
	const filePath = path.join(TMP_DIR, name);

	if (!fs.existsSync(filePath)) {
		throw new TempFileMissingError(name);
	}

	try {
		return fs.readFileSync(filePath, 'utf-8');
	}
	catch (error) {
		throw new Error(`Cannot read temporary file ${name}: ${error}`);
	}
}

export async function zipFolder(folderPath: string) {
	const tempFilePath = path.join(os.tmpdir(), path.basename(folderPath));
	await zip(folderPath, tempFilePath);

	return tempFilePath;
}
