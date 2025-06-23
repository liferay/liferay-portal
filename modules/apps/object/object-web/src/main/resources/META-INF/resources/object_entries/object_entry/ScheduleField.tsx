/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import {DatePicker} from '@liferay/object-js-components-web';
import React, {useCallback, useEffect, useState} from 'react';

interface ScheduleFieldProps {
	checkboxLabel: string;
	customValidation?: (date: string) => string;
	dateLabel: string;
	error?: string;
	id: string;
	isChecked: boolean;
	onCheckboxChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
	onDateChange: (value: string) => void;
	value: string;
}

export default function ScheduleField({
	checkboxLabel,
	customValidation,
	dateLabel,
	id,
	isChecked,
	onCheckboxChange,
	onDateChange,
	value,
}: ScheduleFieldProps) {
	const [dateError, setDateError] = useState<string>('');

	const [checked, setChecked] = useState<boolean>(isChecked);

	const handleError = useCallback(
		(value: string) => {
			if (!value && !checked) {
				setDateError(Liferay.Language.get('this-field-is-required'));
			}
			else if (customValidation) {
				setDateError(() => customValidation(value));
			}
			else {
				setDateError('');
			}
		},
		[checked, customValidation]
	);

	useEffect(() => {
		const handleSubmit = () => {
			handleError(value);
		};

		Liferay.on('submitObjectEntry', handleSubmit);

		return () => {
			Liferay.detach('submitObjectEntry', handleSubmit);
		};
	}, [handleError, value]);

	return (
		<div className="col-lg-6">
			<DatePicker
				disabled={checked}
				error={dateError}
				id={id}
				label={dateLabel}
				onBlur={() => {
					handleError(value);
				}}
				onChange={(value) => {
					handleError(value);
					onDateChange(value);
				}}
				required
				type="DateTime"
				value={value}
			/>

			<ClayCheckbox
				checked={checked}
				label={checkboxLabel}
				onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
					if (event.target.checked) {
						setDateError('');
					}
					setChecked(event.target.checked);
					onCheckboxChange(event);
				}}
			/>
		</div>
	);
}
