/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from '@liferay/object-js-components-web';
import {useFormik} from 'formik';
import {useCallback, useEffect, useMemo} from 'react';

import {generateExternalReferenceCode} from '../../utils/externalReferenceCode';
import {required, requiredLocalized, validate} from '../../utils/validations';
import {DEFAULT_AGENT_DEFINITION} from '../constants';
import {
	disassociateAgentDefinitionFromContentRetriever,
	disassociateAgentDefinitionFromGuardrail,
	getAgentDefinition,
	postAgentDefinition,
	putAgentDefinition,
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
	externalReferenceCode: string;
	readOnly: boolean;
}

export function useAgentDefinitionForm({
	accountEntryExternalReferenceCode,
	externalReferenceCode,
}: UseAgentDefinitionFormProps) {
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

	const {
		errors,
		handleBlur,
		handleSubmit,
		isSubmitting,
		setFieldTouched,
		setFieldValue,
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
		onSubmit: async (formValues) => {
			try {
				const response = externalReferenceCode
					? await putAgentDefinition(
							formValues,
							externalReferenceCode
						)
					: await postAgentDefinition(formValues);

				if (formValues.externalReferenceCode) {
					await Promise.all([
						contentRetrievers.sync(
							formValues.externalReferenceCode
						),
						guardrails.sync(formValues.externalReferenceCode),
					]);
				}

				if (response?.status?.label === 'approved') {
					openToast({
						message: Liferay.Language.get(
							'agent-saved-successfully'
						),
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
							: Liferay.Language.get(
									'an-unexpected-error-occurred'
								),
					type: 'danger',
				});
			}
		},
		validate: (formValues) =>
			validate(
				{
					description: [required],
					externalReferenceCode: [required],
					inputVariables: [required],
					outputVariable: [required],
					title_i18n: [requiredLocalized],
					workflowDefinitionName: [required],
				},
				formValues
			),
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

	const {reset: resetContentRetrievers} = contentRetrievers;
	const {reset: resetGuardrails} = guardrails;

	useEffect(() => {
		async function fetchFormData() {
			if (!externalReferenceCode) {
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

		fetchFormData();
	}, [
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
		handleSubmit,
		isSubmitting,
		setField,
		setFieldTouched,
		touched,
		values,
	};
}
