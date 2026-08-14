/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal} from '@liferay/layout-js-components-web';
import {sub} from 'frontend-js-web';

export default function openDeleteLayoutModal({
	message,
	multiple = false,
	onDelete,
}) {
	openConfirmModal({
		blocking: true,
		buttonLabel: Liferay.Language.get('delete'),
		onConfirm: async () => {
			await onDelete();
		},
		status: 'danger',
		text: message,
		title: sub(
			Liferay.Language.get('delete-x'),
			multiple
				? Liferay.Language.get('pages')
				: Liferay.Language.get('page')
		),
	});
}
