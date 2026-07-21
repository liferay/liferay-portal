/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import {deletePrompt} from '../../services/deletePrompt';
import {PromptActionContext} from '../../types';
import {openErrorToast, openSuccessToast} from '../../utils';

export default function confirmAndDeletePromptAction({
	itemData,
	loadData,
}: PromptActionContext) {
	const {id, name} = itemData;

	if (id === undefined) {
		return;
	}

	openModal({
		bodyHTML: `
			<p>
				${Liferay.Util.sub(
					Liferay.Language.get(
						'this-will-permanently-delete-x-prompt-from-your-mcp-server-configuration'
					),
					`<strong>${Liferay.Util.escapeHTML(name)}</strong>`
				)}
				${Liferay.Language.get('anyone-using-this-prompt-will-lose-access-immediately')}
			</p>
			<p>${Liferay.Language.get('do-you-want-to-proceed')}</p>
		`,
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				onClick: ({processClose}: {processClose: () => void}) =>
					processClose(),
			},
			{
				displayType: 'danger',
				label: Liferay.Language.get('delete'),
				onClick: async ({processClose}: {processClose: () => void}) => {
					processClose();

					const {error} = await deletePrompt(id);

					if (error) {
						openErrorToast(error);

						return;
					}

					loadData();

					openSuccessToast(
						Liferay.Util.sub(
							Liferay.Language.get('x-was-deleted-successfully'),
							name
						)
					);
				},
			},
		],
		status: 'danger',
		title: Liferay.Language.get('delete-mcp-prompt'),
	});
}
