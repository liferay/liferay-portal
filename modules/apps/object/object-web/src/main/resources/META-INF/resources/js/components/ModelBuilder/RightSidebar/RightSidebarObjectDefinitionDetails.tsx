/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {API, stringUtils} from '@liferay/object-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';
import {useStore} from 'react-flow-renderer';

import {AccountRestrictionContainer} from '../../ObjectDetails/AccountRestrictionContainer';
import {ConfigurationContainer} from '../../ObjectDetails/ConfigurationContainer';
import {Scope} from '../../ObjectDetails/EditObjectDetails';
import {EntryDisplayContainer} from '../../ObjectDetails/EntryDisplayContainer';
import {ObjectDataContainer} from '../../ObjectDetails/ObjectDataContainer';
import {ScopeContainer} from '../../ObjectDetails/ScopeContainer';
import {SeoContainer} from '../../ObjectDetails/SeoContainer';
import {TranslationsContainer} from '../../ObjectDetails/TranslationsContainer';
import {useObjectDetailsForm} from '../../ObjectDetails/useObjectDetailsForm';
import {useObjectFolderContext} from '../ModelBuilderContext/objectFolderContext';
import {TYPES} from '../ModelBuilderContext/typesEnum';
import {nonRelationshipObjectFieldsInfo} from '../types';

import './RightSidebarObjectDefinitionDetails.scss';
import {getObjectDefinitionInfo} from '../../ViewObjectDefinitions/objectDefinitionUtil';
import WorkflowContainer from '../../WorkflowContainer';

interface RightSidebarObjectDefinitionDetailsProps {
	companies: Scope[];
	sites: Scope[];
}

function setAccountRelationshipFieldMandatory(
	values: Partial<ObjectDefinition>
) {
	const {objectFields} = values;

	const newObjectFields = objectFields?.map((objectField) => {
		if (objectField.name === values.accountEntryRestrictedObjectFieldName) {
			return {
				...objectField,
				required: true,
			};
		}

		return objectField;
	});

	return {
		...values,
		objectFields: newObjectFields,
	};
}

