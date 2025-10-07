/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Liferay} from '../../services/liferay';
import {ROUTE_TYPES} from './routeTypes';

const siteURL = Liferay.ThemeDisplay.getLayoutRelativeURL();

const LiferayURL = `${Liferay.ThemeDisplay.getPortalURL()}${siteURL.substring(
	0,
	siteURL.lastIndexOf('/')
)}`;

export const PAGE_ROUTER_TYPES = {
	onboarding: (externalReferenceCode: string) =>
		`${LiferayURL}/${ROUTE_TYPES.onboarding}/#/${externalReferenceCode}`,
	project: (externalReferenceCode: string) =>
		`${LiferayURL}/${ROUTE_TYPES.project}/#/${externalReferenceCode}`,
};
