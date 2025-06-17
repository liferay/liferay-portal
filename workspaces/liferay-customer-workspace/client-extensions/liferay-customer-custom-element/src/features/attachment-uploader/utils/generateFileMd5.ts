/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import SparkMD5 from 'spark-md5';

export async function generateFileMd5(file: File): Promise<string> {
	const chunkSize = 2 * 1024 * 1024;
	const chunks = Math.ceil(file.size / chunkSize);
	let currentChunk = 0;

	const spark = new SparkMD5.ArrayBuffer();
	const fileReader = new FileReader();

	return new Promise((resolve, reject) => {
		const loadNext = () => {
			const chunkStart = currentChunk * chunkSize;
			const chunkEnd = Math.min(chunkStart + chunkSize, file.size);
			const blob = file.slice(chunkStart, chunkEnd);
			fileReader.readAsArrayBuffer(blob);
		};

		fileReader.onload = (error) => {
			if (error.target?.result) {
				spark.append(error.target.result as ArrayBuffer);
				currentChunk++;

				if (currentChunk < chunks) {
					loadNext();
				}
				else {
					resolve(spark.end());
				}
			}
			else {
				reject(new Error('Failed to read file chunk'));
			}
		};

		fileReader.onerror = () => {
			reject(fileReader.error);
		};

		loadNext();
	});
}
