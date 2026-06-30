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
	HandlerSelection,
	LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY,
	PRIVATE_PAGES_CONTROL_NAME,
	PUBLIC_PAGES_CONTROL_NAME,
} from './contentSelection';

export function toRequestPortletDataHandlers(
	sections: PreviewPortletDataHandlerSection[],
	contentSelection: ContentSelection | undefined
): RequestPortletDataHandler[] {
	if (!contentSelection) {
		return [];
	}

	const requestPortletDataHandlers: RequestPortletDataHandler[] = [];

	for (const section of sections) {
		const sectionSelection = contentSelection[section.name];

		if (!sectionSelection) {
			continue;
		}

		for (const handler of section.previewPortletDataHandlers ?? []) {
			const handlerSelection = sectionSelection[handler.name];

			if (!handlerSelection) {
				continue;
			}

			if (handler.name === LAYOUT_SET_LAYOUTS_PORTLET_DATA_KEY) {
				requestPortletDataHandlers.push(
					toLayoutSetRequestHandler(handler.name, handlerSelection)
				);

				continue;
			}

			const requestPortletDataHandlerControls = toRequestControls(
				handler.previewPortletDataHandlerControls,
				handlerSelection
			);

			requestPortletDataHandlers.push({
				name: handler.name,
				...(requestPortletDataHandlerControls.length && {
					requestPortletDataHandlerControls,
				}),
			});
		}
	}

	return requestPortletDataHandlers;
}

function toRequestControls(
	controls: PreviewPortletDataHandlerControl[] | undefined,
	selection: HandlerSelection
): RequestPortletDataHandlerControl[] {
	if (!controls || typeof selection !== 'object') {
		return [];
	}

	const map = selection as Record<string, HandlerSelection>;
	const result: RequestPortletDataHandlerControl[] = [];

	for (const control of controls) {
		const value = map[control.name];

		if (!value) {
			continue;
		}

		if (typeof value === 'string') {
			result.push({name: control.name, values: [value]});

			continue;
		}

		if (value === true) {
			result.push({name: control.name});

			continue;
		}

		const nested =
			'previewPortletDataHandlerControls' in control
				? toRequestControls(
						control.previewPortletDataHandlerControls,
						value as HandlerSelection
					)
				: [];

		result.push({
			name: control.name,
			...(nested.length && {
				requestPortletDataHandlerControls: nested,
			}),
		});
	}

	return result;
}

function toLayoutSetRequestHandler(
	name: string,
	selection: HandlerSelection
): RequestPortletDataHandler {
	if (typeof selection !== 'object') {
		return {name};
	}

	const {layoutIds, privateLayout = false} = selection as {
		layoutIds?: number[];
		privateLayout?: boolean;
	};

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
