/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import PromptService from '../../services/PromptService';
import {PromptActionContext} from '../../types/Prompt';
import {openErrorToast, openSuccessToast} from '../../utils/toasts';

export default async function duplicatePromptAction({
	itemData,
	loadData,
}: PromptActionContext) {
	const {data: saved, error} = await PromptService.postPrompt({
		description: itemData.description ?? '',
		identifier: `copy-of-${itemData.identifier}`,
		name: sub(Liferay.Language.get('copy-of-x'), itemData.name),
		prompt: itemData.prompt,
		promptStatus: {key: 'inactive'},
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
