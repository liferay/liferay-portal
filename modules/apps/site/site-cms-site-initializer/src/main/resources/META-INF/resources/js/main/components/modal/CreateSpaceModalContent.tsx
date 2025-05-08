/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {useFormik} from 'formik';
import {navigate} from 'frontend-js-web';
import React from 'react';

import SpaceService from '../../../services/SpaceService';
import {SpaceData} from '../../FDSPropsTransformer/actions/createSpaceAction';
import {FieldText} from '../forms';
import {required, validate} from '../forms/validations';

type Props = {
	action: SpaceData['action'];
	closeModal: () => void;
	redirect: string;
	title: string;
};

export default function CreateSpaceModalContent({
	action,
	closeModal,
	redirect,
	title,
}: Props) {
	const {errors, handleChange, handleSubmit, touched, values} = useFormik({
		initialValues: {
			name: '',
		},
		onSubmit: (values) => {
			const {name} = values;

			SpaceService.addSpace({name}).then((response) => {
				const url = new URL(redirect + '/' + response.id);

				url.searchParams.set('name', name);

				navigate(url.pathname + url.search);
			});
		},
		validate: (values) =>
			validate(
				{
					name: action === 'createSpace' ? [required] : [],
				},
				values
			),
	});

	return (
		<form onSubmit={handleSubmit}>
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body>
				<FieldText
					errorMessage={touched.name ? errors.name : undefined}
					label={Liferay.Language.get('name')}
					name="name"
					onChange={handleChange}
					required
					value={values.name}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton displayType="primary" type="submit">
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
}
