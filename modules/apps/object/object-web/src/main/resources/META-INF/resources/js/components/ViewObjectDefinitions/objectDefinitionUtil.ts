/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {API} from '@liferay/object-js-components-web';
import {openModal} from 'frontend-js-components-web';
import {createResourceURL, sub} from 'frontend-js-web';
import {SetStateAction} from 'react';

import {exportObjectEntity} from '../../utils/exportObjectEntity';
import {formatActionURL} from '../../utils/fds';
import {TYPES} from '../ModelBuilder/ModelBuilderContext/typesEnum';
import {DropDownItems, TAction} from '../ModelBuilder/types';
import {ModalImportProperties} from './ViewObjectDefinitions';

type DeleteObjectDefinitionProps = {
	baseResourceURL: string;
	handleDeleteObjectDefinition?: (value: DeletedObjectDefinition) => void;
	handleShowDeleteObjectDefinitionModal?: () => void;
	objectDefinitionId: number;
	objectDefinitionName: string;
	onAfterDeleteObjectDefinition?: () => void;
};

type ObjectDefinitionNodeActionsProps = {
	baseResourceURL: string;
	dispatch: React.Dispatch<TAction>;
	hasObjectDefinitionDeleteResourcePermission: boolean;
	hasObjectDefinitionManagePermissionsResourcePermission: boolean;
	hasObjectDefinitionUpdateResourcePermission: boolean;
	isTreeStructure: boolean;
	objectDefinitionId: number;
	objectDefinitionName: string;
	objectDefinitionPermissionsURL: string;
	objectFoldersLenght: number;
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
};

export async function deleteObjectFolder(id: number, objectFolderName: string) {
	await API.deleteObjectFolder(Number(id)).then(() => {
		Liferay.Util.openToast({
			message: sub(
				Liferay.Language.get('x-was-deleted-successfully'),
				`<strong>${Liferay.Util.escapeHTML(objectFolderName)}</strong>`
			),
		});
	});
}

export async function deleteObjectDefinitionToast(
	id: number,
	objectDefinitionName: string
) {
	await API.deleteObjectDefinition(Number(id)).then(() => {
		Liferay.Util.openToast({
			message: sub(
				Liferay.Language.get('x-was-deleted-successfully'),
				`<strong>${Liferay.Util.escapeHTML(
					objectDefinitionName
				)}</strong>`
			),
		});
	});
}

export async function deleteObjectDefinition({
	baseResourceURL,
	handleDeleteObjectDefinition,
	handleShowDeleteObjectDefinitionModal,
	objectDefinitionId,
	objectDefinitionName,
	onAfterDeleteObjectDefinition,
}: DeleteObjectDefinitionProps) {
	const url = createResourceURL(baseResourceURL, {
		objectDefinitionId,
		p_p_resource_id:
			'/object_definitions/get_object_definition_delete_info',
	}).href;

	const {hasObjectRelationship, objectEntriesCount, status} =
		await API.fetchJSON<{
			hasObjectRelationship: boolean;
			objectEntriesCount: number;
			status: number;
		}>(url);

	if (status !== 0) {
		await deleteObjectDefinitionToast(
			objectDefinitionId,
			objectDefinitionName
		);

		if (onAfterDeleteObjectDefinition) {
			onAfterDeleteObjectDefinition();
		}
		else {
			setTimeout(() => window.location.reload(), 1000);
		}

		return;
	}

	const deletedObjectDefinition = {
		hasObjectRelationship,
		id: objectDefinitionId,
		name: objectDefinitionName,
		objectEntriesCount,
	};

	if (handleDeleteObjectDefinition) {
		handleDeleteObjectDefinition(deletedObjectDefinition);
	}

	if (handleShowDeleteObjectDefinitionModal) {
		handleShowDeleteObjectDefinitionModal();
	}

	return deletedObjectDefinition;
}

