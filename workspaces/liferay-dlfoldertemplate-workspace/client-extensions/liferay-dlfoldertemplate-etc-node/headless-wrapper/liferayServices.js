/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {lookupConfig, lxcConfig} from '@rotty3000/config-node';
import fetch from 'node-fetch';

import {getServerToken} from '../util/silent-authorization.js';

const lxcDXPMainDomain = lxcConfig.dxpMainDomain();

const lxcDXPServerProtocol = lxcConfig.dxpProtocol();

const oauth2JWKSURI = `${lxcDXPServerProtocol}://${lxcDXPMainDomain}`;

const folderTemplateNodesEndpoint = lookupConfig(
	'folder.template.nodes.end.point'
);

export async function getFolderTemplateNodesPage(templateID) {
	const token = await getServerToken();

	const prom = new Promise((resolve, reject) => {
		const requestHeaders = new Headers();

		requestHeaders.append('Authorization', `Bearer ${token}`);

		const requestOptions = {
			headers: requestHeaders,
			method: 'GET',
			redirect: 'follow',
		};

		fetch(
			`${oauth2JWKSURI}${folderTemplateNodesEndpoint}?filter=templateId eq ${templateID}&page=0`,
			requestOptions
		)
			.then((response) => response.json())
			.then((result) => resolve(result.items))
			.catch((error) => reject(error));
	});

	return prom;
}

export async function postDocumentFolder(
	parentDocumentFolderId,
	DocumentFolder
) {
	const token = await getServerToken();

	const prom = new Promise((resolve, reject) => {
		const requestHeaders = new Headers();

		requestHeaders.append('Authorization', `Bearer ${token}`);

		requestHeaders.append('Content-Type', 'application/json');

		const requestOptions = {
			body: JSON.stringify(DocumentFolder),
			headers: requestHeaders,
			method: 'POST',
			redirect: 'follow',
		};
		fetch(
			`${oauth2JWKSURI}/o/headless-delivery/v1.0/document-folders/${parentDocumentFolderId}/document-folders`,
			requestOptions
		)
			.then((response) => response.json())
			.then((result) => resolve(result))
			.catch((error) => reject(error));
	});

	return prom;
}
