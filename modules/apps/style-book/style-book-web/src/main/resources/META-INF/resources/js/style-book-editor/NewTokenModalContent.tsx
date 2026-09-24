/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {FormikProvider, useFormik} from 'formik';
import {openToast, useId} from 'frontend-js-components-web';
import {escapeHTML, fetch, objectToFormData} from 'frontend-js-web';
import React, {useState} from 'react';

import ModalFormFooter from './ModalFormFooter';
import {FrontendTokenSetOption} from './NewTokenSetModalContent';
import TextField from './TextField';
import {FRONTEND_TOKEN_EDITOR_TYPES} from './constants/frontendTokenEditorTypes';
import EditorTypeField from './new_token/EditorTypeField';
import TokenSetField from './new_token/TokenSetField';
import {required, validate} from './utils/validations';

interface AddFrontendTokenSuccessData {
	customFrontendTokenDefinition: unknown;
	frontendTokensValues: unknown;
}

interface NewTokenFormValues {
	defaultValue: string;
	description: string;
	editorType: string;
	label: string;
	tokenSetName: string;
}

interface NewTokenModalContentProps {
	addFrontendTokenURL: string;
	categoryLabel?: string;
	categoryName: string;
	closeModal: () => void;
	namespace: string;
	onSuccess: (data: AddFrontendTokenSuccessData) => void;
	styleBookEntryId: number;
	tokenSets: FrontendTokenSetOption[];
}

const NewTokenModalContent = ({
	addFrontendTokenURL,
	categoryLabel,
	categoryName,
	closeModal,
	namespace,
	onSuccess,
	styleBookEntryId,
	tokenSets,
}: NewTokenModalContentProps) => {
	const [tokenSetItems, setTokenSetItems] =
		useState<FrontendTokenSetOption[]>(tokenSets);

	const formId = useId();

	const handleSubmit = (values: NewTokenFormValues) => {
		const selectedTokenSet = tokenSetItems.find(
			({name}) => name === values.tokenSetName
		);

		const body = Liferay.Util.ns(namespace, {
			...values,
			categoryLabel: categoryLabel ?? categoryName,
			categoryName,
			styleBookEntryId,
			tokenSetDescription: selectedTokenSet?.description ?? '',
			tokenSetLabel: selectedTokenSet?.label ?? values.tokenSetName,
		});

		return fetch(addFrontendTokenURL, {
			body: objectToFormData(body),
			method: 'POST',
		})
			.then((response) => response.json())
			.then(
				({
					customFrontendTokenDefinition,
					error,
					frontendTokensValues,
				}) => {
					if (error) {
						openToast({
							message: escapeHTML(error),
							type: 'danger',
						});
					}
					else {
						onSuccess({
							customFrontendTokenDefinition,
							frontendTokensValues,
						});
						openToast({
							message: Liferay.Language.get(
								'your-request-completed-successfully'
							),
							type: 'success',
						});
						closeModal();
					}
				}
			)
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	};

	const formik = useFormik<NewTokenFormValues>({
		initialValues: {
			defaultValue: '',
			description: '',
			editorType: FRONTEND_TOKEN_EDITOR_TYPES.default,
			label: '',
			tokenSetName: tokenSets[0]?.name ?? '',
		},
		onSubmit: handleSubmit,
		validate: (values) =>
			validate(
				{
					defaultValue: [required],
					label: [required],
					tokenSetName: [required],
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
				{Liferay.Language.get('new-custom-token')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm id={formId} onSubmit={formik.handleSubmit}>
					<TextField
						label={Liferay.Language.get('token-name')}
						name="label"
						required
					/>

					<EditorTypeField />

					<TextField
						label={Liferay.Language.get('value')}
						name="defaultValue"
						placeholder="#FFF456"
						required
					/>

					<TokenSetField
						onCreate={(tokenSet) =>
							setTokenSetItems((tokenSetItems) => [
								...tokenSetItems,
								tokenSet,
							])
						}
						tokenSets={tokenSetItems}
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
				submitLabel={Liferay.Language.get('create-token')}
			/>
		</FormikProvider>
	);
};

export default NewTokenModalContent;
