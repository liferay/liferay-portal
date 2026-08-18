/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

type LinkReference = {
	className: string;
	externalReferenceCode: string;
};

export default function addProductRelationships({
	scopeKey,
	sourceLinkReference,
	targetLinkReferences,
	type,
}: {
	scopeKey: string;
	sourceLinkReference: LinkReference;
	targetLinkReferences: LinkReference[];
	type: string;
}): Promise<Response> {
	return fetch(`/o/headless-pim/v1.0/scopes/${scopeKey}/links`, {
		body: JSON.stringify({
			sourceLinkReference,
			targetLinkReferences,
			type,
		}),
		headers: {'Content-Type': 'application/json'},
		method: 'POST',
	});
}

export function getRelatedObjectEntryIds({
	className,
	externalReferenceCode,
	scopeKey,
}: {
	className: string;
	externalReferenceCode: string;
	scopeKey: string;
}): Promise<number[]> {
	return fetch(
		`/o/headless-pim/v1.0/scopes/${scopeKey}/links?className=${encodeURIComponent(
			className
		)}&externalReferenceCode=${encodeURIComponent(
			externalReferenceCode
		)}&pageSize=200`
	)
		.then((response) => response.json())
		.then((page) =>
			(page?.items || []).map((linkReference: any) => linkReference.id)
		);
}
