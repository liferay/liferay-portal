/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PortletDataHandlerSection} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';

export const mockPortletDataHandlerSectionsForFilterTest: PortletDataHandlerSection[] =
	[
		{
			key: 'additions-only',
			label: 'Additions Only',
			portletDataHandlers: [
				{
					additionCount: 5,
					deletionCount: 0,
					label: 'Additions Only Handler',
					name: 'additions-only-handler',
				},
			],
		},
		{
			key: 'deletions-only',
			label: 'Deletions Only',
			portletDataHandlers: [
				{
					additionCount: 0,
					deletionCount: 3,
					label: 'Deletions Only Handler',
					name: 'deletions-only-handler',
				},
			],
		},
		{
			key: 'both',
			label: 'Both',
			portletDataHandlers: [
				{
					additionCount: 2,
					deletionCount: 4,
					label: 'Both Handler',
					name: 'both-handler',
				},
			],
		},
		{
			key: 'no-counts',
			label: 'No Counts',
			portletDataHandlers: [
				{label: 'No Counts Handler', name: 'no-counts-handler'},
			],
		},
	];
