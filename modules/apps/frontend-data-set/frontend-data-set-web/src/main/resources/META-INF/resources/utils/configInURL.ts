/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import JsonURL from '@jsonurl/jsonurl';

import {EConfigInURLBehavior, IConfigInURL} from './types';

export const FDS_CONFIG_PARAM_NAME = '_fdsConfig';

function getConfigParamName(id: string): string {
	return `${id}${FDS_CONFIG_PARAM_NAME}`;
}

export function readConfigFromURL(id: string): Partial<IConfigInURL> | null {
	if (!Liferay.FeatureFlags['LPD-22473']) {
		return null;
	}

	const params = new URLSearchParams(window.location.search);

	const configParam = params.get(getConfigParamName(id));

	if (!configParam) {
		return null;
	}

	let config = {};

	try {
		config = JsonURL.parse(configParam, {
			AQF: true,
			noEmptyComposite: true,
		});
	}
	catch (error) {
		return null;
	}

	return config;
}

export function writeConfigInURL(
	id: string,
	config: Partial<IConfigInURL>,
	configInURLBehavior: EConfigInURLBehavior
) {
	if (
		!config ||
		configInURLBehavior === EConfigInURLBehavior.OFF ||
		!Liferay.FeatureFlags['LPD-22473']
	) {
		return;
	}

	const currentConfig = readConfigFromURL(id);

	let currentConfigChanged: boolean = false;
	Object.keys(config).forEach((key: string) => {
		const configKey: keyof IConfigInURL = key as keyof IConfigInURL;

		if (config[configKey] === undefined) {
			delete config[configKey];

			if (currentConfig && currentConfig[configKey]) {
				delete currentConfig[configKey];
				currentConfigChanged = true;
			}
		}
	});

	if (contains(config, currentConfig) && !currentConfigChanged) {
		return;
	}

	const params = new URLSearchParams(window.location.search);

	params.set(
		getConfigParamName(id),
		JsonURL.stringify(
			sortObjectKeys({...(currentConfig || {}), ...config}),
			{AQF: true, noEmptyComposite: true}
		) || ''
	);

	const path = `${window.location.pathname}?${decodeURLParams(params)}`;

	const replaceState =
		configInURLBehavior === EConfigInURLBehavior.REPLACE || !currentConfig;

	if (Liferay.SPA && Liferay.SPA.app) {
		Liferay.SPA.app.browserPathBeforeNavigate = path;

		Liferay.SPA.app.updateHistory_(
			document.title,
			path,
			{
				...window.history.state,
				path,
				redirectPath: path,
				senna: true,
			},
			replaceState
		);

		return;
	}

	if (replaceState) {
		window.history.replaceState(
			{...window.history.state},
			document.title,
			path
		);
	}
	else {
		window.history.pushState(
			{...window.history.state},
			document.title,
			path
		);
	}
}

export function contains(
	a: Partial<IConfigInURL> | null,
	b: Partial<IConfigInURL> | null
) {
	if (a === null || b === null) {
		return false;
	}

	return deepContains(a, b);
}

function decodeURLParams(params: URLSearchParams) {
	return params
		.toString()
		.replace(/%28/g, '(')
		.replace(/%29/g, ')')
		.replace(/%2C/g, ',')
		.replace(/%3A/g, ':');
}

function deepContains(subset: any, superset: any) {
	if (typeof subset !== 'object' || subset === null) {
		return subset === superset;
	}

	if (typeof superset !== 'object' || superset === null) {
		return false;
	}

	if (Array.isArray(subset)) {
		if (!Array.isArray(superset) || subset.length > superset.length) {
			return false;
		}

		if (!subset.length && !!superset.length) {
			return false;
		}

		if (subset.length === superset.length && !superset.length) {
			return true;
		}

		const supersetItems = [...superset];

		for (const subsetItem of subset) {
			const index = supersetItems.findIndex((supersetItem) =>
				deepContains(subsetItem, supersetItem)
			);

			if (index === -1) {
				return false;
			}

			supersetItems.splice(index, 1);
		}

		if (supersetItems.length) {
			return false;
		}

		return true;
	}

	for (const key of Object.keys(subset)) {
		if (!Object.prototype.hasOwnProperty.call(superset, key)) {
			return false;
		}

		if (!deepContains(subset[key], superset[key])) {
			return false;
		}
	}

	return true;
}

function sortObjectKeys(object: any): any {
	if (typeof object !== 'object' || object === null) {
		return object;
	}

	if (Array.isArray(object)) {
		return object.map(sortObjectKeys).sort();
	}

	return Object.keys(object)
		.sort()
		.reduce((result: {[key: string]: any}, key: string) => {
			result[key] = sortObjectKeys(object[key]);

			return result;
		}, {});
}
