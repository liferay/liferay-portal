/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {InputHTMLAttributes, ReactNode} from 'react';

import {BaseWrapper} from '../Base';

type InputSelectProps = {
	className?: string;
	defaultOption?: boolean;
	disabled?: boolean;
	errors?: any;
	forceSelectOption?: boolean;
	id?: string;
	isLoading?: boolean;
	label?: string | ReactNode;
	name: string;
	options: {label: string; value: string | number}[];
	register?: any;
	registerOptions?: any;
	required?: boolean;
	type?: string;
} & InputHTMLAttributes<HTMLInputElement>;

const InputSelect: React.FC<InputSelectProps> = ({
	className,
	disabled = false,
	registerOptions,
	defaultOption = true,
	errors = {},
	defaultValue,
	label,
	name,
	register = () => {},
	id = name,
	isLoading,
	options,
	forceSelectOption = false,
	required = false,
	...otherProps
}) => {
	return (
		<BaseWrapper
			disabled={disabled}
			error={errors[name]?.message}
			label={label}
			required={required}
		>
			<select
				className={classNames('form-control rounded-xs', className)}
				defaultValue={defaultValue}
				disabled={disabled}
				id={id}
				name={name}
				{...otherProps}
				{...register(name, {required, ...registerOptions})}
			>
				{defaultOption && <option value=""></option>}

				{isLoading ? (
					<option value="">Loading...</option>
				) : (
					options?.map(({label, value}, index) => {
						const valueOption =
							name.includes('teamToComponents/name') ||
							name.includes('componentToCaseResult/name')
								? label
								: value;

						return (
							<option
								key={index}
								label={label}
								selected={
									forceSelectOption
										? value === defaultValue
										: undefined
								}
								value={valueOption}
							>
								{label}
							</option>
						);
					})
				)}
			</select>
		</BaseWrapper>
	);
};

export default InputSelect;
