/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {createPortletURL} from 'frontend-js-web';
import React, {useState} from 'react';

import './WorkflowContainer.scss';
import {getObjectDefinitionInfo} from './ViewObjectDefinitions/objectDefinitionUtil';

interface WorkflowContainerProps {
	baseResourceURL: string;
	className: string;
	objectDefinitionId: number;
	workflowLabel: string;
}

const processBuilderConfigurationURL = createPortletURL(
	Liferay.ThemeDisplay.getLayoutRelativeControlPanelURL(),
	{
		p_p_id: 'com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet',
		p_p_lifecycle: 0,
		p_p_mode: 'view',
		p_p_state: 'maximized',
		tab: 'configuration',
	}
);

export default function WorkflowContainer({
	baseResourceURL,
	className,
	objectDefinitionId,
	workflowLabel: initialWorkflowLabel,
}: WorkflowContainerProps) {
	const [workflowLabel, setWorkflowLabel] = useState('');

	async function updateWorkflowLabel() {
		const objectDefinitionInfo = await getObjectDefinitionInfo({
			baseResourceURL,
			objectDefinitionId,
		});

		setWorkflowLabel(objectDefinitionInfo.workflowDefinitionTitle);
	}

	return (
		<ClayForm.Group className={className}>
			<label htmlFor="workflowLabelInput">
				{Liferay.Language.get('workflow')}
			</label>

			<Text as="p" color="secondary" size={2} weight="light">
				{Liferay.Language.get(
					'to-set-a-workflow-go-to-applications-process-builder-configuration'
				)}
			</Text>

			<div className="lfr-objects__workflow-details-section">
				<ClayInput
					className="form-control lfr-objects__workflow-input"
					disabled
					id="workflowLabelInput"
					placeholder={Liferay.Language.get('no-workflow')}
					readOnly
					value={workflowLabel ? workflowLabel : initialWorkflowLabel}
				/>

				<div className="lfr-objects__workflow-buttons">
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('refresh')}
						className="lfr-objects__workflow-reload-button"
						data-tooltip-align="top"
						displayType="secondary"
						onClick={updateWorkflowLabel}
						symbol="reload"
						title={Liferay.Language.get('refresh')}
					/>

					<ClayButton
						aria-label={Liferay.Language.get(
							'process-builder-configurations'
						)}
						className="lfr-objects__workflow-process-builder-button"
						displayType="secondary"
						onClick={() => {
							window.open(
								processBuilderConfigurationURL,
								'_blank'
							);
						}}
					>
						<span className="icon">
							{Liferay.Language.get(
								'process-builder-configurations'
							)}
						</span>

						<ClayIcon symbol="shortcut" />
					</ClayButton>
				</div>
			</div>
		</ClayForm.Group>
	);
}
