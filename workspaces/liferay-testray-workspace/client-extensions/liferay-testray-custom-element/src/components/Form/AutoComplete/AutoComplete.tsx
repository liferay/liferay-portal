/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayDropDown from '@clayui/drop-down';
import {useEffect, useState} from 'react';
import {Params} from 'react-router-dom';

import useDebounce from '../../../hooks/useDebounce';
import {useFetch} from '../../../hooks/useFetch';

export type AutoCompleteProps = {
	label?: string;
	onSearch: (keyword: string) => any;
	resource?: ((params: Readonly<Params<string>>) => string) | string;
	transformData?: (item: any) => any;
};

const AutoComplete: React.FC<AutoCompleteProps> = ({
	label,
	onSearch,
	resource,
	transformData,
}) => {
	const [showValue, setShowValue] = useState('');
	const [value, setValue] = useState('');
	const [active, setActive] = useState(false);

	const debouncedValue = useDebounce(value, 1000);

	const {called, data, error, isValidating} = useFetch(
		debouncedValue ? (resource as unknown as string) : null,
		{
			params: {
				filter: onSearch(debouncedValue),
			},
			transformData,
		}
	);

	const items = data?.items || [];
	const onClickItem = (name: string) => {
		setShowValue(name);
		setActive(false);
	};

	useEffect(() => {
		if (debouncedValue) {
			setActive(true);
		}
	}, [called, debouncedValue]);

	return (
		<ClayAutocomplete className="mb-4">
			<label>{label}</label>

			<ClayAutocomplete.Input
				onBlur={() => setTimeout(() => setActive(false), 200)}
				onChange={(event) => {
					setValue(event.target.value);
					setShowValue(event.target.value);
				}}
				placeholder="Type here"
				value={showValue || value}
			/>

			<ClayAutocomplete.DropDown active={active}>
				<ClayDropDown.ItemList>
					{called && (error || (items && !items.length)) && (
						<ClayDropDown.Item className="disabled">
							No Results Found
						</ClayDropDown.Item>
					)}

					{!error &&
						items?.map((item: any) => (
							<ClayAutocomplete.Item
								key={item.id}
								match={value}
								onClick={() => onClickItem(item.name)}
								value={item.name}
							/>
						))}
				</ClayDropDown.ItemList>
			</ClayAutocomplete.DropDown>

			{isValidating && <ClayAutocomplete.LoadingIndicator />}
		</ClayAutocomplete>
	);
};

export default AutoComplete;
