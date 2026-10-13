/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

import {Launch, isPublished} from '../api/launches';
import openNewLaunchModal from '../modals/openNewLaunchModal';

interface CreationMenu {
	primaryItems: Array<{[key: string]: unknown}>;
}

interface Props {
	additionalProps: {viewLaunchURL: string};
	creationMenu?: CreationMenu;
	[key: string]: unknown;
}

export default function LaunchesFDSPropsTransformer({
	additionalProps,
	creationMenu,
	...props
}: Props) {
	return {
		...props,
		additionalProps,
		creationMenu: creationMenu && {
			...creationMenu,
			primaryItems: creationMenu.primaryItems.map((primaryItem) => ({
				...primaryItem,
				onClick: () =>
					openNewLaunchModal({
						viewLaunchURL: additionalProps.viewLaunchURL,
					}),
			})),
		},
		customRenderers: {
			tableCell: [
				{
					component: ({itemData}: {itemData: Launch}) =>
						isPublished(itemData) ? (
							<ClayLabel displayType="success">
								{Liferay.Language.get('published')}
							</ClayLabel>
						) : (
							<ClayLabel displayType="info">
								{Liferay.Language.get('in-progress')}
							</ClayLabel>
						),
					name: 'launchStatus',
					type: 'internal',
				},
			],
		},
	};
}
