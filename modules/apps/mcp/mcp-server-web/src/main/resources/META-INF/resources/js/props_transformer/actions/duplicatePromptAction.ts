/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {postPrompt} from '../../services/postPrompt';
import {PromptActionContext} from '../../types';
import {openErrorToast, openSuccessToast} from '../../utils';

export default async function duplicatePromptAction({
	itemData,
	loadData,
}: PromptActionContext) {
	const {data: saved, error} = await postPrompt({
		description: itemData.description ?? '',
		name: Liferay.Util.sub(
			Liferay.Language.get('copy-of-x'),
			itemData.name
		),
		prompt: itemData.prompt,
	});

	if (error) {
		openErrorToast(error);

		return;
	}

	loadData();

	if (saved) {
		openSuccessToast(
			Liferay.Util.sub(
				Liferay.Language.get('x-was-saved-successfully'),
				saved.name
			)
		);
	}
}
