/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {stringUtils} from '@liferay/object-js-components-web';
import {Edge, Node, isEdge, isNode} from 'react-flow-renderer';

import {getObjectDefinitionNodeActions} from '../../ViewObjectDefinitions/objectDefinitionUtil';
import {ObjectRelationshipMap} from '../Edges/ObjectRelationshipMap';
import {objectRelationshipEdgeFactory} from '../Edges/objectRelationshipEdgeFactory';
import {
	LeftSidebarItem,
	LeftSidebarObjectDefinitionItem,
	ObjectRelationshipEdgeData,
	RightSidebarType,
	TAction,
	TState,
} from '../types';
import {
	getObjectDefinitionNodeNextPosition,
	getObjectDefinitionNodePosition,
	getObjectFolderDiagramCenterPosition,
} from '../utils';
import {
	convertAllObjectFieldsToUnselected,
	objectFieldsCustomSort,
} from './objectFolderReducerUtil';
import {TYPES} from './typesEnum';

export function ObjectFolderReducer(state: TState, action: TAction): TState {
	switch (action.type) {
		case TYPES.ADD_OBJECT_DEFINITION_TO_OBJECT_FOLDER: {
			const {
				dbTableName,
				dispatch,
				elements,
				leftSidebarItems,
				newObjectDefinition,
				objectFolders,
				selectedObjectFolder,
			} = action.payload;

			const objectDefinitionNodes = elements.filter((element) =>
				isNode(element)
			) as Node<ObjectDefinitionNodeData>[];

			const {baseResourceURL, objectDefinitionPermissionsURL} = state;

			let objectDefinitionNodePosition;

			if (objectDefinitionNodes.length) {
				objectDefinitionNodePosition =
					getObjectDefinitionNodeNextPosition(
						selectedObjectFolder.objectFolderItems
					);
			}
			else {
				objectDefinitionNodePosition =
					getObjectFolderDiagramCenterPosition();
			}

			const newLeftSidebarItems = leftSidebarItems.map(
				(leftSidebarItem) => {
					let newLeftSidebarObjectDefinitionItem;

					if (
						leftSidebarItem.objectFolderName ===
						selectedObjectFolder.name
					) {
						const kebabOptions = getObjectDefinitionNodeActions({
							baseResourceURL,
							dispatch,
							hasObjectDefinitionDeleteResourcePermission:
								!!newObjectDefinition.actions.delete,
							hasObjectDefinitionManagePermissionsResourcePermission:
								!!newObjectDefinition.actions.permissions,
							hasObjectDefinitionUpdateResourcePermission:
								!!newObjectDefinition.actions.update,
							isTreeStructure:
								!!newObjectDefinition.objectDefinitionSettings?.some(
									(setting) =>
										setting.name ===
										'rootObjectDefinitionExternalReferenceCodes'
								),
							objectDefinitionId: newObjectDefinition.id,
							objectDefinitionName: newObjectDefinition.name,
							objectDefinitionPermissionsURL,
							objectFoldersLength: objectFolders.length,
							status: newObjectDefinition.status,
						});

						newLeftSidebarObjectDefinitionItem = {
							dbTableName: newObjectDefinition.dbTableName,
							externalReferenceCode:
								newObjectDefinition.externalReferenceCode,
							hiddenObjectDefinitionNode: false,
							id: newObjectDefinition.id,
							kebabOptions,
							label: stringUtils.getLocalizableLabel({
								fallbackLabel: newObjectDefinition.name,
								fallbackLanguageId:
									newObjectDefinition.defaultLanguageId,
								labels: newObjectDefinition.label,
							}),
							name: newObjectDefinition.name,
							selected: true,
							type: !dbTableName
								? 'dummyObjectDefinition'
								: 'objectDefinition',
						} as LeftSidebarObjectDefinitionItem;

						const updatedObjectDefinitions =
							leftSidebarItem.leftSidebarObjectDefinitionItems?.map(
								(leftSidebarObjectDefinitionItem) => {
									return {
										...leftSidebarObjectDefinitionItem,
										selected: false,
									};
								}
							);

						return {
							...leftSidebarItem,
							leftSidebarObjectDefinitionItems:
								newLeftSidebarObjectDefinitionItem
									? [
											...updatedObjectDefinitions!,
											newLeftSidebarObjectDefinitionItem,
										]
									: [...updatedObjectDefinitions!],
						};
					}
					else {
						return {
							...leftSidebarItem,
						};
					}
				}
			) as LeftSidebarItem[];

			const objectFields = newObjectDefinition.objectFields.map(
				(objectField) => {
					return {
						businessType: objectField.businessType,
						externalReferenceCode:
							objectField.externalReferenceCode,
						id: objectField.id,
						label: objectField.label,
						name: objectField.name,
						primaryKey: objectField.name === 'id',
						required: objectField.required,
						selected: false,
					} as ObjectFieldNodeRow;
				}
			);

			selectedObjectFolder.objectFolderItems.push({
				linkedObjectDefinition:
					selectedObjectFolder.externalReferenceCode !==
					newObjectDefinition.objectFolderExternalReferenceCode,
				objectDefinitionExternalReferenceCode:
					newObjectDefinition.externalReferenceCode,
				positionX: objectDefinitionNodePosition.x,
				positionY: objectDefinitionNodePosition.y,
			});

			const updatedObjectFolders = objectFolders.filter(
				(objectFolder) => {
					objectFolder.externalReferenceCode !==
						selectedObjectFolder.externalReferenceCode;
				}
			);

			updatedObjectFolders.push(selectedObjectFolder);

			const updatedElements = elements.map((element) => {
				if (Array.isArray(element.data)) {
					return {
						...element,
						data: element.data.map((objectRelationshipEdgeData) => {
							return {
								...objectRelationshipEdgeData,
								selected: false,
							};
						}),
					};
				}

				return {
					...element,
					data: {
						...element.data,
						selected: false,
					},
				};
			});

			const newObjectDefinitionNode = {
				data: {
					...newObjectDefinition,
					dbTableName,
					hasObjectDefinitionDeleteResourcePermission:
						!!newObjectDefinition.actions.delete,
					hasObjectDefinitionManagePermissionsResourcePermission:
						!!newObjectDefinition.actions.permissions,
					hasObjectDefinitionUpdateResourcePermission:
						!!newObjectDefinition.actions.update,
					hasObjectDefinitionViewResourcePermission: false,
					label: newObjectDefinition.label,
					linkedObjectDefinition: false,
					objectFields: objectFieldsCustomSort(objectFields),
					selected: true,
				},
				id: newObjectDefinition.id.toString(),
				position: objectDefinitionNodePosition,
				type: 'objectDefinitionNode',
			} as Node<ObjectDefinitionNodeData>;

			return {
				...state,
				elements: [...updatedElements, newObjectDefinitionNode] as Node<
					ObjectDefinitionNodeData | ObjectRelationshipEdgeData[]
				>[],
				leftSidebarItems: newLeftSidebarItems,
				objectFolders: updatedObjectFolders,
				rightSidebarType: 'objectDefinitionDetails',
				selectedObjectDefinitionNode: newObjectDefinitionNode,
				selectedObjectFolder,
				showChangesSaved: true,
			};
		}

		case TYPES.ADD_OBJECT_FIELD: {
			const {
				newObjectField,
				objectDefinitionExternalReferenceCode,
				objectDefinitionNodes,
				objectRelationshipEdges,
			} = action.payload;

			const selectedObjectField: ObjectFieldNodeRow = {
				businessType: newObjectField.businessType,
				externalReferenceCode: newObjectField.externalReferenceCode,
				id: newObjectField.id,
				label: newObjectField.label,
				name: newObjectField.name,
				primaryKey: false,
				required: newObjectField.required,
				selected: true,
			};

			let selectedObjectDefinitionNode: Node<ObjectDefinitionNodeData> | null =
				null;

			const newObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					if (
						objectDefinitionNode.data?.externalReferenceCode ===
						objectDefinitionExternalReferenceCode
					) {
						const {objectFields} = objectDefinitionNode.data;

						const newObjectFields =
							convertAllObjectFieldsToUnselected(objectFields);

						newObjectFields.push(selectedObjectField);

						selectedObjectDefinitionNode = {
							...objectDefinitionNode,
							data: {
								...objectDefinitionNode.data,
								objectFields: newObjectFields,
								selected: true,
								showAllObjectFields: true,
							},
						};

						return selectedObjectDefinitionNode;
					}

					const unselectedObjectFields =
						convertAllObjectFieldsToUnselected(
							objectDefinitionNode.data
								?.objectFields as ObjectFieldNodeRow[]
						);

					return {
						...objectDefinitionNode,
						data: {
							...objectDefinitionNode.data,
							objectFields: unselectedObjectFields,
							selected: false,
						},
					};
				}
			) as Node<ObjectDefinitionNodeData>[];

			return {
				...state,
				elements: [
					...newObjectDefinitionNodes,
					...objectRelationshipEdges,
				],
				rightSidebarType: 'objectFieldDetails',
				selectedObjectDefinitionNode,
				selectedObjectField,
			};
		}

		case TYPES.BULK_CHANGE_NODE_VIEW: {
			const {
				hiddenObjectFolderObjectDefinitionNodes,
				objectDefinitionNodes,
				objectRelationshipEdges,
			} = action.payload;
			const {leftSidebarItems} = state;

			const updatedObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode: Node<ObjectDefinitionNodeData>) => {
					return {
						...objectDefinitionNode,
						data: {...objectDefinitionNode.data, selected: false},
						isHidden: !hiddenObjectFolderObjectDefinitionNodes,
					};
				}
			) as Node<ObjectDefinitionNodeData>[];

			const updatedObjectRelationshipEdges = objectRelationshipEdges.map(
				(
					objectRelationshipEdge: Edge<ObjectRelationshipEdgeData[]>
				) => {
					return {
						...objectRelationshipEdge,
						isHidden: !hiddenObjectFolderObjectDefinitionNodes,
					};
				}
			) as Edge<ObjectRelationshipEdgeData[]>[];

			const updatedLeftSidebarItems = leftSidebarItems.map(
				(leftSidebarItem: LeftSidebarItem) => {
					const updateLeftSidebarObjectDefinitionItems =
						leftSidebarItem.leftSidebarObjectDefinitionItems?.map(
							(leftSidebarObjectDefinitionItem) => {
								return {
									...leftSidebarObjectDefinitionItem,
									hiddenObjectDefinitionNode:
										!hiddenObjectFolderObjectDefinitionNodes,
									selected: false,
								};
							}
						);
					if (
						leftSidebarItem.objectFolderName ===
						leftSidebarItem.objectFolderName
					) {
						return {
							...leftSidebarItem,
							hiddenObjectFolderObjectDefinitionNodes:
								!hiddenObjectFolderObjectDefinitionNodes,
							leftSidebarObjectDefinitionItems:
								updateLeftSidebarObjectDefinitionItems,
						};
					}

					return leftSidebarItem;
				}
			);

			return {
				...state,
				elements: [
					...updatedObjectRelationshipEdges,
					...updatedObjectDefinitionNodes,
				],
				leftSidebarItems: updatedLeftSidebarItems,
				rightSidebarType: 'empty',
			};
		}

		case TYPES.CHANGE_NODE_VIEW: {
			const {
				hiddenObjectDefinitionNode,
				objectDefinitionId,
				objectDefinitionName,
				objectDefinitionNodes,
				objectRelationshipEdges,
				selectedSidebarItem,
			} = action.payload;
			const {leftSidebarItems} = state;
			let isObjectDefinitionNodeSelected = false;

			const updatedObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode: Node<ObjectDefinitionNodeData>) => {
					if (objectDefinitionNode.data?.id === objectDefinitionId) {
						return {
							...objectDefinitionNode,
							data: {
								...objectDefinitionNode.data,
								selected: false,
							},
							isHidden: !hiddenObjectDefinitionNode,
						};
					}

					return objectDefinitionNode;
				}
			);

			const getObjectDefinitionNodeById = (id: string) => {
				return updatedObjectDefinitionNodes.find(
					(updatedObjectDefinitionNode) =>
						updatedObjectDefinitionNode.id === id
				) as Node<ObjectDefinitionNodeData>;
			};

			const updatedObjectRelationshipEdges = objectRelationshipEdges.map(
				(
					objectRelationshipEdge: Edge<ObjectRelationshipEdgeData[]>
				) => {
					const sourceNode = getObjectDefinitionNodeById(
						objectRelationshipEdge.source
					);

					const targetNode = getObjectDefinitionNodeById(
						objectRelationshipEdge.target
					);

					const bothNodesVisible =
						!sourceNode.isHidden && !targetNode.isHidden;

					if (
						objectRelationshipEdge.source ===
							objectDefinitionId.toString() ||
						objectRelationshipEdge.target ===
							objectDefinitionId.toString()
					) {
						return {
							...objectRelationshipEdge,
							isHidden: !bothNodesVisible,
						};
					}

					return objectRelationshipEdge;
				}
			);

			const updatedLeftSidebarItems = leftSidebarItems.map(
				(leftSidebarItem) => {
					if (
						leftSidebarItem.objectFolderName ===
						selectedSidebarItem.objectFolderName
					) {
						const updatedObjectDefinitions =
							leftSidebarItem.leftSidebarObjectDefinitionItems?.map(
								(leftSidebarObjectDefinitionItem) => {
									if (
										leftSidebarObjectDefinitionItem.name ===
										objectDefinitionName
									) {
										isObjectDefinitionNodeSelected =
											leftSidebarObjectDefinitionItem.selected;

										return {
											...leftSidebarObjectDefinitionItem,
											hiddenObjectDefinitionNode:
												!hiddenObjectDefinitionNode,
											selected: false,
										};
									}

									return leftSidebarObjectDefinitionItem;
								}
							);

						return {
							...leftSidebarItem,
							leftSidebarObjectDefinitionItems:
								updatedObjectDefinitions,
						};
					}

					return leftSidebarItem;
				}
			);

			return {
				...state,
				elements: [
					...updatedObjectRelationshipEdges,
					...updatedObjectDefinitionNodes,
				],
				leftSidebarItems: updatedLeftSidebarItems,
				rightSidebarType: isObjectDefinitionNodeSelected
					? 'empty'
					: state.rightSidebarType,
			};
		}

		case TYPES.DELETE_OBJECT_FIELD: {
			const {
				objectDefinitionNodes,
				objectRelationshipEdges,
				selectedObjectDefinitionNode,
				selectedObjectField,
			} = action.payload;

			const newSelectedObjectDefinitionNodeObjectFields =
				selectedObjectDefinitionNode?.data?.objectFields.filter(
					(objectField) =>
						objectField.externalReferenceCode !==
						selectedObjectField.externalReferenceCode
				);

			const updatedObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					if (
						objectDefinitionNode.data?.externalReferenceCode ===
						selectedObjectDefinitionNode?.data
							?.externalReferenceCode
					) {
						return {
							...objectDefinitionNode,
							data: {
								...objectDefinitionNode.data,
								objectFields:
									newSelectedObjectDefinitionNodeObjectFields,
								selected: false,
							},
						} as Node<ObjectDefinitionNodeData>;
					}

					return objectDefinitionNode;
				}
			);

			return {
				...state,
				elements: [
					...updatedObjectDefinitionNodes,
					...objectRelationshipEdges,
				],
				rightSidebarType: 'empty',
				selectedObjectField: undefined,
			};
		}

		case TYPES.SET_DELETE_OBJECT_DEFINITION: {
			const {deletedObjectDefinition} = action.payload;

			return {
				...state,
				deletedObjectDefinition,
			};
		}

		case TYPES.SET_ELEMENTS: {
			const {newElements} = action.payload;
			const {selectedObjectDefinitionNode} = state;

			const updatedSelectedObjectDefinitionNode = newElements.find(
				(element) => {
					if (isNode(element) && element.data) {
						return (
							(element.data as ObjectDefinitionNodeData).id ===
							selectedObjectDefinitionNode?.data?.id
						);
					}
				}
			) as Node<ObjectDefinitionNodeData> | null;

			return {
				...state,
				elements: newElements,
				selectedObjectDefinitionNode:
					updatedSelectedObjectDefinitionNode,
			};
		}

		case TYPES.SET_MOVED_OBJECT_DEFINITION: {
			const {movedObjectDefinitionId} = action.payload;

			return {
				...state,
				movedObjectDefinitionId: movedObjectDefinitionId as number,
			};
		}

		case TYPES.SET_NODE_HANDLE_CONNECTION: {
			const {nodeHandleConnectable} = action.payload;

			return {
				...state,
				nodeHandleConnectable,
			};
		}

		case TYPES.SET_OBJECT_FOLDER_NAME: {
			const {objectFolderName} = action.payload;

			return {
				...state,
				objectFolderName,
				rightSidebarType: 'empty',
			};
		}

		case TYPES.SET_LOADING_OBJECT_FOLDER: {
			const {isLoadingObjectFolder} = action.payload;

			return {
				...state,
				isLoadingObjectFolder,
			};
		}

		case TYPES.SET_SELECTED_OBJECT_DEFINITION_NODE: {
			const {
				objectDefinitionNodes,
				objectRelationshipEdges,
				selectedObjectDefinitionId,
			} = action.payload;

			const {leftSidebarItems} = state;

			let selectedObjectDefinitionNode: Node<ObjectDefinitionNodeData> | null =
				null;

			const newObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					const newObjectFields =
						objectDefinitionNode.data?.objectFields.map(
							(objectField) => ({
								...objectField,
								selected: false,
							})
						);

					if (
						objectDefinitionNode.id === selectedObjectDefinitionId
					) {
						selectedObjectDefinitionNode = {
							...objectDefinitionNode,
							data: {
								...objectDefinitionNode.data,
								objectFields: newObjectFields,
								selected: true,
							},
						} as Node<ObjectDefinitionNodeData>;

						return selectedObjectDefinitionNode;
					}

					return {
						...objectDefinitionNode,
						data: {
							...objectDefinitionNode.data,
							objectFields: newObjectFields,
							selected: false,
						},
					};
				}
			) as Node<ObjectDefinitionNodeData>[];

			const newLeftSidebarItems = leftSidebarItems.map((sidebarItem) => {
				const newLeftSidebarObjectDefinitions =
					sidebarItem.leftSidebarObjectDefinitionItems?.map(
						(leftSidebarObjectDefinitionItem) => ({
							...leftSidebarObjectDefinitionItem,
							selected:
								selectedObjectDefinitionId ===
								leftSidebarObjectDefinitionItem.id.toString(),
						})
					);

				return {
					...sidebarItem,
					leftSidebarObjectDefinitionItems:
						newLeftSidebarObjectDefinitions,
				};
			});

			const selectedObjectRelationshipEdge = objectRelationshipEdges.find(
				(objectRelationshipEdge) =>
					objectRelationshipEdge.data?.some(
						(objectRelationshipEdgeData) =>
							objectRelationshipEdgeData.selected
					)
			);

			const newObjectRelationshipEdges = objectRelationshipEdges;

			if (selectedObjectRelationshipEdge?.data) {
				const selectedObjectRelationshipEdgeIndex =
					objectRelationshipEdges.findIndex(
						(objectRelationshipEdge) =>
							objectRelationshipEdge.data?.some(
								(objectRelationshipEdgeData) =>
									objectRelationshipEdgeData.selected
							)
					);

				selectedObjectRelationshipEdge.data =
					selectedObjectRelationshipEdge.data.map(
						(objectRelationshipEdgeData) => {
							return {
								...objectRelationshipEdgeData,
								selected: false,
							};
						}
					);

				newObjectRelationshipEdges[
					selectedObjectRelationshipEdgeIndex
				] = selectedObjectRelationshipEdge;
			}

			return {
				...state,
				elements: [
					...newObjectDefinitionNodes,
					...newObjectRelationshipEdges,
				],
				leftSidebarItems: newLeftSidebarItems,
				rightSidebarType: 'objectDefinitionDetails',
				selectedObjectDefinitionNode,
				selectedObjectField: undefined,
			};
		}

		case TYPES.SET_SELECTED_OBJECT_DEFINITION_NODE_POSITION: {
			const {
				newObjectDefinitionNodePosition,
				objectDefinitionNodes,
				objectRelationshipEdges,
				updatedObjectDefinitionNodeId,
				updatedObjectFolder,
			} = action.payload;

			const newObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					const isSelected =
						objectDefinitionNode.data?.id ===
						updatedObjectDefinitionNodeId;

					return {
						...objectDefinitionNode,
						data: {
							...objectDefinitionNode.data,
							objectFields:
								objectDefinitionNode.data?.objectFields.map(
									(objectField) => {
										return {
											...objectField,
											selected: false,
										};
									}
								),
							selected: isSelected,
						},
						...(isSelected && {
							position: newObjectDefinitionNodePosition,
						}),
					} as Node<ObjectDefinitionNodeData>;
				}
			);

			const newObjectRelationshipEdges = objectRelationshipEdges.map(
				(objectRelationshipEdge) => {
					return {
						...objectRelationshipEdge,
						data: objectRelationshipEdge.data?.map(
							(objectRelationshipEdgeData) => {
								return {
									...objectRelationshipEdgeData,
									selected: false,
								};
							}
						),
					} as Edge<ObjectRelationshipEdgeData[]>;
				}
			);

			return {
				...state,
				elements: [
					...newObjectDefinitionNodes,
					...newObjectRelationshipEdges,
				],
				selectedObjectFolder: updatedObjectFolder,
			};
		}

		case TYPES.SET_SELECTED_OBJECT_FIELD: {
			const {
				objectDefinitionNodes,
				objectRelationshipEdges,
				selectedObjectDefinitionId,
				selectedObjectField,
				selectedObjectFieldName,
			} = action.payload;

			let selectedObjectDefinitionNode: Node<ObjectDefinitionNodeData> | null =
				null;

			const newObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					const newObjectDefinitionNode = {
						...objectDefinitionNode,
						data: {
							...objectDefinitionNode.data,
							objectFields:
								objectDefinitionNode.data?.objectFields.map(
									(objectField) => ({
										...objectField,
										selected:
											objectDefinitionNode.data?.id ===
												selectedObjectDefinitionId &&
											objectField.name ===
												selectedObjectFieldName,
									})
								),
							selected:
								objectDefinitionNode.data?.id ===
								selectedObjectDefinitionId,
						},
					} as Node<ObjectDefinitionNodeData>;

					if (
						objectDefinitionNode.data?.id ===
						selectedObjectDefinitionId
					) {
						selectedObjectDefinitionNode = newObjectDefinitionNode;
					}

					return newObjectDefinitionNode;
				}
			) as Node<ObjectDefinitionNodeData>[];

			const newObjectRelationshipEdges = objectRelationshipEdges.map(
				(objectRelationshipEdge) => ({
					...objectRelationshipEdge,
					data: objectRelationshipEdge.data?.map(
						(objectRelationshipEdgeData) => {
							return {
								...objectRelationshipEdgeData,
								selected: false,
							};
						}
					),
				})
			) as Edge<ObjectRelationshipEdgeData[]>[];

			return {
				...state,
				elements: [
					...newObjectDefinitionNodes,
					...newObjectRelationshipEdges,
				],
				rightSidebarType: 'objectFieldDetails',
				selectedObjectDefinitionNode,
				selectedObjectField,
			};
		}

		case TYPES.SET_SELECTED_OBJECT_FOLDER_DETAILS: {
			const {leftSidebarItems} = state;
			const {updatedSelectedObjectFolder} = action.payload;

			const updatedLeftSidebarItems: LeftSidebarItem[] =
				leftSidebarItems.map((leftSidebarItem) => {
					if (
						leftSidebarItem.objectFolderName ===
						updatedSelectedObjectFolder.name
					) {
						return {
							...leftSidebarItem,
							name: stringUtils.getLocalizableLabel({
								fallbackLabel: updatedSelectedObjectFolder.name,
								labels: updatedSelectedObjectFolder.label,
							}),
						};
					}

					return leftSidebarItem;
				});

			return {
				...state,
				leftSidebarItems: updatedLeftSidebarItems,
				selectedObjectFolder: updatedSelectedObjectFolder,
			};
		}

		case TYPES.SET_SELECTED_OBJECT_RELATIONSHIP_EDGE: {
			const {selectedObjectRelationshipId} = action.payload;

			const {elements} = state;

			const edges = elements.filter((element) => isEdge(element)) as Edge<
				ObjectRelationshipEdgeData[]
			>[];

			const nodes = elements.filter((element) =>
				isNode(element)
			) as Node<ObjectDefinitionNodeData>[];

			let selectedObjectRelationship:
				| ObjectRelationshipEdgeData
				| undefined;

			const newObjectRelationshipEdges = edges.map(
				(objectRelationshipEdge) => {
					const newObjectRelationshipEdgeData =
						objectRelationshipEdge?.data?.map(
							(objectRelationshipEdgeData) => {
								if (
									objectRelationshipEdgeData.id ===
									selectedObjectRelationshipId
								) {
									selectedObjectRelationship = {
										...objectRelationshipEdgeData,
										selected: true,
									};
								}

								return {
									...objectRelationshipEdgeData,
									selected:
										objectRelationshipEdgeData.id ===
										selectedObjectRelationshipId,
								};
							}
						);

					return {
						...objectRelationshipEdge,
						data: newObjectRelationshipEdgeData,
					};
				}
			) as Edge<ObjectRelationshipEdgeData[]>[];

			const selectedObjectDefinitionNode = nodes.find(
				(objectDefinitionNode) => objectDefinitionNode.data?.selected
			);

			const newObjectDefinitionNodes = nodes;

			if (selectedObjectDefinitionNode?.data) {
				const {objectFields} = selectedObjectDefinitionNode.data;
				const selectedObjectDefinitionNodeIndex = nodes.findIndex(
					(objectDefinitionNode) =>
						objectDefinitionNode.data?.selected
				);

				const newObjectFields = objectFields.map((objectField) => ({
					...objectField,
					selected: false,
				}));

				selectedObjectDefinitionNode.data.selected = false;
				selectedObjectDefinitionNode.data.objectFields =
					newObjectFields;

				newObjectDefinitionNodes[selectedObjectDefinitionNodeIndex] =
					selectedObjectDefinitionNode;
			}

			return {
				...state,
				elements: [
					...newObjectDefinitionNodes,
					...newObjectRelationshipEdges,
				],
				rightSidebarType: 'objectRelationshipDetails',
				selectedObjectField: undefined,
				selectedObjectRelationship,
			};
		}

		case TYPES.SET_SHOW_ALL_OBJECT_FIELDS: {
			const {objectDefinitionExternalReferenceCode, showAllObjectFields} =
				action.payload;

			const {elements} = state;

			const objectDefinitionNodes = elements.filter((element) =>
				isNode(element)
			) as Node<ObjectDefinitionNodeData>[];

			const objectRelationshipEdges = elements.filter((element) =>
				isEdge(element)
			) as Edge<ObjectRelationshipEdgeData[]>[];

			const newObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					if (
						objectDefinitionNode?.data?.externalReferenceCode ===
						objectDefinitionExternalReferenceCode
					) {
						return {
							...objectDefinitionNode,
							data: {
								...objectDefinitionNode.data,
								showAllObjectFields: !showAllObjectFields,
							},
						};
					}

					return objectDefinitionNode;
				}
			) as Node<ObjectDefinitionNodeData>[];

			return {
				...state,
				elements: [
					...newObjectDefinitionNodes,
					...objectRelationshipEdges,
				],
			};
		}

		case TYPES.SET_SHOW_CHANGES_SAVED: {
			const {updatedShowChangesSaved} = action.payload;

			return {
				...state,
				showChangesSaved: updatedShowChangesSaved,
			};
		}

		case TYPES.SET_SHOW_SIDEBARS: {
			const {updatedShowSidebars} = action.payload;

			return {
				...state,
				showSidebars: updatedShowSidebars,
			};
		}

		case TYPES.UPDATE_MODEL_BUILDER_STRUCTURE: {
			const {
				dispatch,
				objectFolders,
				rightSidebarType,
				selectedObjectFolderName,
				selectedObjectRelationshipId,
			} = action.payload;
			const {baseResourceURL, objectDefinitionPermissionsURL} = state;
			const objectRelationshipMap = new ObjectRelationshipMap();

			const newLeftSidebarItems = objectFolders.map((objectFolder) => {
				const leftSidebarObjectDefinitionItems =
					objectFolder.objectDefinitions?.map((objectDefinition) => {
						objectDefinition.objectRelationships.forEach(
							(objectRelationship) => {
								if (
									!objectDefinition.linkedObjectDefinition &&
									!objectRelationship.reverse
								) {
									objectRelationshipMap.setValue(
										objectRelationship
									);
								}
							}
						);

						const kebabOptions = getObjectDefinitionNodeActions({
							baseResourceURL,
							dispatch,
							hasObjectDefinitionDeleteResourcePermission:
								objectDefinition.hasObjectDefinitionDeleteResourcePermission,
							hasObjectDefinitionManagePermissionsResourcePermission:
								objectDefinition.hasObjectDefinitionManagePermissionsResourcePermission,
							hasObjectDefinitionUpdateResourcePermission:
								objectDefinition.hasObjectDefinitionUpdateResourcePermission,
							isTreeStructure:
								!!objectDefinition.objectDefinitionSettings?.some(
									(setting) =>
										setting.name ===
										'rootObjectDefinitionExternalReferenceCodes'
								),
							objectDefinitionId: objectDefinition.id,
							objectDefinitionName: objectDefinition.name,
							objectDefinitionPermissionsURL,
							objectFoldersLength: objectFolders.length,
							status: objectDefinition.status,
						});

						return {
							dbTableName: objectDefinition.dbTableName,
							externalReferenceCode:
								objectDefinition.externalReferenceCode,
							hiddenObjectDefinitionNode: false,
							id: objectDefinition.id,
							kebabOptions,
							label: stringUtils.getLocalizableLabel({
								fallbackLabel: objectDefinition.name,
								fallbackLanguageId:
									objectDefinition.defaultLanguageId,
								labels: objectDefinition.label,
							}),
							name: objectDefinition.name,
							selected: false,
							type: !objectDefinition.dbTableName
								? 'dummyObjectDefinition'
								: objectDefinition.linkedObjectDefinition
									? 'linkedObjectDefinition'
									: 'objectDefinition',
						} as LeftSidebarObjectDefinitionItem;
					});

				return {
					hiddenObjectFolderObjectDefinitionNodes: false,
					leftSidebarObjectDefinitionItems,
					name: stringUtils.getLocalizableLabel({
						fallbackLabel: objectFolder.name,
						labels: objectFolder.label,
					}),
					objectFolderName: objectFolder.name,
					type: 'objectFolder',
				} as LeftSidebarItem;
			});

			const selectedObjectFolder = objectFolders.find(
				(objectFolder) => objectFolder.name === selectedObjectFolderName
			);

			const updatedObjectFolderItems: ObjectFolderItem[] = [];

			let objectDefinitionNodes: Node<ObjectDefinitionNodeData>[] = [];
			const objectRelationshipEdges: Edge<
				ObjectRelationshipEdgeData[]
			>[] = [];

			if (selectedObjectFolder) {
				const positionColumn = {x: 0, y: 0};

				objectDefinitionNodes =
					selectedObjectFolder.objectDefinitions!.map(
						(objectDefinition, index) => {
							objectDefinition.objectRelationships.forEach(
								(objectRelationship) => {
									const objectRelationshipEdge =
										objectRelationshipEdgeFactory({
											objectDefinition,
											objectRelationship,
											objectRelationshipMap,
											selectedObjectRelationshipId,
										});

									if (objectRelationshipEdge) {
										objectRelationshipEdges.push(
											objectRelationshipEdge
										);
									}
								}
							);

							const {x, y} = getObjectDefinitionNodePosition({
								index,
								objectDefinition,
								objectFolderExternalReferenceCode:
									selectedObjectFolder.externalReferenceCode,
								outdatedObjectFolderItems:
									selectedObjectFolder.objectFolderItems,
								positionColumn,
								updatedObjectFolderItems,
							});

							updatedObjectFolderItems.push({
								linkedObjectDefinition:
									objectDefinition.objectFolderExternalReferenceCode !==
									selectedObjectFolder.externalReferenceCode,
								objectDefinitionExternalReferenceCode:
									objectDefinition.externalReferenceCode,
								positionX: x,
								positionY: y,
							});

							return {
								data: {
									...objectDefinition,
									objectFields: objectFieldsCustomSort(
										objectDefinition.objectFields
									),
									showAllObjectFields: false,
								},
								id: objectDefinition.id.toString(),
								position: {x, y},
								type: 'objectDefinitionNode',
							} as Node<ObjectDefinitionNodeData>;
						}
					);

				selectedObjectFolder.objectFolderItems =
					updatedObjectFolderItems;
			}

			let newModelBuilderState = {
				...state,
				elements: [
					...objectDefinitionNodes,
					...objectRelationshipEdges,
				],
				leftSidebarItems: newLeftSidebarItems,
				objectFolders,
				rightSidebarType: 'empty' as RightSidebarType,
				selectedObjectFolder: selectedObjectFolder as ObjectFolder,
				selectedObjectRelationship: null,
			};

			if (rightSidebarType) {
				newModelBuilderState = {
					...newModelBuilderState,
					rightSidebarType,
				};
			}

			return newModelBuilderState;
		}

		case TYPES.UPDATE_OBJECT_DEFINITION_NODE: {
			const {
				currentObjectFolderName,
				objectDefinitionNodes,
				objectDefinitionRelationshipEdges,
				updatedObjectDefinition,
			} = action.payload;

			const {leftSidebarItems} = state;

			const updatedObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					if (
						objectDefinitionNode.data?.id ===
						updatedObjectDefinition.id
					) {
						return {
							...objectDefinitionNode,
							data: {
								...objectDefinitionNode.data,
								label: updatedObjectDefinition.label,
								name: updatedObjectDefinition.name,
								pluralLabel: {
									[updatedObjectDefinition.defaultLanguageId!]:
										updatedObjectDefinition.pluralLabel,
								},
							},
						};
					}

					return objectDefinitionNode;
				}
			) as Node<ObjectDefinitionNodeData>[];

			let updatedObjectDefinitions;

			const newLeftSidebarItems = leftSidebarItems.map(
				(leftSidebarItem: LeftSidebarItem) => {
					if (
						leftSidebarItem.objectFolderName ===
						currentObjectFolderName
					) {
						updatedObjectDefinitions =
							leftSidebarItem.leftSidebarObjectDefinitionItems?.map(
								(leftSidebarObjectDefinitionItem) => {
									if (
										leftSidebarObjectDefinitionItem.id ===
										updatedObjectDefinition.id
									) {
										return {
											...leftSidebarObjectDefinitionItem,
											label: stringUtils.getLocalizableLabel(
												{
													fallbackLabel:
														updatedObjectDefinition.name,
													labels: updatedObjectDefinition.label,
												}
											),
										};
									}

									return leftSidebarObjectDefinitionItem;
								}
							);

						return {
							...leftSidebarItem,
							leftSidebarObjectDefinitionItems: [
								...updatedObjectDefinitions!,
							],
						};
					}
					else {
						return {
							...leftSidebarItem,
						};
					}
				}
			) as LeftSidebarItem[];

			return {
				...state,
				elements: [
					...objectDefinitionRelationshipEdges,
					...updatedObjectDefinitionNodes,
				],
				leftSidebarItems: newLeftSidebarItems,
			};
		}

		case TYPES.UPDATE_OBJECT_FIELD_NODE_ROW: {
			const {
				objectDefinitionNodes,
				objectRelationshipEdges,
				selectedObjectDefinitionNode,
				updatedObjectField,
			} = action.payload;

			const newSelectedObjectFields =
				selectedObjectDefinitionNode?.data?.objectFields.map(
					(objectField) => {
						if (
							objectField.externalReferenceCode ===
							updatedObjectField.externalReferenceCode
						) {
							return {
								...updatedObjectField,
								primaryKey: false,
								selected: true,
							} as ObjectFieldNodeRow;
						}

						return objectField;
					}
				);

			const newObjectDefinitionNodes = objectDefinitionNodes.map(
				(objectDefinitionNode) => {
					if (
						objectDefinitionNode.data?.externalReferenceCode ===
						selectedObjectDefinitionNode?.data
							?.externalReferenceCode
					) {
						return {
							...selectedObjectDefinitionNode,
							data: {
								...selectedObjectDefinitionNode?.data,
								objectFields: newSelectedObjectFields,
							},
						} as Node<ObjectDefinitionNodeData>;
					}

					return objectDefinitionNode;
				}
			);

			return {
				...state,
				elements: [
					...newObjectDefinitionNodes,
					...objectRelationshipEdges,
				],
			};
		}

		case TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS: {
			const {updatedModelBuilderModals} = action.payload;

			const {modelBuilderModals} = state;

			return {
				...state,
				modelBuilderModals: {
					...modelBuilderModals,
					...updatedModelBuilderModals,
				},
			};
		}

		default:
			return state;
	}
}
