/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import {
	AddMembersInputApi,
	MemberType,
	MembersSelectOptions,
} from 'frontend-js-components-web';
import {addParams} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

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

const ENDPOINTS = {
	[MemberType.GROUPS]: '/o/headless-admin-user/v1.0/user-groups',
	[MemberType.USERS]: '/o/headless-admin-user/v1.0/user-accounts',
} as const;

export default function AddMembersInput({
	excludeMembers,
	filter,
	onAutocompleteItemSelected,
	onSelectChange,
	selectValue,
}: AddMembersInputApi) {
	const [value, setValue] = useState('');

	const apiURL = useMemo(() => {
		const pathContext = Liferay.ThemeDisplay.getPathContext() || '';

		const endpoint = addParams(
			{},
			`${pathContext}${ENDPOINTS[selectValue]}`
		);

		const filters: string[] = [];

		if (excludeMembers?.length) {
			const filterKey =
				selectValue === MemberType.USERS ? 'id' : 'userGroupId';

			filters.push(
				excludeMembers
					.map((member) => `${filterKey} ne '${member.id}'`)
					.join(' and ')
			);
		}

		if (filter) {
			filters.push(filter);
		}

		return filters.length
			? addParams({filter: filters.join(' and ')}, endpoint)
			: endpoint;
	}, [excludeMembers, filter, selectValue]);

	const renderUserAccountItem = (item: AdminUserAccount) => {
		return (
			<ItemSelector.Item
				className="align-items-center d-flex text-truncate"
				key={item.id}
				onClick={() => {
					onAutocompleteItemSelected({
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
				<ClaySticker displayType="secondary" shape="circle" size="lg">
					<ClaySticker.Image
						alt={item.name}
						src={
							item.image ||
							`${Liferay.ThemeDisplay.getPathContext() || ''}/image/user_portrait`
						}
					/>
				</ClaySticker>

				<span className="ml-2 text-truncate">
					{item.name}

					{item.emailAddress &&
						` (${item.emailAddress.split('@')[0]})`}
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
					onAutocompleteItemSelected({
						externalReferenceCode: item.externalReferenceCode,
						id: String(item.id),
						name: item.name,
						numberOfUserAccounts: String(groupCount),
						roles: [],
					});
				}}
				textValue={item.name}
			>
				<ClaySticker displayType="secondary" shape="circle" size="lg">
					<ClayIcon symbol="users" />
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
		<MembersSelectOptions
			label={Liferay.Language.get('add-people-to-collaborate')}
			onSelectChange={onSelectChange}
			selectValue={selectValue}
		>
			{selectValue === MemberType.USERS ? (
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
		</MembersSelectOptions>
	);
}
