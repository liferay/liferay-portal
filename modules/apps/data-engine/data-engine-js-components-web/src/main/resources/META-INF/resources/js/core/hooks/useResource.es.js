/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useResource} from '@clayui/data-provider';
import {fetch} from 'frontend-js-web';

import {useStorage} from './useStorage.es';

const ENDPOINT_FIELD_TYPES = `${
	window.location.origin
}${themeDisplay.getPathContext()}/o/dynamic-data-mapping-form-field-types`;

const HEADERS = {
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
};

export function useFieldTypesResource() {
	const storage = useStorage();

	return useResource({
		fetch: (url, options) => fetch(url, options).then((res) => res.json()),
		fetchOptions: {
			headers: HEADERS,
		},
		fetchPolicy: 'cache-first',
		link: ENDPOINT_FIELD_TYPES,
		storage,
		variables: {
			p_auth: Liferay.authToken,
		},
	});
}
