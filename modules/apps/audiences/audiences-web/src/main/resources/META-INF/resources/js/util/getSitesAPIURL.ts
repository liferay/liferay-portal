/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

import {Scope} from '../types';

const SITES_ENDPOINT = '/o/headless-admin-site/v1.0/sites';

export function getSitesAPIURL(companyGroupERC: string, scope: Scope): string {
	const searchParams = new URLSearchParams({active: 'true'});

	if (companyGroupERC) {
		searchParams.append('excludedExternalReferenceCodes', companyGroupERC);
	}

	if (scope !== 'all') {
		scope.forEach((site) =>
			searchParams.append(
				'excludedExternalReferenceCodes',
				site.externalReferenceCode
			)
		);
	}

	const pathContext = Liferay.ThemeDisplay.getPathContext() || '';

	return addParams(
		searchParams.toString(),
		`${pathContext}${SITES_ENDPOINT}`
	);
}
