/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {useFormik} from 'formik';
import {ILearnResourceContext, openToast} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React from 'react';

import {
	invalidCharacters,
	maxLength,
	nonNumeric,
	notNull,
	required,
	validate,
} from '../../common/components/forms/validations';
import SpaceService from '../../common/services/SpaceService';
import {LogoColor} from '../../common/types/Space';
import focusInvalidElement from '../../common/utils/focusInvalidElement';
import {getImage} from '../../common/utils/getImage';
import {NewSpaceFormSection} from './NewSpaceFormSection';
import BaseFields from './SpaceBaseFields';

export interface NewSpaceProps {
	baseAddSpaceMembersURL: string;
	learnResources: ILearnResourceContext;
}

const NewSpace = ({baseAddSpaceMembersURL, learnResources}: NewSpaceProps) => {
	const {
		errors,
		handleBlur,
		handleChange,
		handleSubmit,
		isSubmitting,
		setFieldValue,
		setSubmitting,
		submitForm,
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

			SpaceService.addSpace({
				description,
				name,
				settings: {logoColor},
			}).then((response) => {
				if (response.data) {
					navigate(
						baseAddSpaceMembersURL +
							'?assetLibraryId=' +
							response.data.id
					);
				}

				if (response.error) {
					setSubmitting(false);
					openToast({
						message: Liferay.Language.get('unable-to-create-space'),
						type: 'danger',
					});
				}
			});
		},
		validate: (values) =>
			validate(
				{
					name: [
						required,
						nonNumeric,
						notNull,
						invalidCharacters(['*']),
						maxLength(150),
					],
				},
				values
			),
	});

	const shouldDisableContinueBtn = isSubmitting || !values.name;

	return (
		<ClayLayout.Row className="m-2 m-md-4">
			<ClayLayout.Col className="px-md-4 px-xl-9" lg={6}>
				<NewSpaceFormSection
					description={Liferay.Language.get(
						'spaces-are-essential-for-organizing-defining-and-managing-your-content-and-files'
					)}
					learnResourceKey="general"
					learnResources={learnResources}
					onSubmit={handleSubmit}
					step={1}
					title={Liferay.Language.get('add-space')}
				>
					<BaseFields
						errors={errors}
						onBlurName={handleBlur}
						onChangeDescription={(value) =>
							setFieldValue('description', value)
						}
						onChangeLogoColor={(color) =>
							setFieldValue('logoColor', color)
						}
						onChangeName={handleChange}
						touched={touched}
						values={values}
					/>

					<ClayButton.Group className="mb-0 w-100" spaced vertical>
						<ClayButton
							className="mt-4"
							disabled={shouldDisableContinueBtn}
							onClick={() => {
								if (errors.name) {
									focusInvalidElement();

									return;
								}

								submitForm();
							}}
						>
							{Liferay.Language.get('continue')}
						</ClayButton>
					</ClayButton.Group>
				</NewSpaceFormSection>
			</ClayLayout.Col>

			<ClayLayout.Col className="d-lg-flex d-none" lg={6}>
				<div className="border overflow-hidden rounded-lg">
					<img
						alt=""
						src={getImage('create_space_step_one_illustration.svg')}
					></img>
				</div>
			</ClayLayout.Col>
		</ClayLayout.Row>
	);
};

export default NewSpace;
