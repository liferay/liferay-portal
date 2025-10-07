/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {ERRORS} from './errors';

interface Actions {
	delete?: HTTPMethod;
	get?: HTTPMethod;
	permissions?: HTTPMethod;
	update?: HTTPMethod;
}

export interface ErrorDetails extends Error {
	detail?: string;
	type?: string;
}

interface HTTPMethod {
	href: string;
	method: string;
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

type NotificationTemplateType = 'email' | 'userNotification';

type RecipientType = 'role' | 'term' | 'user';

type Recipient = {
	bcc: string;
	bccType: string;
	cc: string;
	ccType: string;
	from: string;
	fromName: LocalizedValue<string>;
	to: LocalizedValue<string>;
	toType: string;
};

type URLParameters = {
	filter?: string;
	pageSize?: string;
	search?: string;
	sort?: string;
};
export interface NotificationTemplate {
	attachmentObjectFieldIds: string[] | number[];
	bcc: string;
	body: LocalizedValue<string>;
	cc: string;
	description: string;
	editorType: 'freemarker' | 'richText';
	externalReferenceCode: string;
	from: string;
	fromName: LocalizedValue<string>;
	id: number;
	name: string;
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionId: number | null;
	recipientType: RecipientType;
	recipients: Recipient[];
	subject: LocalizedValue<string>;
	system: boolean;
	to: LocalizedValue<string>;
	type: NotificationTemplateType;
}

interface ObjectDefinitions {
	actions: Actions;
	items: ObjectDefinition[];
}

interface ObjectFolderItem {
	linkedObjectDefinition: boolean;
	objectDefinitionExternalReferenceCode: string;
	positionX: number;
	positionY: number;
}

interface ObjectFolder {
	actions: Actions;
	dateCreated: string;
	dateModified: string;
	externalReferenceCode: string;
	id: number;
	label: LocalizedValue<string>;
	name: string;
	objectFolderItems: ObjectFolderItem[];
}

interface ObjectFolderRequestInfo {
	actions: Actions;
	items: ObjectFolder[];
}

type ObjectRelationshipType = 'manyToMany' | 'oneToMany' | 'oneToOne';

interface ObjectRelationship {
	deletionType: string;
	edge: boolean;
	externalReferenceCode: string;
	id: number;
	label: LocalizedValue<string>;
	name: string;
	objectDefinitionExternalReferenceCode1: string;
	objectDefinitionExternalReferenceCode2: string;
	objectDefinitionId1: number;
	objectDefinitionId2: number;
	readonly objectDefinitionName2: string;
	parameterObjectFieldId?: number;
	reverse: boolean;
	type: ObjectRelationshipType;
}

interface saveProps {
	item: unknown;
	method?: 'PATCH' | 'POST' | 'PUT';
	returnValue?: boolean;
	url: string;
}

const headers = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
});

export async function deleteItem(url: string) {
	const response = await fetch(url, {headers, method: 'DELETE'});

	if (response.status === 401) {
		window.location.reload();
	}
	else if (!response.ok) {
		const {
			title,
		}: {
			title?: string;
		} = await response.json();

		const errorMessage = title || Liferay.Language.get('an-error-occurred');

		throw new Error(errorMessage);
	}
}

export function deleteListTypeEntry(listTypeEntryId: number) {
	return deleteItem(
		`/o/headless-admin-list-type/v1.0/list-type-entries/${listTypeEntryId}`
	);
}

export function deleteObjectDefinition(objectDefinitionId: number) {
	return deleteItem(
		`/o/object-admin/v1.0/object-definitions/${objectDefinitionId}`
	);
}

export function deleteObjectField(objectFieldId: number) {
	return deleteItem(`/o/object-admin/v1.0/object-fields/${objectFieldId}`);
}

export function deleteObjectFolder(objectFolderId: number) {
	return deleteItem(`/o/object-admin/v1.0/object-folders/${objectFolderId}`);
}

export function deleteObjectRelationship(objectRelationshipId: number) {
	return deleteItem(
		`/o/object-admin/v1.0/object-relationships/${objectRelationshipId}`
	);
}

export async function fetchJSON<T>(input: RequestInfo, init?: RequestInit) {
	const result = await fetch(input, {headers, method: 'GET', ...init});

	return (await result.json()) as T;
}

export async function getAllObjectDefinitions() {
	const fetchData = fetchJSON<ObjectDefinitions>(
		'/o/object-admin/v1.0/object-definitions?page=-1'
	);

	return await fetchData;
}

