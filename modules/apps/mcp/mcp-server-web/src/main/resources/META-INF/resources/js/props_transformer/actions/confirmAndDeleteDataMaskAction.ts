/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import {deleteDataMask} from '../../services/deleteDataMask';
import {getProfileDataMasks} from '../../services/getProfileDataMasks';
import {ActionContext} from '../../types';
import {openErrorToast, openSuccessToast} from '../../utils';

async function getAssociatedProfilesCount(
	dataMaskExternalReferenceCode?: string
): Promise<number | null> {
	if (!dataMaskExternalReferenceCode) {
		return 0;
	}

	const {data, error} = await getProfileDataMasks(
		dataMaskExternalReferenceCode
	);

	if (error) {
		openErrorToast(error);

		return null;
	}

	return data?.totalCount ?? 0;
}

function deleteBody(associatedProfilesCount: number) {
	if (!associatedProfilesCount) {
		return Liferay.Language.get('are-you-sure-you-want-to-delete-this');
	}

	return [
		Liferay.Util.sub(
			Liferay.Language.get(
				'this-mask-is-currently-being-used-in-x-profile-s-deleting-it-will-remove-the-masking-rules-from-all-of-them-and-matching-values-will-no-longer-be-masked-in-incoming-data'
			),
			String(associatedProfilesCount)
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

	const associatedProfilesCount = await getAssociatedProfilesCount(
		externalReferenceCode
	);

	if (associatedProfilesCount === null) {
		return;
	}

	openModal({
		bodyHTML: deleteBody(associatedProfilesCount),
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
		title: Liferay.Language.get('delete-data-mask'),
	});
}
