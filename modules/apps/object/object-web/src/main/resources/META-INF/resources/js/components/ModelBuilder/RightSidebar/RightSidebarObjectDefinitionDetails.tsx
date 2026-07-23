/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useBrowserTabVisibility} from '@liferay/frontend-js-react-web';
import {API, openToast, stringUtils} from '@liferay/object-js-components-web';
import {LearnResourcesContext} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';
import {useStore} from 'react-flow-renderer';

import {checkHasStandaloneEntries} from '../../../utils/checkHasStandaloneEntries';
import {Error, handleErrors} from '../../../utils/errors';
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
import {InheritanceObjectDefinitionAlert} from '../../ObjectDetails/InheritanceObjectDefinitionAlert';
import {SubscriptionsContainer} from '../../ObjectDetails/SubscriptionsContainer';

interface RightSidebarObjectDefinitionDetailsProps {
	companies: Scope[];
	sites: Scope[];
}

function isDescendantObjectDefinition(
	objectDefinition: Partial<ObjectDefinition>
): boolean {
	return (
		!!objectDefinition.rootObjectDefinitionExternalReferenceCode &&
		objectDefinition.externalReferenceCode !==
			objectDefinition.rootObjectDefinitionExternalReferenceCode
	);
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

	const [
		{
			learnResourceContext,
			selectedObjectDefinitionNode,
			selectedObjectFolder,
		},
		dispatch,
	] = useObjectFolderContext();

	const [backEndErrors, setBackEndErrors] = useState<Error>({});
	const [hasStandaloneEntries, setHasStandaloneEntries] = useState(false);

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

				setNonRelationshipObjectFieldsInfo(
					newNonRelationshipObjectFieldsInfo
				);
				setValues(selectedObjectDefinition);

				setHasStandaloneEntries(
					await checkHasStandaloneEntries(
						isDescendantObjectDefinition(selectedObjectDefinition),
						selectedObjectDefinition
					)
				);
			}
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedObjectDefinitionNode?.id]);

	// Refresh the standalone entries check when the tab regains focus, so
	// entries created in another tab are reflected without a manual reload.
	// The initial fetch is owned by the load useEffect above, so skip the
	// first run here to avoid a duplicate request on mount.

	const isBrowserTabVisible = useBrowserTabVisibility();
	const isInitialVisibilityRunRef = useRef(true);
	const valuesRef = useRef(values);

	valuesRef.current = values;

	useEffect(() => {
		if (isInitialVisibilityRunRef.current) {
			isInitialVisibilityRunRef.current = false;

			return;
		}

		if (!isBrowserTabVisible || !valuesRef.current.id) {
			return;
		}

		checkHasStandaloneEntries(
			isDescendantObjectDefinition(valuesRef.current),
			valuesRef.current
		).then(setHasStandaloneEntries);
	}, [isBrowserTabVisible]);

	const onSubmit = async (
		editedObjectDefinition?: Partial<ObjectDefinition>
	) => {
		const validationErrors = handleValidate(
			editedObjectDefinition ?? values
		);

		if (!Object.keys(validationErrors).length) {
			let objectDefinition = editedObjectDefinition ?? {...values};

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

				if (!updatedObjectDefinitionResponse.ok) {
					const errorDetails =
						await updatedObjectDefinitionResponse.json();

					throw errorDetails;
				}
				else {
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

					openToast({
						message: Liferay.Language.get(
							'the-object-was-saved-successfully'
						),
						type: 'success',
					});
				}
			}
			catch (error) {
				const {detail, title} = error as Error;

				handleErrors({detail, title}, setBackEndErrors);

				return;
			}
		}
	};

	const isRootDescendantNode = isDescendantObjectDefinition(values);

	const objectDefinitionNodeDetailsTitle = sub(
		Liferay.Language.get('x-details'),
		stringUtils.getLocalizableLabel({
			fallbackLabel: values?.name,
			fallbackLanguageId:
				values.defaultLanguageId as Liferay.Language.Locale,
			labels: values?.label,
		})
	);

	const showSeoSection =
		values.friendlyURLSeparator !== undefined &&
		!(
			(Liferay.FeatureFlags['LPS-135430'] &&
				values.storageType !== 'default') ||
			(!values.modifiable && values.system)
		);

	const showSubscriptionSection = !(!values.modifiable && values.system);

	return (
		<LearnResourcesContext.Provider value={learnResourceContext}>
			<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-details">
				<div
					className="lfr-objects__model-builder-right-sidebar-object-definition-node-details-title text-truncate"
					title={objectDefinitionNodeDetailsTitle}
				>
					<span>{objectDefinitionNodeDetailsTitle}</span>
				</div>
			</div>

			<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
				{isRootDescendantNode && <InheritanceObjectDefinitionAlert />}

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
					onSubmit={onSubmit}
					setValues={setValues}
					sites={sites}
					values={values as ObjectDefinition}
				/>
			</div>

			{values?.modifiable && (
				<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
					<AccountRestrictionContainer
						errors={errors}
						isApproved={values?.status?.label === 'approved'}
						isLinkedObjectDefinition={
							selectedObjectDefinitionNode?.data
								?.linkedObjectDefinition ?? false
						}
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
					hasStandaloneEntries={hasStandaloneEntries}
					hasUpdateObjectDefinitionPermission={
						!!values.actions?.update
					}
					isApproved={values?.status?.label === 'approved'}
					isEnableObjectEntrySchedule={
						!!values.enableObjectEntrySchedule
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
				<TranslationsContainer />
			</div>

			{showSeoSection && (
				<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
					<SeoContainer
						errors={backEndErrors}
						hasUpdateObjectDefinitionPermission={
							!!values.actions?.update
						}
						isLinkedObjectDefinition={
							selectedObjectDefinitionNode?.data
								?.linkedObjectDefinition ?? false
						}
						onSubmit={onSubmit}
						setErrors={setBackEndErrors}
						setValues={setValues}
						values={values}
					/>
				</div>
			)}

			{showSubscriptionSection && (
				<div className="lfr-objects__model-builder-right-sidebar-object-definition-node-content">
					<SubscriptionsContainer
						hasUpdateObjectDefinitionPermission={
							!!values.actions?.update
						}
						isLinkedObjectDefinition={
							selectedObjectDefinitionNode?.data
								?.linkedObjectDefinition ?? false
						}
						onSubmit={onSubmit}
						setValues={setValues}
						values={values}
					/>
				</div>
			)}
		</LearnResourcesContext.Provider>
	);
}
