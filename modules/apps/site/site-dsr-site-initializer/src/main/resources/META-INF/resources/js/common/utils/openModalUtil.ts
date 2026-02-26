/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelper} from '@liferay/site-cms-site-initializer';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import RoomService from '../services/RoomService';
import {displayErrorToast, displaySuccessToast} from './toastUtil';

const openFDSDeleteConfirmationModal = ({
	bodyHTML,
	itemName,
	loadData,
	title,
	url,
}: {
	bodyHTML: string;
	itemName: string;
	loadData: any;
	title: string;
	url: string;
}) => {
	openModal({
		bodyHTML,
		buttons: [
			{
				autoFocus: true,
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				displayType: 'danger',
				label: Liferay.Language.get('delete'),
				onClick: ({processClose}: {processClose: Function}) => {
					if (url) {
						ApiHelper.delete(url)
							.then(() => {
								processClose();

								displaySuccessToast();

								loadData();
							})
							.catch(() => {
								displayErrorToast();
							});
					}
					else {
						displayErrorToast();
					}
				},
			},
		],
		containerProps: {
			className: '',
		},
		status: 'danger',
		title: sub(title, '"' + itemName + '"'),
	});
};

const openRoomUserAccountDeleteConfirmationModal = ({
	isInvitedMember,
	loadData,
	roomId,
	userId,
}: {
	isInvitedMember?: boolean;
	loadData: () => void;
	roomId: number;
	userId: number;
}) => {
	Liferay.Util.openModal({
		bodyHTML: `<p>${Liferay.Language.get(
			'are-you-sure-you-want-to-remove-this-user'
		)}</p>`,
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				displayType: 'danger',
				label: Liferay.Language.get('remove'),
				onClick: async ({processClose}: {processClose: () => void}) => {
					try {
						if (isInvitedMember) {
							await RoomService.deleteRoomInvitedMember(
								roomId,
								userId
							);
						}
						else {
							await RoomService.deleteRoomUserAccount(
								roomId,
								userId
							);
						}

						Liferay.Util.openToast({
							message: Liferay.Language.get(
								'user-was-removed-successfully'
							),
							type: 'success',
						});

						loadData();
					}
					catch (error) {
						Liferay.Util.openToast({
							message: Liferay.Language.get('an-error-occurred'),
							type: 'danger',
						});
					}
					finally {
						processClose();
					}
				},
			},
		],
		role: 'alert',
		status: 'danger',
		title: Liferay.Language.get('remove-user-from-digital-sales-room'),
	});
};

export {
	openFDSDeleteConfirmationModal,
	openRoomUserAccountDeleteConfirmationModal,
};
