/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayForm from '@clayui/form';
import classNames from 'classnames';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {ErrorFeedback} from '../form/FormBase';

const RecipientUserAutocomplete = ({error, id, label, onSelectUser, value}) => {
	const [query, setQuery] = useState(value || '');
	const [userAccounts, setUserAccounts] = useState([]);

	const selectedEmailAddressRef = useRef(value || '');

	useEffect(() => {
		const emailAddress = value || '';

		if (emailAddress === selectedEmailAddressRef.current) {
			return;
		}

		selectedEmailAddressRef.current = emailAddress;

		setQuery(emailAddress);
	}, [value]);

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
				allowsCustomValue
				filterKey="emailAddress"
				id={id}
				items={userAccounts}
				menuTrigger="focus"
				messages={{
					loading: Liferay.Language.get('loading'),
					notFound: Liferay.Language.get('no-results-found'),
				}}
				onActiveChange={(active) => {
					if (!active && !selectedEmailAddressRef.current) {
						setQuery('');
					}
				}}
				onChange={(newQuery) => {
					setQuery(newQuery);

					if (
						selectedEmailAddressRef.current &&
						newQuery !== selectedEmailAddressRef.current
					) {
						selectedEmailAddressRef.current = '';

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
							selectedEmailAddressRef.current =
								userAccount.emailAddress;

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
