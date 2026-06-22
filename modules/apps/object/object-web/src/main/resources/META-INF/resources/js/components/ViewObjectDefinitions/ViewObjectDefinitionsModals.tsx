/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {stringUtils} from '@liferay/object-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

import ModalDeletionNotAllowed from '../ModalDeletionNotAllowed';
import ModalImport from '../ModalImport/ModalImport';
import {ModalAddObjectDefinition} from './ModalAddObjectDefinition';
import {ModalAddObjectFolder} from './ModalAddObjectFolder';
import {ModalDeleteObjectDefinition} from './ModalDeleteObjectDefinition';
import {ModalDeleteObjectFolder} from './ModalDeleteObjectFolder';
import {ModalEditObjectFolder} from './ModalEditObjectFolder';
import {ModalMoveObjectDefinition} from './ModalMoveObjectDefinition';

import type {ModalImportProperties} from './ViewObjectDefinitions';

interface ViewObjectDefinitionsModalsProps {
	deletedObjectDefinition?: DeletedObjectDefinition | null;
	learnResourceContext: any;
	modalImportProperties: ModalImportProperties;
	moveObjectDefinition?: ObjectDefinition | null;
	nameMaxLength: string;
	objectDefinitionsStorageTypes: LabelValueObject[];
	objectFoldersRequestInfo: ObjectFoldersRequestInfo;
	portletNamespace: string;
	selectedObjectDefinition?: ObjectDefinition;
	selectedObjectFolder: Partial<ObjectFolder>;
	setDeletedObjectDefinition: React.Dispatch<
		React.SetStateAction<DeletedObjectDefinition | null | undefined>
	>;
	setMoveObjectDefinition: React.Dispatch<
		React.SetStateAction<ObjectDefinition | null | undefined>
	>;
	setObjectFoldersRequestInfo: React.Dispatch<
		React.SetStateAction<ObjectFoldersRequestInfo>
	>;
	setReloadFDS: (value: boolean) => void;
	setSelectedObjectFolder: React.Dispatch<
		React.SetStateAction<Partial<ObjectFolder>>
	>;
	setShowModal: React.Dispatch<
		React.SetStateAction<ShowObjectDefinitionsModals>
	>;
	showModal: ShowObjectDefinitionsModals;
}

