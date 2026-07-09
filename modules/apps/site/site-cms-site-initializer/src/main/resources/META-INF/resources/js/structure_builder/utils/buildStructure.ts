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
} from '../../common/types/ObjectDefinition';
import {
	ReferencedStructure,
	RelatedContent,
	RepeatableGroup,
	Structure,
} from '../types/Structure';
import {Uuid} from '../types/Uuid';
import {Field, FieldType, SelectFromListField} from './field';
import getUuid from './getUuid';
import isCustomObjectField from './isCustomObjectField';
import sortChildren from './state/sortChildren';

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
		id: mainObjectDefinition.id,
		label: mainObjectDefinition.label,
		name: mainObjectDefinition.name ?? '',
		path: mainObjectDefinition.restContextPath ?? '',
		settings: getSettings(mainObjectDefinition),
		spaces: getSpaces(mainObjectDefinition),
		status: isPublished ? 'published' : 'draft',
		system: mainObjectDefinition.system ?? false,
		type: mainObjectDefinition.objectFolderExternalReferenceCode as Structure['type'],
		uuid,
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
		if (
			!isCustomObjectField(
				objectField,
				objectDefinition.externalReferenceCode
			)
		) {
			continue;
		}

		const field = buildField({objectField, parent});

		children.set(field.uuid, field);
	}

	for (const objectRelationship of objectRelationships) {
		if (isRelatedContent(objectRelationship)) {
			const relatedContent: RelatedContent = {
				erc: objectRelationship.externalReferenceCode,
				label: objectRelationship.label,
				multiselection: true,
				name: objectRelationship.name,
				parent,
				relatedStructureERC:
					objectRelationship.objectDefinitionExternalReferenceCode2,
				type: 'related-content',
				uuid: getUuid(),
			};

			children.set(relatedContent.uuid, relatedContent);
		}
		else if (isRepeatableGroup(objectRelationship, objectDefinitions)) {
			const repeatableGroup = buildRepeatableGroup({
				ancestors: [
					...ancestors,
					objectDefinition.externalReferenceCode,
				],
				erc: objectRelationship.objectDefinitionExternalReferenceCode2,
				objectDefinitions,
				parent,
				relationshipERC: objectRelationship.externalReferenceCode,
				relationshipName: objectRelationship.name,
			});

			children.set(repeatableGroup.uuid, repeatableGroup);
		}
		else if (objectRelationship.edge) {
			const referencedStructure = buildReferencedStructure({
				ancestors: [
					...ancestors,
					objectDefinition.externalReferenceCode,
				],
				erc: objectRelationship.objectDefinitionExternalReferenceCode2,
				objectDefinitions,
				parent,
				relationshipERC: objectRelationship.externalReferenceCode,
				relationshipName: objectRelationship.name,
			});

			children.set(referencedStructure.uuid, referencedStructure);
		}
	}

	const relatedContentObjectRelationships =
		getRelatedContentObjectRelationships(
			objectDefinition,
			objectDefinitions
		);

	for (const relatedContentObjectRelationship of relatedContentObjectRelationships) {
		const relatedContent: RelatedContent = {
			erc: relatedContentObjectRelationship.externalReferenceCode,
			label: relatedContentObjectRelationship.label,
			multiselection: false,
			name: relatedContentObjectRelationship.name,
			parent,
			relatedStructureERC:
				relatedContentObjectRelationship.objectDefinitionExternalReferenceCode1,
			type: 'related-content',
			uuid: getUuid(),
		};

		children.set(relatedContent.uuid, relatedContent);
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
		field.type === 'select-from-list' &&
		!isNullOrUndefined(objectField.listTypeDefinitionId)
	) {
		(field as SelectFromListField).picklistId =
			objectField.listTypeDefinitionId;
	}

	if (objectField.businessType === 'MultiselectPicklist') {
		(field as SelectFromListField).multiselection = true;
	}

	if (objectField.businessType === 'Picklist') {
		(field as SelectFromListField).multiselection = false;
	}

	return field;
}

