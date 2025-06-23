/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const salesEnablementHubCard = document.querySelector(
	'.sales-enablement-hub-card'
);

const userGroupName = await Liferay.Util.fetch(
	`/o/headless-admin-user/v1.0/user-accounts/${Liferay.ThemeDisplay.getUserId()}/user-groups`
)
	.then((response) => response.json())
	.then((data) => {
		return data.items[0].name;
	});

if (userGroupName === 'Employees' || userGroupName === 'Partners') {
	salesEnablementHubCard.classList.remove('hide');
}
