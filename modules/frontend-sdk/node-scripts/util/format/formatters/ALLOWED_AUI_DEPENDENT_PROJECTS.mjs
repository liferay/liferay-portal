/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Projects that are still allowed to declare "alloy-ui" as a dependency while
 * they are being migrated away from it. Do not add new entries.
 */
export default [
	'modules/apps/calendar/calendar-web',
	'modules/apps/portal-search/portal-search-web',
	'modules/dxp/apps/portal-workflow/portal-workflow-kaleo-designer-web',
];
