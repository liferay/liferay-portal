/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Preview} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/exportImportPreview';
import {
	PortletDataHandler,
	PortletDataHandlerTreeSelection,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';
import {
	CONTROL_NAME_PAGES,
	CONTROL_NAME_VISIBILITY,
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';
import {toMockPortletDataHandlers} from './mockPortletDataHandlerSections';

const choiceLabels: Record<string, string> = {
	'private-pages': 'Private Pages',
	'public-pages': 'Public Pages',
};

const lookAndFeelControlLabels: Record<string, string> = {
	LAYOUT_SET_PROTOTYPE_SETTINGS: 'Site Template Settings',
	LAYOUT_SET_SETTINGS: 'Site Pages Settings',
	LOGO: 'Logo',
	THEME_REFERENCE: 'Theme Settings',
};

export function toMockLayoutSetPortletDataHandler(
	choice: string,
	treeSelection?: Partial<PortletDataHandlerTreeSelection>
): PortletDataHandler {
	return {
		name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
		portletDataHandlerControls: [
			{
				choice,
				name: CONTROL_NAME_VISIBILITY,
				portletDataHandlerControls: [
					{
						...treeSelection,
						name: CONTROL_NAME_PAGES,
						selected: true,
						type: 'TreeSelection',
					},
				],
				type: 'Choice',
			},
		],
	};
}

export function toMockLayoutSetPreviewPortletDataHandler(
	choiceNames: string[],
	choice = choiceNames[0]
): PortletDataHandler {
	return {
		label: 'Pages',
		name: PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
		portletDataHandlerControls: [
			{
				choice,
				choices: choiceNames.map((name) => ({
					label: choiceLabels[name],
					name,
				})),
				label: 'Visibility',
				name: CONTROL_NAME_VISIBILITY,
				portletDataHandlerControls: [
					{
						label: 'Pages',
						name: CONTROL_NAME_PAGES,
						selected: true,
						type: 'TreeSelection',
					},
				],
				type: 'Choice',
			},
		],
	};
}

export function toMockLookAndFeelPortletDataHandler(
	controlNames: string[],
	unselectedControlNames: string[] = []
): PortletDataHandler {
	return {
		name: 'LOOK_AND_FEEL',
		portletDataHandlerControls: controlNames.map((name) => ({
			name,
			selected: !unselectedControlNames.includes(name),
			type: 'Boolean',
		})),
	};
}

export function toMockLookAndFeelPreviewPortletDataHandler(
	controlNames: string[]
): PortletDataHandler {
	return {
		label: 'Look and Feel',
		name: 'LOOK_AND_FEEL',
		portletDataHandlerControls: controlNames.map((name) => ({
			label: lookAndFeelControlLabels[name],
			name,
			selected: true,
			type: 'Boolean',
		})),
	};
}

export function toMockSiteBuilderPreview(
	portletDataHandlers: PortletDataHandler[]
): Preview {
	return {
		additionCount: 1,
		deletionCount: 0,
		portletDataHandlers: toMockPortletDataHandlers([
			{
				key: 'category.site_administration.build',
				label: 'Site Builder',
				portletDataHandlers,
			},
		]),
	};
}
