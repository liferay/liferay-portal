/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {InsightType} from '../types/InsightType';

const INSIGHT_TYPE_BASE_URI = '/o/seo-studio/insight-types';

const INSIGHT_TYPE_BY_ERC_URI = `${INSIGHT_TYPE_BASE_URI}/by-external-reference-code/`;

async function getInsightType(
	externalReferenceCode: string
): Promise<InsightType> {
	const response = await fetch(
		`${INSIGHT_TYPE_BY_ERC_URI}${externalReferenceCode}`,
		{method: 'GET'}
	);

	const json = await response.json();

	if (json?.name && typeof json.name === 'object') {
		json.name = json.name.name;
	}

	return json;
}

async function getInsightTypes() {
	const response = await fetch(INSIGHT_TYPE_BASE_URI, {method: 'GET'});

	return response.json();
}

export {getInsightType, getInsightTypes};
