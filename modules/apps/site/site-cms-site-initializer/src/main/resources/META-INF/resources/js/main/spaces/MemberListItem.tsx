/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import {sub} from 'frontend-js-web';
import React from 'react';

import {UserAccount, UserGroup} from '../../types/UserAccount';

interface MembersListItemProps {
	assetLibraryCreatorUserId?: string;
	currentUserId?: string;
	emptyMessage: string;
	itemType: 'user' | 'group';
	items: (UserAccount | UserGroup)[];
	onRemoveItem: (item: UserAccount | UserGroup) => Promise<void>;
}

export function MembersListItem({
	assetLibraryCreatorUserId,
	currentUserId,
	emptyMessage,
	itemType,
	items,
	onRemoveItem,
}: MembersListItemProps) {
	if (!items || !items.length) {
		return (
			<li className="d-flex justify-content-center">{emptyMessage}</li>
		);
	}

	return (
		<>
			{items.map((item) => {
				const isUser = itemType === 'user';
				const isOwner =
					isUser && assetLibraryCreatorUserId === String(item.id);

				return (
					<li
						className="align-items-center d-flex justify-content-between"
						key={item.id}
					>
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
						</div>

						{isOwner ? (
							<span className="text-3 text-capitalize text-secondary">
								({Liferay.Language.get('owner')})
							</span>
						) : (
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
						)}
					</li>
				);
			})}
		</>
	);
}
