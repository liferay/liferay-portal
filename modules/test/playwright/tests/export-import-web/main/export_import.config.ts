/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {existsSync, readdirSync} from 'fs';
import path from 'path';

const repositoryDir = path.resolve(__dirname, '../../../../../..');

function hasTomcat(directory: string): boolean {
	return (
		existsSync(directory) &&
		readdirSync(directory).some((fileName) =>
			fileName.startsWith('tomcat-')
		)
	);
}

function getLiferayHome(): string {
	const candidates = [
		process.env.LIFERAY_HOME,
		path.join(repositoryDir, 'bundles'),
		path.resolve(repositoryDir, '../bundles'),
	];

	const liferayHome = candidates.find(
		(candidate) => candidate && hasTomcat(candidate)
	);

	if (!liferayHome) {
		throw new Error(
			'Unable to locate the Liferay bundle. Set LIFERAY_HOME to a ' +
				'directory that contains a tomcat-* folder.'
		);
	}

	return liferayHome;
}

export const exportImportConfig = {
	environment: {
		tomcatDir: getLiferayHome(),
	},
};
