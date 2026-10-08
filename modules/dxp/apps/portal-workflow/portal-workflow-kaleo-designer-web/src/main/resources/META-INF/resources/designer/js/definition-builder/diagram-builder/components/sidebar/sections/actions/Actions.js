/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useEffect, useState} from 'react';

import {DefinitionBuilderContext} from '../../../../../DefinitionBuilderContext';
import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import {DisabledGroovyScriptAlert} from '../../../shared-components/DisabledGroovyScriptAlert';
import ActionsInfo from './ActionsInfo';

const Actions = (props) => {
	const {selectedItem} = useContext(DiagramBuilderContext);
	const {
		allowScriptContentToBeExecutedOrIncluded,
		hasGroovyOrJavaScript,
		scriptManagementConfigurationPortletURL,
	} = useContext(DefinitionBuilderContext);

	const {actions} = selectedItem.data;
	const [sections, setSections] = useState([{identifier: `${Date.now()}-0`}]);

	useEffect(() => {
		const sectionsData = [];

		if (actions) {
			for (let i = 0; i < actions.name.length; i++) {
				sectionsData.push({
					description: actions.description[i],
					executionType: actions.executionType[i],
					identifier: `${Date.now()}-${i}`,
					name: actions.name[i],
					priority: actions.priority[i],
					script: actions.script[i],
					scriptLanguage: actions.scriptLanguage[i],
					status: actions?.status[i],
				});
			}

			setSections(sectionsData);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

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

			{sections.map(({identifier}, index) => {
				return (
					<ActionsInfo
						{...props}
						identifier={identifier}
						index={index}
						key={`section-${identifier}`}
						sectionsLength={sections?.length}
						setSections={setSections}
					/>
				);
			})}
		</>
	);
};
export default Actions;
