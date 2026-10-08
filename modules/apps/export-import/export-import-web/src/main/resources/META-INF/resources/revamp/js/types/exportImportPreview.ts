/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PortletDataHandler} from './portletDataHandler';

export interface Preview {
	additionCount: number;
	deletionCount: number;
	portletDataHandlers: PortletDataHandler[];
}

export type GroupType = 'assetLibrary' | 'site';

export interface ImportPreview {
	additionCount: number;
	author: string;
	deletionCount: number;
	exportDate: string;
	fileName: string;
	fileSize: number;
	portletDataHandlers: PortletDataHandler[];
	previewAssetLibraries?: PreviewAssetLibrary[];
	sites?: Site[];
}

export interface PreviewAssetLibrary {
	descriptiveName?: string;
	existsInInstance?: boolean;
	externalReferenceCode: string;
}

export interface Site {
	childSitesCount?: number;
	descriptiveName?: string;
	existsInInstance?: boolean;
	externalReferenceCode: string;
	path?: string;
}
