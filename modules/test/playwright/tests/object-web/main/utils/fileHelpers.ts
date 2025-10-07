/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import path from 'path';

const DEPENDENCIES_DIR = path.resolve(__dirname, '../dependencies');

export function createFile(fileName: string, fileSize: number) {
	const filePath = path.join(DEPENDENCIES_DIR, fileName);

	if (!fs.existsSync(filePath)) {
		const sizeInBytes = fileSize * 1024 * 1024;

		const buffer = Buffer.alloc(sizeInBytes, 'X');
		fs.mkdirSync(DEPENDENCIES_DIR, {recursive: true});
		fs.writeFileSync(filePath, buffer);
	}

	return filePath;
}

export function deleteFile(fileName: string) {
	const filePath = path.join(DEPENDENCIES_DIR, fileName);

	if (fs.existsSync(filePath)) {
		fs.unlinkSync(filePath);
	}
}
