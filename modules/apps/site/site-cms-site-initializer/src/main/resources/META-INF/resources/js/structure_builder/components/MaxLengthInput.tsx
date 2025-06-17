/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayCheckbox} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {useStateDispatch} from '../contexts/StateContext';
import {Field, MaxLengthSettingsField} from '../utils/field';
import Input from './Input';

export default function MaxLengthInput({
	disabled,
	field,
}: {
	disabled?: boolean;
	field: Field;
}) {
	const maxLengthSettingsField = field as MaxLengthSettingsField;

	const dispatch = useStateDispatch();

	const [enableLimitCharacters, setEnableLimitCharacters] = useState(
		!!maxLengthSettingsField.settings.maxLength
	);

	return (
		<>
			<ClayForm.Group className="mb-3">
				<ClayCheckbox
					checked={enableLimitCharacters}
					disabled={disabled}
					label={Liferay.Language.get('limit-characters')}
					onChange={(event) => {
						const value = event.target.checked;

						setEnableLimitCharacters(value);

						if (!value) {
							dispatch({
								settings: {
									...maxLengthSettingsField.settings,
									maxLength: undefined,
									showCounter: undefined,
								},
								type: 'update-field',
								uuid: field.uuid,
							});
						}
					}}
				/>
			</ClayForm.Group>
			{enableLimitCharacters ? (
				<ClayForm.Group className="mb-3">
					<Input
						disabled={disabled}
						helpMessage={sub(
							Liferay.Language.get(
								'set-the-maximum-number-of-characters-accepted-this-value-cant-be-less-than-x-or-greater-than-x'
							),
							'1',
							'280'
						)}
						inputProps={{
							max: 280,
							min: 1,
							type: 'number',
						}}
						label={Liferay.Language.get(
							'maximum-number-of-characters'
						)}
						onValueChange={(value) => {
							dispatch({
								settings: {
									...maxLengthSettingsField.settings,
									maxLength: parseInt(value, 10),
									showCounter: true,
								},
								type: 'update-field',
								uuid: field.uuid,
							});
						}}
						required
						value={String(
							maxLengthSettingsField.settings.maxLength || ''
						)}
					/>
				</ClayForm.Group>
			) : null}
		</>
	);
}