export async function getAllObjectFolders() {
	return await fetchJSON<ObjectFolderRequestInfo | undefined>(
		'/o/object-admin/v1.0/object-folders?pageSize=-1'
	);
}

export async function getList<T>(
	url: string,
	urlParameters: URLParameters = {}
) {
	const searchParams = new URLSearchParams();

	for (const [key, value] of Object.entries(urlParameters)) {
		if (value) {
			searchParams.append(key, value);
		}
	}

	const queryString = searchParams.toString().replace(/\+/g, '%20');

	const requestUrl = queryString ? `${url}?${queryString}` : url;

	const {items} = await fetchJSON<{items: T[]}>(requestUrl);

	return items;
}

export async function getListTypeDefinition(
	listTypeDefinitionId: number
): Promise<ListTypeDefinition> {
	return await fetchJSON<ListTypeDefinition>(
		`/o/headless-admin-list-type/v1.0/list-type-definitions/${listTypeDefinitionId}`
	);
}

export async function getListTypeDefinitionListTypeEntries(
	listTypeDefinitionId: number,
	urlParameters: URLParameters = {}
) {
	return await getList<ListTypeEntry>(
		`/o/headless-admin-list-type/v1.0/list-type-definitions/${listTypeDefinitionId}/list-type-entries`,
		{pageSize: '-1', ...urlParameters}
	);
}

export async function getListTypeDefinitions(
	urlParameters: URLParameters = {}
) {
	return await getList<ListTypeDefinition>(
		'/o/headless-admin-list-type/v1.0/list-type-definitions',
		{pageSize: '-1', ...urlParameters}
	);
}

export async function getNotificationTemplateByExternalReferenceCode(
	externalReferenceCode: string
) {
	return await fetchJSON<NotificationTemplate>(
		`/o/notification/v1.0/notification-templates/by-external-reference-code/${externalReferenceCode}`
	);
}

export async function getNotificationTemplateById(
	notificationTemplateId: number
) {
	return await fetchJSON<NotificationTemplate>(
		`/o/notification/v1.0/notification-templates/${notificationTemplateId}`
	);
}

export async function getNotificationTemplates(
	urlParameters: URLParameters = {}
) {
	return await getList<NotificationTemplate>(
		'/o/notification/v1.0/notification-templates',
		{pageSize: '-1', ...urlParameters}
	);
}

export async function getObjectDefinitionByExternalReferenceCode(
	externalReferenceCode: string
) {
	return await fetchJSON<ObjectDefinition>(
		`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${externalReferenceCode}`
	);
}

export async function getObjectDefinitionByExternalReferenceCodeObjectFields(
	externalReferenceCode: string,
	urlParameters: URLParameters = {}
) {
	return await getList<ObjectField>(
		`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${externalReferenceCode}/object-fields`,
		{pageSize: '-1', ...urlParameters}
	);
}

export async function getObjectDefinitionByExternalReferenceCodeObjectRelationships(
	externalReferenceCode: string,
	urlParameters: URLParameters = {}
) {
	return await getList<ObjectRelationship>(
		`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${externalReferenceCode}/object-relationships`,
		{...urlParameters}
	);
}

export async function getObjectDefinitionById(objectDefinitionId: number) {
	return await fetchJSON<ObjectDefinition>(
		`/o/object-admin/v1.0/object-definitions/${objectDefinitionId}`
	);
}

export async function getObjectDefinitionObjectFields(
	objectDefinitionId: number,
	urlParameters: URLParameters = {}
) {
	return await getList<ObjectField>(
		`/o/object-admin/v1.0/object-definitions/${objectDefinitionId}/object-fields`,
		{pageSize: '-1', ...urlParameters}
	);
}

export async function getObjectDefinitions(urlParameters: URLParameters = {}) {
	return await getList<ObjectDefinition>(
		`/o/object-admin/v1.0/object-definitions`,
		{pageSize: '-1', ...urlParameters}
	);
}

export async function getObjectField(objectFieldId: number) {
	return await fetchJSON<ObjectField>(
		`/o/object-admin/v1.0/object-fields/${objectFieldId}`
	);
}

export async function getObjectFolderByExternalReferenceCode(
	externalReferenceCode: string
) {
	const objectFolderResponse = await fetch(
		`/o/object-admin/v1.0/object-folders/by-external-reference-code/${externalReferenceCode}`,
		{method: 'GET'}
	);

	const objectFolder = (await objectFolderResponse.json()) as ObjectFolder;

	return objectFolder;
}

