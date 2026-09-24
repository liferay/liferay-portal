/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {FormikProvider, useFormik} from 'formik';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import ModalFormFooter from './ModalFormFooter';
import TextField from './TextField';
import {required, unique, validate} from './utils/validations';

export interface FrontendTokenSetOption {
	description?: string;
	label: string;
	name: string;
}

interface NewTokenSetFormValues {
	description: string;
	label: string;
}

interface NewTokenSetModalContentProps {
	closeModal: () => void;
	existingTokenSets: FrontendTokenSetOption[];
	onSuccess: (frontendTokenSet: FrontendTokenSetOption) => void;
}

const NewTokenSetModalContent = ({
	closeModal,
	existingTokenSets,
	onSuccess,
}: NewTokenSetModalContentProps) => {
	const formId = useId();

	const formik = useFormik<NewTokenSetFormValues>({
		initialValues: {
			description: '',
			label: '',
		},
		onSubmit: ({description, label}) => {
			const trimmedLabel = label.trim();

			onSuccess({
				description,
				label: trimmedLabel,
				name: trimmedLabel,
			});
			closeModal();
		},
		validate: (values) =>
			validate(
				{
					label: [
						required,
						unique(
							existingTokenSets.flatMap(({label, name}) => [
								label,
								name,
							]),
							Liferay.Language.get(
								'a-token-set-with-that-name-already-exists'
							)
						),
					],
				},
				values
			),
		validateOnMount: true,
	});

	return (
		<FormikProvider value={formik}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('new-token-set')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm id={formId} onSubmit={formik.handleSubmit}>
					<TextField
						label={Liferay.Language.get('name')}
						name="label"
						required
					/>

					<TextField
						className="mb-0"
						component="textarea"
						label={Liferay.Language.get('description')}
						name="description"
					/>
				</ClayForm>
			</ClayModal.Body>

			<ModalFormFooter
				closeModal={closeModal}
				formId={formId}
				submitLabel={Liferay.Language.get('create-token-set')}
			/>
		</FormikProvider>
	);
};

export default NewTokenSetModalContent;
