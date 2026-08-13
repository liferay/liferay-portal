/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Packages that are allowed to declare a non-explicit (range) version instead of a fixed version
 * number.
 */
export default [

	// Clay uses range versions for unclear historical reasons but since changing it requires
	// modifyinig its release scripts and since it could apparently lead to build caching issues, we
	// are leaving it like that since it doesn't hurt in any case.

	'@clayui/alert',
	'@clayui/autocomplete',
	'@clayui/badge',
	'@clayui/breadcrumb',
	'@clayui/button',
	'@clayui/card',
	'@clayui/color-picker',
	'@clayui/core',
	'@clayui/css',
	'@clayui/data-provider',
	'@clayui/date-picker',
	'@clayui/drop-down',
	'@clayui/empty-state',
	'@clayui/form',
	'@clayui/icon',
	'@clayui/label',
	'@clayui/layout',
	'@clayui/link',
	'@clayui/list',
	'@clayui/loading-indicator',
	'@clayui/localized-input',
	'@clayui/management-toolbar',
	'@clayui/modal',
	'@clayui/multi-select',
	'@clayui/multi-step-nav',
	'@clayui/nav',
	'@clayui/navigation-bar',
	'@clayui/pagination',
	'@clayui/pagination-bar',
	'@clayui/panel',
	'@clayui/popover',
	'@clayui/progress-bar',
	'@clayui/provider',
	'@clayui/shared',
	'@clayui/slider',
	'@clayui/sticker',
	'@clayui/table',
	'@clayui/tabs',
	'@clayui/time-picker',
	'@clayui/toolbar',
	'@clayui/tooltip',
	'@clayui/upper-toolbar',
];
