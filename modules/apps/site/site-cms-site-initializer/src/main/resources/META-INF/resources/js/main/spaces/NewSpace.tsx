/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import Form, {ClayInput} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {useFormik} from 'formik';
import {navigate} from 'frontend-js-web';
import React from 'react';

import SpaceService from '../../services/SpaceService';
import SpaceColorDropdown from '../components/SpaceLogoColorDropdown';
import SpaceSticker, {LogoColor} from '../components/SpaceSticker';
import {FieldText} from '../components/forms';
import {required, validate} from '../components/forms/validations';
import {getImage} from '../util/getImage';
import {NewSpaceFormSection} from './NewSpaceFormSection';

export interface NewSpaceProps {
	baseRedirectUrl: string;
}

const NewSpace = ({baseRedirectUrl}: NewSpaceProps) => {
	const {
		errors,
		handleChange,
		handleSubmit,
		isSubmitting,
		setFieldValue,
		touched,
		values,
	} = useFormik({
		initialValues: {
			description: '',
			logoColor: 'outline-0' as LogoColor,
			name: '',
		},
		onSubmit: (values) => {
			const {description, logoColor = 'outline-0', name} = values;

				SpaceService.addSpace({description, name, settings: {logoColor}}).then((response) => {
					if (response.data) {
						navigate(baseRedirectUrl + response.data.id);
					}
				});
			},
			validate: (values) =>
				validate(
					{
						name: [required],
					},
					values
				),
		});

	return (
		<ClayLayout.Row className="p-4">
			<ClayLayout.Col className="mw-50 px-9 w-50">
				<NewSpaceFormSection
					description={Liferay.Language.get(
						'spaces-are-essential-for-organizing-defining-and-managing-your-content-and-files'
					)}
					linkLabel={Liferay.Language.get('learn-more-about-spaces')}
					linkUrl="/"
					onSubmit={handleSubmit}
					step={1}
					title={Liferay.Language.get('create-a-space')}
				>
					<label htmlFor="sicker">
						{Liferay.Language.get('space-logo')}
					</label>

					<SpaceSticker
						className="d-block"
						displayType={values.logoColor}
						hiddenName
						id="sicker"
						name={values.name || 'S'}
						size="xl"
					/>

					<SpaceColorDropdown
						className="my-4"
						onChange={(color) => {
							setFieldValue('logoColor', color);
						}}
					/>

					<FieldText
						errorMessage={touched.name ? errors.name : undefined}
						label={Liferay.Language.get('space-name')}
						name="name"
						onChange={handleChange}
						placeholder={Liferay.Language.get('enter-a-space-name')}
						required
						value={values.name}
					/>

					<Form.Group>
						<label htmlFor="description">
							{Liferay.Language.get('description')}
						</label>

						<ClayInput
							component="textarea"
							id="description"
							name="description"
							onChange={handleChange}
							placeholder={Liferay.Language.get(
								'enter-a-description-for-your-space'
							)}
							type="text"
							value={values.description}
						/>
					</Form.Group>

					<ClayButton.Group className="mb-0 w-100" spaced vertical>
						<ClayButton className="mt-4">
							{Liferay.Language.get('add-members')}
						</ClayButton>

						<ClayButton
							borderless
							className="mt-2"
							disabled={isSubmitting}
							displayType="secondary"
							outline
							type="submit"
						>
							{Liferay.Language.get(
								'create-a-space-without-members'
							)}
						</ClayButton>
					</ClayButton.Group>
				</NewSpaceFormSection>
			</ClayLayout.Col>

			<ClayLayout.Col>
				<img
					aria-hidden="true"
					src={getImage('create_space_step_one_illustration.svg')}
				></img>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
};

export default NewSpace;
