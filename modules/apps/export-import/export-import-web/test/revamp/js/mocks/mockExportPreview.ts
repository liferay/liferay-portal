/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ExportPreview} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';
import {mockPreviewPortletDataHandlerSections} from './mockPortletDataHandlerSections';

export const mockExportPreview: ExportPreview = {
	additionCount: 42,
	deletionCount: 5,
	previewPortletDataHandlerSections: mockPreviewPortletDataHandlerSections,
};
