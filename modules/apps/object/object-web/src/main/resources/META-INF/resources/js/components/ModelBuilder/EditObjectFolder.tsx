/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	API,
	ModalEditObjectDefinitionExternalReferenceCode,
	openToast,
} from '@liferay/object-js-components-web';
import {createResourceURL} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';
import {
	Edge,
	Elements,
	FlowElement,
	Node,
	isEdge,
	isNode,
} from 'react-flow-renderer';

import {formatActionURL} from '../../utils/fds';
import {Scope} from '../ObjectDetails/EditObjectDetails';
import {ModalAddObjectField} from '../ObjectField/ModalAddObjectField';
import {ModalAddObjectRelationship} from '../ObjectRelationship/ModalAddObjectRelationship';
import {ModalAddObjectDefinition} from '../ViewObjectDefinitions/ModalAddObjectDefinition';
import {ModalDeleteObjectDefinition} from '../ViewObjectDefinitions/ModalDeleteObjectDefinition';
import {ModalEditObjectFolder} from '../ViewObjectDefinitions/ModalEditObjectFolder';
import {
	getObjectDefinitionInfo,
	getUpdatedModelBuilderStructurePayload,
} from '../ViewObjectDefinitions/objectDefinitionUtil';
import Diagram from './Diagram/Diagram';
import EditObjectFolderHeader from './EditObjectFolderHeader/EditObjectFolderHeader';
import {ModalPublishObjectDefinitions} from './EditObjectFolderHeader/ModalPublishObjectDefinitions';
import EmptyObjectFolderCard from './EmptyObjectFolderCard/EmptyObjectFolderCard';
import LeftSidebar from './LeftSidebar/LeftSidebar';
import {useObjectFolderContext} from './ModelBuilderContext/objectFolderContext';
import {TYPES} from './ModelBuilderContext/typesEnum';
import {RedirectToEditObjectDetailsModal} from './ObjectDefinitionNode/RedirectToEditObjectDetailsModal';
import {RightSideBar} from './RightSidebar/index';
import {LeftSidebarItem, ObjectRelationshipEdgeData} from './types';
import {updatePreviousURLParam} from './utils';

import './EditObjectFolder.scss';
import ModalDeletionNotAllowed from '../ModalDeletionNotAllowed';
import {ModalMoveObjectDefinition} from '../ViewObjectDefinitions/ModalMoveObjectDefinition';

interface EditObjectFolder {
	companies: Scope[];
	objectRelationshipDeletionTypes: LabelValueObject[];
	sites: Scope[];
	viewObjectDefinitionsURL: string;
}

