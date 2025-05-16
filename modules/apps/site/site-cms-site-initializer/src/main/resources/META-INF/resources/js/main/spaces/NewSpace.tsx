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

import SpaceService from '../../structure_builder/services/SpaceService';
import {FieldText} from '../components/forms';
import {required, validate} from '../components/forms/validations';
import {getImage} from '../util/getImage';
import {NewSpaceFormSection} from './NewSpaceFormSection';

export interface NewSpaceProps {
	baseRedirectUrl: string;
}

const NewSpace = ({baseRedirectUrl}: NewSpaceProps) => {
	const {errors, handleChange, handleSubmit, isSubmitting, touched, values} =
		useFormik({
			initialValues: {
				description: '',
				name: '',
			},
			onSubmit: (values) => {
				const {description, name} = values;

				SpaceService.addSpace({description, name}).then((response) => {
					navigate(baseRedirectUrl + response.id);
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
							'enter-a-decription-for-your-space'
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
						{Liferay.Language.get('create-a-space-without-members')}
					</ClayButton>
				</ClayButton.Group>
			</NewSpaceFormSection>

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
