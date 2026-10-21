/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type ProfileTool = {
	externalReferenceCode: string;
	id?: number;
	r_mcpServerProfileToTools_l_mcpServerProfileId?: number;
	restrictFields?: string;
	toolName: string;
	toolSetName: string;
};

export type ProfileToolActionContext = {
	itemData: ProfileTool;
	loadData: () => void;
};

export type ProfileToolPayload = {
	r_mcpServerProfileToTools_l_mcpServerProfileERC: string;
	toolName: string;
	toolSetName: string;
};
