/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {PublishProcessRequest} from '../types/exportImportProcess';
import {
	PortletDataHandler,
	PortletDataHandlerControl,
	PortletDataHandlerSection,
} from '../types/portletDataHandler';
import {
	CHOICE_NAME_PRIVATE_PAGES,
	CONTROL_NAME_PAGES,
	CONTROL_NAME_VISIBILITY,
	LayoutSetSelection,
	PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS,
	PortletDataHandlerSelection,
	SECTION_KEY_CONTENT,
	SECTION_KEY_CONTENT_AND_DATA,
	getNestedPortletDataHandlerControls,
} from './contentSelection';

import type {ContentSelection} from '../components/forms/content_selector/ContentSelector';

function toLayoutSetSelection(
	portletDataHandler: PortletDataHandler
): LayoutSetSelection | undefined {
	const portletDataHandlerControls =
		portletDataHandler.portletDataHandlerControls ?? [];

	const visibilityPortletDataHandlerControl = portletDataHandlerControls.find(
		(portletDataHandlerControl) =>
			portletDataHandlerControl.name === CONTROL_NAME_VISIBILITY
	);

	const pagesPortletDataHandlerControl = [
		...portletDataHandlerControls,
		...((visibilityPortletDataHandlerControl?.type === 'Choice' &&
			visibilityPortletDataHandlerControl.portletDataHandlerControls) ||
			[]),
	].find(
		(portletDataHandlerControl) =>
			portletDataHandlerControl.name === CONTROL_NAME_PAGES
	);

	if (
		pagesPortletDataHandlerControl?.type === 'TreeSelection' &&
		pagesPortletDataHandlerControl.selected === false
	) {
		return undefined;
	}

	const privateLayout =
		visibilityPortletDataHandlerControl?.type === 'Choice' &&
		visibilityPortletDataHandlerControl.choice ===
			CHOICE_NAME_PRIVATE_PAGES;

	if (pagesPortletDataHandlerControl?.type !== 'TreeSelection') {
		return {all: true, privateLayout};
	}

	const {
		excludedItemExternalReferenceCodes: excludedItems,
		excludedSubtreeExternalReferenceCodes: excludedSubtrees,
		itemExternalReferenceCodes: items,
		selectAll: all = true,
		subtreeExternalReferenceCodes: subtrees,
	} = pagesPortletDataHandlerControl;

	return {
		...(all && {all: true}),
		...(excludedItems?.length && {excludedItems}),
		...(excludedSubtrees?.length && {excludedSubtrees}),
		...(items?.length && {items}),
		privateLayout,
		...(subtrees?.length && {subtrees}),
	};
}

function toPortletDataHandlerSelection(
	portletDataHandlerControls: PortletDataHandlerControl[] | undefined,
	requestPortletDataHandlerControls: PortletDataHandlerControl[] | undefined
): PortletDataHandlerSelection {
	if (!portletDataHandlerControls?.length) {
		return true;
	}

	const selections: Record<string, PortletDataHandlerSelection> = {};

	const requestPortletDataHandlerControlsMap = new Map(
		(requestPortletDataHandlerControls ?? []).map(
			(requestPortletDataHandlerControl) => [
				requestPortletDataHandlerControl.name,
				requestPortletDataHandlerControl,
			]
		)
	);

	for (const portletDataHandlerControl of portletDataHandlerControls) {
		const requestPortletDataHandlerControl =
			requestPortletDataHandlerControlsMap.get(
				portletDataHandlerControl.name
			);

		if (portletDataHandlerControl.type === 'Choice') {
			const choiceName =
				requestPortletDataHandlerControl?.type === 'Choice'
					? requestPortletDataHandlerControl.choice
					: undefined;

			selections[portletDataHandlerControl.name] =
				choiceName ??
				portletDataHandlerControl.choice ??
				portletDataHandlerControl.choices?.[0]?.name ??
				'';

			continue;
		}

		const selected =
			requestPortletDataHandlerControl?.type !== 'Choice' &&
			!!requestPortletDataHandlerControl?.selected;

		if (!selected) {
			continue;
		}

		selections[portletDataHandlerControl.name] =
			toPortletDataHandlerSelection(
				getNestedPortletDataHandlerControls(portletDataHandlerControl),
				requestPortletDataHandlerControl
					? getNestedPortletDataHandlerControls(
							requestPortletDataHandlerControl
						)
					: undefined
			);
	}

	return selections;
}

function toSelectedFlags(
	flags: Record<string, boolean | undefined>
): Record<string, boolean> {
	return Object.fromEntries(
		Object.entries(flags)
			.filter(([, value]) => value)
			.map(([name]) => [name, true])
	);
}

export function toContentSelection(
	portletDataHandlerSections: PortletDataHandlerSection[],
	publishProcessRequest: PublishProcessRequest,
	{
		commentsAndRatingsEnabled = false,
	}: {commentsAndRatingsEnabled?: boolean} = {}
): ContentSelection | undefined {
	const contentSelection: ContentSelection = {};

	const requestPortletDataHandlersMap = new Map(
		(publishProcessRequest.portletDataHandlers ?? [])
			.filter(
				(requestPortletDataHandler) =>
					requestPortletDataHandler.selected !== false
			)
			.map((requestPortletDataHandler) => [
				requestPortletDataHandler.name,
				requestPortletDataHandler,
			])
	);

	for (const portletDataHandlerSection of portletDataHandlerSections) {
		const sectionSelection: Record<string, PortletDataHandlerSelection> =
			{};

		for (const portletDataHandler of portletDataHandlerSection.portletDataHandlers) {
			const requestPortletDataHandler = requestPortletDataHandlersMap.get(
				portletDataHandler.name
			);

			if (!requestPortletDataHandler) {
				continue;
			}

			if (
				portletDataHandler.name === PORTLET_DATA_KEY_LAYOUT_SET_LAYOUTS
			) {
				const layoutSetSelection = toLayoutSetSelection(
					requestPortletDataHandler
				);

				if (layoutSetSelection) {
					sectionSelection[portletDataHandler.name] =
						layoutSetSelection as PortletDataHandlerSelection;
				}

				continue;
			}

			sectionSelection[portletDataHandler.name] =
				toPortletDataHandlerSelection(
					portletDataHandler.portletDataHandlerControls,
					requestPortletDataHandler.portletDataHandlerControls
				);
		}

		if (Object.keys(sectionSelection).length) {
			contentSelection[portletDataHandlerSection.key] = sectionSelection;
		}
	}

	if (commentsAndRatingsEnabled) {
		const commentsAndRatings = toSelectedFlags({
			comments: publishProcessRequest.comments,
			ratings: publishProcessRequest.ratings,
		});

		const sectionKey = [
			SECTION_KEY_CONTENT,
			SECTION_KEY_CONTENT_AND_DATA,
		].find((contentSectionKey) => contentSelection[contentSectionKey]);

		if (Object.keys(commentsAndRatings).length && sectionKey) {
			contentSelection[sectionKey] = {
				...contentSelection[sectionKey],
				commentsAndRatings,
			};
		}
	}

	if (!Object.keys(contentSelection).length) {
		return undefined;
	}

	return contentSelection;
}
