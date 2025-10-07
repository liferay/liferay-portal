/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

interface Actions {
	create?: HTTPMethod;
	delete?: HTTPMethod;
	get?: HTTPMethod;
	permissions?: HTTPMethod;
	update?: HTTPMethod;
}

interface AddObjectEntryDefinitions {
	externalReferenceCode: string;
	id: number;
	label: string;
	related?: boolean;
	system?: boolean;
}

interface ObjectActionTriggerExecutorItem {
	checked?: boolean;
	description?: string;
	disabled?: boolean;
	label: string;
	name?: string;
	popover?: {body: string; header: string};
	type?: string;
	value?: string;
}

type DefinitionAction = {
	href: string;
	method: string;
};

type DefinitionActions = {
	delete: DefinitionAction;
	get: DefinitionAction;
	permissions: DefinitionAction;
	update: DefinitionAction;
};

interface DeletedObjectDefinition {
	hasObjectRelationship: boolean;
	id: number;
	name: string;
	objectEntriesCount: number;
}

type ObjectFieldDeleteInfoProps = {
	deleteLastPublishedObjectDefinitionObjectField: boolean;
	deleteObjectFieldObjectValidationRuleSetting: boolean;
	showObjectFieldDeletionConfirmationModal: boolean;
	showObjectFieldDeletionNotAllowedModal: boolean;
};

type ExcludesFilterOperator = {
	not: {
		in: string[] | number[];
	};
};

interface HTTPMethod {
	href: string;
	method: string;
}

interface IItem extends LabelValueObject {
	checked?: boolean;
}

type IncludesFilterOperator = {
	in: string[] | number[];
};

interface KeyValueObject {
	key: string;
	value: string;
}

interface LabelKeyObject {
	key: string;
	label: string;
}

interface LabelNameObject {
	label: string;
	name: string;
}

interface LabelValueObject<T = string> {
	label: string;
	value: T;
}

interface ListTypeDefinition {
	actions: Actions;
	externalReferenceCode: string;
	id: number;
	key: string;
	listTypeEntries: ListTypeEntry[];
	name: string;
	name_i18n: LocalizedValue<string>;
	system: boolean;
}

interface ListTypeEntry {
	externalReferenceCode: string;
	id: number;
	key: string;
	listTypeDefinitionId: number;
	name: string;
	name_i18n: LocalizedValue<string>;
}

type LocalizedValue<T> = Liferay.Language.LocalizedValue<T>;

interface ModelBuilderModals
	extends Omit<
		ViewObjectDefinitionsModals,
		| 'bindToRootObjectDefinition'
		| 'objectFieldDeletionNotAllowed'
		| 'unbindFromRootObjectDefinition'
	> {
	addObjectField: boolean;
	addObjectRelationship: boolean;
	deleteObjectRelationship: boolean;
	editObjectDefinitionExternalReferenceCode: boolean;
	publishObjectDefinitions: boolean;
	redirectToEditObjectDefinitionDetails: boolean;
}

interface NameValueObject {
	name: string;
	value: string;
}

type NotificationTemplate = {
	attachmentObjectFieldIds: string[] | number[];
	bcc: string;
	body: LocalizedValue<string>;
	cc: string;
	description: string;
	externalReferenceCode: string;
	from: string;
	fromName: LocalizedValue<string>;
	id: number;
	name: string;
	objectDefinitionId: number | null;
	subject: LocalizedValue<string>;
	to: LocalizedValue<string>;
	type: 'email' | 'userNotification';
};

interface ObjectAction {
	active: boolean;
	conditionExpression?: string;
	description?: string;
	errorMessage: LocalizedValue<string>;
	id?: number;
	label: LocalizedValue<string>;
	name: string;
	objectActionExecutorKey: string;
	objectActionTriggerKey: string;
	objectDefinitionId?: number;
	objectDefinitionsRelationshipsURL: string;
	parameters: ObjectActionParameters;
	script?: string;
	system: boolean;
}

interface ObjectActionParameters {
	lineCount?: number;
	notificationTemplateExternalReferenceCode?: string;
	notificationTemplateId?: number;
	objectDefinitionExternalReferenceCode?: string;
	objectDefinitionId?: number;
	predefinedValues?: PredefinedValue[];
	relatedObjectEntries?: boolean;
	script?: string;
	secret?: string;
	system?: boolean;
	url?: string;
	usePreferredLanguageForGuests?: boolean;
}

