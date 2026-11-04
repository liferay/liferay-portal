/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';

const ACCOUNT_GROUPS_API_URL = '/o/headless-admin-user/v1.0/account-groups';

function buildAPIURL(excludedAccountGroupIds) {
	const url = new URL(ACCOUNT_GROUPS_API_URL, window.location.origin);

	if (excludedAccountGroupIds.length) {
		url.searchParams.set(
			'filter',
			`not (id in (${excludedAccountGroupIds.join(',')}))`
		);
	}

	return url.toString();
}

export default function getAccountGroupsItemSelectorProps({
	excludedAccountGroupIds = [],
	portletNamespace,
}) {
	return {
		apiURL: buildAPIURL(excludedAccountGroupIds),
		fdsProps: {
			configInURLBehavior: EConfigInURLBehavior.OFF,
			id: `${portletNamespace}accountGroupsItemSelector`,
			pagination: {
				deltas: [{label: 20}, {label: 40}, {label: 60}],
				initialDelta: 20,
			},
			views: [
				{
					contentRenderer: 'table',
					label: '',
					name: 'table',
					schema: {
						fields: [
							{
								fieldName: 'name',
								label: Liferay.Language.get('name'),
							},
							{
								fieldName: 'description',
								label: Liferay.Language.get('description'),
							},
						],
					},
				},
			],
		},
		locator: {
			id: 'id',
			label: 'name',
			value: 'id',
		},
		multiSelect: true,
		size: 'lg',
		title: Liferay.Language.get('select-account-group'),
	};
}
