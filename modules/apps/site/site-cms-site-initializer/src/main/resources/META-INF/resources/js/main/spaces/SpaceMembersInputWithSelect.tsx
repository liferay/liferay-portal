/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/spaces/SpaceMembersInputWithSelect.scss';

import Autocomplete from '@clayui/autocomplete';
import {FetchPolicy, useResource} from '@clayui/data-provider';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {fetch} from 'frontend-js-web';
import React, {useId, useState} from 'react';

import {UserAccount, UserGroup} from '../../types/UserAccount';

export enum SelectOptions {
	USERS = 'users',
	GROUPS = 'groups',
}

export interface SpaceMembersInputWithSelectProps {
	className?: string;
	onAutocompleteItemSelected?: (item: UserAccount | UserGroup) => void;
	onSelectChange?: (value: SelectOptions) => void;
	selectValue?: SelectOptions;
}

export function SpaceMembersInputWithSelect({
	className,
	onAutocompleteItemSelected,
	onSelectChange,
	selectValue,
}: SpaceMembersInputWithSelectProps) {
	const selectId = useId();
	const [value, setValue] = useState('');
	const [networkStatus, setNetworkStatus] = useState(4);

	const endpoint =
		selectValue === SelectOptions.USERS
			? '/o/headless-admin-user/v1.0/user-accounts'
			: '/o/headless-admin-user/v1.0/user-groups';

	const {refetch, resource} = useResource({
		fetch: async (link, options) => {
			const result = await fetch(link, {
				...options,
				headers: {
					...(options?.headers ? options.headers : {}),
					'x-csrf-token': Liferay.authToken,
				},
			});

			const json = await result.json();

			return {
				cursor: json.next,
				items: json.items,
			};
		},
		fetchPolicy: 'no-cache' as FetchPolicy.NoCache,
		link: `${window.location.origin}${endpoint}`,
		onNetworkStatusChange: setNetworkStatus,
		variables: {search: value},
	});

	const renderAutocompleteItem = () => {
		if (selectValue === SelectOptions.USERS) {
			return (item: UserAccount) => {
				return (
					<Autocomplete.Item
						className="align-items-center d-flex text-truncate"
						key={item.id}
						onClick={() => {
							onAutocompleteItemSelected?.(item);
							setTimeout(() => setValue(''), 0);
						}}
						textValue={item.name}
					>
						<ClaySticker
							displayType="primary"
							shape="circle"
							size="sm"
						>
							<img
								alt={item.name}
								className="sticker-img"
								src={item.image || '/image/user_portrait'}
							/>
						</ClaySticker>

						<span className="ml-2 text-truncate">
							{item.name} ({item.emailAddress?.split('@')[0]})
						</span>
					</Autocomplete.Item>
				);
			};
		}

		return (item: UserGroup) => {
			return (
				<Autocomplete.Item
					className="align-items-center d-flex text-truncate"
					key={item.id}
					onClick={() => {
						onAutocompleteItemSelected?.(item);
						setTimeout(() => setValue(''), 0);
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
				</Autocomplete.Item>
			);
		};
	};

	return (
		<ClayForm.Group
			className={classNames('space-members-input-with-select', className)}
		>
			<label className="d-block" htmlFor={selectId}>
				{Liferay.Language.get('add-people-to-collaborate')}
			</label>

			<ClayInput.Group>
				<ClayInput.GroupItem prepend shrink>
					<ClaySelectWithOption
						id={selectId}
						onChange={(event) => {
							onSelectChange?.(
								event.target.value as SelectOptions
							);
						}}
						options={[
							{
								label: Liferay.Language.get('users'),
								value: 'users',
							},
							{
								label: Liferay.Language.get('groups'),
								value: 'groups',
							},
						]}
						value={selectValue}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem append>
					<Autocomplete
						allowsCustomValue
						id="autocomplete"
						items={(resource?.items ?? []) as any}
						loadingState={networkStatus}
						menuTrigger="focus"
						messages={{
							loading: Liferay.Language.get('loading...'),
							notFound: Liferay.Language.get('no-results-found'),
						}}
						onChange={(value: string) => {
							setValue(value);
						}}
						onFocusCapture={refetch}
						placeholder={Liferay.Language.get(
							'enter-name-or-email'
						)}
						value={value}
					>
						{renderAutocompleteItem()}
					</Autocomplete>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</ClayForm.Group>
	);
}
