/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {hideProductMenuIfPresent} from '@liferay/layout-js-components-web';

import {switchSidebarPanel as switchSidebarPanelAction} from '../actions/index';

interface Action {
	hidden?: boolean;
	itemConfigurationOpen?: boolean;
}

export default function switchSidebarPanel(action: Action) {
	return (
		dispatch: (action: ReturnType<typeof switchSidebarPanelAction>) => void
	) => {
		hideProductMenuIfPresent({
			onHide: () => {
				dispatch(switchSidebarPanelAction({...action}));
			},
		});
	};
}
