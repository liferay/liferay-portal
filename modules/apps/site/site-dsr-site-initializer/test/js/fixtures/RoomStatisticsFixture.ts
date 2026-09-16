/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const roomStatisticsFixture = {
	identityActivity: {count: 10},
	identityActivityToday: {count: 3},
	identityComment: {count: 5},
	identityCommentToday: {count: 2},
	siteVisitorBehavior: {
		knownVisitors: 20,
		totalSessionDuration: 45 * 60000,
		visitors: 100,
	},
	siteVisitorBehaviorToday: {
		knownVisitors: 4,
		totalSessionDuration: 10 * 60000,
		visitors: 8,
	},
};
