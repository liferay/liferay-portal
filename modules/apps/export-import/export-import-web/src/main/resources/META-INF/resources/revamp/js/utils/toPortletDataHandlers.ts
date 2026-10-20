/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ContentSelection} from '../components/forms/content_selector/ContentSelector';
import {
	PortletDataHandler,
	PortletDataHandlerControl,
	PortletDataHandlerSection,
	PortletDataHandlerTreeSelection,
} from '../types/portletDataHandler';
import {
	CHOICE_NAME_PRIVATE_PAGES,
	CHOICE_NAME_PUBLIC_PAGES,
	CONTROL_NAME_PAGES,
	CONTROL_NAME_VISIBILITY,
	LayoutSetSelection,
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
	PortletDataHandlerSelection,
	getNestedPortletDataHandlerControls,
	isAllLayoutsSelected,
} from './contentSelection';

export function toPortletDataHandlers(
	portletDataHandlerSections: PortletDataHandlerSection[],
	contentSelection: ContentSelection | null | undefined
): PortletDataHandler[] {
	if (!contentSelection) {
		return [];
	}

	const portletDataHandlers: PortletDataHandler[] = [];

	for (const portletDataHandlerSection of portletDataHandlerSections) {
		const sectionSelection =
			contentSelection[portletDataHandlerSection.key];

		if (!sectionSelection) {
			continue;
		}

		for (const portletDataHandler of portletDataHandlerSection.portletDataHandlers) {
			const portletDataHandlerSelection =
				sectionSelection[portletDataHandler.name];

			if (!portletDataHandlerSelection) {
				continue;
			}

			if (
				portletDataHandler.name === PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
			) {
				portletDataHandlers.push(
					toLayoutSetPortletDataHandler(
						portletDataHandler,
						portletDataHandlerSelection
					)
				);

				continue;
			}

			const portletDataHandlerControls = toPortletDataHandlerControls(
				portletDataHandler.portletDataHandlerControls,
				portletDataHandlerSelection
			);

			portletDataHandlers.push({
				name: portletDataHandler.name,
				...(portletDataHandlerControls.length && {
					portletDataHandlerControls,
				}),
			});
		}
	}

	return portletDataHandlers;
}

export function toPortletDataHandlerTreeSelection(
	layoutSetSelection: LayoutSetSelection
): Partial<PortletDataHandlerTreeSelection> {
	if (isAllLayoutsSelected(layoutSetSelection)) {
		return {};
	}

	const {
		all = false,
		excludedItems,
		excludedSubtrees,
		items,
		subtrees,
	} = layoutSetSelection;

	return {
		...(excludedItems?.length && {
			excludedItemExternalReferenceCodes: excludedItems,
		}),
		...(excludedSubtrees?.length && {
			excludedSubtreeExternalReferenceCodes: excludedSubtrees,
		}),
		...(items?.length && {itemExternalReferenceCodes: items}),
		selectAll: all,
		...(subtrees?.length && {subtreeExternalReferenceCodes: subtrees}),
	};
}

function toLayoutSetPortletDataHandler(
	portletDataHandler: PortletDataHandler,
	portletDataHandlerSelection: PortletDataHandlerSelection
): PortletDataHandler {
	const layoutSetSelection = (
		typeof portletDataHandlerSelection === 'object'
			? portletDataHandlerSelection
			: {all: true}
	) as LayoutSetSelection;

	const pagesPortletDataHandlerControl: PortletDataHandlerControl = {
		...toPortletDataHandlerTreeSelection(layoutSetSelection),
		name: CONTROL_NAME_PAGES,
		selected: true,
		type: 'TreeSelection',
	};

	if (
		!portletDataHandler.portletDataHandlerControls?.some(
			(portletDataHandlerControl) =>
				portletDataHandlerControl.name === CONTROL_NAME_VISIBILITY
		)
	) {
		return {
			name: portletDataHandler.name,
			portletDataHandlerControls: [pagesPortletDataHandlerControl],
		};
	}

	return {
		name: portletDataHandler.name,
		portletDataHandlerControls: [
			{
				choice: layoutSetSelection.privateLayout
					? CHOICE_NAME_PRIVATE_PAGES
					: CHOICE_NAME_PUBLIC_PAGES,
				name: CONTROL_NAME_VISIBILITY,
				portletDataHandlerControls: [pagesPortletDataHandlerControl],
				type: 'Choice',
			},
		],
	};
}

function toPortletDataHandlerControls(
	availablePortletDataHandlerControls:
		| PortletDataHandlerControl[]
		| undefined,
	portletDataHandlerSelection: PortletDataHandlerSelection
): PortletDataHandlerControl[] {
	if (
		!availablePortletDataHandlerControls ||
		typeof portletDataHandlerSelection !== 'object'
	) {
		return [];
	}

	const portletDataHandlerControls: PortletDataHandlerControl[] = [];

	const portletDataHandlerSelections = portletDataHandlerSelection as Record<
		string,
		PortletDataHandlerSelection
	>;

	for (const portletDataHandlerControl of availablePortletDataHandlerControls) {
		const nestedPortletDataHandlerSelection =
			portletDataHandlerSelections[portletDataHandlerControl.name];

		if (portletDataHandlerControl.type === 'Choice') {
			if (typeof nestedPortletDataHandlerSelection === 'string') {
				portletDataHandlerControls.push({
					choice: nestedPortletDataHandlerSelection,
					name: portletDataHandlerControl.name,
					type: 'Choice',
				});
			}

			continue;
		}

		if (!nestedPortletDataHandlerSelection) {
			portletDataHandlerControls.push({
				name: portletDataHandlerControl.name,
				selected: false,
				type: portletDataHandlerControl.type,
			});

			continue;
		}

		const nestedPortletDataHandlerControls = toPortletDataHandlerControls(
			getNestedPortletDataHandlerControls(portletDataHandlerControl),
			nestedPortletDataHandlerSelection
		);

		portletDataHandlerControls.push({
			name: portletDataHandlerControl.name,
			...(nestedPortletDataHandlerControls.length && {
				portletDataHandlerControls: nestedPortletDataHandlerControls,
			}),
			selected: true,
			type: portletDataHandlerControl.type,
		});
	}

	return portletDataHandlerControls;
}