export function ViewObjectDefinitionsModals({
	deletedObjectDefinition,
	learnResourceContext,
	modalImportProperties,
	moveObjectDefinition,
	nameMaxLength,
	objectDefinitionsStorageTypes,
	objectFoldersRequestInfo,
	portletNamespace,
	selectedObjectDefinition,
	selectedObjectFolder,
	setDeletedObjectDefinition,
	setMoveObjectDefinition,
	setObjectFoldersRequestInfo,
	setReloadFDS,
	setSelectedObjectFolder,
	setShowModal,
	showModal,
}: ViewObjectDefinitionsModalsProps) {
	return (
		<>
			{showModal.addObjectDefinition && (
				<ModalAddObjectDefinition
					handleOnClose={() => {
						setShowModal((previousState) => ({
							...previousState,
							addObjectDefinition: false,
						}));
					}}
					learnResourceContext={learnResourceContext}
					objectDefinitionsStorageTypes={
						objectDefinitionsStorageTypes
					}
					objectFolderExternalReferenceCode={
						selectedObjectFolder.externalReferenceCode
					}
					onAfterSubmit={() => {
						setReloadFDS(true);
					}}
				/>
			)}

			{showModal.importModal && (
				<ModalImport
					{...(modalImportProperties.modalImportKey ===
						'objectDefinition' && {
						onAfterImport: () => setReloadFDS(true),
					})}
					JSONInputId={modalImportProperties.JSONInputId}
					apiURL={modalImportProperties.apiURL}
					handleOnClose={() => {
						setShowModal((previousState) => ({
							...previousState,
							importModal: false,
						}));
					}}
					importExtendedInfo={
						modalImportProperties.importExtendedInfo as KeyValueObject
					}
					importURL={modalImportProperties.importURL}
					modalImportKey={modalImportProperties.modalImportKey}
					nameMaxLength={nameMaxLength}
					objectFolderExternalReferenceCode={
						selectedObjectFolder.externalReferenceCode
					}
					portletNamespace={portletNamespace}
					showModal={showModal.importModal}
				/>
			)}

			{showModal.addObjectFolder && (
				<ModalAddObjectFolder
					handleOnClose={() => {
						setShowModal((previousState) => ({
							...previousState,
							addObjectFolder: false,
						}));
					}}
					setObjectFoldersRequestInfo={setObjectFoldersRequestInfo}
					setSelectedObjectFolder={setSelectedObjectFolder}
				/>
			)}

			{showModal.deleteObjectDefinition && (
				<ModalDeleteObjectDefinition
					handleDeleteObjectDefinition={() =>
						setDeletedObjectDefinition
					}
					handleOnClose={() => {
						setShowModal((previousState) => ({
							...previousState,
							deleteObjectDefinition: false,
						}));
					}}
					objectDefinition={
						deletedObjectDefinition as DeletedObjectDefinition
					}
					onAfterDeleteObjectDefinition={() => setReloadFDS(true)}
				/>
			)}

			{showModal.deleteObjectFolder && (
				<ModalDeleteObjectFolder
					handleOnClose={() => {
						setShowModal((previousState) => ({
							...previousState,
							deleteObjectFolder: false,
						}));
					}}
					objectFolder={selectedObjectFolder as ObjectFolder}
				/>
			)}

			{showModal.editObjectFolder && (
				<ModalEditObjectFolder
					externalReferenceCode={
						selectedObjectFolder.externalReferenceCode as string
					}
					handleOnClose={() => {
						setShowModal((previousState) => ({
							...previousState,
							editObjectFolder: false,
						}));
					}}
					id={selectedObjectFolder.id as number}
					initialLabel={selectedObjectFolder.label}
					name={selectedObjectFolder.name}
					onAfterSubmit={(editedObjectFolder) => {
						setSelectedObjectFolder(editedObjectFolder);
						setObjectFoldersRequestInfo({
							...objectFoldersRequestInfo,
							items: objectFoldersRequestInfo.items.map(
								(objectFolder) => {
									if (
										objectFolder.name ===
										editedObjectFolder.name
									) {
										return {
											...objectFolder,
											externalReferenceCode:
												editedObjectFolder.externalReferenceCode,
											label: editedObjectFolder.label,
										};
									}

									return objectFolder;
								}
							),
						});
					}}
				/>
			)}

			{showModal.moveObjectDefinition && (
				<ModalMoveObjectDefinition
					handleOnClose={() => {
						setShowModal((previousState) => ({
							...previousState,
							moveObjectDefinition: false,
						}));
					}}
					objectDefinitionId={moveObjectDefinition?.id as number}
					objectFolders={objectFoldersRequestInfo.items}
					onAfterMoveObjectDefinition={() => setReloadFDS(true)}
					setMoveObjectDefinition={setMoveObjectDefinition}
				/>
			)}

			{showModal.objectDefinitionOnRootModelDeletionNotAllowed &&
				selectedObjectDefinition && (
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
							setShowModal((previousState) => ({
								...previousState,
								objectDefinitionOnRootModelDeletionNotAllowed:
									false,
							}))
						}
					/>
				)}

			{showModal.objectFieldDeletionNotAllowed &&
				selectedObjectDefinition && (
					<ModalDeletionNotAllowed
						content={
							<span
								dangerouslySetInnerHTML={{
									__html: sub(
										Liferay.Language.get(
											'x-is-being-used-by-a-root-object-and-cannot-be-deleted'
										),
										`<strong>"${stringUtils.getLocalizableLabel(
											{
												fallbackLabel:
													selectedObjectDefinition.name,
												fallbackLanguageId:
													selectedObjectDefinition.defaultLanguageId,
												labels: selectedObjectDefinition.label,
											}
										)}"</strong>`
									),
								}}
							/>
						}
						onModalClose={() =>
							setShowModal((previousState) => ({
								...previousState,
								objectFieldDeletionNotAllowed: false,
							}))
						}
					/>
				)}
		</>
	);
}
