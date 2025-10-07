/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import cx from 'classnames';
import {sub} from 'frontend-js-web';
import React from 'react';

import {Role} from '../../common/types/Role';
import {UserAccount, UserGroup} from '../../common/types/UserAccount';
import {SpaceMembersPermissionSelect} from './SpaceMembersPermissionSelect';

interface MembersListItemProps {
	assetLibraryCreatorUserId?: string | number;
	currentUserId?: string;
	hasAssignMembersPermission: boolean;
	itemType: 'user' | 'group';
	items: (UserAccount | UserGroup)[];
	onRemoveItem: (item: UserAccount | UserGroup) => Promise<void>;
	onUpdateItemRoles: (item: UserAccount | UserGroup, roles: string[]) => void;
	roles: Role[];
}

export function MembersListItem({
	assetLibraryCreatorUserId,
	currentUserId,
	hasAssignMembersPermission,
	itemType,
	items,
	onRemoveItem,
	onUpdateItemRoles,
	roles,
}: MembersListItemProps) {
	return (
		<>
			{items.map((item) => {
				const isUser = itemType === 'user';
				const isOwner =
					isUser &&
					String(assetLibraryCreatorUserId) === String(item.id);

				const memberRoles = item.roles.map((r) => r.name);
				const selectedRoles = memberRoles.length ? memberRoles : [];
				const classes = cx(
					'align-items-center d-flex justify-content-between',
					{
						'c-pt-2 c-pb-2': isOwner,
					}
				);

				const renderGroupCount = () => {
					if (!isUser) {
						const userGroup = item as UserGroup;
						const groupCount = userGroup.numberOfUserAccounts || 0;

						return (
							<span className="ml-1">{`(${Liferay.Util.sub(
								Liferay.Language.get('x-members'),
								groupCount
							)})`}</span>
						);
					}
				};

				return (
					<li className={classes} key={item.id}>
						<div className="align-items-center d-flex">
							<ClaySticker
								displayType="primary"
								shape="circle"
								size="sm"
							>
								{isUser ? (
									<img
										alt={item.name}
										className="sticker-img"
										src={
											(item as UserAccount).image ||
											'/image/user_portrait'
										}
									/>
								) : (
									<ClayIcon
										className="text-secondary"
										fontSize="24px"
										symbol="users"
									/>
								)}
							</ClaySticker>

							<span className="ml-2 text-truncate">
								{item.name}
							</span>

							{isUser && currentUserId === String(item.id) && (
								<span className="ml-1 text-lowercase text-secondary">
									({Liferay.Language.get('you')})
								</span>
							)}

							{renderGroupCount()}
						</div>

						{isOwner ? (
							<span className="text-3 text-capitalize text-secondary">
								({Liferay.Language.get('owner')})
							</span>
						) : hasAssignMembersPermission ? (
							<div className="align-items-center c-gap-2 d-flex">
								<SpaceMembersPermissionSelect
									onChange={(newRoles) =>
										onUpdateItemRoles(item, newRoles)
									}
									roles={roles}
									selectedRoles={selectedRoles}
								/>

								<ClayButtonWithIcon
									aria-label={sub(
										Liferay.Language.get('remove-x'),
										isUser
											? Liferay.Language.get('user')
											: Liferay.Language.get('group')
									)}
									borderless
									displayType="secondary"
									onClick={async () => {
										await onRemoveItem(item);
									}}
									symbol="times-circle"
									translucent
								/>
							</div>
						) : null}
					</li>
				);
			})}
		</>
	);
}
