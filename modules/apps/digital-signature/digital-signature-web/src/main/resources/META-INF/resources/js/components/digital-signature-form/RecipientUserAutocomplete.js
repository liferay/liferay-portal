/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayForm from '@clayui/form';
import classNames from 'classnames';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {ErrorFeedback} from '../form/FormBase';

const RecipientUserAutocomplete = ({error, id, label, onSelectUser, value}) => {
	const [query, setQuery] = useState(value || '');
	const [userAccounts, setUserAccounts] = useState([]);

	useEffect(() => {
		if (!query) {
			setUserAccounts([]);

			return;
		}

		const timeoutId = setTimeout(() => {
			fetch(
				`/o/headless-admin-user/v1.0/user-accounts?pageSize=10&search=${encodeURIComponent(
					query
				)}`
			)
				.then((response) => response.json())
				.then(({items = []}) => setUserAccounts(items))
				.catch(() => setUserAccounts([]));
		}, 300);

		return () => clearTimeout(timeoutId);
	}, [query]);

	return (
		<ClayForm.Group
			className={classNames('mb-0 w-100', {'has-error': error})}
		>
			<label htmlFor={id}>{label}</label>

			<ClayAutocomplete
				id={id}
				items={userAccounts}
				menuTrigger="focus"
				onChange={(value) => {
					setQuery(value);

					if (!value) {
						onSelectUser({emailAddress: '', name: ''});
					}
				}}
				placeholder={Liferay.Language.get('search-for')}
				value={query}
			>
				{(userAccount) => (
					<ClayAutocomplete.Item
						key={userAccount.id}
						onClick={() => {
							setQuery(userAccount.emailAddress);
							onSelectUser(userAccount);
						}}
						textValue={userAccount.emailAddress}
					>
						<div>{`${userAccount.name} (${userAccount.emailAddress})`}</div>
					</ClayAutocomplete.Item>
				)}
			</ClayAutocomplete>

			{typeof error === 'string' && <ErrorFeedback error={error} />}
		</ClayForm.Group>
	);
};

export default RecipientUserAutocomplete;
