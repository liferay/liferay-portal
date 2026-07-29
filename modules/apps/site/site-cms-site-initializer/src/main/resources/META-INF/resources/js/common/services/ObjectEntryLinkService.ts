/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from './ApiHelper';

export type ObjectEntryLinkContext = {
	objectEntryId: string;
	relationshipObjectFieldName: string;
	restContextPath: string;
	scopeGroupId: string;
};

export type LinkedAsset = {
	classExternalReferenceCode: string;
	className: string;
	groupExternalReferenceCode: string;
};

type LinkRequest = {
	context: ObjectEntryLinkContext;
	linkedAsset: LinkedAsset;
};

export function toLinkContext(
	data: Partial<ObjectEntryLinkContext>
): ObjectEntryLinkContext | null {
	const {
		objectEntryId,
		relationshipObjectFieldName,
		restContextPath,
		scopeGroupId,
	} = data;

	if (
		!objectEntryId ||
		!relationshipObjectFieldName ||
		!restContextPath ||
		!scopeGroupId
	) {
		return null;
	}

	return {
		objectEntryId,
		relationshipObjectFieldName,
		restContextPath,
		scopeGroupId,
	};
}

export function toLinkedAsset({
	embedded,
	entryClassName,
}: {
	embedded: {
		externalReferenceCode: string;
		systemProperties?: {scope?: {externalReferenceCode?: string}};
	};
	entryClassName: string;
}): LinkedAsset {
	return {
		classExternalReferenceCode: embedded.externalReferenceCode,
		className: entryClassName,
		groupExternalReferenceCode:
			embedded.systemProperties?.scope?.externalReferenceCode ?? '',
	};
}

/**
 * Escapes a value for an OData string literal. An external reference code may
 * contain an apostrophe, which would otherwise close the literal early and
 * make the filter unparseable.
 */
function escapeLiteral(value: string): string {
	return value.replace(/'/g, "''");
}

async function fetchLinkId({
	context,
	linkedAsset,
}: LinkRequest): Promise<RequestResult<number | null>> {
	const filter = [
		`${context.relationshipObjectFieldName} eq '${escapeLiteral(
			context.objectEntryId
		)}'`,
		`className eq '${escapeLiteral(linkedAsset.className)}'`,
		`classExternalReferenceCode eq '${escapeLiteral(
			linkedAsset.classExternalReferenceCode
		)}'`,
		`groupExternalReferenceCode eq '${escapeLiteral(
			linkedAsset.groupExternalReferenceCode
		)}'`,
	].join(' and ');

	const {data, error, status, type} = await ApiHelper.get<{
		items: Array<{id: number}>;
	}>(
		`${context.restContextPath}/scopes/${
			context.scopeGroupId
		}?filter=${encodeURIComponent(filter)}`
	);

	if (error !== null) {
		return {data: null, error, status, type};
	}

	return {data: data.items?.[0]?.id ?? null, error: null};
}

async function linkAsset({
	context,
	linkedAsset,
}: LinkRequest): Promise<RequestResult<{id: number}>> {
	return ApiHelper.post<{id: number}>(
		`${context.restContextPath}/scopes/${context.scopeGroupId}`,
		{
			...linkedAsset,
			[context.relationshipObjectFieldName]: Number(
				context.objectEntryId
			),
		}
	);
}

async function unlinkAsset({
	context,
	linkedAsset,
}: LinkRequest): Promise<RequestResult<null>> {
	const {
		data: linkId,
		error,
		status,
		type,
	} = await fetchLinkId({
		context,
		linkedAsset,
	});

	if (error !== null) {
		return {data: null, error, status, type};
	}

	if (linkId === null) {
		return {
			data: null,
			error: Liferay.Language.get('an-unexpected-error-occurred'),
		};
	}

	return ApiHelper.delete(`${context.restContextPath}/${linkId}`);
}

const ObjectEntryLinkService = {
	linkAsset,
	unlinkAsset,
};

export default ObjectEntryLinkService;
