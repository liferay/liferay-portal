/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as frontendJSWebModule from 'frontend-js-web';

import {
	getReviewTaskDefinitionWithReference,
	getUsersXML,
} from './workflowDefinition';

const NOT_FOUND = {status: 'NOT_FOUND'};

function getUserAccount(id) {
	return {
		alternateName: `user${id}`,
		emailAddress: `user${id}@liferay.com`,
		id,
		name: `User ${id}`,
	};
}

const userAccountByExternalReferenceCode = getUserAccount(1);
const userAccountByExternalReferenceCodeOnly = getUserAccount(5);
const userAccountById = getUserAccount(10);

export const foundUserAccounts = [
	userAccountByExternalReferenceCode,
	userAccountById,
	userAccountByExternalReferenceCodeOnly,
];

export function getSectionData({alternateName, emailAddress, id, name}) {
	return {emailAddress, name, screenName: alternateName, userId: id};
}

export function getUserReferencesDefinition() {
	return getReviewTaskDefinitionWithReference(
		getUsersXML([
			{externalReferenceCode: 'USER_EXTERNAL_REFERENCE_CODE_1', id: 1},
			{id: 10},
			{id: 99},
			{externalReferenceCode: 'USER_EXTERNAL_REFERENCE_CODE_5'},
		])
	);
}

export function mockUserAccountsFetch() {
	const userAccountsByPath = {
		'10': userAccountById,
		'by-external-reference-code/USER_EXTERNAL_REFERENCE_CODE_1':
			userAccountByExternalReferenceCode,
		'by-external-reference-code/USER_EXTERNAL_REFERENCE_CODE_5':
			userAccountByExternalReferenceCodeOnly,
	};

	return jest
		.spyOn(frontendJSWebModule, 'fetch')
		.mockImplementation((url) => {
			const path = Object.keys(userAccountsByPath).find((path) =>
				url.endsWith(`/user-accounts/${path}`)
			);

			return Promise.resolve({
				json: async () => (path ? userAccountsByPath[path] : NOT_FOUND),
			});
		});
}
