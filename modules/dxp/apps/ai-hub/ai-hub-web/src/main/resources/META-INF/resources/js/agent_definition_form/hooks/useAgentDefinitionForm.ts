/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from '@liferay/object-js-components-web';
import {useFormik} from 'formik';
import {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import {generateExternalReferenceCode} from '../../utils/externalReferenceCode';
import {required, requiredLocalized, validate} from '../../utils/validations';
import {DEFAULT_AGENT_DEFINITION} from '../constants';
import {
	disassociateAgentDefinitionFromContentRetriever,
	disassociateAgentDefinitionFromGuardrail,
	getAgentDefinition,
	postAgentDefinition,
	postAgentDefinitionDraft,
	putAgentDefinition,
	putAgentDefinitionDraft,
	putAgentDefinitionToContentRetrievers,
	putAgentDefinitionToGuardrails,
} from '../services/AgentDefinitionService';
import {getContentRetrievers} from '../services/ContentRetrieverService';
import {getGuardrails} from '../services/GuardrailService';
import {AgentDefinition} from '../types/AgentDefinition';
import {ContentRetriever} from '../types/ContentRetriever';
import {Guardrail} from '../types/Guardrail';
import {useRelationshipPicker} from './useRelationshipPicker';

interface UseAgentDefinitionFormProps {
	accountEntryExternalReferenceCode: string;
	editAgentDefinitionURL: string;
	externalReferenceCode: string;
	readOnly: boolean;
}

export function useAgentDefinitionForm({
	accountEntryExternalReferenceCode,
	editAgentDefinitionURL,
	externalReferenceCode,
}: UseAgentDefinitionFormProps) {
	const draftRequestedRef = useRef(false);

	const [published, setPublished] = useState(false);

	const generatedExternalReferenceCode = useMemo(
		() => generateExternalReferenceCode(),
		[]
	);

	const contentRetrievers = useRelationshipPicker<ContentRetriever>({
		deleteRelationship: disassociateAgentDefinitionFromContentRetriever,
		fetchSourceList: getContentRetrievers,
		putRelationship: putAgentDefinitionToContentRetrievers,
	});
	const guardrails = useRelationshipPicker<Guardrail>({
		deleteRelationship: disassociateAgentDefinitionFromGuardrail,
		fetchSourceList: getGuardrails,
		putRelationship: putAgentDefinitionToGuardrails,
	});

	const submitAgentDefinition = async (
		formValues: AgentDefinition,
		draft: boolean
	) => {
		try {
			let response;

			if (!externalReferenceCode) {
				response = await postAgentDefinition(formValues);
			}
			else if (draft) {
				response = await putAgentDefinitionDraft(formValues);
			}
			else {
				response = await putAgentDefinition(
					formValues,
					externalReferenceCode
				);
			}

			if (formValues.externalReferenceCode) {
				await Promise.all([
					contentRetrievers.sync(formValues.externalReferenceCode),
					guardrails.sync(formValues.externalReferenceCode),
				]);
			}

			if (response?.status?.label === (draft ? 'draft' : 'approved')) {
				setPublished(!draft);

				openToast({
					message: Liferay.Language.get('agent-saved-successfully'),
					type: 'success',
				});
			}
			else {
				openToast({
					message: Liferay.Language.get('failed-to-save-agent'),
					type: 'danger',
				});
			}
		}
		catch (error) {
			console.error(error);

			openToast({
				message:
					error instanceof Error && error.message
						? error.message
						: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	const {
		errors,
		handleBlur,
		handleSubmit,
		isSubmitting,
		setErrors,
		setFieldTouched,
		setFieldValue,
		setSubmitting,
		setTouched,
		setValues,
		touched,
		values,
	} = useFormik<AgentDefinition>({
		initialValues: {
			...DEFAULT_AGENT_DEFINITION,
			externalReferenceCode: generatedExternalReferenceCode,
			r_accountToAIHubAgentDefinitions_accountEntryERC:
				accountEntryExternalReferenceCode,
		},
		onSubmit: (formValues) => submitAgentDefinition(formValues, false),
		validate: (formValues) =>
			validate(
				{
					description: [required],
					externalReferenceCode: [required],
					inputVariables: [required],
					outputVariable: [required],
					title_i18n: [requiredLocalized],
				},
				formValues
			),

		// Only validate when the user attempts to publish. Validating on
		// every change or blur would flag the mandatory fields as required
		// while the user is still filling in a draft, even though Save as
		// Draft never requires them.

		validateOnBlur: false,
		validateOnChange: false,
	});

	const setField = useCallback(
		<K extends keyof AgentDefinition>(
			field: K,
			value: AgentDefinition[K]
		) => {
			setFieldValue(field as string, value);
		},
		[setFieldValue]
	);

	const handleSaveAsDraft = async () => {
		setErrors({});
		setTouched({}, false);

		setSubmitting(true);

		try {
			await submitAgentDefinition(values, true);
		}
		finally {
			setSubmitting(false);
		}
	};

	const {reset: resetContentRetrievers} = contentRetrievers;
	const {reset: resetGuardrails} = guardrails;

	useEffect(() => {
		async function loadOrCreateDraft() {
			if (!externalReferenceCode) {
				if (draftRequestedRef.current) {
					return;
				}

				draftRequestedRef.current = true;

				try {
					const agentDefinition = await postAgentDefinitionDraft();

					window.location.replace(
						`${editAgentDefinitionURL}?externalReferenceCode=` +
							`${encodeURIComponent(
								agentDefinition.externalReferenceCode
							)}&workflowDefinitionName=` +
							`${encodeURIComponent(
								agentDefinition.workflowDefinitionName
							)}`
					);
				}
				catch (error) {
					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				}

				return;
			}

			try {
				const agentDefinition = await getAgentDefinition(
					externalReferenceCode
				);

				setValues({
					active: agentDefinition.active,
					description: agentDefinition.description,
					externalReferenceCode:
						agentDefinition.externalReferenceCode,
					inputVariables: agentDefinition.inputVariables,
					outputVariable: agentDefinition.outputVariable,
					r_accountToAIHubAgentDefinitions_accountEntryERC:
						agentDefinition.r_accountToAIHubAgentDefinitions_accountEntryERC,
					title_i18n: agentDefinition.title_i18n,
					workflowDefinitionName:
						agentDefinition.workflowDefinitionName,
				});

				setPublished(agentDefinition.status?.label === 'approved');

				resetContentRetrievers(
					agentDefinition.agentDefinitionsToContentRetrievers || []
				);
				resetGuardrails(
					agentDefinition.aiHubAgentDefinitionsToAIHubGuardrails || []
				);
			}
			catch (error) {
				openToast({
					message: Liferay.Language.get('failed-to-load-agent-data'),
					type: 'danger',
				});
			}
		}

		loadOrCreateDraft();
	}, [
		editAgentDefinitionURL,
		externalReferenceCode,
		resetContentRetrievers,
		resetGuardrails,
		setValues,
	]);

	return {
		contentRetrievers,
		errors,
		guardrails,
		handleBlur,
		handleSaveAsDraft,
		handleSubmit,
		isSubmitting,
		published,
		setField,
		setFieldTouched,
		touched,
		values,
	};
}
