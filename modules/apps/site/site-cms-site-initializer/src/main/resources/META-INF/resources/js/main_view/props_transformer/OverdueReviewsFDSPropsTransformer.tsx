/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import {IBulkActionFDSData} from '../../common/types/BulkActionTask';
import {openCMSModal} from '../../common/utils/openCMSModal';
import ScheduleDateModalContent from '../modal/ScheduleDateModalContent';
import {triggerAssetBulkAction} from './actions/triggerAssetBulkAction';
import ReviewDateRenderer from './cell_renderers/ReviewDateRenderer';
import {getFileMimeTypeObjectDefinitionStickerValue} from './utils/transformViewsItemProps';

interface AdditionalProps {
	additionalAPIURLParameters?: string;
	fileMimeTypeCssClasses?: Record<string, string>;
	fileMimeTypeIcons?: Record<string, string>;
	objectDefinitionCssClasses?: Record<string, string>;
	objectDefinitionIcons?: Record<string, string>;
}

function OverdueReviewTitleRenderer({
	itemData,
	value,
}: {
	itemData: {dateReview?: string};
	value?: string;
}) {
	return (
		<div className="d-flex flex-column">
			<span>{value}</span>

			<span className="font-weight-normal text-3">
				<ReviewDateRenderer itemData={itemData} />
			</span>
		</div>
	);
}

export default function OverdueReviewsFDSPropsTransformer({
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

	const openUpdateReviewDateModal = (
		selectedData: IBulkActionFDSData,
		reviewDate?: string
	) => {
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) => (
				<ScheduleDateModalContent
					closeModal={closeModal}
					date={reviewDate}
					fieldLabel={Liferay.Language.get('review-date')}
					fieldName="reviewDate"
					neverLabel={Liferay.Language.get('never-review')}
					onSave={async (newReviewDate: string) => {
						triggerAssetBulkAction({
							apiURL: bulkActionAPIURL,
							dataSetId: otherProps.id,
							keyValues: newReviewDate
								? {reviewDate: newReviewDate}
								: {},
							selectedData,
							type: 'UpdateReviewDateObjectBulkSelectionAction',
						});

						return true;
					}}
					saveRequirementLabel={Liferay.Language.get(
						'enter-a-review-date-or-select-never-review-to-enable-the-save-button'
					)}
					title={Liferay.Language.get('update-review-date')}
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
					component: OverdueReviewTitleRenderer,
					name: 'overdueReviewTitle',
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
			itemData: ISearchAssetObjectEntry & {dateReview?: string};
		}) => {
			if (action?.data?.id === 'update-review-date') {
				event?.preventDefault();

				openUpdateReviewDateModal(
					{items: [itemData], selectAll: false},
					itemData.dateReview
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
			if (action?.data?.id === 'update-review-date') {
				openUpdateReviewDateModal(selectedData);
			}
		},
		sorts: [
			{
				active: true,
				direction: 'asc',
				key: 'dateReview',
				label: Liferay.Language.get('review-date'),
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
					titleRendererName: 'overdueReviewTitle',
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
