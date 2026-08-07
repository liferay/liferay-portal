/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

import FieldWrapper from '../../common/components/forms/FieldWrapper';
import ApiHelper from '../../common/services/ApiHelper';
import StructureService from '../../common/services/StructureService';
import {openCMSModal} from '../../common/utils/openCMSModal';

const JSON_EXTENSION = '.json';

const REPEATABLE_GROUPS_FOLDER = 'L_CMS_STRUCTURE_REPEATABLE_GROUPS';

function readJSONFile(file: File): Promise<any> {
	return new Promise((resolve, reject) => {
		const fileReader = new FileReader();

		fileReader.onerror = () => reject();

		fileReader.onload = () => {
			try {
				resolve(JSON.parse(fileReader.result as string));
			}
			catch (error) {
				reject(error);
			}
		};

		fileReader.readAsText(file);
	});
}

function getImportErrorMessage(error: string): string {
	let parsedError;

	try {
		parsedError = JSON.parse(error);
	}
	catch (parseError) {
		return error;
	}

	if (Array.isArray(parsedError)) {
		const messages = parsedError
			.map((errorItem) => errorItem?.error?.title)
			.filter(Boolean);

		if (messages.length) {
			return messages.join(' ');
		}
	}

	return error;
}

async function importStructures(
	objectDefinitions: any,
	importURL: string,
	successMessage: string,
	loadData?: () => void
) {
	const formData = new FormData();

	formData.append('active', 'true');

	formData.append(
		'objectDefinitions',
		JSON.stringify(
			Array.isArray(objectDefinitions)
				? objectDefinitions
				: [objectDefinitions]
		)
	);

	const {error} = await ApiHelper.postFormData(formData, importURL);

	if (error) {
		openToast({
			message: getImportErrorMessage(error),
			type: 'danger',
		});

		return;
	}

	openToast({
		message: successMessage,
		type: 'success',
	});

	loadData?.();
}

