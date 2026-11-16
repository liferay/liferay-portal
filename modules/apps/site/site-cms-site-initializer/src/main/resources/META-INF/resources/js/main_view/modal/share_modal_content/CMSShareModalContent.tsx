/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import {
	AutocompleteItem,
	Collaborator as GenericCollaborator,
	CollaboratorPayload,
	CollaboratorService,
	PermissionOption,
	ShareModalContent as GenericShareModalContent,
	ShareModalUserAccount,
	ShareModalUserGroup,
	formatDateToISO,
} from 'frontend-js-components-web';
import React from 'react';

import {
	ExternalUser,
	UserAccount,
	UserGroup,
} from '../../../common/types/UserAccount';
import {
	COLLABORATOR_TYPE,
	CollaboratorType,
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
} from '../../../common/utils/constants';
import isEmailAddressValid from '../../../common/utils/isEmailAddressValid';

export interface Collaborator {
	actionIds: string;
	dateExpired?: string;
	error?: string;
	share: boolean;
	toBeShared?: boolean;
	type: CollaboratorType;
	user: ExternalUser | UserAccount | UserGroup;
}

const EXTERNAL_USER_PERMISSION_OPTIONS: PermissionOption[] = [
	{
		label: Liferay.Language.get('view-and-download'),
		value: 'VIEW',
	},
];

const FOLDER_PERMISSION_OPTIONS: PermissionOption[] = [
	{
		label: Liferay.Language.get('view-and-download'),
		value: 'VIEW',
	},
	{
		label: Liferay.Language.get('view-download-and-update'),
		value: ['UPDATE', 'VIEW'].join(','),
	},
];

const PERMISSION_OPTIONS: PermissionOption[] = [
	{
		label: Liferay.Language.get('view-and-download'),
		value: 'VIEW',
	},
	{
		label: Liferay.Language.get('view-download-and-comment'),
		value: ['ADD_DISCUSSION', 'VIEW'].join(','),
	},
	{
		label: Liferay.Language.get('view-download-comment-and-update'),
		value: ['ADD_DISCUSSION', 'UPDATE', 'VIEW'].join(','),
	},
];

