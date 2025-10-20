/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import {executeAsyncItemAction} from '../utils/executeAsyncItemAction';

export default function deleteStructureAction({
	bodyHTML,
	deleteAction,
	loadData,
	successMessage,
	title,
}: {
	bodyHTML: string;
	deleteAction: {href: string; method: string};
	loadData: () => void;
	successMessage: string;
	title: string;
}) {
	openModal({
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
				onClick: ({processClose}) => {
					processClose();

					executeAsyncItemAction({
						method: deleteAction.method,
						refreshData: loadData,
						successMessage,
						url: deleteAction.href,
					});
				},
			},
		],
		role: 'alert',
		status: 'danger',
		title,
	});
}