export default function ImportStructuresModalContent({
	closeModal,
	importURL,
	loadData,
}: {
	closeModal: () => void;
	importURL: string;
	loadData?: () => void;
}) {
	const [jsonFile, setJsonFile] = useState<File | null>(null);
	const [errorMessage, setErrorMessage] = useState('');
	const [loading, setLoading] = useState(false);

	const onFileChange = (file: File | null) => {
		if (!file) {
			setErrorMessage('');
		}

		setJsonFile(file);
	};

	const importStructure = async (
		objectDefinitions: any,
		successMessage: string
	) => {
		setLoading(true);

		await importStructures(
			objectDefinitions,
			importURL,
			successMessage,
			loadData
		);

		setLoading(false);

		closeModal();
	};

	const onImportButtonClick = async () => {
		if (!jsonFile) {
			setErrorMessage(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('json-file')
				)
			);

			return;
		}

		setLoading(true);

		let parsedFile: any;

		try {
			parsedFile = await readJSONFile(jsonFile);
		}
		catch (error) {
			setLoading(false);
			setErrorMessage(
				Liferay.Language.get('you-have-entered-invalid-json')
			);

			return;
		}

		const definitions = Array.isArray(parsedFile)
			? parsedFile
			: [parsedFile];

		const mainStructures = definitions.filter(
			(definition) =>
				definition?.externalReferenceCode &&
				definition.objectFolderExternalReferenceCode !==
					REPEATABLE_GROUPS_FOLDER
		);

		const existingNames: string[] = [];

		for (const definition of mainStructures) {
			const {data, error} = await StructureService.getStructure(
				definition.externalReferenceCode
			);

			if (!error && data) {
				const objectDefinition = data as {
					label: {[key: string]: string};
					name: string;
				};

				existingNames.push(
					objectDefinition.label?.[
						Liferay.ThemeDisplay.getLanguageId()
					] || objectDefinition.name
				);
			}
		}

		setLoading(false);

		if (existingNames.length) {
			closeModal();

			openCMSModal({
				contentComponent: ({closeModal}: {closeModal: () => void}) =>
					WarningModalContent({
						closeModal,
						existingStructureNames: existingNames,
						importStructure: () =>
							importStructures(
								parsedFile,
								importURL,
								Liferay.Language.get(
									'the-content-structure-was-successfully-imported-and-the-existing-content-structure-was-overwritten'
								),
								loadData
							),
					}),
				size: 'md',
				status: 'warning',
			});

			return;
		}

		const successMessage =
			mainStructures.length > 1
				? Liferay.Language.get(
						'the-content-structures-were-successfully-imported'
					)
				: Liferay.Language.get(
						'the-content-structure-was-successfully-imported'
					);

		await importStructure(parsedFile, successMessage);
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('import-content-structures')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="text-secondary">
					{Liferay.Language.get(
						'select-a-json-file-to-import-the-content-structures'
					)}
				</p>

				<SelectFileField
					errorMessage={errorMessage}
					fieldId="jsonFileId"
					label={Liferay.Language.get('json-file')}
					onFileChange={onFileChange}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={loading}
							displayType="primary"
							onClick={onImportButtonClick}
						>
							{Liferay.Language.get('import')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

function WarningModalContent({
	closeModal,
	existingStructureNames,
	importStructure,
}: {
	closeModal: () => void;
	existingStructureNames: string[];
	importStructure: () => Promise<void>;
}) {
	const [loading, setLoading] = useState(false);

	const onImportButtonClick = async () => {
		setLoading(true);

		await importStructure();

		closeModal();
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('import-content-structures')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>
					{Liferay.Language.get(
						'import-and-override-content-structure-warning-message'
					)}
				</p>

				<ul>
					{existingStructureNames.map((name, index) => (
						<li key={index}>
							<strong>{name}</strong>
						</li>
					))}
				</ul>

				<p>
					{Liferay.Language.get(
						'do-you-want-to-proceed-with-the-import-process'
					)}
				</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={loading}
							displayType="warning"
							onClick={onImportButtonClick}
						>
							{Liferay.Language.get('import')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

function SelectFileField({
	errorMessage,
	fieldId,
	label,
	onFileChange,
}: {
	errorMessage?: string;
	fieldId: string;
	label: string;
	onFileChange?: (file: File | null) => void;
}) {
	const fileInputRef = useRef<HTMLInputElement | null>(null);
	const [file, setFile] = useState<File | null>(null);

	const resetFileInput = () => {
		if (fileInputRef && fileInputRef.current) {
			fileInputRef.current.value = '';
		}

		setFile(null);

		onFileChange?.(null);
	};

	const handleFileInputChange = ({
		target,
	}: React.ChangeEvent<HTMLInputElement>) => {
		if (
			!target.files ||
			target.files?.length === 0 ||
			!target.files[0].name.endsWith(JSON_EXTENSION)
		) {
			return;
		}

		setFile(target.files[0]);

		onFileChange?.(target.files[0]);
	};

	return (
		<FieldWrapper
			errorMessage={errorMessage}
			fieldId={fieldId}
			label={label}
		>
			<div className="align-items-center d-flex">
				<ClayButton
					className="flex-shrink-0"
					displayType="secondary"
					onClick={() => fileInputRef.current?.click()}
					type="button"
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="upload" />
					</span>

					{Liferay.Language.get('select-file')}
				</ClayButton>

				<input
					accept={JSON_EXTENSION}
					className="d-none"
					onChange={handleFileInputChange}
					ref={fileInputRef}
					type="file"
				/>

				{file && (
					<>
						<strong className="ml-3 overflow-hidden text-break">
							{file.name}
						</strong>

						<ClayButtonWithIcon
							aria-label={sub(
								Liferay.Language.get('remove-x'),
								Liferay.Language.get('file')
							)}
							borderless
							displayType="secondary"
							monospaced
							onClick={resetFileInput}
							symbol="times-circle-full"
							title={sub(
								Liferay.Language.get('remove-x'),
								Liferay.Language.get('file')
							)}
							type="button"
						/>
					</>
				)}
			</div>
		</FieldWrapper>
	);
}
