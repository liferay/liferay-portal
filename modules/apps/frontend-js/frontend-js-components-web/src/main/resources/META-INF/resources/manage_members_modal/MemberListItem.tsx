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

import {MembersPermissionSelect} from './MembersPermissionSelect';
import {Role, RoleExternalReferenceCode, UserAccount, UserGroup} from './types';

interface MemberListItemProps {
	currentUserId?: string;
	defaultRoleExternalReferenceCode: RoleExternalReferenceCode;
	hasAssignMembersPermission: boolean;
	itemType: 'group' | 'user';
	items: (UserAccount | UserGroup)[];
	onRemoveItem: (item: UserAccount | UserGroup) => Promise<void>;
	onUpdateItemRoles: (item: UserAccount | UserGroup, roles: string[]) => void;
	ownerId?: string | number;
	roleNames?: Partial<Record<RoleExternalReferenceCode, string>>;
	roles: Role[];
}

export function MemberListItem({
	currentUserId,
	defaultRoleExternalReferenceCode,
	hasAssignMembersPermission,
	itemType,
	items,
	onRemoveItem,
	onUpdateItemRoles,
	ownerId,
	roleNames,
	roles,
}: MemberListItemProps) {
	return (
		<>
			{items.map((item) => {
				const isUser = itemType === 'user';
				const isCurrentUser =
					isUser && currentUserId === String(item.id);
				const isOwner = isUser && String(ownerId) === String(item.id);

				const defaultRole = roles.find(
					(role) =>
						role.externalReferenceCode ===
						defaultRoleExternalReferenceCode
				);
				const memberRoles = item.roles.map((role) => role.name);
				const selectedRoles = memberRoles.length
					? memberRoles
					: defaultRole
						? [defaultRole.name]
						: [];
				const classes = cx(
					'align-items-center d-flex justify-content-between',
					{
						'c-pt-2 c-pb-2': isOwner || !hasAssignMembersPermission,
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
									<ClaySticker.Image
										alt={item.name}
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

							{isCurrentUser && (
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
								<MembersPermissionSelect
									defaultRoleExternalReferenceCode={
										defaultRoleExternalReferenceCode
									}
									disabled={isCurrentUser}
									onChange={(newRoles) => {
										onUpdateItemRoles(item, newRoles);
									}}
									roleNames={roleNames}
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
