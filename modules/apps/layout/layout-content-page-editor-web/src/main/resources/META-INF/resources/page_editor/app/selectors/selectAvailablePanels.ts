/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidebarPanel} from '../../types/SidebarPanel';
import {VIEWPORT_SIZES, ViewportSize} from '../config/constants/viewportSizes';
import {isLaunchDraft} from '../utils/isLaunchDraft';

import type {PermissionsState} from '../reducers/permissionsReducer';

export default function selectAvailablePanels(sidebarPanels: SidebarPanel[]) {
	return function ({
		permissions,
		selectedViewportSize,
	}: {
		permissions: PermissionsState;
		selectedViewportSize: ViewportSize;
	}) {
		const availablePanels = ['browser', 'comments', 'page_content'];

		const panels = isLaunchDraft()
			? sidebarPanels.filter(
					({sidebarPanelId}) => sidebarPanelId !== 'comments'
				)
			: sidebarPanels;

		if (
			permissions.LOCKED_SEGMENTS_EXPERIMENT ||
			(!permissions.UPDATE &&
				!permissions.UPDATE_LAYOUT_LIMITED &&
				!permissions.UPDATE_LAYOUT_BASIC) ||
			selectedViewportSize !== VIEWPORT_SIZES.desktop
		) {
			return panels.filter(({sidebarPanelId}) =>
				availablePanels.includes(sidebarPanelId)
			);
		}

		return panels;
	};
}
