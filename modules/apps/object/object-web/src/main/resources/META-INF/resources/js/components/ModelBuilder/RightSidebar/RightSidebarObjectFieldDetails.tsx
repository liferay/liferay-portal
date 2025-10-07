/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayPanel from '@clayui/panel';
import {API, stringUtils} from '@liferay/object-js-components-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';
import {useStore} from 'react-flow-renderer';

import ModalDeletionNotAllowed from '../../ModalDeletionNotAllowed';
import {objectFieldInitialValues} from '../../ObjectField/EditObjectField';
import {EditObjectFieldContent} from '../../ObjectField/EditObjectFieldContent';
import {ModalDeleteObjectField} from '../../ObjectField/ModalDeleteObjectField';
import {useObjectFieldForm} from '../../ObjectField/useObjectFieldForm';
import {useObjectFolderContext} from '../ModelBuilderContext/objectFolderContext';
import {TYPES} from '../ModelBuilderContext/typesEnum';

import './RightSidebarObjectFieldDetails.scss';
import {handleTriggerDeleteObjectField} from '../../ObjectField/deleteObjectFieldUtil';

export function RightSidebarObjectFieldDetails() {
	const [objectFieldDeleteInfo, setObjectFieldDeleteInfo] =
		useState<ObjectFieldDeleteInfoProps>({
			deleteLastPublishedObjectDefinitionObjectField: false,
			deleteObjectFieldObjectValidationRuleSetting: false,
			showObjectFieldDeletionConfirmationModal: false,
			showObjectFieldDeletionNotAllowedModal: false,
		});

	const [
		{
			baseResourceURL,
			filterOperators,
			forbiddenChars,
			forbiddenLastChars,
			forbiddenNames,
			isRootDescendantNode,
			learnResourceContext,
			selectedObjectDefinitionNode,
			selectedObjectField,
			workflowStatuses,
		},
		dispatch,
	] = useObjectFolderContext();
	const store = useStore();

	const {edges, nodes} = store.getState();

	const objectDefinitionNodeData =
		selectedObjectDefinitionNode?.data as ObjectDefinitionNodeData;

	const {errors, handleChange, handleValidate, setValues, values} =
		useObjectFieldForm({
			forbiddenChars,
			forbiddenLastChars,
			forbiddenNames,
			initialValues: objectFieldInitialValues,
			objectFields: objectDefinitionNodeData.objectFields,
			onSubmit: () => {},
		});

	const onSubmit = async (editedObjectField?: Partial<ObjectField>) => {
		const validationErrors = handleValidate(editedObjectField ?? values);

		if (validationErrors.defaultValue) {
			if (errors.defaultValue) {
				openToast({
					message: Liferay.Language.get(
						'please-fill-out-all-required-fields'
					),
					type: 'danger',
				});
			}
		}

		if (!Object.keys(validationErrors).length) {
			let objectField: Partial<ObjectField>;

			if (!editedObjectField) {
				objectField = {...values};
			}
			else {
				objectField = {...editedObjectField};
			}

			delete objectField.defaultValue;
			delete objectField.listTypeDefinitionId;

			try {
				const updatedObjectFieldResponse = await API.save<ObjectField>({
					item: objectField,
					returnValue: true,
					url: `/o/object-admin/v1.0/object-fields/${objectField.id}`,
				});

				if (
					selectedObjectDefinitionNode &&
					updatedObjectFieldResponse
				) {
					dispatch({
						payload: {
							objectDefinitionNodes: nodes,
							objectRelationshipEdges: edges,
							selectedObjectDefinitionNode,
							updatedObjectField:
								updatedObjectFieldResponse as ObjectField,
						},
						type: TYPES.UPDATE_OBJECT_FIELD_NODE_ROW,
					});

					dispatch({
						payload: {
							updatedShowChangesSaved: true,
						},
						type: TYPES.SET_SHOW_CHANGES_SAVED,
					});
				}
			}
			catch (error) {
				openToast({
					autoClose: 15000,
					message: (error as Error).message,
					type: 'danger',
				});
			}
		}
	};

	useEffect(() => {
		if (errors.defaultValue) {
			openToast({
				message: Liferay.Language.get(
					'please-fill-out-all-required-fields'
				),
				type: 'danger',
			});
		}
	}, [errors]);

	return (
		<>
			<div className="lfr-objects__model-builder-right-sidebar-definition-node-title">
				<span>{Liferay.Language.get('field-details')}</span>

				<div className="lfr-objects__model-builder-right-sidebar-definition-node-title-buttons-container">
					{!values.system &&
						values.businessType !== 'Relationship' && (
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('delete')}
								className="lfr-objects__model-builder-right-sidebar-definition-node-title-delete-button"
								displayType="secondary"
								onClick={() =>
									handleTriggerDeleteObjectField({
										baseResourceURL,
										objectFieldId: selectedObjectField?.id!,
										objectFieldLabel:
											stringUtils.getLocalizableLabel({
												fallbackLabel:
													objectDefinitionNodeData.name,
												fallbackLanguageId:
													objectDefinitionNodeData.defaultLanguageId,
												labels: objectDefinitionNodeData.label,
											}),
										onAfterDelete: () => {
											if (
												selectedObjectField &&
												selectedObjectDefinitionNode
											) {
												dispatch({
													payload: {
														objectDefinitionNodes:
															nodes,
														objectRelationshipEdges:
															edges,
														selectedObjectDefinitionNode,
														selectedObjectField,
													},
													type: TYPES.DELETE_OBJECT_FIELD,
												});
											}
										},
										setObjectFieldDeleteInfo,
									})
								}
								symbol="trash"
								title={Liferay.Language.get('delete')}
							/>
						)}
				</div>
			</div>

			<div>
				<div className="lfr-objects__model-builder-right-sidebar-definition-node-content">
					{selectedObjectField?.id && (
						<EditObjectFieldContent
							baseResourceURL={baseResourceURL}
							containerWrapper={ClayPanel}
							creationLanguageId={
								objectDefinitionNodeData.defaultLanguageId
							}
							errors={errors}
							filterOperators={filterOperators}
							handleChange={handleChange}
							isDefaultStorageType={
								objectDefinitionNodeData.storageType ===
								'default'
							}
							isRootDescendantNode={isRootDescendantNode}
							learnResources={learnResourceContext}
							modelBuilder
							objectDefinition={objectDefinitionNodeData}
							objectFieldId={selectedObjectField.id}
							onSubmit={onSubmit}
							readOnly={
								!objectDefinitionNodeData.hasObjectDefinitionUpdateResourcePermission
							}
							setValues={setValues}
							values={values}
							workflowStatuses={workflowStatuses}
						/>
					)}
				</div>
			</div>

			{objectFieldDeleteInfo.showObjectFieldDeletionConfirmationModal && (
				<ModalDeleteObjectField
					handleOnClose={() =>
						setObjectFieldDeleteInfo(
							(prevState: ObjectFieldDeleteInfoProps) => ({
								...prevState,
								showObjectFieldDeletionConfirmationModal: false,
							})
						)
					}
					objectField={values as ObjectField}
					onAfterSubmit={() => {
						if (
							selectedObjectField &&
							selectedObjectDefinitionNode
						) {
							dispatch({
								payload: {
									objectDefinitionNodes: nodes,
									objectRelationshipEdges: edges,
									selectedObjectDefinitionNode,
									selectedObjectField,
								},
								type: TYPES.DELETE_OBJECT_FIELD,
							});
						}
					}}
				/>
			)}

			{objectFieldDeleteInfo?.showObjectFieldDeletionNotAllowedModal && (
				<ModalDeletionNotAllowed
					content={
						objectFieldDeleteInfo?.deleteObjectFieldObjectValidationRuleSetting ? (
							<Text>
								{sub(
									Liferay.Language.get(
										'the-object-field-x-cannot-be-deleted-because-it-is-the-only-custom-object-field-of-the-published-object-definition'
									),
									`${stringUtils.getLocalizableLabel({
										fallbackLabel: values.name,
										fallbackLanguageId:
											objectDefinitionNodeData.defaultLanguageId as Liferay.Language.Locale,
										labels: values.label,
									})}`
								)}
							</Text>
						) : (
							<Text>
								{sub(
									Liferay.Language.get(
										'the-object-field-x-cannot-be-deleted-because-it-is-used-in-a-unique-composite-key-validation'
									),
									`${stringUtils.getLocalizableLabel({
										fallbackLabel: values.name,
										fallbackLanguageId:
											objectDefinitionNodeData.defaultLanguageId as Liferay.Language.Locale,
										labels: values.label,
									})}`
								)}
							</Text>
						)
					}
					onModalClose={() =>
						setObjectFieldDeleteInfo({
							...objectFieldDeleteInfo,
							showObjectFieldDeletionNotAllowedModal: false,
						})
					}
				/>
			)}
		</>
	);
}
