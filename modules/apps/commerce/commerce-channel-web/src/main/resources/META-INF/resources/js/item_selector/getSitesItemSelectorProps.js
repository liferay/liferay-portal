/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';

export default function getSitesItemSelectorProps({
	commerceChannelId,
	portletNamespace,
}) {
	return {
		apiURL: `/o/headless-commerce-admin-channel/v1.0/channels/${commerceChannelId}/available-sites`,
		fdsProps: {
			configInURLBehavior: EConfigInURLBehavior.OFF,
			id: `${portletNamespace}sitesItemSelector`,
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
								fieldName: 'friendlyUrlPath',
								label: Liferay.Language.get('friendly-url'),
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
		size: 'lg',
		title: sub(
			Liferay.Language.get('select-x'),
			Liferay.Language.get('site')
		),
	};
}
