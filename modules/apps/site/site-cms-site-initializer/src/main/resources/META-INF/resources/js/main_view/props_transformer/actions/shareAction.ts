/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal, openToast} from 'frontend-js-components-web';

import CollaboratorService from '../../../common/services/CollaboratorService';
import ShareModalContent, {
	Collaborator,
} from '../../modal/share_modal_content/ShareModalContent';

export default async function shareAction({
	autocompleteURL,
	collaboratorURL,
	creator,
	itemId,
	title,
}: {
	autocompleteURL: string;
	collaboratorURL: string;
	creator: {
		contentType: string;
		id: number;
		image?: string;
		name: string;
	};
	itemId: number;
	title: string;
}) {
	try {
		const items = await CollaboratorService.getCollaborators(
			collaboratorURL,
			itemId
		);

		const initialCollaborators: Collaborator[] = items.reverse().map(
			({actionIds, dateExpired, id, name, portrait, share, type}) =>
				({
					actionIds: actionIds.sort().join(','),
					dateExpired,
					share,
					type,
					user: {
						id: id.toString(),
						image: portrait,
						name,
					},
				}) as Collaborator
		);

		openModal({
			className: 'share-modal',
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				ShareModalContent({
					autocompleteURL,
					closeModal,
					collaboratorURL,
					creator: {...creator, id: creator.id.toString()},
					initialCollaborators,
					itemId,
					title,
				}),
			size: 'md',
		});
	}
	catch (error: any) {
		openToast({
			message:
				error.message ||
				Liferay.Language.get('an-unexpected-error-occurred'),
			type: 'danger',
		});
	}
}
