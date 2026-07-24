/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import ItemWithStickerRenderer from './ItemWithStickerRenderer';

const OWNER_ROLE_EXTERNAL_REFERENCE_CODE = 'L_DESIGN_LIBRARY_OWNER';

export interface MemberData {
	id: number;
	image?: string;
	numberOfUserAccounts?: number;
	roles?: Array<{externalReferenceCode: string}>;
}

const MemberRenderer = ({
	itemData,
	ownerId,
	value,
}: {
	itemData: MemberData;
	ownerId?: string;
	value: string;
}) => {
	if (itemData.numberOfUserAccounts !== undefined) {
		return (
			<ItemWithStickerRenderer
				label={value}
				stickerContent={<ClayIcon symbol="users" />}
				suffix={
					<span className="ml-1">
						(
						{Liferay.Util.sub(
							Liferay.Language.get('x-members'),
							itemData.numberOfUserAccounts
						)}
						)
					</span>
				}
			/>
		);
	}

	const roles = itemData.roles || [];

	const isOwner =
		(ownerId !== undefined && String(itemData.id) === String(ownerId)) ||
		roles.some(
			(role) =>
				role.externalReferenceCode ===
				OWNER_ROLE_EXTERNAL_REFERENCE_CODE
		);

	return (
		<ItemWithStickerRenderer
			label={value}
			stickerContent={
				<ClaySticker.Image
					alt={value}
					src={
						itemData.image ||
						`${Liferay.ThemeDisplay.getPathContext() || ''}/image/user_portrait`
					}
				/>
			}
			suffix={
				isOwner ? (
					<span className="ml-1">
						({Liferay.Language.get('owner')})
					</span>
				) : undefined
			}
		/>
	);
};

export default MemberRenderer;
