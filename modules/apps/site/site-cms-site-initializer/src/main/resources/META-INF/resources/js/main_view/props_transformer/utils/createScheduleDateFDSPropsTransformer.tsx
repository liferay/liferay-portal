/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import {
	IBulkActionFDSData,
	IBulkActionType,
} from '../../../common/types/BulkActionTask';
import {
	AssetListFDSProps,
	createAssetListFDSPropsBuilder,
} from './createAssetListFDSPropsBuilder';
import createScheduleDateModalOpener from './createScheduleDateModalOpener';

interface ScheduleDateFDSConfig {
	actionId: string;
	bulkActionType: keyof IBulkActionType;
	getItemDate: (itemData: ISearchAssetObjectEntry) => string | undefined;
	keyValuesKey: string;
	modalFieldLabel: string;
	modalFieldName: string;
	modalNeverLabel: string;
	modalSaveRequirementLabel: string;
	modalTitle: string;
	renderItemDate: (itemData: ISearchAssetObjectEntry) => React.ReactNode;
	sortKey: string;
	sortLabel: string;
	titleRendererName: string;
}

export function createScheduleDateFDSPropsTransformer(
	config: ScheduleDateFDSConfig
) {
	const getAssetListFDSProps = createAssetListFDSPropsBuilder({
		renderSubtitle: config.renderItemDate,
		titleRendererName: config.titleRendererName,
	});

	return function ScheduleDateFDSPropsTransformer({
		additionalProps,
		itemsActions = [],
		...otherProps
	}: AssetListFDSProps) {
		const {additionalAPIURLParameters} = additionalProps || {};

		const bulkActionAPIURL =
			additionalAPIURLParameters && otherProps.apiURL
				? `${otherProps.apiURL}${
						otherProps.apiURL.includes('?') ? '&' : '?'
					}${additionalAPIURLParameters}`
				: otherProps.apiURL;

		const openScheduleDateModal = createScheduleDateModalOpener({
			apiURL: bulkActionAPIURL,
			bulkActionType: config.bulkActionType,
			dataSetId: otherProps.id,
			keyValuesKey: config.keyValuesKey,
			modalFieldLabel: config.modalFieldLabel,
			modalFieldName: config.modalFieldName,
			modalNeverLabel: config.modalNeverLabel,
			modalSaveRequirementLabel: config.modalSaveRequirementLabel,
			modalTitle: config.modalTitle,
		});

		return {
			...getAssetListFDSProps({
				additionalProps,
				itemsActions,
				...otherProps,
			}),
			onActionDropdownItemClick: ({
				action,
				event,
				itemData,
			}: {
				action: {data?: {id?: string}};
				event?: Event;
				itemData: ISearchAssetObjectEntry;
			}) => {
				if (action?.data?.id === config.actionId) {
					event?.preventDefault();

					openScheduleDateModal(
						{items: [itemData], selectAll: false},
						config.getItemDate(itemData)
					);
				}
			},
			onBulkActionItemClick: ({
				action,
				selectedData,
			}: {
				action: {data?: {id?: string}};
				selectedData: IBulkActionFDSData;
			}) => {
				if (action?.data?.id === config.actionId) {
					openScheduleDateModal(selectedData);
				}
			},
			sorts: [
				{
					active: true,
					direction: 'asc',
					key: config.sortKey,
					label: config.sortLabel,
				},
				{
					direction: 'asc',
					key: 'title',
					label: Liferay.Language.get('title'),
				},
			],
		};
	};
}
