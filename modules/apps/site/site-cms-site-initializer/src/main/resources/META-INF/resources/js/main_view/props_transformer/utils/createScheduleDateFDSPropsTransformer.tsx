/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import {
	IBulkActionFDSData,
	IBulkActionType,
} from '../../../common/types/BulkActionTask';
import {openCMSModal} from '../../../common/utils/openCMSModal';
import ScheduleDateModalContent from '../../modal/ScheduleDateModalContent';
import {triggerAssetBulkAction} from '../actions/triggerAssetBulkAction';
import {getFileMimeTypeObjectDefinitionStickerValue} from './transformViewsItemProps';

export interface AdditionalProps {
	additionalAPIURLParameters?: string;
	fileMimeTypeCssClasses?: Record<string, string>;
	fileMimeTypeIcons?: Record<string, string>;
	objectDefinitionCssClasses?: Record<string, string>;
	objectDefinitionIcons?: Record<string, string>;
}

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
	function TitleRenderer({
		itemData,
		value,
	}: {
		itemData: ISearchAssetObjectEntry;
		value?: string;
	}) {
		return (
			<div className="d-flex flex-column">
				<span>{value}</span>

				<span className="font-weight-normal text-3">
					{config.renderItemDate(itemData)}
				</span>
			</div>
		);
	}

	return function ScheduleDateFDSPropsTransformer({
		additionalProps,
		itemsActions = [],
		...otherProps
	}: {
		additionalProps: AdditionalProps;
		apiURL?: string;
		id?: string;
		itemsActions?: IItemsActions[];
	}) {
		const {additionalAPIURLParameters, ...remainingAdditionalProps} =
			additionalProps || {};

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
			...otherProps,
			additionalAPIURLParameters,
			additionalProps: remainingAdditionalProps,
			customRenderers: {
				listSection: [
					{
						component: TitleRenderer,
						name: config.titleRendererName,
						type: 'internal',
					} as IInternalRenderer,
				],
			},
			hideManagementBarInEmptyState: true,
			itemsActions,
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
			views: [
				{
					contentRenderer: 'list',
					default: true,
					label: Liferay.Language.get('list'),
					name: 'list',
					schema: {
						description: '',
						sticker: 'sticker',
						symbol: 'symbol',
						title: 'title',
						titleRendererName: config.titleRendererName,
					},
					setItemComponentProps: ({
						item,
						props,
					}: {
						item: ISearchAssetObjectEntry;
						props: Record<string, unknown>;
					}) => ({
						...props,
						item: {
							...item,
							sticker: {
								className:
									getFileMimeTypeObjectDefinitionStickerValue(
										remainingAdditionalProps.fileMimeTypeCssClasses,
										remainingAdditionalProps.objectDefinitionCssClasses ??
											{},
										item
									),
							},
							symbol: getFileMimeTypeObjectDefinitionStickerValue(
								remainingAdditionalProps.fileMimeTypeIcons,
								remainingAdditionalProps.objectDefinitionIcons ??
									{},
								item
							),
						},
					}),
					thumbnail: 'list',
				},
			],
		};
	};
}
