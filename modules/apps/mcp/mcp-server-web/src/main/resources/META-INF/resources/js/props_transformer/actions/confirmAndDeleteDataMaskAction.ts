/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal, openToast} from 'frontend-js-components-web';

import {deleteDataMask} from '../../services/deleteDataMask';
import {getProfile} from '../../services/getProfile';
import {getProfileDataMasks} from '../../services/getProfileDataMasks';
import {ActionContext} from '../../types';

const DATA_MASK_FK_FIELD = 'r_dataMaskToProfileDataMasks_mcpServerDataMaskId';

async function getProfileNames(dataMaskId: number): Promise<string[]> {
	const {data} = await getProfileDataMasks();

	const profileIds = (data?.items ?? [])
		.filter((item) => item[DATA_MASK_FK_FIELD] === dataMaskId)
		.map((item) => item.mcpServerProfileId)
		.filter((profileId): profileId is number => Boolean(profileId));

	if (!profileIds.length) {
		return [];
	}

	const names = await Promise.all(
		profileIds.map(async (profileId) => {
			const {data: profile} = await getProfile(profileId);

			return profile?.name ?? null;
		})
	);

	return names.filter((name): name is string => Boolean(name));
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
	const {id, name} = itemData;

	if (id === undefined) {
		return;
	}

	const profileNames = await getProfileNames(id);

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
							name
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
