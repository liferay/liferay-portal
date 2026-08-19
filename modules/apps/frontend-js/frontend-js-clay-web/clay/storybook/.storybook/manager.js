/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addons} from '@storybook/addons';
import {create} from '@storybook/theming';

const clayTheme = create({
	base: 'light',
	brandTitle: 'Clay Storybook',
});

addons.setConfig({
	theme: clayTheme,
});
