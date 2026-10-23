/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {escapeHTML, sub} from 'frontend-js-web';

import ProfileService from '../../services/ProfileService';
import {ProfileActionContext} from '../../types/Profile';
import {openErrorToast, openSuccessToast} from '../../utils/toasts';

export default function confirmAndDeleteProfileAction({
	itemData,
	loadData,
}: ProfileActionContext) {
	const {externalReferenceCode, name} = itemData;

	if (!externalReferenceCode) {
		return;
	}

	openModal({
		bodyHTML: `
			<p>
				${sub(
					Liferay.Language.get(
						'this-will-permanently-delete-x-profile-from-your-mcp-server-configuration'
					),
					`<strong>${escapeHTML(name)}</strong>`
				)}
				${Liferay.Language.get('anyone-using-this-profile-will-lose-access-immediately')}
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

					const {error} = await ProfileService.deleteProfile(
						externalReferenceCode
					);

					if (error) {
						openErrorToast(error);

						return;
					}

					loadData();

					openSuccessToast(
						sub(
							Liferay.Language.get('x-was-deleted-successfully'),
							name
						)
					);
				},
			},
		],
		status: 'danger',
		title: Liferay.Language.get('delete-mcp-profile'),
	});
}
