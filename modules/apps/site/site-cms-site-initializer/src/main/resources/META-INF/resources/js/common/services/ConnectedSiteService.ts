/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Site} from '../types/Site';
import ApiHelper from './ApiHelper';

async function connectSiteToSpace(
	externalReferenceCode: string,
	siteExternalReferenceCode: string,
	searchable?: string
) {
	return await ApiHelper.put<Site>(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}/connected-sites/by-external-reference-code/${siteExternalReferenceCode}`,
		{
			searchable: searchable ? searchable : 'true',
		}
	);
}

async function disconnectSiteFromSpace(
	externalReferenceCode: string,
	siteExternalReferenceCode: string
) {
	return await ApiHelper.delete(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}/connected-sites/by-external-reference-code/${siteExternalReferenceCode}`
	);
}

async function getConnectedSitesFromSpace(externalReferenceCode: string) {
	return await ApiHelper.get<{items: Site[]}>(
		`/o/headless-asset-library/v1.0/asset-libraries/by-external-reference-code/${externalReferenceCode}/connected-sites`
	);
}

async function getAllSites() {
	return await ApiHelper.get<{items: Site[]}>(
		`/o/headless-site/v1.0/sites?active=true`
	);
}

export default {
	connectSiteToSpace,
	disconnectSiteFromSpace,
	getAllSites,
	getConnectedSitesFromSpace,
};
