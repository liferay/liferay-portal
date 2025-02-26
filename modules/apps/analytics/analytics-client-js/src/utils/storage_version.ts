/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {STORAGE_KEY_CONTEXTS, STORAGE_KEY_STORAGE_VERSION} from './constants';
import {setContexts} from './contexts';
import {getItem, setItem} from './storage';

export const AC_CLIENT_STORAGE_VERSION = 1.0;

const upgradeStorageSteps = [
	[
		1.0,
		() => {
			const storedContextKvArr = getItem(STORAGE_KEY_CONTEXTS);

			if (storedContextKvArr && !Array.isArray(storedContextKvArr[0])) {
				const storedContexts = new Map();

				setContexts(storedContexts);
			}
		},
	],
];

function getStorageVersion() {
	const storageVersion = getItem(STORAGE_KEY_STORAGE_VERSION);

	return storageVersion ? parseFloat(storageVersion) : 0;
}

function setStorageVersion(version = AC_CLIENT_STORAGE_VERSION) {
	return setItem(STORAGE_KEY_STORAGE_VERSION, version.toString());
}

function upgradeStorage() {
	const version = getStorageVersion();

	if (version === AC_CLIENT_STORAGE_VERSION) {
		return true;
	}

	upgradeStorageSteps.forEach(([stepVersion, upgradeFn]) => {
		if (stepVersion > version) {
			upgradeFn();
		}
	});

	setStorageVersion(AC_CLIENT_STORAGE_VERSION);

	return true;
}

export {getStorageVersion, setStorageVersion, upgradeStorage};
