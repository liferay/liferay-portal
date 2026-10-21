/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {MCPStatusKey, MCPStatusPicklistValue} from './MCPStatus';

export type Prompt = {
	dateModified?: string;
	description: string;
	externalReferenceCode?: string;
	id?: number;
	identifier: string;
	name: string;
	prompt: string;
	promptStatus?: MCPStatusPicklistValue;
};

export type PromptActionContext = {
	itemData: Prompt;
	loadData: () => void;
};

export type PromptFormValues = {
	active: boolean;
	description: string;
	identifier: string;
	name: string;
	prompt: string;
};

export type PromptPayload = {
	description: string;
	identifier: string;
	name: string;
	prompt: string;
	promptStatus: {key: MCPStatusKey};
};
