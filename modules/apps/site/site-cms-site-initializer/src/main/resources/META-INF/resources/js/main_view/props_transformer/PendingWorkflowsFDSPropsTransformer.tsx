/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import {dateUtils, sub} from 'frontend-js-web';
import React from 'react';

import {getPendingWorkflowTask} from '../../common/services/WorkflowService';
import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import {openActionNotAllowedModal} from '../../common/utils/openActionNotAllowedModal';
import {openCMSModal} from '../../common/utils/openCMSModal';
import {displayErrorToast} from '../../common/utils/toastUtil';
import AssignToModalContent from '../home/modal/AssignToModalContent';
import UpdateDueDateModalContent from '../home/modal/UpdateDueDateModalContent';
import {
	AssetListFDSProps,
	createAssetListFDSPropsBuilder,
} from './utils/createAssetListFDSPropsBuilder';

const getAssetListFDSProps = createAssetListFDSPropsBuilder({
	renderSubtitle: (itemData) => {
		if (!itemData.dateModified) {
			return '--';
		}

		const creatorName = itemData.embedded?.creator?.name;
		const formattedDate = dateUtils.fromNow(
			new Date(itemData.dateModified)
		);

		return creatorName
			? sub(
					Liferay.Language.get('modified-x-by-x'),
					formattedDate,
					creatorName
				)
			: sub(Liferay.Language.get('modified-x'), formattedDate);
	},
	titleRendererName: 'pendingWorkflowTitle',
});

export default function PendingWorkflowsFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: AssetListFDSProps) {
	const loadData = async () => {
		Liferay.fire(FDS_EVENT.UPDATE_DISPLAY, {id: otherProps.id});
	};

	const openWorkflowTaskAction = async (
		actionId: string,
		itemData: ISearchAssetObjectEntry
	) => {
		try {
			const workflowTask = await getPendingWorkflowTask({
				assetClassName: itemData.entryClassName,
				assetPrimaryKey: itemData.embedded.id,
			});

			if (!workflowTask) {
				openActionNotAllowedModal();

				return;
			}

			const workflowTaskId = Number(workflowTask.id);

			if (actionId === 'update-due-date') {
				openCMSModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<UpdateDueDateModalContent
							closeModal={closeModal}
							dueDate={workflowTask.dateDue}
							loadData={loadData}
							workflowTaskId={workflowTaskId}
						/>
					),
					size: 'md',
				});
			}
			else {
				openCMSModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) => (
						<AssignToModalContent
							assignable={actionId === 'assign-to'}
							closeModal={closeModal}
							loadData={loadData}
							workflowTaskId={workflowTaskId}
						/>
					),
					size: 'md',
				});
			}
		}
		catch (error) {
			displayErrorToast(
				error instanceof Error ? error.message : undefined
			);
		}
	};

	return {
		...getAssetListFDSProps({additionalProps, itemsActions, ...otherProps}),
		onActionDropdownItemClick: ({
			action,
			event,
			itemData,
		}: {
			action: {data?: {id?: string}};
			event?: Event;
			itemData: ISearchAssetObjectEntry;
		}) => {
			const actionId = action?.data?.id;

			if (
				actionId === 'assign-to' ||
				actionId === 'assign-to-me' ||
				actionId === 'update-due-date'
			) {
				event?.preventDefault();

				openWorkflowTaskAction(actionId, itemData);
			}
		},
	};
}
