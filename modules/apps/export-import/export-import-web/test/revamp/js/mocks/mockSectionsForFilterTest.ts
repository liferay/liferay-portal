/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PreviewPortletDataHandlerSection} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';

export const mockPreviewPortletDataHandlerSectionsForFilterTest: PreviewPortletDataHandlerSection[] =
	[
		{
			additionCount: 5,
			deletionCount: 0,
			label: 'Additions Only',
			name: 'additions-only',
			previewPortletDataHandlers: [],
		},
		{
			additionCount: 0,
			deletionCount: 3,
			label: 'Deletions Only',
			name: 'deletions-only',
			previewPortletDataHandlers: [],
		},
		{
			additionCount: 2,
			deletionCount: 4,
			label: 'Both',
			name: 'both',
			previewPortletDataHandlers: [],
		},
		{
			label: 'No Counts',
			name: 'no-counts',
			previewPortletDataHandlers: [],
		},
	];
