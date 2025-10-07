/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {
	ObjectDefinition,
	ObjectDefinitions,
	ObjectField,
	ObjectRelationship,
} from '../types/ObjectDefinition';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import {Field, FieldType, MultiselectField, SingleSelectField} from './field';
import getUuid from './getUuid';
import isCustomObjectField from './isCustomObjectField';
import sortChildren from './sortChildren';

export default function buildStructure({
	mainObjectDefinition,
	objectDefinitions,
}: {
	mainObjectDefinition: ObjectDefinition;
	objectDefinitions: ObjectDefinitions;
}): Structure {
	const uuid = getUuid();

	const isPublished = mainObjectDefinition.status?.code === 0;

	return {
		children: buildChildren({
			objectDefinition: mainObjectDefinition,
			objectDefinitions,
			parent: uuid,
		}),
		erc: mainObjectDefinition.externalReferenceCode,
		label: mainObjectDefinition.label,
		name: mainObjectDefinition.name ?? '',
		spaces: getSpaces(mainObjectDefinition),
		status: isPublished ? 'published' : 'draft',
		type: mainObjectDefinition.objectFolderExternalReferenceCode as Structure['type'],
		uuid: getUuid(),
		workflows: getWorkflows(mainObjectDefinition),
	};
}

export function buildChildren({
	ancestors = [],
	objectDefinition,
	objectDefinitions,
	parent,
}: {
	ancestors?: Array<ObjectDefinition['externalReferenceCode']>;
	objectDefinition: ObjectDefinition;
	objectDefinitions: ObjectDefinitions;
	parent: Uuid;
}) {
	const objectFields = objectDefinition.objectFields || [];
	const objectRelationships = objectDefinition.objectRelationships || [];

	const children: Structure['children'] = new Map();

	if (ancestors.includes(objectDefinition.externalReferenceCode)) {
		return children;
	}

	for (const objectField of objectFields) {
		if (!isCustomObjectField(objectField)) {
			continue;
		}

		const field = buildField({objectField, parent});

		children.set(field.uuid, field);
	}

	for (const objectRelationship of objectRelationships) {
		if (isRepeatableGroup(objectRelationship, objectDefinitions)) {
			const repeatableGroup = buildRepeatableGroup({
				ancestors: [
					...ancestors,
					objectDefinition.externalReferenceCode,
				],
				erc: objectRelationship.objectDefinitionExternalReferenceCode2,
				objectDefinitions,
				parent,
				relationshipName: objectRelationship.name,
			});

			children.set(repeatableGroup.uuid, repeatableGroup);
		}
		else {
			const referencedStructure = buildReferencedStructure({
				ancestors: [
					...ancestors,
					objectDefinition.externalReferenceCode,
				],
				erc: objectRelationship.objectDefinitionExternalReferenceCode2,
				objectDefinitions,
				parent,
				relationshipName: objectRelationship.name,
			});

			children.set(referencedStructure.uuid, referencedStructure);
		}
	}

	return sortChildren(children);
}

export function buildField({
	objectField,
	parent,
}: {
	objectField: ObjectField;
	parent: Uuid;
}) {
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
		locked: objectField.system,
		name: objectField.name,
		parent,
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

	return field;
}

export function buildReferencedStructure({
	ancestors,
	erc,
	objectDefinitions,
	parent,
	relationshipName,
}: {
	ancestors: Array<ObjectDefinition['externalReferenceCode']>;
	erc: ReferencedStructure['erc'];
	objectDefinitions: ObjectDefinitions;
	parent: Uuid;
	relationshipName: ObjectRelationship['name'];
}): ReferencedStructure {
	const uuid = getUuid();

	const objectDefinition = objectDefinitions[erc]!;

	const url = new URL(window.location.href);

	url.searchParams.set(
		'objectDefinitionExternalReferenceCode',
		objectDefinition.externalReferenceCode
	);
	url.searchParams.set(
		'objectFolderExternalReferenceCode',
		String(objectDefinition.objectFolderExternalReferenceCode)
	);

	return {
		children: buildChildren({
			ancestors,
			objectDefinition,
			objectDefinitions,
			parent: uuid,
		}),
		editURL: url.href,
		erc,
		label: objectDefinition.label,
		name: objectDefinition.name!,
		parent,
		relationshipName,
		spaces: getSpaces(objectDefinition),
		type: 'referenced-structure',
		uuid,
		workflows: getWorkflows(objectDefinition),
	};
}

export function buildRepeatableGroup({
	ancestors,
	erc,
	objectDefinitions,
	parent,
	relationshipName,
}: {
	ancestors: Array<ObjectDefinition['externalReferenceCode']>;
	erc: RepeatableGroup['erc'];
	objectDefinitions: ObjectDefinitions;
	parent: Uuid;
	relationshipName: ObjectRelationship['name'];
}): RepeatableGroup {
	const uuid = getUuid();

	const objectDefinition = objectDefinitions[erc]!;

	return {
		children: buildChildren({
			ancestors,
			objectDefinition,
			objectDefinitions,
			parent: uuid,
		}),
		erc,
		label: objectDefinition.label,
		name: objectDefinition.name!,
		parent,
		relationshipName,
		type: 'repeatable-group',
		uuid,
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

export function getSpaces(objectDefinition: ObjectDefinition) {
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

export function getWorkflows(objectDefinition: ObjectDefinition) {
	const workflows: Structure['workflows'] = {};

	const definitionLinks = objectDefinition.workflowDefinitionLinks || [];

	for (const {
		groupExternalReferenceCode,
		workflowDefinitionName,
	} of definitionLinks) {
		workflows[groupExternalReferenceCode] = workflowDefinitionName;
	}

	return workflows;
}

function isRepeatableGroup(
	objectRelationship: ObjectRelationship,
	objectDefinitions: ObjectDefinitions
) {
	const objectDefinition =
		objectDefinitions[
			objectRelationship.objectDefinitionExternalReferenceCode2
		];

	return (
		objectDefinition.objectFolderExternalReferenceCode ===
		'L_CMS_STRUCTURE_REPEATABLE_GROUPS'
	);
}