interface ObjectDefinition {
	accountEntryRestricted: boolean;
	accountEntryRestrictedObjectFieldId: string;
	accountEntryRestrictedObjectFieldName: string;
	actions: Actions;
	active: boolean;
	dateCreated: string;
	dateModified: string;
	dbTableName?: string;
	defaultLanguageId: Liferay.Language.Locale;
	enableCategorization: boolean;
	enableComments: boolean;
	enableFormContainer: boolean;
	enableFriendlyURLCustomization: boolean;
	enableIndexSearch: boolean;
	enableLocalization: boolean;
	enableObjectEntryDraft: boolean;
	enableObjectEntryHistory: boolean;
	enableObjectEntrySchedule: boolean;
	enableObjectEntrySubscription: boolean;
	externalReferenceCode: string;
	friendlyURLSeparator: string;
	id: number;
	label: LocalizedValue<string>;
	modifiable?: boolean;
	name: string;
	objectActions: [];
	objectDefinitionSettings?: NameValueObject[];
	objectFields: ObjectField[];
	objectFolderExternalReferenceCode: string;
	objectLayouts: [];
	objectRelationships: ObjectRelationship[];
	objectViews: [];
	panelCategoryKey: string;
	parameterRequired?: boolean;
	pluralLabel: LocalizedValue<string>;
	portlet: boolean;
	restContextPath: string;
	rootObjectDefinitionExternalReferenceCode: string;
	scope: string;
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
	storageType?: string;
	system: boolean;
	titleObjectFieldId: number | string;
	titleObjectFieldName: string;
}

interface ObjectDefinitions {
	actions: Actions;
	items: ObjectDefinition[];
}

interface ObjectDefinitionNodeData
	extends Omit<ObjectDefinition, 'objectFields'> {
	hasObjectDefinitionDeleteResourcePermission: boolean;
	hasObjectDefinitionManagePermissionsResourcePermission: boolean;
	hasObjectDefinitionUpdateResourcePermission: boolean;
	hasObjectDefinitionViewResourcePermission: boolean;
	linkedObjectDefinition: boolean;
	objectFields: ObjectFieldNodeRow[];
	selected: boolean;
	showAllObjectFields: boolean;
}

interface ObjectEntry {
	actions: Actions;
	creator: {
		additionalName: string;
		contentType: string;
		familyName: string;
		givenName: string;
		id: number;
		name: string;
	};
	dateCreated: string;
	dateModified: string;
	displayDate?: string | null;
	externalReferenceCode: string;
	id: number;
	name: string;
	status: {
		code: number;
		label: string;
		label_i18n: string;
	};
	systemProperties?: {
		version?: {
			number: number;
		};
	};
	[key: string]: string | number | unknown;
}

interface ObjectField {
	DBType: string;
	businessType: ObjectFieldBusinessTypeName;
	defaultValue?: string;
	externalReferenceCode: string;
	id: number;
	indexed: boolean;
	indexedAsKeyword: boolean;
	indexedLanguageId: Liferay.Language.Locale | string;
	label: LocalizedValue<string>;
	listTypeDefinitionExternalReferenceCode: string;
	listTypeDefinitionId?: number;
	localized: boolean;
	name: string;
	objectDefinitionExternalReferenceCode1: string;
	objectFieldSettings?: ObjectFieldSetting[];
	objectRelationshipExternalReferenceCode?: string;
	readOnly: ReadOnlyFieldValue;
	readOnlyConditionExpression: string;
	relationshipId?: number;
	relationshipType?: unknown;
	required: boolean;
	state: boolean;
	system?: boolean;
}

type ObjectFieldBusinessTypeName =
	| 'Assignee'
	| 'Aggregation'
	| 'Attachment'
	| 'AutoIncrement'
	| 'Boolean'
	| 'Date'
	| 'DateTime'
	| 'Decimal'
	| 'Encrypted'
	| 'Formula'
	| 'Integer'
	| 'LongInteger'
	| 'LongText'
	| 'MultiselectPicklist'
	| 'Picklist'
	| 'PrecisionDecimal'
	| 'Relationship'
	| 'RichText'
	| 'Text';

type ObjectFieldDateRangeFilterSettings = {
	[key: string]: string;
};

type ObjectFieldFilterSetting = {
	filterBy?: string;
	filterType?: string;
	json:
		| {
				[key: string]:
					| string
					| string[]
					| ObjectFieldDateRangeFilterSettings
					| undefined;
		  }
		| ExcludesFilterOperator
		| IncludesFilterOperator
		| string;
};

interface ObjectFieldNodeRow extends Partial<ObjectField> {
	primaryKey: boolean;
	required: boolean;
	selected: boolean;
}

type ObjectFieldPicklistSetting = {
	id: number;
	objectStates: ObjectState[];
};

