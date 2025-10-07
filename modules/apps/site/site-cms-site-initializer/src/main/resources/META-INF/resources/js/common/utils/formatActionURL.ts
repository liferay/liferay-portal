/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getLocalizedValue from './getLocalizedValue';

// based on FDS code
// https://github.com/liferay/liferay-portal/blob/6c07bf39568cc6334f88c5e1925a521fb3816fa9/modules/apps/frontend-data-set/frontend-data-set-web/src/main/resources/META-INF/resources/utils/actionItems/formatActionURL.ts#L29

function getValueFromItem(fieldName: string | string[], item: any) {
	if (Array.isArray(fieldName)) {
		return fieldName.reduce((acc, key) => {
			if (key === 'LANG') {
				return (
					getLocalizedValue(
						acc,
						Liferay.ThemeDisplay.getLanguageId()
					) || getLocalizedValue(acc)
				);
			}

			return acc[key];
		}, item);
	}

	return item[fieldName];
}

export default function formatActionURL(item: any, url: string) {
	if (!item) {
		return url;
	}

	return url.replace(/(?:%7B|{)(.*?)(?:%7D|})/g, (match, key) =>
		encodeURIComponent(getValueFromItem(key.split('.'), item))
	);
}
