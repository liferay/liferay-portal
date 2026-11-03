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

export interface ImportPreview {
	additionCount: number;
	assetLibraries?: AssetLibrary[];
	author: string;
	deletionCount: number;
	exportDate: string;
	fileName: string;
	fileSize: number;
	portletDataHandlers: PortletDataHandler[];
	sites?: Site[];
}

export interface Group {
	descriptiveName?: string;
	existsInInstance?: boolean;
	externalReferenceCode: string;
}

export type GroupType = 'site' | 'space';

export type AssetLibraryType =
	| 'AssetLibrary'
	| 'DesignLibrary'
	| 'Project'
	| 'Space';

export interface AssetLibrary extends Group {
	type?: AssetLibraryType;
}

export interface Site extends Group {
	childSitesCount?: number;
	path?: string;
}
