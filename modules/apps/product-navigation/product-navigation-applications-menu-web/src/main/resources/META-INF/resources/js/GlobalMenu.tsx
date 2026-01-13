/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import React from 'react';

export default function GlobalMenu() {
	return (
		<ClayButtonWithIcon
			aria-haspopup="dialog"
			className="control-menu-nav-link dropdown-toggle lfr-portal-tooltip"
			data-qa-id="applicationsMenu"
			data-title-set-as-html
			data-tooltip-align="bottom-left"
			displayType="unstyled"
			size="sm"
			symbol="grid"
		/>
	);
}
