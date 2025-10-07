/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	Input,
	MultiSelectItem,
	MultipleSelect,
	SingleSelect,
} from '@liferay/object-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {HEADERS} from '../../util/constants';
import {getRoles, getUserNotificationRoles} from './rolesUtil';
import {getUserGroups} from './userGroupsUtil';

interface User {
	alternateName: string;
	givenName: string;
}

interface UserNotificationSettingsProps {
	setValues: (values: Partial<NotificationTemplate>) => void;
	values: NotificationTemplate;
}

const RECIPIENT_TYPE_DETAILS: {
	[key: string]: {
		label: string;
		placeholder: string;
		search?: boolean;
		searchPlaceholder?: string;
		selectAllOption?: boolean;
	};
} = {
	'role': {
		label: Liferay.Language.get('role'),
		placeholder: Liferay.Language.get('enter-a-role'),
		searchPlaceholder: Liferay.Language.get('search-for-a-role'),
	},
	'user': {
		label: Liferay.Language.get('users'),
		placeholder: Liferay.Language.get('enter-user-name'),
	},
	'user-group': {
		label: Liferay.Language.get('user-group'),
		placeholder: Liferay.Language.get('select-user-group'),
		search: true,
		searchPlaceholder: Liferay.Language.get('search-for-a-user-group'),
		selectAllOption: true,
	},
};

const RECIPIENT_TYPE_OPTIONS = [
	{
		label: Liferay.Language.get('definition-of-terms'),
		value: 'term',
	},
	{
		label: Liferay.Language.get('role'),
		value: 'role',
	},
	{
		label: Liferay.Language.get('user'),
		value: 'user',
	},
	Liferay.FeatureFlags['LPD-50091'] && {
		label: Liferay.Language.get('user-group'),
		value: 'user-group',
	},
] as LabelValueObject[];

export function UserNotificationSettings({
	setValues,
	values,
}: UserNotificationSettingsProps) {
	const [selectItems, setSelectItems] = useState<MultiSelectItem[]>([]);
	const [toTerms, setToTerms] = useState<string>('');

	const {recipientType, recipients} = values;

	const showMultipleSelect =
		recipientType === 'role' ||
		recipientType === 'user' ||
		(recipientType === 'user-group' && Liferay.FeatureFlags['LPD-50091']);

	const getUserRoles = async () => {
		const roles = getUserNotificationRoles(
			await getRoles(),
			recipients as {['roleName']: string}[]
		);

		setSelectItems([roles]);
	};

	const getTerms = async () => {
		const recipientList = recipients as UserNotificationRecipients[];

		setToTerms(recipientList.map(({term}) => term).join());
	};

	const getUserAccounts = async () => {
		const apiURL = '/o/headless-admin-user/v1.0/user-accounts';
		const query = `${apiURL}?page=-1&sort=givenName:asc`;

		const response = await fetch(query, {
			headers: HEADERS,
			method: 'GET',
		});

		const {items} = (await response.json()) as {items: User[]};

		const selectedUser = new Set(
			(recipients as Partial<UserNotificationRecipients>[]).map(
				(recipient) => recipient.userScreenName
			)
		);

		const users = {
			children: items.map(({alternateName, givenName}) => {
				return {
					checked: selectedUser.has(alternateName),
					label: givenName,
					value: alternateName,
				};
			}),
			label: '',
			value: 'usersList',
		} as MultiSelectItem;

		setSelectItems([users]);
	};

	const handleMultiSelectItemsChange = (items: MultiSelectItem[]) => {
		const keySet = {
			'role': 'roleName',
			'term': 'userScreenName',
			'user': 'userScreenName',
			'user-group': 'userGroupName',
		} as {[key: string]: string};

		const key = keySet[recipientType];

		const newRecipients: UserNotificationRecipients[] = [];

		if (items.length) {
			const [itemsGroup] = items as MultiSelectItem[];

			itemsGroup.children.forEach((child) => {
				if (child.checked) {
					newRecipients.push({[key]: child.value});
				}
			});
		}

		setValues({
			...values,
			recipients: newRecipients,
		});
	};

	useEffect(() => {
		const makeFetch = async () => {
			if (recipientType === 'role') {
				await getUserRoles();

				return;
			}

			if (recipientType === 'term') {
				await getTerms();

				return;
			}

			if (recipientType === 'user') {
				await getUserAccounts();

				return;
			}

			if (recipientType === 'user-group') {
				const userGroups = await getUserGroups(
					recipients as Partial<UserNotificationRecipients>[]
				);

				setSelectItems(userGroups);

				return;
			}
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [recipientType]);

	useEffect(() => {
		const regex = /,/g;
		const toItems = toTerms
			.replace(regex, ' ')
			.split(' ')
			.filter((item) => {
				if (item !== '') {
					return item;
				}
			});

		if (toItems.length) {
			const toRecipients = toItems.map((item) => {
				return {term: item};
			});

			setValues({
				...values,
				recipients: toRecipients,
			});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [toTerms]);

	return (
		<>
			<SingleSelect<LabelValueObject>
				disabled={values.system}
				items={RECIPIENT_TYPE_OPTIONS}
				label={Liferay.Language.get('recipients')}
				onSelectionChange={async (value) => {
					setValues({
						...values,
						recipientType: value as string,
						recipients: [],
					});

					if (value === 'role') {
						await getUserRoles();
					}
					else if (value === 'user-group') {
						const userGroups = await getUserGroups(
							recipients as Partial<UserNotificationRecipients>[]
						);

						setSelectItems(userGroups);
					}
				}}
				selectedKey={recipientType}
			/>

			{recipientType === 'term' && (
				<Input
					component="textarea"
					label={Liferay.Language.get('to[recipient]')}
					onChange={({target}) => {
						setToTerms(target.value);
					}}
					placeholder={Liferay.Util.sub(
						Liferay.Language.get(
							'use-terms-to-configure-recipients-for-this-notification-x'
						),
						'[%OBJECT_AUTHOR_ID%]',
						'.'
					)}
					type="text"
					value={toTerms}
				/>
			)}

			{showMultipleSelect && (
				<MultipleSelect
					{...RECIPIENT_TYPE_DETAILS[recipientType]}
					options={selectItems}
					setOptions={(items) => {
						handleMultiSelectItemsChange(items);
						setSelectItems(items);
					}}
				/>
			)}
		</>
	);
}
