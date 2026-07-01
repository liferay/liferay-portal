/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import Link from '@clayui/link';
import React from 'react';

import './AgentDefinitionForm.scss';
import Toolbar from '../components/ToolBar';
import AIProviderPanel from './AIProviderPanel';
import DataSourcesPanel from './DataSourcesPanel';
import DetailsPanel from './DetailsPanel';
import GuardrailsPanel from './GuardrailsPanel';
import VariablesPanel from './VariablesPanel';
import WorkflowPanel from './WorkflowPanel';
import {useAgentDefinitionForm} from './hooks/useAgentDefinitionForm';

const FORM_ID = 'agentDefinitionForm';

interface IProps {
	accountEntryExternalReferenceCode: string;
	backURL: string;
	editAgentDefinitionURL: string;
	externalReferenceCode: string;
	kaleoDesignerNamespace: string;
	readOnly: boolean;
	workflowDefinitionURL: string;
}

export default function AgentDefinitionForm({
	accountEntryExternalReferenceCode,
	backURL,
	editAgentDefinitionURL,
	externalReferenceCode,
	kaleoDesignerNamespace,
	readOnly,
	workflowDefinitionURL,
}: IProps) {
	const {
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
	} = useAgentDefinitionForm({
		accountEntryExternalReferenceCode,
		editAgentDefinitionURL,
		externalReferenceCode,
		readOnly,
	});

	return (
		<>
			<Toolbar
				backURL={backURL}
				title={Liferay.Language.get('create-agent')}
			>
				<Toolbar.Item>
					<Link
						aria-label={Liferay.Language.get('cancel')}
						borderless
						button
						displayType="secondary"
						href={backURL}
						small
					>
						{Liferay.Language.get('cancel')}
					</Link>
				</Toolbar.Item>

				{!published && (
					<Toolbar.Item>
						<Button
							aria-label={Liferay.Language.get('save-as-draft')}
							disabled={readOnly || isSubmitting}
							displayType="secondary"
							onClick={handleSaveAsDraft}
							size="sm"
							type="button"
						>
							{Liferay.Language.get('save-as-draft')}
						</Button>
					</Toolbar.Item>
				)}

				<Toolbar.Item>
					<Button
						aria-label={Liferay.Language.get(
							published ? 'save' : 'publish'
						)}
						disabled={readOnly || isSubmitting}
						form={FORM_ID}
						size="sm"
						type="submit"
					>
						{Liferay.Language.get(published ? 'save' : 'publish')}
					</Button>
				</Toolbar.Item>
			</Toolbar>

			<ClayLayout.ContainerFluid className="agent-definition-form">
				<ClayForm id={FORM_ID} onSubmit={handleSubmit}>
					<ClayLayout.Row>
						<ClayLayout.Col md={12}>
							<DetailsPanel
								errors={errors}
								handleBlur={handleBlur}
								readOnly={readOnly}
								setField={setField}
								setFieldTouched={setFieldTouched}
								touched={touched}
								values={values}
							/>

							<WorkflowPanel
								editAgentDefinitionURL={editAgentDefinitionURL}
								kaleoDesignerNamespace={kaleoDesignerNamespace}
								readOnly={readOnly}
								values={values}
								workflowDefinitionURL={workflowDefinitionURL}
							/>

							<VariablesPanel
								errors={errors}
								handleBlur={handleBlur}
								readOnly={readOnly}
								setField={setField}
								touched={touched}
								values={values}
							/>

							<DataSourcesPanel
								contentRetrievers={contentRetrievers}
								readOnly={readOnly}
							/>

							<GuardrailsPanel
								guardrails={guardrails}
								readOnly={readOnly}
							/>

							<AIProviderPanel model={values.model} />
						</ClayLayout.Col>
					</ClayLayout.Row>
				</ClayForm>
			</ClayLayout.ContainerFluid>
		</>
	);
}
