/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import createTempFile from './createTempFile';

export type SizedFileType = 'gif' | 'jpeg' | 'pdf' | 'png';

const MAGIC_BYTES: Record<SizedFileType, Buffer> = {
	gif: Buffer.from('GIF89a', 'ascii'),
	jpeg: Buffer.from([
		0xff, 0xd8, 0xff, 0xe0, 0x00, 0x10, 0x4a, 0x46, 0x49, 0x46, 0x00,
	]),
	pdf: Buffer.from('%PDF-1.4\n', 'ascii'),
	png: Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]),
};

/**
 * Creates a temporary file of the given mime type, padded with zero bytes to
 * the requested size. The leading magic bytes let the server detect the mime
 * type (via Tika) while the total length drives the file size limit checks.
 */
export function createSizedFile(
	fileName: string,
	type: SizedFileType,
	sizeInBytes: number
): string {
	const buffer = Buffer.alloc(sizeInBytes);

	MAGIC_BYTES[type].copy(buffer, 0);

	return createTempFile(fileName, buffer);
}
