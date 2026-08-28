/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type DataMaskTypeKey = 'system' | 'custom';

export type DataMaskPicklistValue = {
	key: DataMaskTypeKey;
	name: string;
};

export type DataMask = {
	dateModified?: string;
	description?: string;
	detectionRegex: string;
	externalReferenceCode?: string;
	id?: number;
	maskType: DataMaskPicklistValue;
	name: string;
	replacementRegex?: string;
	replacementValue: string;
};

export type DataMaskPayload = {
	description: string;
	detectionRegex: string;
	maskType: {key: DataMaskTypeKey};
	name: string;
	replacementRegex: string;
	replacementValue: string;
};

export type DataMaskTreeItem = {
	children?: DataMaskTreeItem[];
	id: string;
	name: string;
};

export type DataMaskFormValues = {
	description: string;
	detectionRegex: string;
	name: string;
	replacementRegex: string;
	replacementValue: string;
};

export type ActionContext = {
	itemData: DataMask;
	loadData: () => void;
};

export type PromptStatusKey = 'active' | 'inactive';

export type PromptStatusPicklistValue = {
	key: PromptStatusKey;
	name: string;
};

export type Profile = {
	dateModified?: string;
	description: string;
	externalReferenceCode?: string;
	friendlyUrlPath?: string;
	id?: number;
	name: string;
};

export type ProfileActionContext = {
	itemData: Profile;
	loadData: () => void;
};

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

export type ProfileFormValues = {
	description: string;
	name: string;
};

export type ProfilePayload = {
	description: string;
	name: string;
};

export type Prompt = {
	dateModified?: string;
	description: string;
	externalReferenceCode?: string;
	id?: number;
	identifier: string;
	name: string;
	prompt: string;
	promptStatus: PromptStatusPicklistValue;
};

export type PromptPayload = {
	description: string;
	identifier: string;
	name: string;
	prompt: string;
	promptStatus: {key: PromptStatusKey};
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
