/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openCMSModal} from './openCMSModal';

function getBodyHTML(messages: string[]): string {
	return `
		<div>
			${messages.map((message) => `<p>${message}</p>`).join('')}
		</div>
	`;
}

/**
 * Opens a confirmation modal for a bulk action. Runs onConfirm only when the
 * user confirms; the modal closes before the action is triggered.
 */
export function openBulkActionConfirmationModal({
	confirmDisplayType = 'primary',
	confirmLabel,
	message,
	onConfirm,
	status,
	title,
}: {
	confirmDisplayType?: string;
	confirmLabel: string;
	message: string | string[];
	onConfirm: () => void;
	status?: string;
	title: string;
}): void {
	openCMSModal({
		bodyHTML: getBodyHTML(Array.isArray(message) ? message : [message]),
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				onClick: ({processClose}: {processClose: () => void}) => {
					processClose();
				},
				type: 'cancel',
			},
			{
				displayType: confirmDisplayType,
				label: confirmLabel,
				onClick: ({processClose}: {processClose: () => void}) => {
					processClose();

					onConfirm();
				},
			},
		],
		center: true,
		status,
		title,
	});
}
