/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * This util should have the same code as in frontend-data-set-admin-web module.
 */
export default function getDataSetResourceURL({
	dataSetERC,
	params,
	relatedResourceERC,
	relationship,
}: {
	dataSetERC?: string;
	params?: Record<string, string>;
	relatedResourceERC?: string;
	relationship?: string;
}): string {
	const apiURL = ['/o/data-set-admin/data-sets'];

	if (dataSetERC) {
		apiURL.push(`/by-external-reference-code/${dataSetERC}`);
	}

	if (relationship) {
		apiURL.push(`/${relationship}`);
	}

	if (relatedResourceERC) {
		apiURL.push(`/${relatedResourceERC}`);
	}

	if (params) {
		apiURL.push(`?${new URLSearchParams(params).toString()}`);
	}

	return apiURL.join('');
}
