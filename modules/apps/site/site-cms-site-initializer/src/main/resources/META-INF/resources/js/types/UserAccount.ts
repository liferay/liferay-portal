/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface UserAccount {
	emailAddress: string;
	id: string;
	image?: string;
	imageId: string;
	name: string;
}

export interface UserGroup {
	id: string;
	name: string;
}
