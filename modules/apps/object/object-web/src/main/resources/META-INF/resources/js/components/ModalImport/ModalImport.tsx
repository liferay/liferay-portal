/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import {API} from '@liferay/object-js-components-web';
import React, {FormEvent, useEffect, useState} from 'react';

import {ModalImportContent} from './ModalImportContent';
import {ModalImportFailed} from './ModalImportFailed';
import {ModalImportWarning} from './ModalImportWarning';
import {
	handleDefaultImport,
	handleImport,
	handleImportMultiplesObjectDefinitions,
} from './handleImportUtil';

export type ModalImportKeys =
	| 'listTypeDefinition'
	| 'objectDefinition'
	| 'objectDefinitions'
	| 'objectFolder';
interface ModalImportProps {
	JSONInputId: string;
	apiURL: string;
	handleOnClose?: () => void;
	importExtendedInfo: KeyValueObject;
	importURL: string;
	modalImportKey: ModalImportKeys;
	nameMaxLength: string;
	objectFolderExternalReferenceCode?: string;
	onAfterImport?: () => void;
	portletNamespace: string;
	showModal?: boolean;
}

export type TFile = {
	fileName?: string;
	inputFile?: File | null;
};

export default function ModalImport({
	JSONInputId,
	apiURL,
	handleOnClose,
	importExtendedInfo,
	importURL,
	modalImportKey,
	nameMaxLength,
	objectFolderExternalReferenceCode,
	onAfterImport,
	portletNamespace,
	showModal,
}: ModalImportProps) {
	const [importLoading, setImportLoading] = useState(false);
	const [error, setError] = useState<API.ErrorDetails>();
	const [existingObjectDefinitions, setExistingObjectDefinitions] = useState<
		ObjectDefinition[]
	>([]);
	const [externalReferenceCode, setExternalReferenceCode] =
		useState<string>('');
	const [importedObjectDefinitions, setImportedObjectDefinitions] =
		useState<ObjectDefinition[]>();
	const [{fileName, inputFile}, setFile] = useState<TFile>({});
	const [importFormData, setImportFormData] = useState<FormData>();
	const importModalComponentId = `${portletNamespace}importModal`;
	const [modalImportKeyState, setModalImportKeyState] =
		useState(modalImportKey);
	const [name, setName] = useState('');
	const [visible, setVisible] = useState(showModal ?? false);
	const [warningModalVisible, setWarningModalVisible] = useState(false);
	const [failedModalVisible, setFailedModalVisible] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			setVisible(false);
			setError(undefined);
			setExternalReferenceCode('');
			setFile({
				fileName: '',
				inputFile: null,
			});
			setName('');
			setFailedModalVisible(false);
			setWarningModalVisible(false);

			if (handleOnClose) {
				handleOnClose();
			}

			if (
				error &&
				error?.message !== '' &&
				!error?.type?.includes('ObjectFolderNameException') &&
				modalImportKey === 'objectFolder'
			) {
				window.location.reload();
			}
		},
	});

	const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		if (importedObjectDefinitions) {
			handleImportMultiplesObjectDefinitions({
				importURL,
				importedObjectDefinitions,
				objectFolderExternalReferenceCode:
					objectFolderExternalReferenceCode as string,
				onClose,
				setError,
				setExistingObjectDefinitions,
				setFailedModalVisible,
				setImportFormData,
				setImportLoading,
				setModalImportKeyState,
				setWarningModalVisible,
			});

			return;
		}

		handleDefaultImport({
			JSONInputId,
			apiURL,
			event,
			externalReferenceCode,
			importExtendedInfo,
			importURL,
			inputFile,
			onAfterImport,
			onClose,
			setError,
			setImportFormData,
			setImportLoading,
			setWarningModalVisible,
		});
	};

	useEffect(() => {
		Liferay.component(
			importModalComponentId,
			{
				open: () => {
					setVisible(true);
				},
			},
			{
				destroyOnNavigate: true,
			}
		);

		return () => Liferay.destroyComponent(importModalComponentId);
	}, [importModalComponentId, setVisible]);

	return visible ? (
		<ClayModal
			center
			observer={observer}
			size={failedModalVisible ? 'lg' : undefined}
			status={
				warningModalVisible || failedModalVisible
					? 'warning'
					: undefined
			}
		>
			{warningModalVisible && (
				<ModalImportWarning
					errorMessage={error?.message ?? ''}
					existingObjectDefinitions={existingObjectDefinitions}
					handleImport={() =>
						handleImport({
							importURL,
							item: importFormData as FormData,
							onAfterImport,
							onClose,
							setError,
							setFailedModalVisible,
							setImportLoading,
							setWarningModalVisible,
						})
					}
					handleOnClose={onClose}
					importLoading={importLoading}
					modalImportKey={modalImportKeyState}
				/>
			)}

			{failedModalVisible &&
				importedObjectDefinitions &&
				error?.type === 'importMultipleObjectDefinitions' && (
					<ModalImportFailed
						error={error}
						handleOnclose={() => onClose()}
						importedObjectDefinitions={importedObjectDefinitions}
					/>
				)}

			{!warningModalVisible && !failedModalVisible && (
				<ModalImportContent
					JSONInputId={JSONInputId}
					apiURL={apiURL}
					error={error}
					externalReferenceCode={externalReferenceCode}
					fileName={fileName as string}
					handleOnClose={onClose}
					handleSubmit={handleSubmit}
					importLoading={importLoading}
					importURL={importURL}
					importedObjectDefinitions={importedObjectDefinitions}
					inputFile={inputFile as File}
					modalImportKey={modalImportKeyState}
					name={name}
					nameMaxLength={nameMaxLength}
					portletNamespace={portletNamespace}
					setError={setError}
					setExternalReferenceCode={setExternalReferenceCode}
					setFile={setFile}
					setImportedObjectDefinitions={setImportedObjectDefinitions}
					setName={setName}
				/>
			)}
		</ClayModal>
	) : null;
}