export function RightSidebarObjectDefinitionDetails({
	companies,
	sites,
}: RightSidebarObjectDefinitionDetailsProps) {
	const [
		nonRelationshipObjectFieldsInfo,
		setNonRelationshipObjectFieldsInfo,
	] = useState<nonRelationshipObjectFieldsInfo[]>();

	const [workflowLabel, setWorkflowLabel] = useState('');

	const [
		{baseResourceURL, selectedObjectDefinitionNode, selectedObjectFolder},
		dispatch,
	] = useObjectFolderContext();

	const store = useStore();

	const {errors, handleChange, handleValidate, setValues, values} =
		useObjectDetailsForm({
			initialValues: {
				defaultLanguageId: 'en_US',
				externalReferenceCode: '',
				id: 0,
				label: {},
				name: '',
				pluralLabel: {},
				titleObjectFieldName: '',
			},
			onSubmit: () => {},
		});

	const isRootDescendantNode =
		!!values.rootObjectDefinitionExternalReferenceCode &&
		values.externalReferenceCode !==
			values.rootObjectDefinitionExternalReferenceCode;

	useEffect(() => {
		const makeFetch = async () => {
			if (selectedObjectDefinitionNode) {
				const selectedObjectDefinition =
					await API.getObjectDefinitionByExternalReferenceCode(
						selectedObjectDefinitionNode.data
							?.externalReferenceCode as string
					);

				const newNonRelationshipObjectFieldsInfo =
					selectedObjectDefinition.objectFields
						.filter(
							(objectField) =>
								objectField.businessType !== 'Relationship'
						)
						.map((objectField) => ({
							label: objectField.label,
							name: objectField.name,
						})) as nonRelationshipObjectFieldsInfo[];

				const objectDefinitionInfo = await getObjectDefinitionInfo({
					baseResourceURL,
					objectDefinitionId: selectedObjectDefinitionNode.data
						?.id as number,
				});

				setNonRelationshipObjectFieldsInfo(
					newNonRelationshipObjectFieldsInfo
				);
				setValues(selectedObjectDefinition);
				setWorkflowLabel(objectDefinitionInfo.workflowDefinitionTitle);
			}
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedObjectDefinitionNode?.id]);

	const onSubmit = async (
		editedObjectDefinition?: Partial<ObjectDefinition>
	) => {
		const validationErrors = handleValidate(
			editedObjectDefinition ?? values
		);

		if (!Object.keys(validationErrors).length) {
			let objectDefinition = editedObjectDefinition ?? values;

			delete objectDefinition.objectRelationships;
			delete objectDefinition.objectActions;
			delete objectDefinition.objectLayouts;
			delete objectDefinition.objectViews;

			if (objectDefinition.accountEntryRestricted) {
				objectDefinition =
					setAccountRelationshipFieldMandatory(objectDefinition);
			}

			try {
				const updatedObjectDefinitionResponse =
					await API.patchObjectDefinitionById(objectDefinition);

				const updatedObjectDefinition =
					(await updatedObjectDefinitionResponse.json()) as ObjectDefinition;

				const {edges, nodes} = store.getState();

				dispatch({
					payload: {
						currentObjectFolderName: selectedObjectFolder.name,
						objectDefinitionNodes: nodes,
						objectDefinitionRelationshipEdges: edges,
						updatedObjectDefinition,
					},
					type: TYPES.UPDATE_OBJECT_DEFINITION_NODE,
				});

				dispatch({
					payload: {
						updatedShowChangesSaved: true,
					},
					type: TYPES.SET_SHOW_CHANGES_SAVED,
				});
			}
			catch (error: unknown) {
				const {message} = error as Error;

				openToast({autoClose: 15000, message, type: 'danger'});
			}
		}
	};

	const objectDefinitionNodeDetailsTitle = sub(
		Liferay.Language.get('x-details'),
		stringUtils.getLocalizableLabel({
			fallbackLabel: values?.name,
			fallbackLanguageId:
				values.defaultLanguageId as Liferay.Language.Locale,
			labels: values?.label,
		})
	);

	return (
		<>
			<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-details">
				<div
					className="lfr-objects__model-builder-right-sidebar-object-definition-node-details-title text-truncate"
					title={objectDefinitionNodeDetailsTitle}
				>
					<span>{objectDefinitionNodeDetailsTitle}</span>
				</div>
			</div>
			<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
				<ObjectDataContainer
					dbTableName={
						selectedObjectDefinitionNode?.data?.dbTableName
					}
					errors={errors}
					handleChange={handleChange}
					hasUpdateObjectDefinitionPermission={
						!!values.actions?.update
					}
					isApproved={values.status?.label === 'approved'}
					isLinkedObjectDefinition={
						selectedObjectDefinitionNode?.data
							?.linkedObjectDefinition ?? false
					}
					onSubmit={onSubmit}
					setValues={setValues}
					values={values as ObjectDefinition}
				/>
			</div>
			<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
				<EntryDisplayContainer
					className="lfr-objects__model-builder-right-sidebar-object-definition-entry-display-container"
					errors={errors}
					isLinkedObjectDefinition={
						selectedObjectDefinitionNode?.data
							?.linkedObjectDefinition ?? false
					}
					nonRelationshipObjectFieldsInfo={
						nonRelationshipObjectFieldsInfo ?? []
					}
					objectFields={values.objectFields ?? []}
					onSubmit={onSubmit}
					setValues={setValues}
					values={values as ObjectDefinition}
				/>

				<ScopeContainer
					className="lfr-objects__model-builder-right-sidebar-object-definition-entry-display-container"
					companies={companies}
					errors={errors}
					hasUpdateObjectDefinitionPermission={true}
					isApproved={values.status?.label === 'approved'}
					isLinkedObjectDefinition={
						selectedObjectDefinitionNode?.data
							?.linkedObjectDefinition ?? false
					}
					isRootDescendantNode={isRootDescendantNode}
					onSubmit={onSubmit}
					setValues={setValues}
					sites={sites}
					values={values as ObjectDefinition}
				/>
			</div>
			{Liferay.FeatureFlags['LPD-34594'] &&
				values.scope === 'company' &&
				values.status?.label === 'approved' && (
					<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
						<WorkflowContainer
							baseResourceURL={baseResourceURL}
							className="lfr-objects__model-builder-right-sidebar-section"
							objectDefinitionId={
								selectedObjectDefinitionNode?.data?.id as number
							}
							workflowLabel={workflowLabel}
						/>
					</div>
				)}
			{values?.modifiable && (
				<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
					<AccountRestrictionContainer
						errors={errors}
						isApproved={values?.status?.label === 'approved'}
						isLinkedObjectDefinition={
							selectedObjectDefinitionNode?.data
								?.linkedObjectDefinition ?? false
						}
						isRootDescendantNode={isRootDescendantNode}
						objectFields={
							(values?.objectFields as ObjectField[]) ?? []
						}
						onSubmit={onSubmit}
						setValues={setValues}
						values={values as ObjectDefinition}
					/>
				</div>
			)}
			<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
				<ConfigurationContainer
					hasUpdateObjectDefinitionPermission={
						!!values.actions?.update
					}
					isLinkedObjectDefinition={
						selectedObjectDefinitionNode?.data
							?.linkedObjectDefinition ?? false
					}
					isRootDescendantNode={isRootDescendantNode}
					onSubmit={onSubmit}
					setValues={setValues}
					values={values as ObjectDefinition}
				/>
			</div>
			<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
				<TranslationsContainer
					onSubmit={onSubmit}
					setValues={setValues}
					values={values}
				/>
			</div>
			{Liferay.FeatureFlags['LPD-21926'] && (
				<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
					<SeoContainer
						onSubmit={onSubmit}
						setValues={setValues}
						values={values}
					/>
				</div>
			)}
		</>
	);
}