export function buildReferencedStructure({
	ancestors,
	erc,
	objectDefinitions,
	parent,
	relationshipERC,
	relationshipName,
}: {
	ancestors: Array<ObjectDefinition['externalReferenceCode']>;
	erc: ReferencedStructure['erc'];
	objectDefinitions: ObjectDefinitions;
	parent: Uuid;
	relationshipERC: string;
	relationshipName: ObjectRelationship['name'];
}): ReferencedStructure {
	const uuid = getUuid();

	const objectDefinition = objectDefinitions[erc]!;

	const url = new URL(window.location.href);

	url.searchParams.set('objectDefinitionId', String(objectDefinition.id));
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
		relationshipERC,
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
	relationshipERC,
	relationshipName,
}: {
	ancestors: Array<ObjectDefinition['externalReferenceCode']>;
	erc: RepeatableGroup['erc'];
	objectDefinitions: ObjectDefinitions;
	parent: Uuid;
	relationshipERC: string;
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
		relationshipERC,
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

	if (objectField.businessType === 'EmailAddress') {
		if (objectFieldSettings.autocompleteDomains) {
			settings.autocompleteDomains =
				objectFieldSettings.autocompleteDomains;
		}

		if (objectFieldSettings.autocompleteEnabled) {
			settings.autocompleteEnabled =
				objectFieldSettings.autocompleteEnabled;
		}

		if (objectFieldSettings.blockedDomains) {
			settings.blockedDomains = objectFieldSettings.blockedDomains;
		}

		if (objectFieldSettings.uniqueValues) {
			settings.uniqueValues = objectFieldSettings.uniqueValues;
		}
	}
	else if (objectField.businessType === 'Attachment') {
		settings.acceptedFileExtensions =
			objectFieldSettings.acceptedFileExtensions;
		settings.fileSource = objectFieldSettings.fileSource;
		settings.maximumFileSize = objectFieldSettings.maximumFileSize;
		settings.storageDepotGroup = objectFieldSettings.storageDepotGroup;

		if (
			objectFieldSettings.fileSource === 'userComputerToCMSBasicDocument'
		) {
			settings.showFilesInLibrary =
				objectFieldSettings.showFilesInLibrary;
			settings.storageDLFolderPath =
				objectFieldSettings.storageDLFolderPath;
			settings.storageDepotGroup = objectFieldSettings.storageDepotGroup;
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
	else if (objectField.businessType === 'PhoneNumber') {
		settings.countrySource = objectFieldSettings.countrySource;

		if (objectFieldSettings.country) {
			settings.country = objectFieldSettings.country;
		}

		if (objectFieldSettings.uniqueValues) {
			settings.uniqueValues = objectFieldSettings.uniqueValues;
		}
	}

	return settings as Field['settings'];
}

function getFieldType(objectField: ObjectField): FieldType {
	if (
		objectField.businessType === 'Picklist' ||
		objectField.businessType === 'MultiselectPicklist'
	) {
		return 'select-from-list';
	}

	const BUSINESS_TYPE_TO_FIELD_TYPE: Record<string, FieldType> = {
		Attachment: 'upload',
		Boolean: 'boolean',
		Date: 'date',
		DateTime: 'datetime',
		Decimal: 'decimal',
		EmailAddress: 'email',
		Integer: 'integer',
		LongText: 'long-text',
		PhoneNumber: 'phone-number',
		RichText: 'rich-text',
		Text: 'text',
	} as const;

	return BUSINESS_TYPE_TO_FIELD_TYPE[objectField.businessType];
}

export function getSettings(
	objectDefinition: ObjectDefinition
): Structure['settings'] {
	const settings = objectDefinition.objectDefinitionSettings || [];

	const allowStandaloneObjectEntry = settings.find(
		({name}) => name === 'allowStandaloneObjectEntry'
	)?.value;

	return {allowStandaloneObjectEntry};
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

function isRelatedContent(objectRelationship: ObjectRelationship) {
	if (
		objectRelationship.type === 'manyToMany' &&
		!objectRelationship.reverse
	) {
		return true;
	}

	return false;
}

function getRelatedContentObjectRelationships(
	mainObjectDefinition: ObjectDefinition,
	objectDefinitions: ObjectDefinitions
) {
	const relationships: ObjectRelationship[] = [];

	for (const objectDefinition of Object.values(objectDefinitions)) {
		for (const objectRelationship of objectDefinition.objectRelationships ||
			[]) {
			if (
				objectRelationship.objectDefinitionExternalReferenceCode2 ===
					mainObjectDefinition.externalReferenceCode &&
				objectRelationship.type === 'oneToMany' &&
				!objectRelationship.edge &&
				!(
					objectDefinition.externalReferenceCode ===
						mainObjectDefinition.externalReferenceCode &&
					isRepeatableGroup(objectRelationship, objectDefinitions)
				)
			) {
				relationships.push(objectRelationship);
			}
		}
	}

	return relationships;
}
