/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type ProfileDataMask = {
	dataMaskExternalReferenceCode: string;
	deleteReason?: string;
	executionOrder?: number;
	externalReferenceCode?: string;
	id?: number;
	mcpServerProfileExternalReferenceCode: string;
};

export type ProfileDataMaskRow = {
	dataMaskExternalReferenceCode: string;
	description: string;
	executionOrder: number;
	externalReferenceCode: string;
	id: number;
	name: string;
	type: string;
};
