/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDatePicker from '@clayui/date-picker';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classnames from 'classnames';
import {dateUtils} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import ErrorMessage from '../ErrorMessage';
import {TGenericFieldProps} from '../FieldsWrapper';

const DateField = ({
	disabled,
	id,
	label,
	mode = 'view',
	name,
	namespace,
	onChange,
	readOnly,
	required,
	specificProps,
	value,
}: TGenericFieldProps) => {
	const [internalValue, setInternalValue] = useState(value || null);
	const [error, setError] = useState<string | null>(null);

	const onChangeHandler = useCallback(
		(value: string) => {
			let hasError = false;
			let isoDateString = null;

			if (value) {
				value = dateUtils.format(
					new Date(value.replace(/(--)/gi, '00')),
					specificProps && specificProps.time
						? 'yyyy-MM-dd HH:mm'
						: 'yyyy-MM-dd'
				);
			}

			setInternalValue(value);

			if (required && value.trim().length <= 0) {
				hasError = true;
				setError(Liferay.Language.get('this-field-is-required'));
			}
			else {
				try {
					if (value) {
						isoDateString = new Date(value).toISOString();
					}
					setError(null);
				}
				catch (error) {
					hasError = true;
					setError(
						Liferay.Language.get('the-field-value-is-invalid')
					);
				}
			}

			onChange({
				hasError,
				name,
				value: isoDateString || null,
			});
		},
		[name, onChange, required, specificProps]
	);

	useEffect(() => {
		if (!readOnly && mode === 'edit') {
			onChangeHandler(value || '');
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return mode === 'edit' ? (
		<ClayForm.Group
			className={classnames({
				'has-error': !!error,
			})}
		>
			<label htmlFor={`${namespace}${name}}`}>
				{label}

				{required && (
					<ClayIcon
						className="c-ml-1 reference-mark"
						symbol="asterisk"
					/>
				)}
			</label>

			<ClayDatePicker
				ariaLabels={{
					buttonChooseDate: `${Liferay.Language.get('select-date')}`,
					buttonDot: `${Liferay.Language.get('select-current-date')}`,
					buttonNextMonth: `${Liferay.Language.get(
						'select-next-month'
					)}`,
					buttonPreviousMonth: `${Liferay.Language.get(
						'select-previous-month'
					)}`,
					dialog: `${Liferay.Language.get('select-date')}`,
					selectMonth: `${Liferay.Language.get('select-a-month')}`,
					selectYear: `${Liferay.Language.get('select-a-year')}`,
				}}
				disabled={disabled || readOnly}
				firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
				id={`${namespace}${id}`}
				inputName={`${namespace}${name}`}
				months={[
					`${Liferay.Language.get('january')}`,
					`${Liferay.Language.get('february')}`,
					`${Liferay.Language.get('march')}`,
					`${Liferay.Language.get('april')}`,
					`${Liferay.Language.get('may')}`,
					`${Liferay.Language.get('june')}`,
					`${Liferay.Language.get('july')}`,
					`${Liferay.Language.get('august')}`,
					`${Liferay.Language.get('september')}`,
					`${Liferay.Language.get('october')}`,
					`${Liferay.Language.get('november')}`,
					`${Liferay.Language.get('december')}`,
				]}
				onChange={onChangeHandler}
				value={internalValue}
				weekdaysShort={dateUtils.getWeekdaysShort()}
				years={{
					end: new Date().getFullYear() + 100,
					start: new Date().getFullYear() - 100,
				}}
				{...specificProps}
			/>

			<ErrorMessage error={error} />
		</ClayForm.Group>
	) : (
		<div key={`${namespace}_${id}`}>
			<div className="sidebar-dt">{label}</div>

			<div className="sidebar-dd">
				{internalValue
					? dateUtils.format(
							internalValue,
							specificProps && specificProps.time
								? 'yyyy-MM-dd HH:mm'
								: 'yyyy-MM-dd'
						)
					: '-'}
			</div>
		</div>
	);
};

export default DateField;
