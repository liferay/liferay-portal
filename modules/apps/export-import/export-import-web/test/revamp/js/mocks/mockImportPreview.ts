/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ImportPreview} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';
import {mockPreviewPortletDataHandlerSections} from './mockPortletDataHandlerSections';

export const mockImportPreview: ImportPreview = {
	additionCount: 42,
	author: 'Test User',
	deletionCount: 5,
	exportDate: '2000-07-27T00:00:00Z',
	fileName: 'site.lar',
	fileSize: 4096,
	previewPortletDataHandlerSections: mockPreviewPortletDataHandlerSections,
};