export async function deleteRelationship(
	id: number,
	reloadAfterDeletion?: boolean
) {
	try {
		await API.deleteObjectRelationship(id);

		Liferay.Util.openToast({
			message: Liferay.Language.get(
				'relationship-was-deleted-successfully'
			),
		});

		if (reloadAfterDeletion) {
			setTimeout(() => window.location.reload(), 1500);
		}
	}
	catch (error) {
		const errorMessage = (error as Error).message;

		openModal({
			bodyHTML: `<p>${errorMessage}</p>`,
			buttons: [
				{
					displayType: 'warning',
					label: Liferay.Language.get('done'),
					type: 'cancel',
				},
			],
			center: true,
			id: 'deleteRelationship',
			size: 'md',
			status: 'warning',
			title: Liferay.Language.get('deletion-not-allowed'),
		});
	}
}

export async function getObjectDefinitionInfo({
	baseResourceURL,
	objectDefinitionId,
}: {
	baseResourceURL: string;
	objectDefinitionId: number;
}) {
	const objectDefinitionInfoURL = createResourceURL(baseResourceURL, {
		objectDefinitionId,
		p_p_resource_id: '/object_definitions/get_object_definition_info',
	}).href;

	const objectDefinitionInfoResponse = await API.fetchJSON<{
		tableName: string;
		workflowDefinitionTitle: string;
	}>(objectDefinitionInfoURL);

	return objectDefinitionInfoResponse;
}

export function getObjectDefinitionNodeActions({
	baseResourceURL,
	dispatch,
	hasObjectDefinitionDeleteResourcePermission,
	hasObjectDefinitionManagePermissionsResourcePermission,
	hasObjectDefinitionUpdateResourcePermission,
	isTreeStructure,
	objectDefinitionId,
	objectDefinitionName,
	objectDefinitionPermissionsURL,
	objectFoldersLenght,
}: ObjectDefinitionNodeActionsProps) {
	const PermissionUrl = formatActionURL(
		objectDefinitionPermissionsURL,
		objectDefinitionId
	);

	const kebabOptions = [
		{
			label: sub(
				Liferay.Language.get('edit-in-x'),
				Liferay.Language.get('page view')
			),
			onClick: () => {
				dispatch({
					payload: {
						updatedModelBuilderModals: {
							redirectToEditObjectDefinitionDetails: true,
						},
					},
					type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
				});
			},
			symbolRight: 'shortcut',
		},
		{
			label: sub(
				Liferay.Language.get('edit-x'),
				Liferay.Language.get('erc')
			),
			onClick: () => {
				dispatch({
					payload: {
						updatedModelBuilderModals: {
							editObjectDefinitionExternalReferenceCode: true,
						},
					},
					type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
				});
			},
			symbolLeft: 'info-panel-closed',
		},
		{type: 'divider'},
		{
			label: Liferay.Language.get('export-as-json'),
			onClick: () => {
				const exportObjectDefinitionURL = createResourceURL(
					baseResourceURL,
					{
						objectDefinitionId,
						p_p_resource_id:
							'/object_definitions/export_object_definition',
					}
				).href;

				exportObjectEntity({
					exportObjectEntityURL: exportObjectDefinitionURL,
					objectEntityId: objectDefinitionId,
				});
			},
			symbolLeft: 'export',
		},
	] as DropDownItems[];

	if (
		objectFoldersLenght > 1 &&
		hasObjectDefinitionUpdateResourcePermission
	) {
		kebabOptions.push(
			{
				label: Liferay.Language.get('move'),
				onClick: () => {
					dispatch({
						payload: {
							movedObjectDefinitionId: objectDefinitionId,
						},
						type: TYPES.SET_MOVED_OBJECT_DEFINITION,
					});

					dispatch({
						payload: {
							updatedModelBuilderModals: {
								moveObjectDefinition: true,
							},
						},
						type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
					});
				},
				symbolLeft: 'move-folder',
			},
			{type: 'divider'}
		);
	}

	if (hasObjectDefinitionManagePermissionsResourcePermission) {
		kebabOptions.push({
			label: sub(
				Liferay.Language.get('manage-x'),
				Liferay.Language.get('permissions')
			),
			onClick: (event: React.MouseEvent) => {
				event.stopPropagation();
				openModal({
					title: Liferay.Language.get('permissions'),
					url: PermissionUrl,
				});
			},
			symbolLeft: 'users',
		});
	}

	if (hasObjectDefinitionDeleteResourcePermission) {
		kebabOptions.push({type: 'divider'});
		kebabOptions.push({
			label: sub(
				Liferay.Language.get('delete-x'),
				Liferay.Language.get('object')
			),
			onClick: async () => {
				if (isTreeStructure) {
					dispatch({
						payload: {
							updatedModelBuilderModals: {
								objectDefinitionOnRootModelDeletionNotAllowed:
									true,
							},
						},
						type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
					});
				}
				else {
					const deletedObjectDefinition =
						await deleteObjectDefinition({
							baseResourceURL,
							objectDefinitionId,
							objectDefinitionName,
						});

					if (deletedObjectDefinition) {
						dispatch({
							payload: {
								deletedObjectDefinition,
							},
							type: TYPES.SET_DELETE_OBJECT_DEFINITION,
						});
						dispatch({
							payload: {
								updatedModelBuilderModals: {
									deleteObjectDefinition: true,
								},
							},
							type: TYPES.UPDATE_VISIBILITY_MODEL_BUILDER_MODALS,
						});
					}
				}
			},
			symbolLeft: 'trash',
		});
	}

	return kebabOptions;
}

