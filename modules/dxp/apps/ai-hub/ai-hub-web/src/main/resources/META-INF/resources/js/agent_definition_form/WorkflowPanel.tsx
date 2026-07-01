/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import Icon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import {Provider} from '@clayui/provider';
import React from 'react';

import {AgentDefinition} from './types/AgentDefinition';

interface IProps {
	editAgentDefinitionURL: string;
	kaleoDesignerNamespace: string;
	readOnly: boolean;
	values: AgentDefinition;
	workflowDefinitionURL: string;
}

const WorkflowPanel: React.FC<IProps> = ({
	editAgentDefinitionURL,
	kaleoDesignerNamespace,
	readOnly,
	values,
	workflowDefinitionURL,
}) => {
	const handleWorkflow = () => {
		if (!values.workflowDefinitionName) {
			return;
		}

		const url = new URL(workflowDefinitionURL, window.location.origin);

		url.searchParams.set(
			`${kaleoDesignerNamespace}name`,
			values.workflowDefinitionName
		);
		url.searchParams.set(
			`${kaleoDesignerNamespace}redirect`,
			`${editAgentDefinitionURL}?externalReferenceCode=` +
				`${encodeURIComponent(values.externalReferenceCode)}` +
				`&workflowDefinitionName=` +
				`${encodeURIComponent(values.workflowDefinitionName)}`
		);

		window.location.href = url.toString();
	};

	return (
		<ClayPanel
			className="agent-definition-form-workflow"
			collapsable={false}
		>
			<ClayPanel.Body>
				<div className="agent-definition-form-header">
					<h2>{Liferay.Language.get('workflow')}</h2>

					<Provider spritemap={Liferay.Icons.spritemap}>
						<Button
							disabled={!values.workflowDefinitionName}
							displayType="secondary"
							onClick={handleWorkflow}
						>
							<span className="inline-item inline-item-before">
								<Icon symbol="icon-rule-builder" />
							</span>

							{readOnly
								? Liferay.Language.get('view-workflow')
								: Liferay.Language.get('edit-workflow')}
						</Button>
					</Provider>
				</div>

				<p className="text-secondary">
					{Liferay.Language.get('every-agent-runs-its-own-workflow')}
				</p>
			</ClayPanel.Body>
		</ClayPanel>
	);
};

export default WorkflowPanel;
