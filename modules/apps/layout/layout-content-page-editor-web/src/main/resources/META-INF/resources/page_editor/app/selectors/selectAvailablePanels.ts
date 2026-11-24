/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidebarPanel} from '../../types/SidebarPanel';
import {VIEWPORT_SIZES, ViewportSize} from '../config/constants/viewportSizes';

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

		if (
			permissions.LOCKED_SEGMENTS_EXPERIMENT ||
			(!permissions.UPDATE &&
				!permissions.UPDATE_LAYOUT_LIMITED &&
				!permissions.UPDATE_LAYOUT_BASIC) ||
			selectedViewportSize !== VIEWPORT_SIZES.desktop
		) {
			return sidebarPanels.filter(({sidebarPanelId}) =>
				availablePanels.includes(sidebarPanelId)
			);
		}

		return sidebarPanels;
	};
}
