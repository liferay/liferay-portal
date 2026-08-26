/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CountryInfo} from '@liferay/object-js-components-web';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import {Scope} from '../ObjectDetails/EditObjectDetails';
import EditObjectFolder from './EditObjectFolder';
import {ObjectFolderContextProvider} from './ModelBuilderContext/objectFolderContext';

interface CustomObjectFolderWrapperProps {
	baseResourceURL: string;
	ckEditor5Config?: object;
	companies: Scope[];
	countries: CountryInfo[];
	decimalSeparator?: string;
	editObjectDefinitionURL: string;
	filterOperators: TFilterOperators;
	forbiddenChars: string[];
	forbiddenLastChars: string[];
	forbiddenNames: string[];
	hasDepotEntry?: boolean;
	learnResourceContext: ILearnResourceContext;
	metadataObjectFieldNames: string[];
	objectDefinitionPermissionsURL: string;
	objectDefinitionsStorageTypes: LabelValueObject[];
	objectRelationshipDeletionTypes: LabelValueObject[];
	sites: Scope[];
	viewObjectDefinitionsURL: string;
	workflowStatuses: LabelValueObject[];
}

const ReactFlowProviderWrapper = ReactFlowProvider as React.FC<{
	children?: React.ReactNode;
}>;

export default function CustomObjectFolderWrapper({
	baseResourceURL,
	ckEditor5Config,
	companies,
	countries,
	decimalSeparator,
	editObjectDefinitionURL,
	filterOperators,
	forbiddenChars,
	forbiddenLastChars,
	forbiddenNames,
	hasDepotEntry,
	learnResourceContext,
	metadataObjectFieldNames,
	objectDefinitionPermissionsURL,
	objectDefinitionsStorageTypes,
	objectRelationshipDeletionTypes,
	sites,
	viewObjectDefinitionsURL,
	workflowStatuses,
}: CustomObjectFolderWrapperProps) {
	return (
		<ReactFlowProviderWrapper>
			<ObjectFolderContextProvider
				value={{
					baseResourceURL,
					ckEditor5Config,
					countries,
					decimalSeparator,
					editObjectDefinitionURL,
					filterOperators,
					forbiddenChars,
					forbiddenLastChars,
					forbiddenNames,
					hasDepotEntry,
					learnResourceContext,
					metadataObjectFieldNames,
					objectDefinitionPermissionsURL,
					objectDefinitionsStorageTypes,
					workflowStatuses,
				}}
			>
				<EditObjectFolder
					companies={companies}
					objectRelationshipDeletionTypes={
						objectRelationshipDeletionTypes
					}
					sites={sites}
					viewObjectDefinitionsURL={viewObjectDefinitionsURL}
				/>
			</ObjectFolderContextProvider>
		</ReactFlowProviderWrapper>
	);
}
