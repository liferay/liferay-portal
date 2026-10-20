/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Input} from '@liferay/object-js-components-web';
import React from 'react';

import {ObjectFieldErrors} from './ObjectFieldFormBase';

import './AutoIncrementFormBase.scss';

interface AutoIncrementFormBaseProps {
	disabled: boolean;
	errors: ObjectFieldErrors;
	modelBuilder?: boolean;
	onSubmit?: (values?: Partial<ObjectField>) => void;
	setValues: (values: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

const PREFIX_SUFFIX_REGEX = /^[a-zA-Z0-9 /:,.()[\]{}#$%+-]*$/;

export function AutoIncrementFormBase({
	disabled,
	errors,
	modelBuilder,
	onSubmit,
	setValues,
	values,
}: AutoIncrementFormBaseProps) {
	return (
		<div
			className={
				modelBuilder
					? 'lfr-objects__auto-increment-form-base-prefix-suffix-model-builder-wrapper'
					: 'lfr-objects__auto-increment-form-base-prefix-suffix-wrapper'
			}
		>
			<Input
				disabled={disabled}
				feedbackMessage={Liferay.Language.get('optional')}
				label={Liferay.Language.get('prefix[string]')}
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={({target: {value}}) => {
					const regexValidation = PREFIX_SUFFIX_REGEX.exec(value);

					if (regexValidation !== null) {
						setValues({
							objectFieldSettings: [
								...(values.objectFieldSettings?.filter(
									(objectFieldSetting) =>
										objectFieldSetting.name !== 'prefix'
								) as ObjectFieldSetting[]),
								{
									name: 'prefix',
									value,
								},
							],
						});
					}
				}}
				value={
					(values.objectFieldSettings?.find(
						(objectFieldSetting) =>
							objectFieldSetting.name === 'prefix'
					)?.value as string) ?? ''
				}
			/>

			<Input
				disabled={disabled}
				error={errors.initialValue}
				label={Liferay.Language.get('initial-value')}
				min="0"
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={({target: {value}}) => {
					setValues({
						objectFieldSettings: [
							...(values.objectFieldSettings?.filter(
								(objectFieldSetting) =>
									objectFieldSetting.name !== 'initialValue'
							) as ObjectFieldSetting[]),
							{
								name: 'initialValue',
								value,
							},
						],
					});
				}}
				onKeyDown={(event) => {
					if (event.key === '.' || event.key === '-') {
						event.preventDefault();
					}
				}}
				required
				tooltip={Liferay.Language.get(
					'set-the-initial-numeric-value-to-increment-this-value-cannot-be-less-than-1'
				)}
				type="number"
				value={
					(values.objectFieldSettings?.find(
						(objectFieldSetting) =>
							objectFieldSetting.name === 'initialValue'
					)?.value as string) ?? ''
				}
			/>

			<Input
				disabled={disabled}
				feedbackMessage={Liferay.Language.get('optional')}
				label={Liferay.Language.get('suffix[value]')}
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={({target: {value}}) => {
					const regexValidation = PREFIX_SUFFIX_REGEX.exec(value);

					if (regexValidation !== null) {
						setValues({
							objectFieldSettings: [
								...(values.objectFieldSettings?.filter(
									(objectFieldSetting) =>
										objectFieldSetting.name !== 'suffix'
								) as ObjectFieldSetting[]),
								{
									name: 'suffix',
									value,
								},
							],
						});
					}
				}}
				value={
					(values.objectFieldSettings?.find(
						(objectFieldSetting) =>
							objectFieldSetting.name === 'suffix'
					)?.value as string) ?? ''
				}
			/>
		</div>
	);
}
