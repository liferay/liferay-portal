/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React from 'react';

import {LaunchEntry, deleteLaunchEntry} from '../api/launches';
import LaunchEntryBaseVersionCell from '../renderers/LaunchEntryBaseVersionCell';
import LaunchEntryStatusCell from '../renderers/LaunchEntryStatusCell';
import LaunchEntrySummaryCell, {
	LaunchEntrySummaryField,
} from '../renderers/LaunchEntrySummaryCell';
import LaunchEntryTitleCell from '../renderers/LaunchEntryTitleCell';
import addLaunchEntry, {LaunchEntryTypeProps} from '../util/addLaunchEntry';

interface AdditionalProps {
	getLaunchEntrySummaryURL: string;
	getLaunchEntryVersionsURL: string;
	itemSelectedEventName: string;
	launchEntryTypes: LaunchEntryTypeProps[];
	launchSetId: number;
	published: boolean;
	resolveLaunchEntryURL: string;
}

interface CreationMenu {
	primaryItems: Array<{[key: string]: unknown}>;
}

interface Props {
	additionalProps: AdditionalProps;
	creationMenu?: CreationMenu;
	portletNamespace: string;
	[key: string]: unknown;
}

export default function LaunchEntriesFDSPropsTransformer({
	additionalProps,
	creationMenu,
	portletNamespace,
	...props
}: Props) {
	const summaryCellProps = {
		getLaunchEntrySummaryURL: additionalProps.getLaunchEntrySummaryURL,
		portletNamespace,
	};

	return {
		...props,
		additionalProps,
		creationMenu: creationMenu && {
			...creationMenu,
			primaryItems: creationMenu.primaryItems.map(
				(primaryItem, index) => ({
					...primaryItem,
					onClick: ({loadData}: {loadData: () => void}) =>
						addLaunchEntry({
							itemSelectedEventName:
								additionalProps.itemSelectedEventName,
							launchEntryType:
								additionalProps.launchEntryTypes[index],
							launchSetId: additionalProps.launchSetId,
							onAdd: loadData,
							portletNamespace,
							resolveLaunchEntryURL:
								additionalProps.resolveLaunchEntryURL,
						}),
				})
			),
		},
		customRenderers: {
			tableCell: [
				{
					component: (cellProps: {itemData: LaunchEntry}) => (
						<LaunchEntryTitleCell
							{...cellProps}
							{...summaryCellProps}
						/>
					),
					name: 'launchEntryTitle',
					type: 'internal',
				},
				{
					component: (cellProps: {
						itemData: LaunchEntry;
						loadData: () => void;
					}) => (
						<LaunchEntryBaseVersionCell
							{...cellProps}
							getLaunchEntrySummaryURL={
								additionalProps.getLaunchEntrySummaryURL
							}
							getLaunchEntryVersionsURL={
								additionalProps.getLaunchEntryVersionsURL
							}
							portletNamespace={portletNamespace}
							published={additionalProps.published}
						/>
					),
					name: 'launchEntryBaseVersion',
					type: 'internal',
				},
				...(
					['version', 'author', 'type', 'space', 'modified'] as const
				).map((field: LaunchEntrySummaryField) => ({
					component: (cellProps: {itemData: LaunchEntry}) => (
						<LaunchEntrySummaryCell
							{...cellProps}
							{...summaryCellProps}
							field={field}
						/>
					),
					name: `launchEntry${field[0].toUpperCase()}${field.slice(1)}`,
					type: 'internal',
				})),
				{
					component: (cellProps: {itemData: LaunchEntry}) => (
						<LaunchEntryStatusCell
							{...cellProps}
							{...summaryCellProps}
						/>
					),
					name: 'launchEntryStatus',
					type: 'internal',
				},
			],
		},
		onActionDropdownItemClick({
			action,
			itemData,
			loadData,
		}: {
			action: {data?: {id?: string}};
			itemData: LaunchEntry;
			loadData: () => void;
		}) {
			if (action?.data?.id !== 'delete') {
				return;
			}

			deleteLaunchEntry(itemData.id)
				.then(() => loadData())
				.catch((exception: Error) =>
					openToast({
						message: escapeHTML(exception.message),
						type: 'danger',
					})
				);
		},
		portletNamespace,
	};
}
