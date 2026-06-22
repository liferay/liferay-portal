/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {API} from '@liferay/object-js-components-web';
import {fetch} from 'frontend-js-web';
import {FormEvent} from 'react';

import {FormDataJSONFormat, jsonToFormData} from '../../utils/formData';
import {ModalImportKeys} from './ModalImport';

interface HandleImportProps {
	importURL: string;
	item: FormData;
	onAfterImport?: () => void;
	onClose: () => void;
	setError: (
		value: React.SetStateAction<API.ErrorDetails | undefined>
	) => void;
	setFailedModalVisible?: (value: boolean) => void;
	setImportLoading: (value: boolean) => void;
	setWarningModalVisible: (value: boolean) => void;
}

interface HandleDefaultImportProps extends Omit<HandleImportProps, 'item'> {
	JSONInputId: string;
	apiURL: string;
	event: FormEvent<HTMLFormElement>;
	externalReferenceCode: string;
	importExtendedInfo: KeyValueObject;
	inputFile?: File | null;
	setImportFormData: (value: FormData) => void;
}

interface HandleImportMultiplesObjectDefinitionsProps
	extends Omit<HandleImportProps, 'item'> {
	importedObjectDefinitions: ObjectDefinition[];
	objectFolderExternalReferenceCode: string;
	setExistingObjectDefinitions: (value: ObjectDefinition[]) => void;
	setImportFormData: (value: FormData) => void;
	setModalImportKeyState: (value: ModalImportKeys) => void;
	setWarningModalVisible: (value: boolean) => void;
}

export async function handleDefaultImport({
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
}: HandleDefaultImportProps) {
	const formData = new FormData(event.currentTarget);
	const formDataObject: FormDataJSONFormat = {};

	formData.forEach((value, key) => {
		if (key.includes(JSONInputId)) {
			formDataObject[key] = inputFile as File;

			return;
		}

		formDataObject[key] = value;

		return;
	});

	if (importExtendedInfo) {
		formDataObject[importExtendedInfo.key] = importExtendedInfo.value;
	}

	const newFormData = jsonToFormData(formDataObject);

	const response = await fetch(`${apiURL}${externalReferenceCode}`);

	if (response.status === 204 || response.status === 404) {
		handleImport({
			importURL,
			item: newFormData,
			onAfterImport,
			onClose,
			setError,
			setImportLoading,
			setWarningModalVisible,
		});
	}
	else {
		setImportFormData(newFormData);
		setWarningModalVisible(true);
	}
}

export async function handleImport({
	importURL,
	item,
	onAfterImport,
	onClose,
	setError,
	setFailedModalVisible,
	setImportLoading,
	setWarningModalVisible,
}: HandleImportProps) {
	try {
		setImportLoading(true);

		await API.save({
			item,
			method: 'POST',
			url: importURL,
		});

		if (onAfterImport) {
			onAfterImport();

			onClose();
		}
		else {
			window.location.reload();
		}

		setImportLoading(false);
	}
	catch (error) {
		if (
			setFailedModalVisible &&
			(error as API.ErrorDetails).type ===
				'importMultipleObjectDefinitions'
		) {
			setFailedModalVisible(true);
			setWarningModalVisible(false);
		}

		setError(error as API.ErrorDetails);

		setImportLoading(false);
	}
}

export async function handleImportMultiplesObjectDefinitions({
	importURL,
	importedObjectDefinitions,
	objectFolderExternalReferenceCode,
	onAfterImport,
	onClose,
	setError,
	setExistingObjectDefinitions,
	setFailedModalVisible,
	setImportFormData,
	setImportLoading,
	setModalImportKeyState,
	setWarningModalVisible,
}: HandleImportMultiplesObjectDefinitionsProps) {
	const {items} = await API.getAllObjectDefinitions();

	const objectDefinitionsMap = new Map<string, ObjectDefinition>(
		items.map((objectDefinition) => [
			objectDefinition.name,
			objectDefinition,
		])
	);

	const existingObjectDefinitions: ObjectDefinition[] = [];

	importedObjectDefinitions.forEach((objectDefinition) => {
		if (objectDefinitionsMap.has(objectDefinition.name)) {
			existingObjectDefinitions.push(
				objectDefinitionsMap.get(
					objectDefinition.name
				) as ObjectDefinition
			);
		}
	});

	const importedObjectDefinitionsFormData = jsonToFormData({
		objectDefinitions: JSON.stringify(importedObjectDefinitions),
		objectFolderExternalReferenceCode,
	});

	setImportFormData(importedObjectDefinitionsFormData);

	if (existingObjectDefinitions.length) {
		setExistingObjectDefinitions(existingObjectDefinitions);

		setModalImportKeyState('objectDefinitions');

		setWarningModalVisible(true);

		return;
	}

	handleImport({
		importURL,
		item: importedObjectDefinitionsFormData,
		onAfterImport,
		onClose,
		setError,
		setFailedModalVisible,
		setImportLoading,
		setWarningModalVisible,
	});
}
