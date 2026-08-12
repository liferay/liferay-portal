/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import DesignLibraryService from '../../services/DesignLibraryService';
import {ConfirmationMessage, EntryActions} from '../../types';

type DeletableEntries = Array<{actions?: EntryActions}>;

export default function confirmAndDeleteEntriesAction({
	confirmationMessage: {
		bodyHTML,
		partialSuccessMessage,
		successMessage,
		title,
	},
	items,
	loadData,
}: {
	confirmationMessage: ConfirmationMessage;
	items: DeletableEntries;
	loadData?: () => void;
}) {
	return openModal({
		bodyHTML,
		buttons: [
			{
				autoFocus: true,
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				displayType: 'danger',
				label: Liferay.Language.get('delete'),
				onClick: async ({processClose}: {processClose: () => void}) => {
					processClose();

					const results = await Promise.allSettled(
						items.flatMap((item) =>
							item.actions?.delete
								? DesignLibraryService.remove(
										item.actions.delete
									)
								: []
						)
					);

					const deletedCount = results.filter(
						(result) => result.status === 'fulfilled'
					).length;

					if (deletedCount === items.length) {
						openToast({message: successMessage, type: 'success'});
					}
					else if (deletedCount) {
						openToast({
							message: sub(
								partialSuccessMessage,
								deletedCount,
								items.length
							),
							type: 'warning',
						});
					}
					else {
						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							type: 'danger',
						});
					}

					if (deletedCount) {
						loadData?.();
					}
				},
			},
		],
		role: 'alert',
		status: 'danger',
		title,
	});
}
