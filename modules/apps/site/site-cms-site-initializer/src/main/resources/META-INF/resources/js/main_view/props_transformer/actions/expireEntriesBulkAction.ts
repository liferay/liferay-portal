/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {IBulkActionFDSData} from '../../../common/types/BulkActionTask';
import {openBulkActionConfirmationModal} from '../../../common/utils/openBulkActionConfirmationModal';
import {triggerAssetBulkAction} from './triggerAssetBulkAction';

function getExpireConfirmation(selectedData: IBulkActionFDSData): {
	message: string;
	title: string;
} {
	const title = Liferay.Language.get('expire-items');

	if (selectedData.selectAll) {
		return {
			message: Liferay.Language.get('expire-all-items-confirmation'),
			title,
		};
	}

	const count = selectedData.items?.length ?? 0;

	if (count > 1) {
		return {
			message: sub(Liferay.Language.get('expire-x-items-confirmation'), [
				count,
			]),
			title,
		};
	}

	return {
		message: Liferay.Language.get('expire-item-confirmation'),
		title,
	};
}

export default function expireEntriesBulkAction({
	apiURL = '',
	dataSetId = '',
	selectedData,
}: {
	apiURL?: string;
	dataSetId?: string;
	selectedData: IBulkActionFDSData;
}): void {
	const {message, title} = getExpireConfirmation(selectedData);

	openBulkActionConfirmationModal({
		confirmDisplayType: 'danger',
		confirmLabel: Liferay.Language.get('expire'),
		message,
		onConfirm: () => {
			triggerAssetBulkAction({
				apiURL,
				dataSetId,
				selectedData,
				type: 'ExpireObjectBulkSelectionAction',
			});
		},
		status: 'danger',
		title,
	});
}
