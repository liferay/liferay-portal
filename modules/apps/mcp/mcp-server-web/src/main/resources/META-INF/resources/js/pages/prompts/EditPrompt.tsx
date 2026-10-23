/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {Form, FormikProvider, useFormik} from 'formik';
import {navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {FormField} from '../../components/forms/FormField';
import {FormSection} from '../../components/forms/FormSection';
import {FormToggle} from '../../components/forms/FormToggle';
import useRequest from '../../hooks/useRequest';
import PromptService from '../../services/PromptService';
import {Prompt, PromptFormValues, PromptPayload} from '../../types/Prompt';
import required from '../../utils/required';
import toIdentifier from '../../utils/toIdentifier';
import {openErrorToast, openSuccessToast} from '../../utils/toasts';

interface EditPromptProps {
	backURL: string;
	promptId: number;
}

export default function EditPrompt({backURL, promptId}: EditPromptProps) {
	const {
		data: prompt,
		error,
		loading,
	} = useRequest(PromptService.getPrompt, promptId > 0 ? promptId : null);

	useEffect(() => {
		if (error) {
			openErrorToast(error);

			navigate(backURL);
		}
	}, [backURL, error]);

	if (loading || error) {
		return (
			<div className="align-items-center d-flex justify-content-center mt-4">
				<ClayLoadingIndicator />
			</div>
		);
	}

	return <EditPromptView backURL={backURL} prompt={prompt} />;
}

interface EditPromptViewProps {
	backURL: string;
	prompt: Prompt | null;
}

function validateIdentifier(value: string): string | undefined {
	const requiredError = required(value);

	if (requiredError) {
		return requiredError;
	}

	return /^[a-z0-9]+(-[a-z0-9]+)*$/.test(value)
		? undefined
		: Liferay.Language.get(
				'please-enter-a-valid-identifier-lowercase-letters-and-numbers-separated-by-single-hyphens'
			);
}

function EditPromptView({backURL, prompt}: EditPromptViewProps) {
	const [identifierChanged, setIdentifierChanged] = useState(false);

	const formik = useFormik<PromptFormValues>({
		initialValues: {
			active: prompt?.promptStatus?.key === 'active',
			description: prompt?.description ?? '',
			identifier: prompt?.identifier ?? '',
			name: prompt?.name ?? '',
			prompt: prompt?.prompt ?? '',
		},
		onSubmit: async (values) => {
			const payload: PromptPayload = {
				description: values.description,
				identifier: values.identifier,
				name: values.name,
				prompt: values.prompt,
				promptStatus: {key: values.active ? 'active' : 'inactive'},
			};

			const {data: saved, error} = prompt?.id
				? await PromptService.patchPrompt(prompt.id, payload)
				: await PromptService.postPrompt(payload);

			if (error) {
				openErrorToast(error);

				return;
			}

			if (saved) {
				openSuccessToast(
					Liferay.Util.sub(
						Liferay.Language.get('x-was-saved-successfully'),
						saved.name
					)
				);

				navigate(backURL);
			}
		},
	});

	const {setFieldValue, values} = formik;

	useEffect(() => {
		if (!prompt?.id && !identifierChanged) {
			setFieldValue('identifier', toIdentifier(values.name));
		}
	}, [identifierChanged, prompt?.id, setFieldValue, values.name]);

	return (
		<FormikProvider value={formik}>
			<Form className="prompt-form" noValidate>
				<FormSection title={Liferay.Language.get('prompt-status')}>
					<div className="align-items-center d-flex justify-content-between">
						<span className="text-secondary">
							{Liferay.Language.get(
								'activate-to-make-this-prompt-available-to-mcp-clients-by-identifier'
							)}
						</span>

						<FormToggle
							ariaLabel={Liferay.Language.get('prompt-status')}
							name="active"
						/>
					</div>
				</FormSection>

				<FormSection
					className="mt-4"
					title={Liferay.Language.get('prompt-information')}
				>
					<FormField
						id="promptName"
						label={Liferay.Language.get('name')}
						name="name"
						required
					/>

					<FormField
						helpMessage={Liferay.Language.get(
							'unique-key-used-by-mcp-clients-to-request-this-prompt-auto-generated-from-name-can-be-modified'
						)}
						id="promptIdentifier"
						label={Liferay.Language.get('identifier')}
						name="identifier"
						onChange={() => setIdentifierChanged(true)}
						required
						validate={validateIdentifier}
					/>

					<FormField
						component="textarea"
						id="promptDescription"
						label={Liferay.Language.get('description')}
						name="description"
						required
					/>
				</FormSection>

				<FormSection
					className="mt-4"
					title={Liferay.Language.get('prompt-content')}
				>
					<FormField
						component="textarea"
						id="promptContent"
						label={Liferay.Language.get('prompt')}
						name="prompt"
						required
					/>
				</FormSection>

				<ClayLayout.SheetFooter>
					<ClayButton
						disabled={formik.isSubmitting}
						displayType="primary"
						type="submit"
					>
						{Liferay.Language.get('save')}
					</ClayButton>

					<ClayButton
						displayType="secondary"
						onClick={() => navigate(backURL)}
						type="button"
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>
				</ClayLayout.SheetFooter>
			</Form>
		</FormikProvider>
	);
}
