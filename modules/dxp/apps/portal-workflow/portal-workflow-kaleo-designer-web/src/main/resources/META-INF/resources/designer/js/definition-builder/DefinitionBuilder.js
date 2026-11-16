/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import '../../css/definition-builder/main.scss';
import {DefinitionBuilderContextProvider} from './DefinitionBuilderContext';
import DiagramBuilder from './diagram-builder/DiagramBuilder';
import {defaultNodes} from './diagram-builder/components/nodes/utils';
import UpperToolbar from './shared/components/toolbar/UpperToolbar';
import SourceBuilder from './source-builder/SourceBuilder';

export default function DefinitionBuilder(props) {
	const [accountEntryId, setAccountEntryId] = useState(props.accountEntryId);
	const [active, setActive] = useState(true);
	const [blockingError, setBlockingError] = useState({errorType: ''});
	const [currentEditor, setCurrentEditor] = useState(null);
	const [definitionDescription, setDefinitionDescription] = useState('');
	const [definitionInfo, setDefinitionInfo] = useState(null);
	const [definitionName, setDefinitionName] = useState(props.definitionName);
	const [definitionTitle, setDefinitionTitle] = useState(props.title);
	const [definitionTitleTranslations, setDefinitionTitleTranslations] =
		useState(props.translations);
	const [deserialize, setDeserialize] = useState(false);
	const [elements, setElements] = useState(defaultNodes);
	const [hadGroovyOrJavaScriptBefore, setHadGroovyOrJavaScriptBefore] =
		useState(false);
	const [hasGroovyOrJavaScript, setHasGroovyOrJavaScript] = useState(false);
	const [selectedLanguageId, setSelectedLanguageId] = useState('');
	const [showDefinitionInfo, setShowDefinitionInfo] = useState(false);
	const [sourceView, setSourceView] = useState(false);
	const [workflowDefinitionVersions, setWorkflowDefinitionVersions] =
		useState(props.definitionVersions ? props.definitionVersions : []);

	const contextProps = {
		accountEntryId,
		active,
		allowScriptContentToBeExecutedOrIncluded:
			props.allowScriptContentToBeExecutedOrIncluded,
		blockingError,
		currentEditor,
		definitionDescription,
		definitionInfo,
		definitionName,
		definitionTitle,
		definitionTitleTranslations,
		deserialize,
		elements,
		functionActionExecutors: props.functionActionExecutors,
		hadGroovyOrJavaScriptBefore,
		hasGroovyOrJavaScript,
		scriptManagementConfigurationPortletURL:
			props.scriptManagementConfigurationPortletURL,
		selectedLanguageId,
		setAccountEntryId,
		setActive,
		setBlockingError,
		setCurrentEditor,
		setDefinitionDescription,
		setDefinitionInfo,
		setDefinitionName,
		setDefinitionTitle,
		setDefinitionTitleTranslations,
		setDeserialize,
		setElements,
		setHadGroovyOrJavaScriptBefore,
		setHasGroovyOrJavaScript,
		setSelectedLanguageId,
		setShowDefinitionInfo,
		setSourceView,
		setWorkflowDefinitionVersions,
		showDefinitionInfo,
		sourceView,
		statuses: props.statuses,
		timeZoneId: props.timeZoneId,
		versions: props.versions,
		workflowDefinitionVersions,
	};

	return (
		<ReactFlowProvider>
			<DefinitionBuilderContextProvider {...contextProps}>
				<div className="definition-builder-app">
					<UpperToolbar {...props} />

					{sourceView ? <SourceBuilder /> : <DiagramBuilder />}
				</div>
			</DefinitionBuilderContextProvider>
		</ReactFlowProvider>
	);
}
