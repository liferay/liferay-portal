/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {IBulkActionFDSData} from '../../../common/types/BulkActionTask';
import {openBulkActionConfirmationModal} from '../../../common/utils/openBulkActionConfirmationModal';
import {triggerAssetBulkAction} from './triggerAssetBulkAction';

function getDuplicateConfirmation(selectedData: IBulkActionFDSData): {
	message: string;
	title: string;
} {
	const title = Liferay.Language.get('duplicate-items');

	if (selectedData.selectAll) {
		return {
			message: Liferay.Language.get('duplicate-all-items-confirmation'),
			title,
		};
	}

	const count = selectedData.items?.length ?? 0;

	if (count > 1) {
		return {
			message: sub(
				Liferay.Language.get('duplicate-x-items-confirmation'),
				count
			),
			title,
		};
	}

	return {
		message: Liferay.Language.get('duplicate-item-confirmation'),
		title,
	};
}

export default function duplicateBulkAction({
	apiURL = '',
	dataSetId = '',
	selectedData,
}: {
	apiURL?: string;
	dataSetId?: string;
	selectedData: IBulkActionFDSData;
}): void {
	const {message, title} = getDuplicateConfirmation(selectedData);

	openBulkActionConfirmationModal({
		confirmLabel: Liferay.Language.get('duplicate'),
		message,
		onConfirm: () => {
			triggerAssetBulkAction({
				apiURL,
				dataSetId,
				selectedData,
				type: 'DuplicateObjectBulkSelectionAction',
			});
		},
		status: 'info',
		title,
	});
}
