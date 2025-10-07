/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface UserRole {
	id: number;
	name: string;
}

export interface UserAccount {
	emailAddress: string;
	externalReferenceCode: string;
	id: string;
	image?: string;
	imageId: string;
	name: string;
	roles: UserRole[];
}

export interface UserGroup {
	externalReferenceCode: string;
	id: string;
	name: string;
	numberOfUserAccounts?: string;
	roles: UserRole[];
}
