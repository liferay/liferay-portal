/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fs from 'fs';
import bourbon from 'node-bourbon';
import path from 'path';

import project from './project.mjs';
import {resolveDependency} from './themeUtil.mjs';

function formatPath(filePath) {
	return filePath.replace(/\\/g, '/');
}

export function createBourbonFile() {
	const {pathSrc} = project.options;

	const bourbonPath = bourbon.includePaths[0];

	const tmpDirPath = project.tmpdir();

	const bourbonFilePath = path.join(tmpDirPath, '_bourbon.scss');

	const bourbonFile = [];

	const deprecatedMixinsFilePath = path.join(
		project.dir,
		pathSrc,
		'css',
		'_deprecated_mixins.scss'
	);

	if (fs.existsSync(deprecatedMixinsFilePath)) {
		bourbonFile.push('@import "');
		bourbonFile.push(formatPath(deprecatedMixinsFilePath));
		bourbonFile.push('";');
	}

	const mixinsPath = resolveDependency('liferay-frontend-common-css');

	bourbonFile.push('@import "');
	bourbonFile.push(formatPath(path.join(bourbonPath, 'bourbon')));
	bourbonFile.push('";');
	bourbonFile.push('@import "');
	bourbonFile.push(
		formatPath(path.join(mixinsPath, 'liferay', '_bourbon_ext.scss'))
	);
	bourbonFile.push('";');

	fs.writeFileSync(bourbonFilePath, bourbonFile.join(''));

	return tmpDirPath;
}