export default function EditObjectFolder({
	companies,
	objectRelationshipDeletionTypes,
	sites,
	viewObjectDefinitionsURL,
}: EditObjectFolder) {
	const [
		{
			baseResourceURL,
			deletedObjectDefinition,
			editObjectDefinitionURL,
			elements,
			isLoadingObjectFolder,
			learnResourceContext,
			leftSidebarItems,
			modelBuilderModals,
			movedObjectDefinitionId,
			objectDefinitionsStorageTypes,
			objectFolderName,
			objectFolders,
			rightSidebarType,
			selectedObjectDefinitionNode,
			selectedObjectFolder,
			showChangesSaved,
		},
		dispatch,
	] = useObjectFolderContext();

	const [
		objectRelationshipParameterRequired,
		setObjectRelationshipParameterRequired,
	] = useState(false);

	const edges = elements.filter((element) => isEdge(element)) as Edge<
		ObjectRelationshipEdgeData[]
	>[];

	const nodes = elements.filter((element) =>
		isNode(element)
	) as Node<ObjectDefinitionNodeData>[];

	const handleDeleteObjectDefinition = (
		deletedObjectDefinition: DeletedObjectDefinition
	) => {
		dispatch({
			payload: {
				deletedObjectDefinition,
			},
			type: TYPES.SET_DELETE_OBJECT_DEFINITION,
		});
	};

	const onAfterAddObjectRelationship = async (
		newObjectRelationship: ObjectRelationship
	) => {
		const payload = await getUpdatedModelBuilderStructurePayload(
			baseResourceURL,
			selectedObjectFolder.name
		);

		if (
			newObjectRelationship.objectDefinitionExternalReferenceCode1 !==
			newObjectRelationship.objectDefinitionExternalReferenceCode2
		) {
			const objectDefinition2 = nodes.find(
				({data}) =>
					data?.externalReferenceCode ===
					newObjectRelationship.objectDefinitionExternalReferenceCode2
			);

			if (objectDefinition2 && objectDefinition2.isHidden) {
				const selectedSidebarItem = leftSidebarItems.find(
					({objectFolderName}) =>
						objectFolderName === selectedObjectFolder.name
				) as LeftSidebarItem;

				dispatch({
					payload: {
						hiddenObjectDefinitionNode: objectDefinition2.isHidden,
						objectDefinitionId: objectDefinition2.data
							?.id as number,
						objectDefinitionName: objectDefinition2.data
							?.name as string,
						objectDefinitionNodes: nodes,
						objectRelationshipEdges: edges,
						selectedSidebarItem,
					},
					type: TYPES.CHANGE_NODE_VIEW,
				});
			}
		}

		dispatch({
			payload: {
				...payload,
				dispatch,
				rightSidebarType: 'objectRelationshipDetails',
				selectedObjectRelationshipId: newObjectRelationship.id,
			},
			type: TYPES.UPDATE_MODEL_BUILDER_STRUCTURE,
		});

		dispatch({
			payload: {
				selectedObjectRelationshipId: newObjectRelationship.id,
			},
			type: TYPES.SET_SELECTED_OBJECT_RELATIONSHIP_EDGE,
		});
	};

	useEffect(() => {
		const makeFetch = async () => {
			if (selectedObjectDefinitionNode) {
				const url = createResourceURL(baseResourceURL, {
					objectDefinitionId: selectedObjectDefinitionNode.data?.id,
					p_p_resource_id:
						'/object_definitions/get_object_relationship_info',
				}).href;

				const {parameterRequired} = await API.fetchJSON<{
					parameterRequired: boolean;
				}>(url);

				setObjectRelationshipParameterRequired(parameterRequired);
			}
		};

		makeFetch();
	}, [baseResourceURL, selectedObjectDefinitionNode]);

	useEffect(() => {
		dispatch({
			payload: {
				isLoadingObjectFolder: true,
			},
			type: TYPES.SET_LOADING_OBJECT_FOLDER,
		});

		const makeFetch = async () => {
			const payload = await getUpdatedModelBuilderStructurePayload(
				baseResourceURL,
				objectFolderName
			);

			dispatch({
				payload: {...payload, dispatch},
				type: TYPES.UPDATE_MODEL_BUILDER_STRUCTURE,
			});

			dispatch({
				payload: {
					isLoadingObjectFolder: false,
				},
				type: TYPES.SET_LOADING_OBJECT_FOLDER,
			});
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [objectFolderName]);

	useEffect(() => {
		if (showChangesSaved) {
			setTimeout(() => {
				dispatch({
					payload: {updatedShowChangesSaved: false},
					type: TYPES.SET_SHOW_CHANGES_SAVED,
				});
			}, 5000);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [showChangesSaved]);

	useEffect(() => {
		if (Object.keys(selectedObjectFolder).length) {
			const makeFetch = async () =>
				await API.putObjectFolderByExternalReferenceCode({
					externalReferenceCode:
						selectedObjectFolder.externalReferenceCode,
					id: selectedObjectFolder.id,
					label: selectedObjectFolder.label,
					name: selectedObjectFolder.name,
					objectFolderItems: selectedObjectFolder.objectFolderItems,
				});

			makeFetch();
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedObjectFolder.objectFolderItems?.length]);

	Liferay.on('beforeNavigate', (event) => {
		const URLparams = new URL(event.path).searchParams;
		const objectFolderNameURLParam = URLparams.get('objectFolderName');

		if (objectFolderNameURLParam) {
			updatePreviousURLParam({
				paramType: 'objectFolderName',
				paramURL: viewObjectDefinitionsURL,
				paramValue: objectFolderName,
			});
		}
	});

	return (
		<>
			{modelBuilderModals.addObjectDefinition && (
				<ModalAddObjectDefinition
					handleOnClose={() =>
						dispatch({
							payload: {
								updatedModelBuilderModals: {
									addObjectDefinition: false,
								},
							},
							type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
						})
					}
					learnResourceContext={learnResourceContext}
					objectDefinitionsStorageTypes={
						objectDefinitionsStorageTypes
					}
					objectFolderExternalReferenceCode={
						selectedObjectFolder.externalReferenceCode
					}
					onAfterSubmit={async (newObjectDefinition) => {
						const objectDefinitionInfo =
							await getObjectDefinitionInfo({
								baseResourceURL,
								objectDefinitionId: newObjectDefinition.id,
							});

						dispatch({
							payload: {
								dbTableName: objectDefinitionInfo.tableName,
								dispatch,
								elements,
								leftSidebarItems,
								newObjectDefinition,
								objectFolders,
								selectedObjectFolder,
							},
							type: TYPES.ADD_OBJECT_DEFINITION_TO_OBJECT_FOLDER,
						});
					}}
					reload={false}
				/>
			)}

			{modelBuilderModals.addObjectField &&
				selectedObjectDefinitionNode?.data && (
					<ModalAddObjectField
						baseResourceURL={baseResourceURL}
						creationLanguageId={
							selectedObjectDefinitionNode.data.defaultLanguageId
						}
						objectDefinitionExternalReferenceCode={
							selectedObjectDefinitionNode.data
								.externalReferenceCode
						}
						objectDefinitionName={
							selectedObjectDefinitionNode.data.name
						}
						onAfterSubmit={(newObjectField) => {
							dispatch({
								payload: {
									newObjectField,
									objectDefinitionExternalReferenceCode:
										selectedObjectDefinitionNode?.data
											?.externalReferenceCode as string,
									objectDefinitionNodes: nodes,
									objectRelationshipEdges: edges,
									selectedObjectDefinitionNode,
								},
								type: TYPES.ADD_OBJECT_FIELD,
							});

							openToast({
								message: Liferay.Language.get(
									'the-field-was-successfully-added'
								),
								type: 'success',
							});

							dispatch({
								payload: {
									updatedModelBuilderModals: {
										addObjectField: false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							});

							dispatch({
								payload: {
									objectDefinitionExternalReferenceCode:
										selectedObjectDefinitionNode.data
											?.externalReferenceCode as string,
									showAllObjectFields:
										selectedObjectDefinitionNode.data
											?.showAllObjectFields as boolean,
								},
								type: TYPES.SET_SHOW_ALL_OBJECT_FIELDS,
							});
						}}
						setVisible={() =>
							dispatch({
								payload: {
									updatedModelBuilderModals: {
										addObjectField: false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							})
						}
					/>
				)}

			{modelBuilderModals.addObjectRelationship &&
				selectedObjectDefinitionNode?.data && (
					<ModalAddObjectRelationship
						baseResourceURL={baseResourceURL}
						handleOnClose={() => {
							dispatch({
								payload: {
									updatedModelBuilderModals: {
										addObjectRelationship: false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							});
						}}
						learnResources={learnResourceContext}
						objectDefinitionExternalReferenceCode1={
							selectedObjectDefinitionNode.data
								.externalReferenceCode
						}
						objectRelationshipParameterRequired={
							objectRelationshipParameterRequired
						}
						onAfterAddObjectRelationship={(newObjectRelationship) =>
							onAfterAddObjectRelationship(newObjectRelationship)
						}
						reload={false}
					/>
				)}

			{modelBuilderModals.deleteObjectDefinition &&
				deletedObjectDefinition && (
					<ModalDeleteObjectDefinition
						handleDeleteObjectDefinition={() =>
							handleDeleteObjectDefinition
						}
						handleOnClose={() => {
							dispatch({
								payload: {
									updatedModelBuilderModals: {
										deleteObjectDefinition: false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							});
						}}
						objectDefinition={deletedObjectDefinition}
					/>
				)}

			{modelBuilderModals.editObjectDefinitionExternalReferenceCode &&
				selectedObjectDefinitionNode?.data && (
					<ModalEditObjectDefinitionExternalReferenceCode
						handleOnClose={() => {
							dispatch({
								payload: {
									updatedModelBuilderModals: {
										editObjectDefinitionExternalReferenceCode:
											false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							});
						}}
						helpMessage={Liferay.Language.get(
							'unique-key-for-referencing-the-object-definition'
						)}
						objectDefinitionExternalReferenceCode={
							selectedObjectDefinitionNode.data
								.externalReferenceCode
						}
						onGetEntity={() =>
							API.getObjectDefinitionById(
								selectedObjectDefinitionNode.data?.id as number
							)
						}
						onObjectDefinitionExternalReferenceCodeChange={(
							externalReferenceCode: string
						) => {
							const updatedElements = elements.map((element) => {
								if (
									isNode(element) &&
									(element as Node<ObjectDefinitionNodeData>)
										.data?.id ===
										selectedObjectDefinitionNode.data?.id
								) {
									return {
										...element,
										data: {
											...element.data,
											externalReferenceCode,
										},
									};
								}

								return element;
							}) as Elements<ObjectDefinitionNodeData>;

							const updatedSelectedObjectFolderItems =
								selectedObjectFolder.objectFolderItems.map(
									(objectFolderItem) => {
										if (
											objectFolderItem.objectDefinitionExternalReferenceCode ===
											selectedObjectDefinitionNode.data
												?.externalReferenceCode
										) {
											return {
												...objectFolderItem,
												objectDefinitionExternalReferenceCode:
													externalReferenceCode,
											};
										}

										return objectFolderItem;
									}
								);

							dispatch({
								payload: {
									updatedSelectedObjectFolder: {
										...selectedObjectFolder,
										objectFolderItems:
											updatedSelectedObjectFolderItems,
									},
								},
								type: TYPES.SET_SELECTED_OBJECT_FOLDER_DETAILS,
							});

							dispatch({
								payload: {
									newElements: updatedElements,
								},
								type: TYPES.SET_ELEMENTS,
							});
						}}
						saveURL={`/o/object-admin/v1.0/object-definitions/${selectedObjectDefinitionNode.data.id}`}
					/>
				)}

			{modelBuilderModals.editObjectFolder && (
				<ModalEditObjectFolder
					externalReferenceCode={
						selectedObjectFolder.externalReferenceCode
					}
					handleOnClose={() => {
						dispatch({
							payload: {
								updatedModelBuilderModals: {
									editObjectFolder: false,
								},
							},
							type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
						});
					}}
					id={selectedObjectFolder.id}
					initialLabel={selectedObjectFolder.label}
					name={selectedObjectFolder.name}
					onAfterSubmit={(editedObjectFolder) => {
						dispatch({
							payload: {
								updatedSelectedObjectFolder: editedObjectFolder,
							},
							type: TYPES.SET_SELECTED_OBJECT_FOLDER_DETAILS,
						});
					}}
				/>
			)}

			{modelBuilderModals.moveObjectDefinition &&
				movedObjectDefinitionId && (
					<ModalMoveObjectDefinition
						handleOnClose={() => {
							dispatch({
								payload: {
									updatedModelBuilderModals: {
										moveObjectDefinition: false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							});
						}}
						objectDefinitionId={movedObjectDefinitionId}
						objectFolders={objectFolders}
						onAfterMoveObjectDefinition={() => {
							setTimeout(async () => {
								const payload =
									await getUpdatedModelBuilderStructurePayload(
										baseResourceURL,
										selectedObjectFolder.name
									);

								dispatch({
									payload: {...payload, dispatch},
									type: TYPES.UPDATE_MODEL_BUILDER_STRUCTURE,
								});
							}, 200);
						}}
						setMoveObjectDefinition={() => {
							dispatch({
								payload: {movedObjectDefinitionId: undefined},
								type: TYPES.SET_MOVED_OBJECT_DEFINITION,
							});
						}}
					/>
				)}

			{modelBuilderModals.objectDefinitionOnRootModelDeletionNotAllowed &&
				Liferay.FeatureFlags['LPD-34594'] && (
					<ModalDeletionNotAllowed
						content={
							<span
								dangerouslySetInnerHTML={{
									__html: Liferay.Language.get(
										'to-delete-this-object-you-must-first-disable-inheritance-and-delete-its-relationships'
									),
								}}
							/>
						}
						onModalClose={() =>
							dispatch({
								payload: {
									updatedModelBuilderModals: {
										objectDefinitionOnRootModelDeletionNotAllowed:
											false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							})
						}
					/>
				)}

			{modelBuilderModals.publishObjectDefinitions && (
				<ModalPublishObjectDefinitions
					disableAutoClose={true}
					dispatch={dispatch}
					elements={elements}
					handleOnClose={() => {
						dispatch({
							payload: {
								updatedModelBuilderModals: {
									publishObjectDefinitions: false,
								},
							},
							type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
						});
					}}
				/>
			)}

			{modelBuilderModals.redirectToEditObjectDefinitionDetails &&
				selectedObjectDefinitionNode?.data && (
					<RedirectToEditObjectDetailsModal
						handleOnClose={() => {
							dispatch({
								payload: {
									updatedModelBuilderModals: {
										redirectToEditObjectDefinitionDetails:
											false,
									},
								},
								type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
							});
						}}
						viewObjectDetailsURL={formatActionURL(
							editObjectDefinitionURL,
							selectedObjectDefinitionNode.data.id
						)}
					/>
				)}

			<EditObjectFolderHeader
				hasDraftObjectDefinitions={elements.some(
					(element) =>
						(element as FlowElement<ObjectDefinitionNodeData>).data
							?.status?.code === 2
				)}
				selectedObjectFolder={selectedObjectFolder}
			/>

			<div className="lfr-objects__model-builder-content">
				<LeftSidebar />

				{!elements.length && !isLoadingObjectFolder && (
					<EmptyObjectFolderCard />
				)}

				<Diagram />

				<RightSideBar.Root>
					{rightSidebarType === 'empty' && <RightSideBar.Empty />}

					{rightSidebarType === 'objectDefinitionDetails' && (
						<RightSideBar.ObjectDefinitionDetails
							companies={companies}
							sites={sites}
						/>
					)}

					{rightSidebarType === 'objectFieldDetails' && (
						<RightSideBar.ObjectFieldDetails />
					)}

					{rightSidebarType === 'objectRelationshipDetails' && (
						<RightSideBar.ObjectRelationshipDetails
							objectRelationshipDeletionTypes={
								objectRelationshipDeletionTypes
							}
						/>
					)}
				</RightSideBar.Root>
			</div>
		</>
	);
}
