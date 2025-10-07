/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import {FormikErrors, FormikTouched} from 'formik';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import SpaceSticker from '../../common/components/SpaceSticker';
import {FieldText} from '../../common/components/forms';
import {LogoColor} from '../../common/types/Space';
import SpacePicker from './SpacePicker';

type Fields = {
	description: string;
	logoColor: LogoColor;
	name: string;
};

export default function BaseFields({
	children,
	errors,
	onBlurName,
	onChangeDescription,
	onChangeLogoColor,
	onChangeName,
	touched,
	values,
}: {
	children?: React.ReactNode;
	errors?: FormikErrors<Fields>;
	onBlurName: (event: React.ChangeEvent<any>) => void;
	onChangeDescription: (value: string) => void;
	onChangeLogoColor: (value: LogoColor) => void;
	onChangeName: (event: React.ChangeEvent<any>) => void;
	touched: FormikTouched<Fields>;
	values: Fields;
}) {
	const id = useId();

	const {description, logoColor, name} = values;

	return (
		<>
			<ClayForm.Group>
				<label>{Liferay.Language.get('space-logo')}</label>

				<SpaceSticker
					className="d-block mb-3"
					displayType={logoColor}
					hideName
					name={name || 'S'}
					size="xl"
				/>
			</ClayForm.Group>

			<SpacePicker
				label={Liferay.Language.get('space-color')}
				logoColor={logoColor}
				onChangeValue={onChangeLogoColor}
			/>

			<FieldText
				errorMessage={touched.name ? errors?.name : undefined}
				label={Liferay.Language.get('space-name')}
				name="name"
				onBlur={onBlurName}
				onChange={onChangeName}
				placeholder={Liferay.Language.get('enter-a-space-name')}
				required
				value={name}
			/>

			<ClayForm.Group>
				<label htmlFor={`${id}description`}>
					{Liferay.Language.get('description')}
				</label>

				<ClayInput
					component="textarea"
					id={`${id}description`}
					onChange={(event) =>
						onChangeDescription(event.target.value)
					}
					value={description}
				/>
			</ClayForm.Group>

			{children}
		</>
	);
}
