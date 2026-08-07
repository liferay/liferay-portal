/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DatePicker} from '@liferay/object-js-components-web';
import {unescapeHTML} from 'frontend-js-web';
import React, {useState} from 'react';

interface IDateInput {
	editMode?: boolean;
	errorMessage?: string;
	helpText?: string;
	label?: string;
	name: string;
	readOnly?: boolean;
	required?: boolean;
	showHelpText?: boolean;
	showLabel?: boolean;
	value?: string;
}

export default function DateInput({
	editMode = false,
	errorMessage = '',
	helpText = '',
	label = '',
	name,
	readOnly = false,
	required = false,
	showHelpText = true,
	showLabel = true,
	value = '',
}: IDateInput) {
	const [error, setError] = useState<string>(errorMessage);
	const [serverValue, setServerValue] = useState<string>(value);

	return (
		<>
			<DatePicker
				disabled={editMode || readOnly}
				error={error}
				feedbackMessage={
					showHelpText ? unescapeHTML(helpText) : undefined
				}
				id={`${name}DateInput`}
				label={showLabel ? unescapeHTML(label) : undefined}
				onBlur={() => {
					if (required && !serverValue) {
						setError(
							Liferay.Language.get('this-field-is-required')
						);
					}
				}}
				onChange={(nextServerValue) => {
					setError('');
					setServerValue(nextServerValue);
				}}
				required={required}
				type="Date"
				value={serverValue}
			/>

			<input name={name} type="hidden" value={serverValue} />
		</>
	);
}
