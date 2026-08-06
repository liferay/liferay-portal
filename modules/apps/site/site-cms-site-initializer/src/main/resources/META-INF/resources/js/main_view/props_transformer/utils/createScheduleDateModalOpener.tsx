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

export interface ScheduleDateModalConfig {
	apiURL?: string;
	bulkActionType: keyof IBulkActionType;
	dataSetId?: string;
	keyValuesKey: string;
	modalFieldLabel: string;
	modalFieldName: string;
	modalNeverLabel: string;
	modalSaveRequirementLabel: string;
	modalTitle: string;
}

type ScheduleDateActionId = 'update-expiration-date' | 'update-review-date';

type ScheduleDateItemData = {
	embedded?: {expirationDate?: string; reviewDate?: string};
};

const SCHEDULE_DATE_ACTIONS: Record<
	ScheduleDateActionId,
	{
		getItemDate: (itemData: ScheduleDateItemData) => string | undefined;
	} & Omit<ScheduleDateModalConfig, 'apiURL' | 'dataSetId'>
> = {
	'update-expiration-date': {
		bulkActionType: 'UpdateExpirationDateObjectBulkSelectionAction',
		getItemDate: (itemData) => itemData.embedded?.expirationDate,
		keyValuesKey: 'expirationDate',
		modalFieldLabel: Liferay.Language.get('expiration-date'),
		modalFieldName: 'expirationDate',
		modalNeverLabel: Liferay.Language.get('never-expire'),
		modalSaveRequirementLabel: Liferay.Language.get(
			'enter-an-expiration-date-or-select-never-expire-to-enable-the-save-button'
		),
		modalTitle: Liferay.Language.get('update-expiration-date'),
	},
	'update-review-date': {
		bulkActionType: 'UpdateReviewDateObjectBulkSelectionAction',
		getItemDate: (itemData) => itemData.embedded?.reviewDate,
		keyValuesKey: 'reviewDate',
		modalFieldLabel: Liferay.Language.get('review-date'),
		modalFieldName: 'reviewDate',
		modalNeverLabel: Liferay.Language.get('never-review'),
		modalSaveRequirementLabel: Liferay.Language.get(
			'enter-a-review-date-or-select-never-review-to-enable-the-save-button'
		),
		modalTitle: Liferay.Language.get('update-review-date'),
	},
};

export function isScheduleDateActionId(
	actionId?: string
): actionId is ScheduleDateActionId {
	return Boolean(actionId && actionId in SCHEDULE_DATE_ACTIONS);
}

export function openScheduleDateModal({
	actionId,
	apiURL,
	dataSetId,
	itemData,
	selectedData,
}: {
	actionId: ScheduleDateActionId;
	apiURL?: string;
	dataSetId?: string;
	itemData?: ScheduleDateItemData;
	selectedData?: IBulkActionFDSData;
}) {
	const {getItemDate, ...config} = SCHEDULE_DATE_ACTIONS[actionId];

	const openModal = createScheduleDateModalOpener({
		...config,
		apiURL,
		dataSetId,
	});

	if (itemData) {
		return openModal(
			{
				items: [itemData as ISearchAssetObjectEntry],
				selectAll: false,
			},
			getItemDate(itemData)
		);
	}

	return openModal(selectedData as IBulkActionFDSData);
}

export default function createScheduleDateModalOpener({
	apiURL,
	bulkActionType,
	dataSetId,
	keyValuesKey,
	modalFieldLabel,
	modalFieldName,
	modalNeverLabel,
	modalSaveRequirementLabel,
	modalTitle,
}: ScheduleDateModalConfig) {
	return (selectedData: IBulkActionFDSData, date?: string) =>
		openCMSModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) => (
				<ScheduleDateModalContent
					closeModal={closeModal}
					date={date}
					fieldLabel={modalFieldLabel}
					fieldName={modalFieldName}
					neverLabel={modalNeverLabel}
					onSave={async (newDate: string) => {
						triggerAssetBulkAction({
							apiURL,
							dataSetId,
							keyValues: newDate ? {[keyValuesKey]: newDate} : {},
							selectedData,
							type: bulkActionType,
						});

						return true;
					}}
					saveRequirementLabel={modalSaveRequirementLabel}
					title={modalTitle}
				/>
			),
			size: 'md',
		});
}
