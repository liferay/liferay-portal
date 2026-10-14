/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FDS_EVENT_UPDATE_DISPLAY} from '../../../common/utils/constants';
import {openCMSModal} from '../../../common/utils/openCMSModal';
import {executeAsyncItemAction} from '../utils/executeAsyncItemAction';

export default function confirmAndDeleteEntryAction({
	bodyHTML,
	confirmButtonLabel = Liferay.Language.get('delete'),
	dataSetId,
	deleteAction,
	loadData,
	status = 'danger',
	successMessage,
	title,
}: {
	bodyHTML: string;
	confirmButtonLabel?: string;
	dataSetId?: string;
	deleteAction: {href: string; method: string};
	loadData: () => void;
	status?: 'danger' | 'warning';
	successMessage: string;
	title: string;
}) {
	openCMSModal({
		bodyHTML,
		buttons: [
			{
				autoFocus: true,
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				displayType: status,
				label: confirmButtonLabel,
				onClick: ({processClose}: {processClose: () => void}) => {
					processClose();

					executeAsyncItemAction({
						method: deleteAction.method,
						refreshData: () => {
							loadData();

							if (dataSetId) {
								Liferay.fire(FDS_EVENT_UPDATE_DISPLAY, {
									id: dataSetId,
								});
							}
						},
						successMessage,
						url: deleteAction.href,
					});
				},
			},
		],
		center: true,
		role: 'alert',
		status,
		title,
	});
}
