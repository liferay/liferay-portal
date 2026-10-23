/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {escapeHTML, sub} from 'frontend-js-web';

import ProfileToolService from '../../services/ProfileToolService';
import {ProfileToolActionContext} from '../../types/ProfileTool';
import {openErrorToast, openSuccessToast} from '../../utils/toasts';

export default function confirmAndRemoveProfileToolAction({
	itemData,
	loadData,
}: ProfileToolActionContext) {
	const {externalReferenceCode, toolName} = itemData;

	openModal({
		bodyHTML: `
			<p>
				${sub(
					Liferay.Language.get(
						'this-will-remove-x-from-this-profile'
					),
					`<strong>${escapeHTML(toolName)}</strong>`
				)}
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
				label: Liferay.Language.get('remove'),
				onClick: async ({processClose}: {processClose: () => void}) => {
					processClose();

					const {error} = await ProfileToolService.deleteProfileTool(
						externalReferenceCode
					);

					if (error) {
						openErrorToast(error);

						return;
					}

					loadData();

					openSuccessToast(
						sub(
							Liferay.Language.get('x-was-removed-successfully'),
							toolName
						)
					);
				},
			},
		],
		status: 'danger',
		title: Liferay.Language.get('remove-tool'),
	});
}
