/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const activityLogFixture = {
	userSessions: {
		totalEvents: 3,
		userSessions: [
			{
				userName: 'John Doe',
				userSessionEvents: [
					{
						assetTitle: 'document_a',
						createDate: '2026-03-06T08:00:00Z',
						emailAddressHashed: 'john.doe@example.com',
						name: 'commentPosted',
					},
					{
						assetTitle: 'document_b',
						createDate: '2026-03-06T09:00:00Z',
						emailAddressHashed: 'john.doe@example.com',
						name: 'documentUploaded',
					},
				],
			},
			{
				userName: 'Paul Gerome',
				userSessionEvents: [
					{
						assetTitle: 'document_c',
						createDate: '2026-03-07T10:00:00Z',
						emailAddressHashed: 'paul.gerome@example.com',
						name: 'pageViewed',
					},
				],
			},
			{
				userSessionEvents: [
					{
						assetTitle: 'document_d',
						createDate: '2026-03-07T11:00:00Z',
						emailAddressHashed: 'unknown@example.com',
						name: 'documentDownloaded',
					},
				],
			},
		],
	},
};
