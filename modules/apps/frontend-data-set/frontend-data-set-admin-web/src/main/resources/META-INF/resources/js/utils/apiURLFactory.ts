/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {API_URL, OBJECT_RELATIONSHIP, RESOURCES} from './constants';

export function createAPIURL(dataSetId: string, resource: string): string {
	const baseAPIURL = `${API_URL.DATA_SETS}/${dataSetId}`;

	if (resource === RESOURCES.ACTIONS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_ACTIONS}`;
	}
	else if (resource === RESOURCES.CARDS_SECTIONS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_CARDS_SECTIONS}`;
	}
	else if (resource === RESOURCES.DATA_SETS) {
		return API_URL.DATA_SETS;
	}
	else if (resource === RESOURCES.CLIENT_EXTENSION_FILTERS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_CLIENT_EXTENSION_FILTERS}`;
	}
	else if (resource === RESOURCES.DATE_FILTERS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_DATE_FILTERS}`;
	}
	else if (resource === RESOURCES.LIST_SECTIONS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_LIST_SECTIONS}`;
	}
	else if (resource === RESOURCES.SELECTION_FILTERS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_SELECTION_FILTERS}`;
	}
	else if (resource === RESOURCES.SORTS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_SORTS}`;
	}
	else if (resource === RESOURCES.TABLE_SECTIONS) {
		return `${baseAPIURL}/${OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS}`;
	}

	throw new Error('No such resource: ' + resource);
}
