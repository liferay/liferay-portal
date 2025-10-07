/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	constantsUtils,
	invalidateRequired,
	openToast,
	useForm,
} from '@liferay/object-js-components-web';
import {sub} from 'frontend-js-web';

import {defaultLanguageId} from '../../utils/constants';
import {normalizeFieldSettings} from '../../utils/fieldSettings';
import {ObjectFieldErrors} from './ObjectFieldFormBase';

const AUTO_INCREMENT_INITIAL_VALUE_REGEX = /^(?!0+$)\d+$/;

interface IUseObjectFieldForm {
	forbiddenChars?: string[];
	forbiddenLastChars?: string[];
	forbiddenNames?: string[];
	initialValues: Partial<ObjectField>;
	objectFields?: Partial<ObjectField>[];
	onSubmit: (field: ObjectField) => void;
}

export function useObjectFieldForm({
	forbiddenChars,
	forbiddenLastChars,
	forbiddenNames,
	initialValues,
	objectFields,
	onSubmit,
}: IUseObjectFieldForm) {
	const validate = (field: Partial<ObjectField>) => {
		const getSourceFolderError = (folderPath: string) => {

			// folder name cannot end with invalid last characters

			const lastChar = folderPath[folderPath.length - 1];

			if (forbiddenLastChars?.some((char) => char === lastChar)) {
				return sub(
					Liferay.Language.get(
						'the-folder-name-cannot-end-with-the-following-characters-x'
					),
					forbiddenLastChars.join(' ')
				);
			}

			// folder name cannot contain invalid characters

			if (forbiddenChars?.some((symbol) => folderPath.includes(symbol))) {
				return sub(
					Liferay.Language.get(
						'the-folder-name-cannot-contain-the-following-invalid-characters-x'
					),
					forbiddenChars.join(' ')
				);
			}

			// folder name cannot be a reserved word

			const reservedNames = new Set(forbiddenNames);

			if (
				forbiddenNames &&
				folderPath.split('/').some((name) => reservedNames.has(name))
			) {
				return sub(
					Liferay.Language.get(
						'the-folder-name-cannot-have-a-reserved-word-such-as-x'
					),
					forbiddenNames.join(', ')
				);
			}

			return null;
		};

		const errors: ObjectFieldErrors = {};

		const label = field.label?.[defaultLanguageId];

		const settings = normalizeFieldSettings(field.objectFieldSettings);

		if (invalidateRequired(label)) {
			errors.label = constantsUtils.REQUIRED_MSG;
		}

		if (invalidateRequired(field.name ?? label)) {
			errors.name = constantsUtils.REQUIRED_MSG;
		}

		if (!field.businessType) {
			errors.businessType = constantsUtils.REQUIRED_MSG;
		}
		else if (field.businessType === 'AutoIncrement') {
			if (!settings.initialValue) {
				errors.initialValue = constantsUtils.REQUIRED_MSG;
			}
			else if (
				!AUTO_INCREMENT_INITIAL_VALUE_REGEX.exec(
					settings.initialValue as string
				)
			) {
				errors.initialValue = Liferay.Language.get(
					'this-value-cannot-be-less-than-1'
				);
			}
		}
		else if (field.businessType === 'Aggregation') {
			if (!settings.function) {
				errors.function = constantsUtils.REQUIRED_MSG;
			}

			if (settings.function !== 'COUNT' && !settings.objectFieldName) {
				errors.objectFieldName = constantsUtils.REQUIRED_MSG;
			}

			if (!settings.objectRelationshipName) {
				errors.objectRelationshipName = constantsUtils.REQUIRED_MSG;
			}
		}
		else if (field.businessType === 'Assignee' && objectFields) {
			if (
				objectFields.some(
					({businessType, externalReferenceCode}) =>
						businessType === 'Assignee' &&
						externalReferenceCode !== field.externalReferenceCode
				)
			) {
				errors.businessType = Liferay.Language.get(
					'an-object-definition-can-only-have-one-assignee-field'
				);
			}
		}
		else if (field.businessType === 'Attachment') {
			const uploadRequestSizeLimit = Math.floor(
				Liferay.PropsValues.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE /
					1048576
			);

			if (
				invalidateRequired(
					settings.acceptedFileExtensions as string | undefined
				)
			) {
				errors.acceptedFileExtensions = constantsUtils.REQUIRED_MSG;
			}
			if (!settings.fileSource) {
				errors.fileSource = constantsUtils.REQUIRED_MSG;
			}
			if (!settings.maximumFileSize && settings.maximumFileSize !== 0) {
				errors.maximumFileSize = constantsUtils.REQUIRED_MSG;
			}
			else if (
				(settings.maximumFileSize as number) > uploadRequestSizeLimit
			) {
				errors.maximumFileSize = sub(
					Liferay.Language.get(
						'file-size-is-larger-than-the-allowed-overall-maximum-upload-request-size-x-mb'
					),
					uploadRequestSizeLimit
				);
			}
			else if ((settings.maximumFileSize as number) < 0) {
				errors.maximumFileSize = sub(
					Liferay.Language.get(
						'only-integers-greater-than-or-equal-to-x-are-allowed'
					),
					0
				);
			}

			if (settings.showFilesInDocumentsAndMedia) {
				if (
					invalidateRequired(
						settings.storageDLFolderPath as string | undefined
					)
				) {
					errors.storageDLFolderPath = constantsUtils.REQUIRED_MSG;
				}
				else {
					const sourceFolderError = getSourceFolderError(
						settings.storageDLFolderPath as string
					);

					if (sourceFolderError !== null) {
						errors.storageDLFolderPath = sourceFolderError;
					}
				}
			}
		}
		else if (field.businessType === 'Formula') {
			if (invalidateRequired(settings.output as string)) {
				errors.output = constantsUtils.REQUIRED_MSG;
			}
		}
		else if (
			field.businessType === 'LongText' ||
			field.businessType === 'Text'
		) {
			if (settings.showCounter && !settings.maxLength) {
				errors.maxLength = constantsUtils.REQUIRED_MSG;
			}
		}
		else if (field.businessType === 'Picklist') {
			if (!field.listTypeDefinitionId) {
				errors.listTypeDefinitionId = constantsUtils.REQUIRED_MSG;
			}

			const thereIsDefaultValueType = field.objectFieldSettings?.some(
				(setting) =>
					setting.name === 'defaultValueType' && setting.value
			);

			const thereIsDefaultValue = field.objectFieldSettings?.some(
				(setting) => setting.name === 'defaultValue' && setting.value
			);

			if (!field.id) {
				if (field.state && !thereIsDefaultValue) {
					errors.defaultValue = constantsUtils.REQUIRED_MSG;

					openToast({
						message: Liferay.Language.get(
							'please-fill-out-all-required-fields'
						),
						type: 'danger',
					});
				}
			}
			else {
				if (thereIsDefaultValueType && !thereIsDefaultValue) {
					errors.defaultValue = constantsUtils.REQUIRED_MSG;
				}
			}
		}

		return errors;
	};

	const {
		errors,
		handleChange,
		handleSubmit,
		handleValidate,
		setValues,
		values,
	} = useForm<ObjectField, {[key in ObjectFieldSettingName]: unknown}>({
		initialValues,
		onSubmit,
		validate,
	});

	return {
		errors,
		handleChange,
		handleSubmit,
		handleValidate,
		setValues,
		values,
	};
}
