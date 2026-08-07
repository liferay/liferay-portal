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
import {openCMSModal} from '../../../common/utils/openCMSModal';
import ScheduleDateModalContent from '../../modal/ScheduleDateModalContent';
import {triggerAssetBulkAction} from '../actions/triggerAssetBulkAction';
import {
	AssetListFDSProps,
	createAssetListFDSPropsBuilder,
} from './createAssetListFDSPropsBuilder';

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

		const openScheduleDateModal = (
			selectedData: IBulkActionFDSData,
			date?: string
		) => {
			openCMSModal({
				contentComponent: ({closeModal}: {closeModal: () => void}) => (
					<ScheduleDateModalContent
						closeModal={closeModal}
						date={date}
						fieldLabel={config.modalFieldLabel}
						fieldName={config.modalFieldName}
						neverLabel={config.modalNeverLabel}
						onSave={async (newDate: string) => {
							triggerAssetBulkAction({
								apiURL: bulkActionAPIURL,
								dataSetId: otherProps.id,
								keyValues: newDate
									? {[config.keyValuesKey]: newDate}
									: {},
								selectedData,
								type: config.bulkActionType,
							});

							return true;
						}}
						saveRequirementLabel={config.modalSaveRequirementLabel}
						title={config.modalTitle}
					/>
				),
				size: 'md',
			});
		};

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
