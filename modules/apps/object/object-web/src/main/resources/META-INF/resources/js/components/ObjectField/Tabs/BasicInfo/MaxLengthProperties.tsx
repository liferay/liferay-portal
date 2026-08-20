/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {Input, Toggle} from '@liferay/object-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef} from 'react';
import {createTextMaskInputElement} from 'text-mask-core';

import {createAutoCorrectedNumberPipe} from '../../../../utils/createAutoCorrectedNumberPipe';
import {
	normalizeFieldSettings,
	removeFieldSettings,
	updateFieldSettings,
} from '../../../../utils/fieldSettings';
import {ObjectFieldErrors} from '../../ObjectFieldFormBase';

interface IMaxLengthPropertiesProps {
	disabled?: boolean;
	errors: ObjectFieldErrors;
	objectField: Partial<ObjectField>;
	objectFieldSettings: ObjectFieldSetting[];
	onSettingsChange: (setting: ObjectFieldSetting) => void;
	onSubmit?: () => void;
	setValues: (values: Partial<ObjectField>) => void;
}

export function MaxLengthProperties({
	disabled,
	errors,
	objectField,
	objectFieldSettings,
	onSettingsChange,
	onSubmit,
	setValues,
}: IMaxLengthPropertiesProps) {
	const [defaultMaxLength, defaultMaxLengthText] =
		objectField.businessType === 'Encrypted' ||
		objectField.businessType === 'Text'
			? [280, '280']
			: [65000, '65,000'];

	const settings = normalizeFieldSettings(objectFieldSettings);

	const inputRef = useRef(null);
	const maskRef = useRef();

	useEffect(() => {
		if (settings.showCounter) {
			maskRef.current = createTextMaskInputElement({
				guide: false,
				inputElement: inputRef.current,
				keepCharPositions: true,
				mask:
					objectField.businessType === 'Text'
						? [/\d/, /\d/, /\d/]
						: [/\d/, /\d/, /\d/, /\d/, /\d/],
				pipe: createAutoCorrectedNumberPipe(defaultMaxLength, 1),
				showMask: true,
			});
		}
	}, [defaultMaxLength, objectField.businessType, settings.showCounter]);

	const handleToggle = (toggled: boolean) => {
		let updatedSettings;

		if (!toggled) {
			updatedSettings = removeFieldSettings(['maxLength'], objectField);
		}
		else {
			updatedSettings = updateFieldSettings(objectFieldSettings, {
				name: 'maxLength',
				value: defaultMaxLength,
			});
		}

		setValues({
			objectFieldSettings: updateFieldSettings(updatedSettings, {
				name: 'showCounter',
				value: toggled,
			}),
		});
	};

	return (
		<>
			<ClayForm.Group>
				<Toggle
					disabled={disabled}
					label={Liferay.Language.get('limit-characters')}
					name="showCounter"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={handleToggle}
					toggled={!!settings.showCounter}
					tooltip={Liferay.Language.get(
						'when-enabled-a-character-counter-is-shown-to-the-user'
					)}
				/>

				{settings.showCounter && (
					<Input
						className="mt-3"
						disabled={disabled}
						error={errors.maxLength}
						feedbackMessage={sub(
							Liferay.Language.get(
								'set-the-maximum-number-of-characters-accepted-this-value-cant-be-less-than-x-or-greater-than-x'
							),
							'1',
							defaultMaxLengthText
						)}
						id="maxLength"
						label={Liferay.Language.get(
							'maximum-number-of-characters'
						)}
						onBlur={(event) => {
							event.stopPropagation();

							if (onSubmit) {
								onSubmit();
							}
						}}
						onChange={({target: {value}}) =>
							onSettingsChange({
								name: 'maxLength',
								value:
									Number(value) <= defaultMaxLength
										? Number(value)
										: (settings.maxLength as number),
							})
						}
						onInput={({target: {value}}: any) =>
							(maskRef.current as any).update(value)
						}
						ref={inputRef}
						required
						value={`${settings.maxLength}`}
					/>
				)}
			</ClayForm.Group>
		</>
	);
}
