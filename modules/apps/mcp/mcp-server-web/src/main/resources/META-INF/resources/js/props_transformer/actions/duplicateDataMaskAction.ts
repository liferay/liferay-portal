/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';

import {postDataMask} from '../../services/postDataMask';
import {ActionContext} from '../../types';

export default async function duplicateDataMaskAction({
	itemData,
	loadData,
}: ActionContext) {
	const {data: saved, error} = await postDataMask({
		description: itemData.description ?? '',
		detectionRegex: itemData.detectionRegex,
		maskType: {key: 'custom'},
		name: Liferay.Util.sub(
			Liferay.Language.get('copy-of-x'),
			itemData.name
		),
		replacementRegex: itemData.replacementRegex ?? '',
		replacementValue: itemData.replacementValue,
	});

	if (error) {
		openToast({
			message: Liferay.Util.escapeHTML(error),
			type: 'danger',
		});

		return;
	}

	loadData();

	if (saved) {
		openToast({
			message: Liferay.Util.sub(
				Liferay.Language.get('x-was-saved-successfully'),
				Liferay.Util.escapeHTML(saved.name)
			),
			type: 'success',
		});
	}
}
