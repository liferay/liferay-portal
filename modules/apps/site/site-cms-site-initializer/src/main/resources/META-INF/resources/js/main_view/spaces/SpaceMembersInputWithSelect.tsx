/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/spaces/SpaceMembersInputWithSelect.scss';

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import React, {useMemo, useState} from 'react';

import {UserAccount, UserGroup} from '../../common/types/UserAccount';
import {
	SelectOptions,
	SpaceMembersSelectOptions,
} from './SpaceMembersSelectOptions';

interface AdminUserAccount {
	emailAddress: string;
	externalReferenceCode: string;
	id: number;
	image: string;
	imageId: number;
	name: string;
}

interface AdminUserGroup {
	externalReferenceCode: string;
	id: number;
	name: string;
	usersCount: number;
}

export interface SpaceMembersInputWithSelectProps {
	className?: string;
	excludeMembers?: (UserAccount | UserGroup)[];
	onAutocompleteItemSelected?: (item: UserAccount | UserGroup) => void;
	onSelectChange?: (value: SelectOptions) => void;
	selectValue: SelectOptions;
}

const endpoints = {
	[SelectOptions.USERS]: `${location.origin}/o/headless-admin-user/v1.0/user-accounts`,
	[SelectOptions.GROUPS]: `${location.origin}/o/headless-admin-user/v1.0/user-groups`,
} as const;

export function SpaceMembersInputWithSelect({
	className,
	excludeMembers,
	onAutocompleteItemSelected,
	onSelectChange,
	selectValue,
}: SpaceMembersInputWithSelectProps) {
	const [value, setValue] = useState('');

	const apiURL = useMemo(() => {
		const endpoint = endpoints[selectValue as SelectOptions];
		const filterKey =
			selectValue === SelectOptions.USERS ? 'id' : 'userGroupId';

		if (excludeMembers?.length) {
			const excludeIds = excludeMembers.map((member) => `'${member.id}'`);

			return `${endpoint}?filter=${filterKey} ne ${excludeIds.join(` and ${filterKey} ne `)}`;
		}

		return endpoint;
	}, [excludeMembers, selectValue]);

	const renderUserAccountItem = (item: AdminUserAccount) => {
		return (
			<ItemSelector.Item
				className="align-items-center d-flex text-truncate"
				key={item.id}
				onClick={() => {
					onAutocompleteItemSelected?.({
						emailAddress: item.emailAddress,
						externalReferenceCode: item.externalReferenceCode,
						id: String(item.id),
						image: item.image,
						imageId: String(item.imageId),
						name: item.name,
						roles: [],
					});
				}}
				textValue={item.name}
			>
				<ClaySticker displayType="primary" shape="circle" size="sm">
					<img
						alt={item.name}
						className="sticker-img"
						src={item.image || '/image/user_portrait'}
					/>
				</ClaySticker>

				<span className="ml-2 text-truncate">
					{item.name} ({item.emailAddress?.split('@')[0]})
				</span>
			</ItemSelector.Item>
		);
	};

	const renderUserGroupItem = (item: AdminUserGroup) => {
		const groupCount = item.usersCount || 0;

		return (
			<ItemSelector.Item
				className="align-items-center d-flex text-truncate"
				key={item.id}
				onClick={() => {
					onAutocompleteItemSelected?.({
						externalReferenceCode: item.externalReferenceCode,
						id: String(item.id),
						name: item.name,
						numberOfUserAccounts: String(groupCount),
						roles: [],
					});
				}}
				textValue={item.name}
			>
				<ClaySticker displayType="primary" shape="circle" size="sm">
					<ClayIcon
						className="text-secondary"
						fontSize="24px"
						symbol="users"
					/>
				</ClaySticker>

				<span className="ml-2 text-truncate">{item.name}</span>

				<span className="ml-1">
					(
					{Liferay.Util.sub(
						Liferay.Language.get('x-members'),
						groupCount
					)}
					)
				</span>
			</ItemSelector.Item>
		);
	};

	return (
		<SpaceMembersSelectOptions
			className={className}
			label={Liferay.Language.get('add-people-to-collaborate')}
			onSelectChange={onSelectChange}
			selectValue={selectValue}
		>
			{selectValue === SelectOptions.USERS ? (
				<ItemSelector<AdminUserAccount>
					apiURL={apiURL}
					id="autocomplete"
					key={apiURL}
					locator={{
						id: 'id',
						label: 'name',
						value: 'id',
					}}
					onChange={setValue}
					placeholder={Liferay.Language.get('enter-name-or-email')}
					value={value}
				>
					{renderUserAccountItem}
				</ItemSelector>
			) : (
				<ItemSelector<AdminUserGroup>
					apiURL={apiURL}
					id="autocomplete"
					key={apiURL}
					locator={{
						id: 'id',
						label: 'name',
						value: 'id',
					}}
					onChange={setValue}
					placeholder={Liferay.Language.get('enter-name-or-email')}
					value={value}
				>
					{renderUserGroupItem}
				</ItemSelector>
			)}
		</SpaceMembersSelectOptions>
	);
}
