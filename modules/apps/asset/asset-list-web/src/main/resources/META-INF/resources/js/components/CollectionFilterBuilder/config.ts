/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type Config = {
	groupIds?: string[];
	namespace: string;
	tagSelectorURL?: string;
	vocabularyIds?: string[];
};

export let config = {} as Config;

export function initializeConfig(backendConfig: Config) {
	config = backendConfig;
}
