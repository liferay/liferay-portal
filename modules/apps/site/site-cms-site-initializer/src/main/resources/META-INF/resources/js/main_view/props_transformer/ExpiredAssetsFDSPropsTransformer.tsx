/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import {IBulkActionFDSData} from '../../common/types/BulkActionTask';
import {formatExpirationDate} from '../../common/utils/expirationStatus';
import {openCMSModal} from '../../common/utils/openCMSModal';
import ScheduleDateModalContent from '../modal/ScheduleDateModalContent';
import {triggerAssetBulkAction} from './actions/triggerAssetBulkAction';
import {getFileMimeTypeObjectDefinitionStickerValue} from './utils/transformViewsItemProps';

interface AdditionalProps {
	additionalAPIURLParameters?: string;
	fileMimeTypeCssClasses?: Record<string, string>;
	fileMimeTypeIcons?: Record<string, string>;
	objectDefinitionCssClasses?: Record<string, string>;
	objectDefinitionIcons?: Record<string, string>;
}

function ExpiredAssetTitleRenderer({
	itemData,
	value,
}: {
	itemData: {embedded?: {expirationDate?: string}};
	value?: string;
}) {
	return (
		<div className="d-flex flex-column">
			<span>{value}</span>

			<span className="font-weight-normal text-3 text-warning">
				{formatExpirationDate(itemData.embedded?.expirationDate) ??
					'--'}
			</span>
		</div>
	);
}

export default function ExpiredAssetsFDSPropsTransformer({
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

	const openUpdateExpirationDateModal = (
		selectedData: IBulkActionFDSData,
		expirationDate?: string
	) => {
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) => (
				<ScheduleDateModalContent
					closeModal={closeModal}
					date={expirationDate}
					fieldLabel={Liferay.Language.get('expiration-date')}
					fieldName="expirationDate"
					neverLabel={Liferay.Language.get('never-expire')}
					onSave={async (newExpirationDate: string) => {
						triggerAssetBulkAction({
							apiURL: bulkActionAPIURL,
							dataSetId: otherProps.id,
							keyValues: newExpirationDate
								? {expirationDate: newExpirationDate}
								: {},
							selectedData,
							type: 'UpdateExpirationDateObjectBulkSelectionAction',
						});

						return true;
					}}
					title={Liferay.Language.get('update-expiration-date')}
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
					component: ExpiredAssetTitleRenderer,
					name: 'expiredAssetTitle',
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
			if (action?.data?.id === 'update-expiration-date') {
				event?.preventDefault();

				openUpdateExpirationDateModal(
					{items: [itemData], selectAll: false},
					itemData.embedded?.expirationDate
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
			if (action?.data?.id === 'update-expiration-date') {
				openUpdateExpirationDateModal(selectedData);
			}
		},
		sorts: [
			{
				active: true,
				direction: 'asc',
				key: 'dateExpiration',
				label: Liferay.Language.get('expiration-date'),
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
					titleRendererName: 'expiredAssetTitle',
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
}