interface ObjectFieldSetting {
	name: ObjectFieldSettingName;
	objectFieldId?: number;
	value: ObjectFieldSettingValue;
}

type ObjectFieldSettingName =
	| 'acceptedFileExtensions'
	| 'defaultValue'
	| 'defaultValueType'
	| 'fileSource'
	| 'filters'
	| 'function'
	| 'initialValue'
	| 'maxLength'
	| 'maximumFileSize'
	| 'objectDefinition1ShortName'
	| 'objectFieldName'
	| 'objectRelationshipName'
	| 'output'
	| 'prefix'
	| 'script'
	| 'showCounter'
	| 'showFilesInDocumentsAndMedia'
	| 'stateFlow'
	| 'storageDLFolderPath'
	| 'suffix'
	| 'timeStorage'
	| 'uniqueValues'
	| 'uniqueValuesErrorMessage';

type ObjectFieldSettingValue =
	| LocalizedValue<string>
	| NameValueObject[]
	| ObjectFieldFilterSetting[]
	| ObjectFieldPicklistSetting
	| boolean
	| number
	| string;

interface ObjectFieldBusinessType {
	businessType: ObjectFieldBusinessTypeName;
	dbType: string;
	description: string;
	label: string;
}

interface ObjectFieldView extends ObjectField {
	checked?: boolean;
	filtered?: boolean;
	hasFilter?: boolean;
	type?: string;
}

interface ObjectFolder {
	actions: Actions;
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	label: LocalizedValue<string>;
	name: string;
	objectDefinitions?: ObjectDefinitionNodeData[];
	objectFolderItems: ObjectFolderItem[];
}

interface ObjectFolderItem {
	linkedObjectDefinition: boolean;
	objectDefinitionExternalReferenceCode: string;
	positionX: number;
	positionY: number;
}

interface ObjectFoldersRequestInfo {
	actions: Actions;
	items: ObjectFolder[];
}

interface ObjectRelationship {
	deletionType: string;
	edge?: boolean;
	id: number;
	label: LocalizedValue<string>;
	name: string;
	objectDefinitionExternalReferenceCode1: string;
	objectDefinitionExternalReferenceCode2: string;
	objectDefinitionId1: number;
	objectDefinitionId2: number;
	readonly objectDefinitionName2: string;
	parameterObjectFieldName?: string;
	reverse: boolean;
	system?: boolean;
	type: ObjectRelationshipType;
}

type ObjectRelationshipType = 'manyToMany' | 'oneToMany' | 'oneToOne';

interface ObjectState {
	key: string;
	objectStateTransitions: {key: string}[];
}

interface ObjectValidation {
	active: boolean;
	description?: string;
	engine: string;
	engineLabel: string;
	errorLabel: LocalizedValue<string>;
	id: number;
	lineCount?: number;
	name: LocalizedValue<string>;
	objectValidationRuleSettings?: ObjectValidationRuleSetting[];
	outputType?: string;
	script: string;
	system?: boolean;
}

interface ObjectValidationRuleSetting {
	name:
		| 'allowActiveStatusUpdate'
		| 'compositeKeyObjectFieldExternalReferenceCode'
		| 'outputObjectFieldExternalReferenceCode';
	value: string | boolean;
}

interface PickListItem {
	externalReferenceCode: string;
	id: number;
	key: string;
	name: string;
	name_i18n: LocalizedValue<string>;
}

interface PickList {
	actions: Actions;
	externalReferenceCode: string;
	id: number;
	key: string;
	listTypeEntries: PickListItem[];
	name: string;
	name_i18n: LocalizedValue<string>;
}

interface PredefinedValue {
	businessType: ObjectFieldBusinessTypeName;
	inputAsValue: boolean;
	label: LocalizedValue<string>;
	name: string;
	value: string;
}

type ReadOnlyFieldValue = '' | 'conditional' | 'false' | 'true';

type TFilterOperators = {
	dateOperators: LabelValueObject[];
	numericOperators: LabelValueObject[];
	picklistOperators: LabelValueObject[];
};

interface ShowObjectDefinitionsModals {
	addObjectDefinition: boolean;
	addObjectField: boolean;
	addObjectFolder: boolean;
	deleteObjectDefinition: boolean;
	deleteObjectFolder: boolean;
	editObjectFolder: boolean;
	importModal: boolean;
	moveObjectDefinition: boolean;
	objectDefinitionOnRootModelDeletionNotAllowed: boolean;
	objectFieldDeletionNotAllowed: boolean;
}

type SubmitError = string | null;
