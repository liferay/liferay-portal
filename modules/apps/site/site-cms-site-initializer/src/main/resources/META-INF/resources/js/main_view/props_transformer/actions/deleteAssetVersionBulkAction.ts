/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import {IBulkActionFDSData} from '../../../common/types/BulkActionTask';
import {openBulkActionConfirmationModal} from '../../../common/utils/openBulkActionConfirmationModal';
import {displayCreateTaskSuccessToast} from '../../../common/utils/toastUtil';
import {getBulkActionTaskMessage} from '../../bulk_actions_monitor/util/notifications';
import {triggerAssetBulkAction} from './triggerAssetBulkAction';

function getBulkDeleteMessage(
	isSingleVersion: boolean,
	selectedData: IBulkActionFDSData,
	objectEntryTitle: string,
	versions: number[]
) {
	const {selectAll} = selectedData;

	if (selectAll) {
		return {
			confirmationMessage: sub(
				Liferay.Language.get('delete-all-versions-confirmation'),
				objectEntryTitle
			),
			title: Liferay.Language.get('delete-versions'),
		};
	}
	else if (isSingleVersion) {
		return {
			confirmationMessage: sub(
				Liferay.Language.get('delete-current-version-info'),
				objectEntryTitle
			),
			title: Liferay.Language.get('delete-version'),
		};
	}
	else if (!versions.length) {
		return {
			confirmationMessage: sub(
				Liferay.Language.get('current-asset-version-cannot-be-deleted'),
				objectEntryTitle
			),
			title: Liferay.Language.get('delete-version'),
		};
	}
	else if (versions.length > 1) {
		return {
			confirmationMessage: sub(
				Liferay.Language.get('delete-versions-confirmation'),
				versions.length,
				objectEntryTitle
			),
			title: Liferay.Language.get('delete-versions'),
		};
	}

	return {
		confirmationMessage: sub(
			Liferay.Language.get('delete-version-confirmation'),
			`<strong>${sub(Liferay.Language.get('version-x'), versions[0])}</strong>`,
			objectEntryTitle
		),
		title: Liferay.Language.get('delete-version'),
	};
}

export default function deleteAssetVersionBulkAction({
	apiURL,
	className,
	classPK,
	dataSetId,
	entryClassName,
	objectEntryCurrentVersion,
	objectEntryTitle,
	objectEntryVersionsCount,
	selectedData,
}: {
	apiURL?: string;
	className: string;
	classPK: string;
	dataSetId?: string;
	entryClassName?: string;
	objectEntryCurrentVersion: number;
	objectEntryTitle: string;
	objectEntryVersionsCount: number;
	selectedData: any;
}) {
	const versions = (selectedData.keyValues || []).filter(
		(version: number) => version !== objectEntryCurrentVersion
	);

	const isSingleVersion = objectEntryVersionsCount === 1;

	const isNotDeletableVersion =
		!selectedData.selectAll && (isSingleVersion || !versions.length);

	const {confirmationMessage, title} = getBulkDeleteMessage(
		isSingleVersion,
		selectedData,
		objectEntryTitle,
		versions
	);

	openBulkActionConfirmationModal({
		confirmDisplayType: isNotDeletableVersion ? 'info' : 'danger',
		confirmLabel: isNotDeletableVersion
			? Liferay.Language.get('ok')
			: Liferay.Language.get('delete'),
		message: confirmationMessage,
		onConfirm: () => {
			if (isNotDeletableVersion) {
				return;
			}

			const type = 'DeleteObjectAssetVersionBulkSelectionAction';

			triggerAssetBulkAction({
				apiURL,
				dataSetId,
				keyValues: {
					className,
					classPK: parseInt(classPK, 10),
					versions,
				},
				onCreateSuccess: () => {
					const message = getBulkActionTaskMessage(
						type,
						'info',
						selectedData
					);

					displayCreateTaskSuccessToast(
						selectedData.selectAll
							? message
							: sub(message, [versions.length || 0])
					);
				},
				overrideDefaultSuccessToast: true,
				selectedData: {
					items: [
						{
							embedded: selectedData.items[0],
							entryClassName,
						} as ISearchAssetObjectEntry,
					],
					selectAll: selectedData.selectAll,
				},
				type,
			});
		},
		status: isNotDeletableVersion ? 'info' : 'danger',
		title,
	});
}
