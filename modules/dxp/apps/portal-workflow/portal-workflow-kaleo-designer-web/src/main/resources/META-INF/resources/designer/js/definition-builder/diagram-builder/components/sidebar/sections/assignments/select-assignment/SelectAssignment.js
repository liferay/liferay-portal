/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelect} from '@clayui/form';
import React, {useContext} from 'react';

import {DefinitionBuilderContext} from '../../../../../../DefinitionBuilderContext';
import {DisabledGroovyScriptAlert} from '../../../../shared-components/DisabledGroovyScriptAlert';
import SidebarPanel from '../../../SidebarPanel';
import HelpIcon from '../../shared-components/HelpIcon';

const options = [
	{
		assignmentType: 'assetCreator',
		label: Liferay.Language.get('asset-creator'),
	},
	{
		assignmentType: 'resourceActions',
		label: Liferay.Language.get('resource-actions'),
	},
	{
		assignmentType: 'roleId',
		label: Liferay.Language.get('role'),
	},
	{
		assignmentType: 'user',
		label: Liferay.Language.get('user'),
	},
	{
		assignmentType: 'roleType',
		label: Liferay.Language.get('role-type'),
	},
	{
		assignmentType: 'scriptedAssignment',
		label: Liferay.Language.get('scripted-assignment'),
	},
];

const SelectAssignment = ({section, setSection, setSections}) => {
	const {
		allowScriptContentToBeExecutedOrIncluded,
		hadGroovyOrJavaScriptBefore,
		hasGroovyOrJavaScript,
		scriptManagementConfigurationPortletURL,
	} = useContext(DefinitionBuilderContext);

	const getAssignmentTypeOptions = () => {
		if (
			!allowScriptContentToBeExecutedOrIncluded &&
			!hadGroovyOrJavaScriptBefore
		) {
			return options.filter(
				(option) => option.assignmentType !== 'scriptedAssignment'
			);
		}

		return options;
	};

	return (
		<>
			{!allowScriptContentToBeExecutedOrIncluded &&
				hasGroovyOrJavaScript && (
					<DisabledGroovyScriptAlert
						scriptManagementConfigurationPortletURL={
							scriptManagementConfigurationPortletURL
						}
					/>
				)}

			<SidebarPanel
				panelTitle={Liferay.Language.get('select-assignment')}
			>
				<ClayForm.Group>
					<label htmlFor="assignment-type">
						{Liferay.Language.get('assignment-type')}

						<HelpIcon
							className="ml-2"
							message={Liferay.Language.get(
								'select-the-assignment-type'
							)}
						/>
					</label>

					<ClaySelect
						aria-label="Select"
						id="assignment-type"
						onChange={(event) => {
							setSection(event.target.value);
							setSections([{identifier: `${Date.now()}-0`}]);
						}}
					>
						{getAssignmentTypeOptions().map((item) => (
							<ClaySelect.Option
								disabled={item?.disabled}
								key={item.assignmentType}
								label={item.label}
								selected={item.assignmentType === section}
								value={item.assignmentType}
							/>
						))}
					</ClaySelect>
				</ClayForm.Group>
			</SidebarPanel>
		</>
	);
};

export {SelectAssignment, options};
