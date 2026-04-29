/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-calendar-a11y',
	(A) => {
		A.CalendarBase.ATTRS.tabIndex.value = 0;

		A.SchedulerEvent.prototype.EVENT_NODE_TEMPLATE =
			A.SchedulerEvent.prototype.EVENT_NODE_TEMPLATE.replace(
				'<div aria-expanded="false"',
				'<div role="button" aria-expanded="false"'
			);
	},
	'',
	{
		requires: ['aui-scheduler', 'calendar'],
	}
);
