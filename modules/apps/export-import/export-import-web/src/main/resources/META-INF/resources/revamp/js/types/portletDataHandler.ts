/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export interface PortletDataHandlerBoolean {
	additionCount?: number;
	deletionCount?: number;
	description?: string;
	label?: string;
	name: string;
	portletDataHandlerControls?: PortletDataHandlerControl[];
	selected?: boolean;
	tag?: string;
	type: 'Boolean';
}

export interface Choice {
	additionCount?: number;
	deletionCount?: number;
	label: string;
	name: string;
}

export interface PortletDataHandlerChoice {
	choice?: string;
	choices?: Choice[];
	label?: string;
	name: string;
	portletDataHandlerControls?: PortletDataHandlerControl[];
	type: 'Choice';
}

export interface PortletDataHandlerTreeSelection {
	additionCount?: number;
	deletionCount?: number;
	excludedItemExternalReferenceCodes?: string[];
	excludedSubtreeExternalReferenceCodes?: string[];
	itemExternalReferenceCodes?: string[];
	label?: string;
	name: string;
	selectAll?: boolean;
	selected?: boolean;
	subtreeExternalReferenceCodes?: string[];
	type: 'TreeSelection';
}

export type PortletDataHandlerControl =
	| PortletDataHandlerBoolean
	| PortletDataHandlerChoice
	| PortletDataHandlerTreeSelection;

export interface PortletDataHandler {
	additionCount?: number;
	deletionCount?: number;
	description?: string;
	label?: string;
	name: string;
	portletDataHandlerControls?: PortletDataHandlerControl[];
	sectionKey?: string;
	sectionLabel?: string;
	selected?: boolean;
	tag?: string;
}

export interface PortletDataHandlerSection {
	additionCount?: number;
	deletionCount?: number;
	key: string;
	label: string;
	portletDataHandlers: PortletDataHandler[];
}
