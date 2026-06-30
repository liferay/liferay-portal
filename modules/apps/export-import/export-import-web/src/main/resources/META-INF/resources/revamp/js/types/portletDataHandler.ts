/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface PreviewPortletDataHandlerBoolean {
	additionCount?: number;
	deletionCount?: number;
	description?: string;
	label: string;
	name: string;
	previewPortletDataHandlerControls?: PreviewPortletDataHandlerControl[];
	tag?: string;
	type: 'Boolean';
}

export interface PreviewPortletDataHandlerChoice {
	choices: {
		additionCount?: number;
		deletionCount?: number;
		label: string;
		name: string;
	}[];
	label: string;
	name: string;
	type: 'Choice';
}

export interface PreviewPortletDataHandlerSetting {
	label: string;
	name: string;
	previewPortletDataHandlerControls?: PreviewPortletDataHandlerControl[];
	type: 'Setting';
}

export type PreviewPortletDataHandlerControl =
	| PreviewPortletDataHandlerBoolean
	| PreviewPortletDataHandlerChoice
	| PreviewPortletDataHandlerSetting;

export type PreviewPortletDataHandler = Omit<
	PreviewPortletDataHandlerBoolean,
	'type'
>;

export interface PreviewPortletDataHandlerSection {
	additionCount?: number;
	deletionCount?: number;
	label: string;
	name: string;
	previewPortletDataHandlers: PreviewPortletDataHandler[];
}

export interface RequestPortletDataHandlerControl {
	name: string;
	requestPortletDataHandlerControls?: RequestPortletDataHandlerControl[];
	value?: string;
	values?: string[];
}

export interface RequestPortletDataHandler {
	name: string;
	requestPortletDataHandlerControls?: RequestPortletDataHandlerControl[];
}
