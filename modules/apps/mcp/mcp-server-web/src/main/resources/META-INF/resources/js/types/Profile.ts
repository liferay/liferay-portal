/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MCPStatusKey, MCPStatusPicklistValue} from './MCPStatus';

export type Profile = {
	dateModified?: string;
	description: string;
	externalReferenceCode?: string;
	id?: number;
	name: string;
	profilePath: string;
	profileStatus?: MCPStatusPicklistValue;
};

export type ProfileActionContext = {
	itemData: Profile;
	loadData: () => void;
};

export type ProfileFormValues = {
	active: boolean;
	description: string;
	name: string;
	profilePath: string;
};

export type ProfilePayload = {
	description: string;
	name: string;
	profilePath: string;
	profileStatus: {key: MCPStatusKey};
};
