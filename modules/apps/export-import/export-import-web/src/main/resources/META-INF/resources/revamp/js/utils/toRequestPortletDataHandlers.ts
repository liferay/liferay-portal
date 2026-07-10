/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ContentSelection} from '../components/forms/content_selector/ContentSelector';
import {
	PreviewPortletDataHandlerControl,
	PreviewPortletDataHandlerSection,
	RequestPortletDataHandler,
	RequestPortletDataHandlerControl,
} from '../types/portletDataHandler';
import {
	LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
	LayoutSetSelection,
	PRIVATE_PAGES_CONTROL_NAME,
	PUBLIC_PAGES_CONTROL_NAME,
	PortletDataHandlerSelection,
} from './contentSelection';

export function toRequestPortletDataHandlers(
	previewPortletDataHandlerSections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): RequestPortletDataHandler[] {
	if (!contentSelection) {
		return [];
	}

	const requestPortletDataHandlers: RequestPortletDataHandler[] = [];

	for (const previewPortletDataHandlerSection of previewPortletDataHandlerSections) {
		const sectionSelection =
			contentSelection[previewPortletDataHandlerSection.name];

		if (!sectionSelection) {
			continue;
		}

		for (const previewPortletDataHandler of previewPortletDataHandlerSection.previewPortletDataHandlers ??
			[]) {
			const portletDataHandlerSelection =
				sectionSelection[previewPortletDataHandler.name];

			if (!portletDataHandlerSelection) {
				continue;
			}

			if (
				previewPortletDataHandler.name ===
				LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY
			) {
				requestPortletDataHandlers.push(
					toLayoutSetRequestPortletDataHandler(
						previewPortletDataHandler.name,
						portletDataHandlerSelection
					)
				);

				continue;
			}

			const requestPortletDataHandlerControls =
				toRequestPortletDataHandlerControls(
					previewPortletDataHandler.previewPortletDataHandlerControls,
					portletDataHandlerSelection
				);

			requestPortletDataHandlers.push({
				name: previewPortletDataHandler.name,
				...(requestPortletDataHandlerControls.length && {
					requestPortletDataHandlerControls,
				}),
			});
		}
	}

	return requestPortletDataHandlers;
}

function toRequestPortletDataHandlerControls(
	previewPortletDataHandlerControls:
		| PreviewPortletDataHandlerControl[]
		| undefined,
	portletDataHandlerSelection: PortletDataHandlerSelection
): RequestPortletDataHandlerControl[] {
	if (
		!previewPortletDataHandlerControls ||
		typeof portletDataHandlerSelection !== 'object'
	) {
		return [];
	}

	const portletDataHandlerSelections = portletDataHandlerSelection as Record<
		string,
		PortletDataHandlerSelection
	>;
	const requestPortletDataHandlerControls: RequestPortletDataHandlerControl[] =
		[];

	for (const previewPortletDataHandlerControl of previewPortletDataHandlerControls) {
		const nestedPortletDataHandlerSelection =
			portletDataHandlerSelections[previewPortletDataHandlerControl.name];

		if (!nestedPortletDataHandlerSelection) {
			continue;
		}

		if (typeof nestedPortletDataHandlerSelection === 'string') {
			requestPortletDataHandlerControls.push({
				name: previewPortletDataHandlerControl.name,
				values: [nestedPortletDataHandlerSelection],
			});

			continue;
		}

		if (nestedPortletDataHandlerSelection === true) {
			requestPortletDataHandlerControls.push({
				name: previewPortletDataHandlerControl.name,
			});

			continue;
		}

		const nestedRequestPortletDataHandlerControls =
			'previewPortletDataHandlerControls' in
			previewPortletDataHandlerControl
				? toRequestPortletDataHandlerControls(
						previewPortletDataHandlerControl.previewPortletDataHandlerControls,
						nestedPortletDataHandlerSelection as PortletDataHandlerSelection
					)
				: [];

		requestPortletDataHandlerControls.push({
			name: previewPortletDataHandlerControl.name,
			...(nestedRequestPortletDataHandlerControls.length && {
				requestPortletDataHandlerControls:
					nestedRequestPortletDataHandlerControls,
			}),
		});
	}

	return requestPortletDataHandlerControls;
}

function toLayoutSetRequestPortletDataHandler(
	name: string,
	portletDataHandlerSelection: PortletDataHandlerSelection
): RequestPortletDataHandler {
	if (typeof portletDataHandlerSelection !== 'object') {
		return {name};
	}

	const {layoutIds, privateLayout = false} =
		portletDataHandlerSelection as LayoutSetSelection;

	const requestPortletDataHandlerControl: RequestPortletDataHandlerControl = {
		name: privateLayout
			? PRIVATE_PAGES_CONTROL_NAME
			: PUBLIC_PAGES_CONTROL_NAME,
		...(layoutIds?.length && {values: layoutIds.map(String)}),
	};

	return {
		name,
		requestPortletDataHandlerControls: [requestPortletDataHandlerControl],
	};
}
