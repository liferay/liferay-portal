/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getTaxonomyItems} from './getTaxonomyItems';

const MAX_EXISTING_TAGS = 150;

const TAXONOMY_ENDPOINT = '/o/headless-admin-taxonomy/v1.0';

interface GetExistingTagsParams {
	cmsGroupId: number | string;
	max?: number;
	scopeId: number;
}

interface KeywordItem {
	name: string;
}

export async function getExistingTags({
	cmsGroupId,
	max = MAX_EXISTING_TAGS,
	scopeId,
}: GetExistingTagsParams): Promise<string[]> {
	if (!Number.isInteger(scopeId)) {
		return [];
	}

	const base = `${Liferay.ThemeDisplay.getPortalURL()}${TAXONOMY_ENDPOINT}/sites`;

	const url =
		scopeId >= 0
			? `${base}/${scopeId}/keywords`
			: `${base}/${cmsGroupId}/keywords?filter=${encodeURIComponent(
					`groupIds in ('${scopeId}')`
				)}`;

	const items = await getTaxonomyItems<KeywordItem>(url, max);

	return items.map((item) => item.name);
}