export default function CMSShareModalContent({
	autocompleteURL,
	canManageCollaborators = true,
	closeModal,
	collaboratorURL,
	creator,
	entryClassName,
	initialCollaborators,
	itemId,
	title,
}: {
	autocompleteURL: string;
	canManageCollaborators?: boolean;
	closeModal: () => void;
	collaboratorURL: string;
	creator: {
		contentType: string;
		id: string;
		image?: string;
		name: string;
	};
	entryClassName?: string;
	initialCollaborators: Collaborator[];
	itemId: number;
	title: string;
}) {
	const isFolder = entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;

	const visibleCollaborators = initialCollaborators;

	const transformSourceItems = (
		rawItems: any[],
		query: string
	): AutocompleteItem[] => {
		const items: AutocompleteItem[] = rawItems
			.filter((item) => item.embedded)
			.map((item) => {
				if (
					item.entryClassName?.includes(COLLABORATOR_TYPE.USER_GROUP)
				) {
					return {
						type: COLLABORATOR_TYPE.USER_GROUP,
						user: {
							id: item.embedded.id.toString(),
							name: item.embedded.name,
						},
					};
				}

				return {
					type: COLLABORATOR_TYPE.USER,
					user: {
						emailAddress: item.embedded.emailAddress,
						id: item.embedded.id.toString(),
						image: item.embedded.image,
						name: item.embedded.name,
					},
				};
			});

		const trimmedQuery = query.trim();

		const offerExternalUserInvite =
			!isFolder &&
			isEmailAddressValid(trimmedQuery) &&
			!items.some(
				({user}) =>
					(
						user as ShareModalUserAccount
					).emailAddress?.toLowerCase() ===
						trimmedQuery.toLowerCase() ||
					user.id.toLowerCase() === trimmedQuery.toLowerCase()
			);

		if (!offerExternalUserInvite) {
			return items;
		}

		return [
			...items,
			{
				type: COLLABORATOR_TYPE.EXTERNAL_USER,
				user: {
					emailAddress: trimmedQuery,
					id: trimmedQuery,
					name: trimmedQuery,
				},
			} as unknown as AutocompleteItem,
		];
	};

	const mapCollaboratorToPayload = ({
		actionIds,
		dateExpired,
		share,
		type,
		user,
	}: GenericCollaborator): CollaboratorPayload => {
		const payload: CollaboratorPayload = {
			actionIds: actionIds ? actionIds.split(',') : [],
			share,
			type,
		};

		if (dateExpired) {
			payload.dateExpired = formatDateToISO(dateExpired);
		}

		if ((type as CollaboratorType) === COLLABORATOR_TYPE.EXTERNAL_USER) {
			payload.emailAddress = (user as ShareModalUserAccount).emailAddress;
		}
		else {
			payload.id = Number(user.id);
		}

		return payload;
	};

	const handleCollaboratorsUpdate = (collaborators: GenericCollaborator[]) =>
		CollaboratorService.updateCollaborators(
			collaboratorURL,
			itemId,
			collaborators.map(mapCollaboratorToPayload)
		);

	const autocompleteItem = ({
		type,
		user,
	}: {
		type: CollaboratorType;
		user: ShareModalUserAccount | ShareModalUserGroup;
	}) => (
		<div className="autofit-row autofit-row-center">
			<div className="autofit-col c-mr-1">
				<ClaySticker className="sticker-user-icon" size="sm">
					{type === COLLABORATOR_TYPE.USER ? (
						'image' in user && user.image ? (
							<div className="sticker-overlay">
								<img
									alt={user.name}
									className="sticker-img"
									src={user.image}
								/>
							</div>
						) : (
							<ClayIcon symbol="user" />
						)
					) : type === COLLABORATOR_TYPE.EXTERNAL_USER ? (
						<ClayIcon symbol="envelope-closed" />
					) : (
						<ClayIcon symbol="users" />
					)}
				</ClaySticker>
			</div>

			<div className="autofit-col">
				<span className="text-weight-semibold">
					{type === COLLABORATOR_TYPE.EXTERNAL_USER ? (
						<>
							<span className="c-mr-1">
								{Liferay.Language.get('invite-external-user')}
							</span>

							<span className="text-secondary text-weight-normal">
								{user.name}
							</span>
						</>
					) : (
						<>
							<span className="c-mr-1">{user.name}</span>

							{'emailAddress' in user && `(${user.emailAddress})`}
						</>
					)}
				</span>
			</div>
		</div>
	);

	const collaboratorBadgeText = ({
		toBeShared,
		type,
	}: {
		toBeShared?: boolean;
		type: CollaboratorType;
		user: ShareModalUserAccount | ShareModalUserGroup;
	}): string | null => {
		if (type === COLLABORATOR_TYPE.EXTERNAL_USER && !toBeShared) {
			return Liferay.Language.get('invited');
		}

		return toBeShared ? Liferay.Language.get('to-be-shared') : null;
	};

	const collaboratorNameSuffix = ({
		type,
	}: {
		type: CollaboratorType;
		user: ShareModalUserAccount | ShareModalUserGroup;
	}): string | null =>
		type === COLLABORATOR_TYPE.EXTERNAL_USER
			? `(${Liferay.Language.get('guest').toLocaleLowerCase()})`
			: null;

	const collaboratorStickerIcon = ({
		type,
		user,
	}: {
		type: CollaboratorType;
		user: ShareModalUserAccount | ShareModalUserGroup;
	}) => {
		if (type === COLLABORATOR_TYPE.EXTERNAL_USER) {
			return <ClayIcon symbol="envelope-closed" />;
		}

		if (type === COLLABORATOR_TYPE.USER_GROUP) {
			return <ClayIcon symbol="users" />;
		}

		if ('image' in user && user.image) {
			return (
				<img
					alt={user.name}
					className="sticker-img"
					src={(user as ShareModalUserAccount).image}
				/>
			);
		}

		return <ClayIcon symbol="user" />;
	};

	const resolvePermissionOptions = (collaborator: GenericCollaborator) => {
		if (
			(collaborator.type as CollaboratorType) ===
			COLLABORATOR_TYPE.EXTERNAL_USER
		) {
			return EXTERNAL_USER_PERMISSION_OPTIONS;
		}

		if (isFolder) {
			return FOLDER_PERMISSION_OPTIONS;
		}

		return PERMISSION_OPTIONS;
	};

	return (
		<GenericShareModalContent
			alwaysShowPermissionSelector
			autocompleteItem={autocompleteItem}
			autocompleteURL={autocompleteURL}
			canManageCollaborators={canManageCollaborators}
			closeModal={closeModal}
			collaboratorBadgeText={collaboratorBadgeText}
			collaboratorNameSuffix={collaboratorNameSuffix}
			collaboratorStickerIcon={collaboratorStickerIcon}
			creator={creator}
			initialCollaborators={
				visibleCollaborators as unknown as GenericCollaborator[]
			}
			onCollaboratorsUpdate={handleCollaboratorsUpdate}
			permissionOptions={resolvePermissionOptions}
			title={title}
			transformSourceItems={transformSourceItems}
		/>
	);
}