export async function getObjectRelationship<T>(objectRelationshipId: number) {
	return fetchJSON<T>(
		`/o/object-admin/v1.0/object-relationships/${objectRelationshipId}`
	);
}

export async function getObjectValidationRuleById<T>(
	objectValidationRuleId: number
) {
	return await fetchJSON<T>(
		`/o/object-admin/v1.0/object-validation-rules/${objectValidationRuleId}`
	);
}

export async function patchObjectDefinitionById(
	objectDefinition: Partial<ObjectDefinition>
) {
	return await fetch(
		`/o/object-admin/v1.0/object-definitions/${objectDefinition.id}`,
		{
			body: JSON.stringify(objectDefinition),
			headers,
			method: 'PATCH',
		}
	);
}

export async function postListTypeEntry({
	key,
	listTypeDefinitionId,
	name_i18n,
}: Partial<ListTypeEntry>) {
	return await save({
		item: {key, name_i18n},
		method: 'POST',
		url: `/o/headless-admin-list-type/v1.0/list-type-definitions/${listTypeDefinitionId}/list-type-entries`,
	});
}

export async function postObjectDefinition(
	objectDefinition: Partial<ObjectDefinition>
) {
	return await save<ObjectDefinition>({
		item: objectDefinition,
		method: 'POST',
		returnValue: true,
		url: `/o/object-admin/v1.0/object-definitions`,
	});
}

export async function postObjectDefinitionPublish(objectDefinitionId: number) {
	return await fetch(
		`/o/object-admin/v1.0/object-definitions/${objectDefinitionId}/publish`,
		{
			headers,
			method: 'POST',
		}
	);
}

export async function putObjectDefinition(
	objectDefinition: Partial<ObjectDefinition>
) {
	return await save<ObjectDefinition>({
		item: objectDefinition,
		method: 'PUT',
		returnValue: true,
		url: `/o/object-admin/v1.0/object-definitions/${objectDefinition.id}`,
	});
}

export async function putObjectDefinitionByExternalReferenceCode(
	objectDefinition: Partial<ObjectDefinition>
) {
	return await fetch(
		`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinition.externalReferenceCode}`,
		{
			body: JSON.stringify(objectDefinition),
			headers,
			method: 'PUT',
		}
	);
}

export async function putObjectFolderByExternalReferenceCode(
	objectFolder: Partial<ObjectFolder>
) {
	return await fetch(
		`/o/object-admin/v1.0/object-folders/by-external-reference-code/${objectFolder.externalReferenceCode}`,
		{
			body: JSON.stringify(objectFolder),
			headers,
			method: 'PUT',
		}
	);
}

export async function putListTypeDefinition({
	externalReferenceCode,
	id,
	listTypeEntries,
	name_i18n,
}: Partial<ListTypeDefinition>) {
	return await save({
		item: {externalReferenceCode, listTypeEntries, name_i18n},
		url: `/o/headless-admin-list-type/v1.0/list-type-definitions/${id}`,
	});
}

export async function putListTypeEntry({
	externalReferenceCode,
	id,
	name_i18n,
}: Partial<ListTypeEntry>) {
	return await save({
		item: {externalReferenceCode, name_i18n},
		url: `/o/headless-admin-list-type/v1.0/list-type-entries/${id}`,
	});
}

export async function putObjectRelationship({
	id,
	...others
}: Partial<ObjectRelationship>) {
	return await save({
		item: others,
		url: `/o/object-admin/v1.0/object-relationships/${id}`,
	});
}

export async function save<T>({
	item,
	method = 'PUT',
	returnValue = false,
	url,
}: saveProps) {
	const isFormData = item instanceof FormData;

	const response = await fetch(url, {
		body: isFormData ? item : JSON.stringify(item),
		...(!isFormData && {headers}),
		method,
	});

	if (response.status === 401) {
		window.location.reload();
	}
	else if (!response.ok) {
		const {
			detail,
			message,
			title,
			type,
		}: {
			detail?: string;
			message?: string | T[];
			title?: string;
			type?: string;
		} = await response.json();

		const errorMessage =
			(type && ERRORS[type]) ??
			title ??
			message ??
			Liferay.Language.get('an-error-occurred');

		const ErrorDetails = () => {
			return {
				detail,
				message: Array.isArray(errorMessage)
					? JSON.stringify(errorMessage)
					: errorMessage,
				type,
			} as ErrorDetails;
		};
		throw ErrorDetails();
	}

	if (returnValue) {
		return (await response.json()) as T;
	}
}
