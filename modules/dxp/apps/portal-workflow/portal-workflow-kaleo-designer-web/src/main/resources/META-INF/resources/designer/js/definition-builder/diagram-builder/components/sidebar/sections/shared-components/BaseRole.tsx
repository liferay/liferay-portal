/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayInput} from '@clayui/form';
import {fetch} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {ChangeEvent, useEffect, useMemo, useState} from 'react';

import {HEADERS, userBaseURL} from '../../../../../util/fetchUtil';

interface BaseRoleProps {
	defaultFieldValue: {
		id: string;
		name: string;
	};
	inputLabel: string;
	selectLabel: string;
	updateSelectedItem: (value: Role) => void;
}

export default function BaseRole({
	defaultFieldValue = {id: '', name: ''},
	inputLabel,
	selectLabel,
	updateSelectedItem,
}: BaseRoleProps) {
	const [active, setActive] = useState(false);
	const [filter, setFilter] = useState(false);
	const [fieldValues, setFieldValues] = useState(defaultFieldValue);
	const [loading, setLoading] = useState(false);
	const [roleItems, setRoleItems] = useState<Role[]>([]);

	useEffect(() => {
		if (defaultFieldValue.name !== '') {
			setFieldValues((previousValues) => {
				const updatedValues = {...previousValues};
				updatedValues.name = defaultFieldValue.name;

				return updatedValues;
			});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [defaultFieldValue]);

	useEffect(() => {
		const makeFetch = async () => {
			setLoading(true);

			const params = new URLSearchParams({
				pageSize: '-1',
				restrictedFields: 'rolePermissions',
			});

			const response = await fetch(
				`${userBaseURL}/roles?${params.toString()}`,
				{
					headers: HEADERS,
				}
			);

			const {items} = (await response.json()) as {items: Role[]};

			setRoleItems(items);

			setLoading(false);
		};

		makeFetch();
	}, []);

	const handleInputFocus = () => {
		setFilter(fieldValues.name === '');
		setActive(true);
	};

	const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
		event.persist();

		if (!filter) {
			setFilter(true);
		}

		setFieldValues((previousValues) => ({
			...previousValues,
			name: event.target.value,
		}));
	};

	const filteredItems = useMemo(() => {
		return roleItems.filter((item) =>
			filter
				? item.name.toLowerCase().match(fieldValues.name.toLowerCase())
				: item
		);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [fieldValues, roleItems]);

	const handleItemClick = (item: Role) => {
		setFieldValues({id: String(item.id), name: item.name});
		setActive(false);

		updateSelectedItem(item);
	};

	return (
		<>
			<ClayForm.Group>
				<ClayAutocomplete>
					<label htmlFor="role-name">{selectLabel}</label>

					<ClayAutocomplete.Input
						autoComplete="off"
						id="role-name"
						onChange={(event) => handleInputChange(event)}
						onFocus={() => handleInputFocus()}
						placeholder="Search"
						value={fieldValues.name}
					/>

					<ClayAutocomplete.DropDown
						active={!!roleItems.length && active}
						closeOnClickOutside
						onSetActive={setActive}
					>
						<ClayDropDown.ItemList>
							{!roleItems.length && (
								<ClayDropDown.Item className="disabled">
									{Liferay.Language.get('no-results-found')}
								</ClayDropDown.Item>
							)}

							{!!roleItems.length &&
								filteredItems.map((item) => (
									<ClayAutocomplete.Item
										key={item.id}
										onClickCapture={() => {
											handleItemClick(item);
										}}
										value={item.name}
									/>
								))}
						</ClayDropDown.ItemList>
					</ClayAutocomplete.DropDown>

					{loading && <ClayAutocomplete.LoadingIndicator />}
				</ClayAutocomplete>
			</ClayForm.Group>
			<ClayForm.Group>
				<label htmlFor="role-id">
					{inputLabel}

					<span className="ml-1 mr-1 text-warning">*</span>
				</label>

				<ClayInput
					component="input"
					disabled
					id="role-id"
					type="text"
					value={fieldValues.id}
				/>
			</ClayForm.Group>
		</>
	);
}

BaseRole.propTypes = {
	inputLabel: PropTypes.string.isRequired,
	selectLabel: PropTypes.string.isRequired,
};
