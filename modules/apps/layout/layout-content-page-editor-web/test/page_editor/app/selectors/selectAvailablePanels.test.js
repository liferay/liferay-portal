/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {VIEWPORT_SIZES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import selectAvailablePanels from '../../../../src/main/resources/META-INF/resources/page_editor/app/selectors/selectAvailablePanels';

const SIDEBAR_PANELS = [
	{
		sidebarPanelId: 'browser',
	},
	{
		sidebarPanelId: 'comments',
	},
	{
		sidebarPanelId: 'fragments_and_widgets',
	},
	{
		sidebarPanelId: 'mapping',
	},
	{
		sidebarPanelId: 'page_content',
	},
	{
		sidebarPanelId: 'page_design_options',
	},
];

const EXPECTED_SIDEBAR_PANELS = [
	{
		sidebarPanelId: 'browser',
	},
	{
		sidebarPanelId: 'comments',
	},
	{
		sidebarPanelId: 'page_content',
	},
];

describe('selectAvailablePanels', () => {
	it('reduce the number of panels in non-desktop viewport', () => {
		const panels = selectAvailablePanels(SIDEBAR_PANELS)({
			permissions: {},
			selectedViewportSize: VIEWPORT_SIZES.landscapeMobile,
		});

		expect(panels).toEqual(EXPECTED_SIDEBAR_PANELS);
	});

	it('reduce the number of panels in locked segments experiment', () => {
		const panels = selectAvailablePanels(SIDEBAR_PANELS)({
			permissions: {LOCKED_SEGMENTS_EXPERIMENT: true},
			selectedViewportSize: VIEWPORT_SIZES.desktop,
		});

		expect(panels).toEqual(EXPECTED_SIDEBAR_PANELS);
	});

	it('reduce the number of panels when user has limited permission', () => {
		const panels = selectAvailablePanels(SIDEBAR_PANELS)({
			permissions: {UPDATE_LAYOUT_CONTENT: true},
			selectedViewportSize: VIEWPORT_SIZES.desktop,
		});

		expect(panels).toEqual(EXPECTED_SIDEBAR_PANELS);
	});

	it('returns all panel with all permissions', () => {
		const panels = selectAvailablePanels(SIDEBAR_PANELS)({
			permissions: {UPDATE: true},
			selectedViewportSize: VIEWPORT_SIZES.desktop,
		});

		expect(panels).toEqual(SIDEBAR_PANELS);
	});
});
