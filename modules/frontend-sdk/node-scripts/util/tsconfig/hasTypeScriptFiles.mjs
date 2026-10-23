/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fg from 'fast-glob';

import fileExists from '../fileExists.mjs';

export default async function hasTypeScriptFiles(dir) {
	if (!(await fileExists(dir))) {
		return false;
	}

	const tsFiles = await fg('**/*.{ts,tsx}', {cwd: dir});

	return !!tsFiles.length;
}
