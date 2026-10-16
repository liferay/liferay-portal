/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as fs from 'fs';
import * as path from 'path';
import {open} from 'yauzl';
import {zip} from 'zip-a-folder';

import {streamToString} from './stream';
import {getTempDir} from './temp';

type ZipOptions = {
	destPath?: string;
};

export async function zipFolder(folderPath: string, zipOptions?: ZipOptions) {
	fs.mkdirSync(getTempDir(), {recursive: true});

	const tempFilePath = path.join(getTempDir(), path.basename(folderPath));

	await zip(folderPath, tempFilePath, zipOptions);

	return tempFilePath;
}

export async function checkInZip(
	filePath: string,
	path: string
): Promise<boolean> {
	return new Promise((resolve, reject) => {
		open(filePath, {lazyEntries: true}, (error, zip) => {
			if (error) {
				return reject(error);
			}

			zip.readEntry();

			zip.on('entry', (entry) => {
				if (
					entry.fileName.match(path) ||
					entry.fileName.startsWith(path + '/')
				) {
					zip.close();
					resolve(true);
				}
				zip.readEntry();
			});

			zip.on('end', () => {
				zip.close();
				resolve(false);
			});

			zip.on('error', (error) => {
				zip.close();
				reject(error);
			});
		});
	});
}

export async function unzipFile(filePath: string): Promise<string> {
	return new Promise((resolve, reject) => {
		open(filePath, {lazyEntries: true}, async (error, zip) => {
			if (error) {
				return reject(error);
			}

			zip.readEntry();

			zip.on('entry', (entry) => {
				if (/\/$/.test(entry.fileName)) {
					zip.readEntry();
				}
				else {
					zip.openReadStream(entry, async (error, stream) => {
						if (error) {
							zip.close();
							reject(error);
						}
						stream.on('end', () => {
							zip.readEntry();
						});
						resolve(await streamToString(stream));
					});
				}
			});
		});
	});
}

export async function getZipEntryNames(filePath: string): Promise<string[]> {
	return new Promise((resolve, reject) => {
		open(filePath, {lazyEntries: true}, (error, zip) => {
			if (error) {
				return reject(error);
			}

			const entryNames: string[] = [];

			zip.readEntry();

			zip.on('entry', (entry) => {
				entryNames.push(entry.fileName);

				zip.readEntry();
			});

			zip.on('end', () => {
				zip.close();
				resolve(entryNames);
			});

			zip.on('error', (error) => {
				zip.close();
				reject(error);
			});
		});
	});
}

export async function readFileFromZip(
	fileName: string,
	filePath: string
): Promise<string> {
	return new Promise((resolve, reject) => {
		open(filePath, {lazyEntries: true}, (error, zip) => {
			if (error) {
				return reject(error);
			}

			zip.readEntry();

			zip.on('entry', (entry) => {
				if (entry.fileName.match(fileName)) {
					zip.openReadStream(entry, (error, readStream) => {
						if (error) {
							return reject(error);
						}

						let data = '';

						readStream.on('data', (chunk) => {
							data += chunk;
						});

						readStream.on('end', () => {
							zip.close();
							resolve(data);
						});
					});
				}
				else {
					zip.readEntry();
				}
			});
		});
	});
}
