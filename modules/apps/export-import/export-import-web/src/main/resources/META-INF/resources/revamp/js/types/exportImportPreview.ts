/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PreviewPortletDataHandlerSection} from './portletDataHandler';

export interface Preview {
	additionCount: number;
	deletionCount: number;
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[];
}

export interface ImportPreview {
	additionCount: number;
	author: string;
	deletionCount: number;
	exportDate: string;
	fileName: string;
	fileSize: number;
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[];
}