interface GetObjectFolderActionsProps {
	actions?: {
		objectDefinitionActions: Actions;
		objectFolderActions: Actions;
	};
	baseResourceURL: string;
	importObjectDefinitionURL: string;
	objectFolderExternalReferenceCode: string;
	objectFolderId: number;
	objectFolderPermissionsURL: string;
	portletNamespace: string;
	setModalImportProperties: (
		value: SetStateAction<ModalImportProperties>
	) => void;
	setShowModal: (value: SetStateAction<ShowObjectDefinitionsModals>) => void;
}

export function getObjectFolderActions({
	actions,
	baseResourceURL,
	importObjectDefinitionURL,
	objectFolderExternalReferenceCode,
	objectFolderId,
	objectFolderPermissionsURL,
	portletNamespace,
	setModalImportProperties,
	setShowModal,
}: GetObjectFolderActionsProps) {
	const url = formatActionURL(objectFolderPermissionsURL, objectFolderId);
	const kebabOptions = [];

	if (actions?.objectFolderActions?.update) {
		kebabOptions.unshift({
			label: Liferay.Language.get('edit-label-and-erc'),
			onClick: () =>
				setShowModal((previousState: ShowObjectDefinitionsModals) => ({
					...previousState,
					editObjectFolder: true,
				})),
			symbolLeft: 'pencil',
			value: 'editObjectFolder',
		});

		kebabOptions.push({type: 'divider'});
	}

	kebabOptions.push({
		label: Liferay.Language.get('export-object-folder'),
		onClick: () => {
			const exportObjectFolderURL = createResourceURL(baseResourceURL, {
				objectFolderId,
				p_p_resource_id: '/object_definitions/export_object_folder',
			}).href;

			exportObjectEntity({
				exportObjectEntityURL: exportObjectFolderURL,
				objectEntityId: objectFolderId,
			});
		},
		symbolLeft: 'export',
		value: 'exportObjectFolder',
	});

	if (actions?.objectDefinitionActions?.create) {
		kebabOptions.push({
			label: Liferay.Language.get('import-object-definition'),
			onClick: () => {
				setModalImportProperties({
					JSONInputId: 'objectDefinitionJSON',
					apiURL: '/o/object-admin/v1.0/object-definitions/by-external-reference-code/',
					importExtendedInfo: {
						key: `${portletNamespace}objectFolderExternalReferenceCode`,
						value: objectFolderExternalReferenceCode,
					},
					importURL: importObjectDefinitionURL,
					modalImportKey: 'objectDefinition',
				});

				setShowModal((previousState: ShowObjectDefinitionsModals) => ({
					...previousState,
					importModal: true,
				}));
			},
			symbolLeft: 'import',
			value: 'importObjectDefinition',
		});
		kebabOptions.push({type: 'divider'});
	}

	if (actions?.objectFolderActions.permissions) {
		kebabOptions.push({
			label: Liferay.Language.get('object-folder-permissions'),
			onClick: () => {
				openModal({
					title: Liferay.Language.get('permissions'),
					url,
				});
			},
			symbolLeft: 'password-policies',
			value: 'objectFolderPermissions',
		});
	}

	if (actions?.objectFolderActions.delete) {
		kebabOptions.push({type: 'divider'});
		kebabOptions.push({
			label: Liferay.Language.get('delete-object-folder'),
			onClick: () =>
				setShowModal((previousState: ShowObjectDefinitionsModals) => ({
					...previousState,
					deleteObjectFolder: true,
				})),
			symbolLeft: 'trash',
			value: 'deleteObjectFolder',
		});
	}

	return kebabOptions;
}

