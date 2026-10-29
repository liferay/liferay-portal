/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

import confirmAndUnscheduleLaunchAction from '../actions/confirmAndUnscheduleLaunchAction';
import {Launch, isPublished} from '../api/launches';
import {
	WORKFLOW_STATUS_DRAFT,
	WORKFLOW_STATUS_SCHEDULED,
} from '../components/WorkflowStatusLabel';
import openNewLaunchModal from '../modals/openNewLaunchModal';
import fillViewLaunchURL from '../util/fillViewLaunchURL';

interface CreationMenu {
	primaryItems: Array<{[key: string]: unknown}>;
}

interface ItemsAction {
	data?: {id?: string};
	[key: string]: unknown;
}

interface Props {
	additionalProps: {unscheduleLaunchURL: string; viewLaunchURL: string};
	creationMenu?: CreationMenu;
	itemsActions?: ItemsAction[];
	[key: string]: unknown;
}

export default function LaunchesFDSPropsTransformer({
	additionalProps,
	creationMenu,
	itemsActions,
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
					component: ({itemData}: {itemData: Launch}) => {
						if (isPublished(itemData)) {
							return (
								<ClayLabel displayType="success">
									{Liferay.Language.get('published')}
								</ClayLabel>
							);
						}

						if (
							itemData.scheduledDate &&
							itemData.status?.code === WORKFLOW_STATUS_DRAFT
						) {
							return (
								<ClayLabel displayType="danger">
									{Liferay.Language.get('failed')}
								</ClayLabel>
							);
						}

						if (
							itemData.status?.code === WORKFLOW_STATUS_SCHEDULED
						) {
							return (
								<ClayLabel displayType="info">
									{Liferay.Language.get('scheduled')}
								</ClayLabel>
							);
						}

						return (
							<ClayLabel displayType="info">
								{Liferay.Language.get('in-progress')}
							</ClayLabel>
						);
					},
					name: 'launchStatus',
					type: 'internal',
				},
			],
		},
		itemsActions: itemsActions?.map((action) => {
			if (action.data?.id === 'unschedule') {
				return {
					...action,
					isVisible: (launch: Launch) =>
						launch.status?.code === WORKFLOW_STATUS_SCHEDULED,
				};
			}

			return action;
		}),
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: ItemsAction;
			itemData: Launch;
			loadData: () => void;
		}) {
			if (action.data?.id === 'unschedule') {
				confirmAndUnscheduleLaunchAction({
					loadData,
					unscheduleLaunchURL: fillViewLaunchURL(
						additionalProps.unscheduleLaunchURL,
						itemData
					),
				});
			}
		},
	};
}
