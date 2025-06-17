/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {ObjectDefinition, ObjectField} from '../types/ObjectDefinition';
import {ReferencedStructure, Structure} from '../types/Structure';
import {Field, FieldType, MultiselectField, SingleSelectField} from './field';
import getUuid from './getUuid';

export default function buildStructure(
	objectDefinition: ObjectDefinition
): Structure | null {
	if (!objectDefinition) {
		return null;
	}

	const fields: Structure['fields'] = new Map();

	objectDefinition.objectFields?.forEach((objectField) => {
		if (objectField.system || objectField.businessType === 'Relationship') {
			return;
		}

		const indexableConfig = {
			indexed: objectField.indexed,
		} as Field['indexableConfig'];

		if (indexableConfig.indexed) {
			indexableConfig.indexedAsKeyword =
				objectField.indexedAsKeyword ?? false;
			indexableConfig.indexedLanguageId =
				objectField.indexedLanguageId !== ''
					? objectField.indexedLanguageId
					: undefined;
		}

		const uuid = getUuid();

		const field: Field = {
			erc: objectField.externalReferenceCode,
			indexableConfig,
			label: objectField.label,
			localized: objectField.localized,
			name: objectField.name,
			required: objectField.required,
			settings: getFieldSettings(objectField),
			type: getFieldType(objectField),
			uuid,
		};

		if (
			(field.type === 'single-select' || field.type === 'multiselect') &&
			!isNullOrUndefined(objectField.listTypeDefinitionId)
		) {
			(field as SingleSelectField | MultiselectField).picklistId =
				objectField.listTypeDefinitionId;
		}

		fields.set(uuid, field);
	});

	objectDefinition.objectRelationships?.forEach((objectRelationship) => {
		const uuid = getUuid();

		const referencedStructure: ReferencedStructure = {
			erc: objectRelationship.objectDefinitionExternalReferenceCode2,
			name: objectRelationship.name,
			type: 'referenced-structure',
			uuid,
		};

		fields.set(uuid, referencedStructure);
	});

	const isPublished = objectDefinition.status?.label === 'approved';

	return {
		erc: objectDefinition.externalReferenceCode,
		error: null,
		fields,
		history: {
			deletedFields: false,
		},
		id: objectDefinition.id ?? null,
		invalids: new Map(),
		label: objectDefinition.label,
		name: objectDefinition.name ?? '',
		publishedFields: isPublished ? new Set(fields.keys()) : new Set(),
		selection: [],
		spaces: getSpaces(objectDefinition),
		status: isPublished ? 'published' : 'draft',
		type: objectDefinition.objectFolderExternalReferenceCode as Structure['type'],
		unsavedChanges: false,
		uuid: getUuid(),
	};
}

function getFieldSettings(objectField: ObjectField): Field['settings'] {
	const settings: Record<string, any> = {};

	const objectFieldSettings: Record<string, any> = {};

	for (const objectFieldSetting of objectField.objectFieldSettings ?? []) {
		objectFieldSettings[objectFieldSetting.name] = objectFieldSetting.value;
	}

	if (objectField.businessType === 'Attachment') {
		settings.acceptedFileExtensions =
			objectFieldSettings.acceptedFileExtensions;
		settings.fileSource = objectFieldSettings.fileSource;
		settings.maximumFileSize = objectFieldSettings.maximumFileSize;

		if (objectFieldSettings.fileSource === 'userComputer') {
			settings.showFilesInDocumentsAndMedia =
				objectFieldSettings.showFilesInDocumentsAndMedia;
			settings.storageDLFolderPath =
				objectFieldSettings.storageDLFolderPath;
		}
	}
	else if (objectField.businessType === 'DateTime') {
		settings.timeStorage = objectFieldSettings.timeStorage;
	}
	else if (
		objectField.businessType === 'Integer' ||
		objectField.businessType === 'LongText' ||
		objectField.businessType === 'Text'
	) {
		if (objectFieldSettings.maxLength) {
			settings.maxLength = objectFieldSettings.maxLength;
		}

		if (objectFieldSettings.showCounter) {
			settings.showCounter = objectFieldSettings.showCounter;
		}

		if (objectFieldSettings.uniqueValues) {
			settings.uniqueValues = objectFieldSettings.uniqueValues;
		}
	}

	return settings as Field['settings'];
}

function getFieldType(objectField: ObjectField): FieldType {
	if (objectField.businessType === 'Picklist') {
		return 'single-select';
	}
	else if (objectField.businessType === 'MultiselectPicklist') {
		return 'multiselect';
	}

	const DB_TYPE_TO_FIELD_TYPE: Record<string, FieldType> = {
		BigDecimal: 'decimal',
		Boolean: 'boolean',
		Clob: 'long-text',
		Date: 'date',
		DateTime: 'datetime',
		Double: 'decimal',
		Integer: 'integer',
		Long: 'upload',
		RichText: 'rich-text',
		String: 'text',
		Upload: 'upload',
	} as const;

	return DB_TYPE_TO_FIELD_TYPE[objectField.DBType];
}

function getSpaces(objectDefinition: ObjectDefinition) {
	const settings = objectDefinition.objectDefinitionSettings || [];

	const acceptedGroupExternalReferenceCodes = settings.find(
		({name}) => name === 'acceptedGroupExternalReferenceCodes'
	)?.value;

	const acceptAllGroups = settings.find(
		({name}) => name === 'acceptAllGroups'
	)?.value;

	const spaces =
		acceptAllGroups === 'true'
			? 'all'
			: acceptedGroupExternalReferenceCodes?.split(',') || [];

	return spaces;
}
