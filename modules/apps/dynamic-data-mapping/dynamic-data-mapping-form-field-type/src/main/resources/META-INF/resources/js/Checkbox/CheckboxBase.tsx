/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox, ClayInput, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {getNonLocalizableFieldMessage} from '../api/FieldBase/translation';

import type {FieldChangeEventHandler, LocalizedValue} from '../types';

const Switcher: React.FC<ISwitcherProps> = ({
	accessibleProps,
	checked,
	disabled,
	label,
	name,
	onChange,
	required,
	showLabel,
	showMaximumRepetitionsInfo,
	systemSettingsURL,
}) => {
	return (
		<>
			<label className="toggle-switch">
				<ClayToggle
					{...accessibleProps}
					disabled={disabled}
					name={name}
					onToggle={(checked) => {
						onChange({target: {value: checked}});
					}}
					toggled={checked}
					value={String(checked)}
				/>

				{showLabel && label}

				{required && (
					<ClayIcon className="reference-mark" symbol="asterisk" />
				)}
			</label>

			{checked && showMaximumRepetitionsInfo && (
				<div className="ddm-info">
					<span className="ddm-tooltip">
						<ClayIcon symbol="info-circle" />
					</span>

					<div
						className="ddm-info-text"
						dangerouslySetInnerHTML={{
							__html: Liferay.Util.sub(
								Liferay.Language.get(
									'for-security-reasons-upload-field-repeatability-is-limited-the-limit-is-defined-in-x-system-settings-x'
								),
								`<a href=${systemSettingsURL} target="_blank">`,
								'</a>'
							),
						}}
					/>
				</div>
			)}
		</>
	);
};

const Toggle: React.FC<ISwitcherProps> = ({
	accessibleProps,
	checked,
	disabled,
	label,
	name,
	onChange,
	required,
	showAsSwitcher = true,
	showLabel,
	showMaximumRepetitionsInfo,
	systemSettingsURL,
	value,
}) => {
	return showAsSwitcher ? (
		<Switcher
			accessibleProps={accessibleProps}
			checked={checked}
			disabled={disabled}
			label={label}
			name={name}
			onChange={onChange}
			required={required}
			showLabel={showLabel}
			showMaximumRepetitionsInfo={showMaximumRepetitionsInfo}
			systemSettingsURL={systemSettingsURL}
			value={value}
		/>
	) : (
		<ClayCheckbox
			{...accessibleProps}
			checked={checked}
			disabled={disabled}
			label={showLabel ? label : ''}
			name={name}
			onChange={({target: {checked}}) => {
				onChange({target: {value: checked}});
			}}
		>
			{showLabel && required && (
				<span className="ddm-label-required reference-mark">
					<ClayIcon symbol="asterisk" />
				</span>
			)}
		</ClayCheckbox>
	);
};

export default function CheckboxBase({
	checked,
	displayErrors,
	editOnlyInDefaultLanguage,
	isLocalizationSupported,
	name,
	readOnly,
	showAsSwitcher = true,
	showLabel = true,
	showMaximumRepetitionsInfo = false,
	valid,
	...otherProps
}: IProps) {
	return (
		<>
			<Toggle
				accessibleProps={{
					...((otherProps.errorMessage || otherProps.tip) && {
						'aria-describedby': `${otherProps.id ?? name}_fieldFeedback`,
					}),
					...(displayErrors && !valid && {'aria-invalid': true}),
					'aria-required': !!otherProps.required,
				}}
				checked={checked}
				disabled={readOnly}
				name={name}
				showAsSwitcher={showAsSwitcher}
				showLabel={showLabel}
				showMaximumRepetitionsInfo={showMaximumRepetitionsInfo}
				{...otherProps}
			/>

			<ClayInput name={name} type="hidden" value={`${checked}`} />

			{editOnlyInDefaultLanguage && showLabel && readOnly && (
				<span
					className="c-ml-2 text-4 text-secondary"
					data-testid="tooltip"
					tabIndex={0}
					title={getNonLocalizableFieldMessage(
						isLocalizationSupported
					)}
				>
					<ClayIcon symbol="question-circle-full" />
				</span>
			)}
		</>
	);
}

export interface ICheckboxBaseProps {
	checked: boolean;
	disabled?: boolean;
	label?: string;
	name: string;
	onChange:
		| FieldChangeEventHandler<boolean>
		| FieldChangeEventHandler<LocalizedValue<boolean>>;
	required?: boolean;
	showAsSwitcher?: boolean;
	showLabel?: boolean;
	value: boolean | LocalizedValue<boolean>;
}

interface ISwitcherProps extends ICheckboxBaseProps {
	accessibleProps: {
		'aria-describedby'?: string;
		'aria-required': boolean;
	};
	showMaximumRepetitionsInfo: boolean;
	systemSettingsURL: string;
}

interface IProps extends ICheckboxBaseProps {
	displayErrors?: boolean;
	editOnlyInDefaultLanguage: boolean;
	errorMessage: string;
	id?: string;
	isLocalizationSupported: boolean;
	predefinedValue?: boolean | String[];
	readOnly?: boolean;
	showAsSwitcher?: boolean;
	showMaximumRepetitionsInfo?: boolean;
	systemSettingsURL: string;
	tip: string;
	valid?: boolean;
	visible?: boolean;
}