export async function getUpdatedModelBuilderStructurePayload(
	baseResourceURL: string,
	currentObjectFolderName: string
) {
	const allObjectFolders = await API.getAllObjectFolders();

	if (allObjectFolders) {
		const {items: objectFolders} = allObjectFolders;

		const currentObjectFolder = objectFolders.find(
			(objectFolder) => objectFolder.name === currentObjectFolderName
		) as ObjectFolder;

		const objectFoldersWithObjectDefinitions: ObjectFolder[] =
			await Promise.all(
				objectFolders.map(async (objectFolder) => {
					const objectFolderWithObjectDefinitions: ObjectDefinitionNodeData[] =
						[];

					const objectDefinitionsFilteredByObjectFolder =
						await API.getObjectDefinitions(
							`filter=objectFolderExternalReferenceCode eq '${objectFolder.externalReferenceCode}'`
						);

					const linkedObjectDefinitions: ObjectDefinition[] = [];

					await Promise.all(
						objectFolder.objectFolderItems
							.filter(
								(objectFolderItem) =>
									objectFolderItem.linkedObjectDefinition
							)
							.map(async (objectFolderItem) => {
								linkedObjectDefinitions.push(
									await API.getObjectDefinitionByExternalReferenceCode(
										objectFolderItem.objectDefinitionExternalReferenceCode
									)
								);
							})
					);

					const updateObjectFolderObjectDefinitions = async ({
						linkedObjectDefinition,
						objectDefinitions,
					}: {
						linkedObjectDefinition: boolean;
						objectDefinitions: ObjectDefinition[];
					}) => {
						for await (const objectDefinition of objectDefinitions) {
							const objectFolderItem =
								objectFolder.objectFolderItems.find(
									(objectFolderItem) =>
										objectFolderItem.objectDefinitionExternalReferenceCode ===
										objectDefinition.externalReferenceCode
								);

							const objectDefinitionInfo =
								await getObjectDefinitionInfo({
									baseResourceURL,
									objectDefinitionId: objectDefinition.id,
								});

							if (objectFolderItem) {
								objectFolderWithObjectDefinitions.push({
									...objectDefinition,
									dbTableName: objectDefinitionInfo.tableName,
									hasObjectDefinitionDeleteResourcePermission:
										!!objectDefinition.actions.delete,
									hasObjectDefinitionManagePermissionsResourcePermission:
										!!objectDefinition.actions.permissions,
									hasObjectDefinitionUpdateResourcePermission:
										!!objectDefinition.actions.update,
									hasObjectDefinitionViewResourcePermission:
										!!objectDefinition.actions.get,
									linkedObjectDefinition,
									objectFields:
										objectDefinition.objectFields.map(
											({
												businessType,
												externalReferenceCode,
												id,
												label,
												name,
												required,
											}) =>
												({
													businessType,
													externalReferenceCode,
													id,
													label,
													name,
													primaryKey: name === 'id',
													required,
													selected: false,
												}) as ObjectFieldNodeRow
										),
									selected: false,
									showAllObjectFields: false,
								});
							}
						}
					};

					await updateObjectFolderObjectDefinitions({
						linkedObjectDefinition: false,
						objectDefinitions:
							objectDefinitionsFilteredByObjectFolder,
					});

					await updateObjectFolderObjectDefinitions({
						linkedObjectDefinition: true,
						objectDefinitions: linkedObjectDefinitions,
					});

					return {
						...objectFolder,
						objectDefinitions: objectFolderWithObjectDefinitions,
					};
				})
			);

		return {
			objectFolders: objectFoldersWithObjectDefinitions,
			selectedObjectFolderName: currentObjectFolder.name,
		};
	}

	return {
		objectFolders: [],
		selectedObjectFolderName: '',
	};
}
