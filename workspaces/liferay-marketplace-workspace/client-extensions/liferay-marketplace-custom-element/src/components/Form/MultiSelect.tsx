/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {InputHTMLAttributes, useEffect, useRef, useState} from 'react';
import ReactSelect, {PropsValue} from 'react-select';

import Form from './index';

type Option = {label: string; value: string};

type MultiSelectProps = {
	isLoading: boolean;
	label?: string;
	options: Option[];
} & InputHTMLAttributes<HTMLSelectElement>;

const MultiSelect: React.FC<MultiSelectProps> = ({
	disabled,
	isLoading,
	label,
	name = '',
	onChange,
	options,
	value,
}) => {
	const [visible, setVisible] = useState(false);
	const multiselectRef = useRef<any>(null);

	useEffect(() => {
		const current = multiselectRef.current;

		if (!visible) {
			current?.blur();
		}
	}, [visible]);

	return (
		<Form.BaseWrapper label={label}>
			<ReactSelect
				classNamePrefix="marketplace-multi-select"
				closeMenuOnSelect
				isDisabled={disabled}
				isLoading={isLoading}
				isMulti
				menuIsOpen={visible}
				menuPosition="fixed"
				menuShouldBlockScroll
				name={name}
				onBlur={() => setVisible(false)}
				onChange={(value) => {
					if (onChange) {
						onChange({target: {name, value}} as any);
					}
				}}
				onFocus={() => !visible && setVisible(true)}
				onKeyDown={(event) => {
					if (event.key === 'Escape' && visible === true) {
						event.stopPropagation();
						setVisible(false);
					}

					return;
				}}
				onMenuClose={() => setVisible(false)}
				openMenuOnClick
				options={options}
				ref={multiselectRef}
				tabSelectsValue={false}
				value={value as PropsValue<unknown>}
			/>
		</Form.BaseWrapper>
	);
};

export default MultiSelect;
