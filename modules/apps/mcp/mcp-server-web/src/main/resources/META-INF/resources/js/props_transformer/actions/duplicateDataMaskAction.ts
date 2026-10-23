/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import DataMaskService from '../../services/DataMaskService';
import {DataMaskActionContext} from '../../types/DataMask';
import {openErrorToast, openSuccessToast} from '../../utils/toasts';

export default async function duplicateDataMaskAction({
	itemData,
	loadData,
}: DataMaskActionContext) {
	const {data: saved, error} = await DataMaskService.postDataMask({
		description: itemData.description ?? '',
		detectionRegex: itemData.detectionRegex,
		maskType: {key: 'custom'},
		name: sub(Liferay.Language.get('copy-of-x'), itemData.name),
		replacementRegex: itemData.replacementRegex ?? '',
		replacementValue: itemData.replacementValue,
	});

	if (error) {
		openErrorToast(error);

		return;
	}

	loadData();

	if (saved) {
		openSuccessToast(
			sub(Liferay.Language.get('x-was-saved-successfully'), saved.name)
		);
	}
}
