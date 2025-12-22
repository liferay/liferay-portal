/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {dateUtils} from 'frontend-js-web';

import {IAssetObjectEntry} from '../../../common/types/AssetType';
import {ASSET_TYPE, ASSET_TYPE_ERC} from './constants';

export function formatDate(date: string): string {
	return dateUtils.format(new Date(date), 'P p');
}

export function getAssetType(objectEntry: IAssetObjectEntry): string {
	const {
		systemProperties: {
			objectDefinitionBrief: {externalReferenceCode = ''} = {},
		} = {},
	} = objectEntry;

	let type = ASSET_TYPE.FOLDER;

	if (externalReferenceCode === ASSET_TYPE_ERC.BASIC_DOCUMENT) {
		type = ASSET_TYPE.FILES;
	}
	else if (externalReferenceCode === ASSET_TYPE_ERC.BASIC_WEB_CONTENT) {
		type = ASSET_TYPE.CONTENTS;
	}
	else if (externalReferenceCode === ASSET_TYPE_ERC.BLOG) {
		type = ASSET_TYPE.BLOGS;
	}
	else if (externalReferenceCode === ASSET_TYPE_ERC.EXTERNAL_VIDEO) {
		type = ASSET_TYPE.FILES;
	}

	return type;
}

export function getAssetLanguages(
	title_i18n: {[key: string]: string} = {}
): string[] {
	const assetLanguages = Object.keys(title_i18n);

	if (assetLanguages.length) {
		return Object.keys(title_i18n).map((key) => key.replace('_', '-'));
	}

	return [];
}
