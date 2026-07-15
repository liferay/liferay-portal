/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal, openToast} from 'frontend-js-components-web';

import {deleteDataMask} from '../../services/deleteDataMask';
import {getProfileDataMasks} from '../../services/getProfileDataMasks';
import {ActionContext} from '../../types';

async function getAssociatedProfiles(
	dataMaskExternalReferenceCode?: string
): Promise<string[]> {
	if (!dataMaskExternalReferenceCode) {
		return [];
	}

	const {data} = await getProfileDataMasks();

	return (data?.items ?? [])
		.filter(
			(item) =>
				item.dataMaskExternalReferenceCode ===
				dataMaskExternalReferenceCode
		)
		.map((item) => item.mcpServerProfileExternalReferenceCode)
		.filter((externalReferenceCode): externalReferenceCode is string =>
			Boolean(externalReferenceCode)
		);
}

function deleteBody(profileNames: string[]) {
	if (!profileNames.length) {
		return Liferay.Language.get('are-you-sure-you-want-to-delete-this');
	}

	return [
		Liferay.Util.sub(
			Liferay.Language.get(
				'this-mask-is-currently-being-used-in-x-profile-s-deleting-it-will-remove-the-masking-rules-from-all-of-them-and-matching-values-will-no-longer-be-masked-in-incoming-data'
			),
			String(profileNames.length)
		),
		Liferay.Language.get('are-you-sure-you-want-to-proceed'),
	].join(' ');
}

export default async function confirmAndDeleteDataMaskAction({
	itemData,
	loadData,
}: ActionContext) {
	const {externalReferenceCode, id, name} = itemData;

	if (id === undefined) {
		return;
	}

	const profileNames = await getAssociatedProfiles(externalReferenceCode);

	openModal({
		bodyHTML: deleteBody(profileNames),
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

					const {error} = await deleteDataMask(id);

					if (error) {
						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							type: 'danger',
						});

						return;
					}

					loadData();

					openToast({
						message: Liferay.Util.sub(
							Liferay.Language.get('x-was-deleted-successfully'),
							Liferay.Util.escapeHTML(name)
						),
						type: 'success',
					});
				},
			},
		],
		status: 'danger',
		title: Liferay.Language.get('delete-data-mask'),
	});
}
