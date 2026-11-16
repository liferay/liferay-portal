/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {ReactNode, createContext} from 'react';
import {Elements} from 'react-flow-renderer';

interface DefinitionBuilderContextProviderProps {
	accountEntryId: string;
	active: boolean;
	allowScriptContentToBeExecutedOrIncluded: boolean;
	blockingError: {
		errorType: string;
	};
	children?: ReactNode;
	currentError: any;
	definitionDescription: string;
	definitionInfo: DefinitionInfo;
	definitionName: string;
	definitionTitle: string;
	definitionTitleTranslations: Liferay.Language.FullyLocalizedValue<string>;
	deserialize: boolean;
	elements: Elements[];
	functionActionExecutors: any[];
	hadGroovyJavaScriptBefore: boolean;
	hasGroovyJavaScript: boolean;
	scriptManagementConfigurationPortletURL: string;
	setAccountEntryId: () => void;
	setActive: () => void;
	setBlockingError: () => void;
	setCurrentError: () => void;
	setDefinitionDescription: () => void;
	setDefinitionId: () => void;
	setDefinitionName: (value: string) => void;
	setDefinitionTitleTranslations: () => void;
	setDeserialize: () => void;
	setElements: () => void;
	setHadGroovyJavaScriptBefore: () => void;
	setHasGroovyJavaScript: () => void;
	setScriptManagementConfigurationPortletURL: () => void;
	setWorkflowDefinitionVersions: React.Dispatch<
		React.SetStateAction<WorkflowDefinitionVersion[]>
	>;
	showDefinitionInfo: boolean;
	sourceView: boolean;
	statuses: LabelValueObject<number>[];
	timeZoneId: string;
	workflowDefinitionVersions: WorkflowDefinitionVersion[];
}

const DefinitionBuilderContext = createContext(
	{} as DefinitionBuilderContextProviderProps
);

const DefinitionBuilderContextProvider = ({
	children,
	...props
}: DefinitionBuilderContextProviderProps) => {
	return (
		<DefinitionBuilderContext.Provider value={props}>
			{children}
		</DefinitionBuilderContext.Provider>
	);
};

export {DefinitionBuilderContext, DefinitionBuilderContextProvider};
