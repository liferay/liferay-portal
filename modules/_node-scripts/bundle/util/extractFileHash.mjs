/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function extractFileHash(filePath) {
	const i = filePath.lastIndexOf('.(');
	const j = filePath.indexOf(').', i + 2);

	return filePath.substring(i + 2, j);
}
