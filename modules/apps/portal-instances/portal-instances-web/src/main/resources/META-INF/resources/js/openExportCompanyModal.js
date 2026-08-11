/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

export default function openExportCompanyModal({onExport}) {
	openModal({
		bodyHTML: Liferay.Language.get(
			'exporting-an-instance-copies-its-data-to-a-new-schema-any-previous-export-must-be-deleted-first'
		),
		buttons: [
			{
				autoFocus: true,
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				displayType: 'primary',
				label: Liferay.Language.get('export'),
				onClick: ({processClose}) => {
					processClose();

					onExport();
				},
			},
		],
		status: 'info',
		title: sub(
			Liferay.Language.get('export-x'),
			Liferay.Language.get('instance')
		),
	});
}
