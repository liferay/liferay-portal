/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {debounce} from 'frontend-js-web';
import React, {useState} from 'react';

let nextInputId = 0;

export default function SearchForm({
	className,
	label,
	onChange,
	placeholder = Liferay.Language.get('search'),
	size,
	variant = 'default',
}: {
	className?: string;
	label?: string;
	onChange: Function;
	placeholder?: string;
	size?: 'sm';
	variant?: 'default' | 'white';
}) {
	const id = `pageEditorSearchFormInput${nextInputId++}`;

	const [searchValue, setSearchValue] = useState('');

	return (
		<ClayForm.Group className={className} role="search">
			<label className="sr-only" htmlFor={id}>
				{label || Liferay.Language.get('search-form')}
			</label>

			<ClayInput.Group small={size === 'sm'}>
				<ClayInput.GroupItem>
					<ClayInput
						className={classNames({
							'bg-white': variant === 'white',
						})}
						id={id}
						insetAfter
						onChange={({target: {value}}) => {
							setSearchValue(value);
							debounce(() => onChange(value), 100)();
						}}
						placeholder={`${placeholder}...`}
						sizing={size}
						spellCheck={false}
						value={searchValue}
					/>

					<ClayInput.GroupInsetItem
						after
						className={classNames({
							'bg-white': variant === 'white',
						})}
						tag="span"
					>
						{searchValue ? (
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'clear-search'
								)}
								borderless
								displayType="secondary"
								monospaced
								onClick={() => {
									setSearchValue('');
									onChange('');
								}}
								size={size}
								symbol={searchValue ? 'times' : 'search'}
								title={Liferay.Language.get('clear-search')}
							/>
						) : (
							<ClayIcon className="mr-2 mt-0" symbol="search" />
						)}
					</ClayInput.GroupInsetItem>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		</ClayForm.Group>
	);
}
