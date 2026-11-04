/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';

const CHANNELS_API_URL = '/o/headless-commerce-admin-channel/v1.0/channels';

function buildAPIURL(excludedChannelIds) {
	const url = new URL(CHANNELS_API_URL, window.location.origin);

	if (excludedChannelIds.length) {
		url.searchParams.set(
			'filter',
			`not (id in (${excludedChannelIds.join(',')}))`
		);
	}

	return url.toString();
}

export default function getChannelsItemSelectorProps({
	excludedChannelIds = [],
	portletNamespace,
}) {
	return {
		apiURL: buildAPIURL(excludedChannelIds),
		fdsProps: {
			configInURLBehavior: EConfigInURLBehavior.OFF,
			id: `${portletNamespace}channelsItemSelector`,
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
								fieldName: 'type',
								label: Liferay.Language.get('type'),
							},
							{
								fieldName: 'currencyCode',
								label: Liferay.Language.get('currency'),
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
		title: Liferay.Language.get('select-channel'),
	};
}
