/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * @deprecated As of Athanasius (7.3.x), with no direct replacement
 */
(function () {
	const PATH_JAVASCRIPT = '/o/frontend-js-aui-web';

	AUI().applyConfig({
		groups: {
			liferaydeprecated: {
				base:
					Liferay.ThemeDisplay.getCDNBaseURL() +
					Liferay.ThemeDisplay.getPathContext() +
					PATH_JAVASCRIPT +
					'/liferay/',
				combine: Liferay.AUI.getCombine(),
				filter: Liferay.AUI.getFilterConfig(),
				modules: {
					'liferay-auto-fields': {
						path: 'auto_fields.js',
						requires: [
							'aui-base',
							'aui-data-set-deprecated',
							'aui-parse-content',
							'base',
							'liferay-form',
							'liferay-menu',
							'liferay-portlet-base',
							'sortable',
						],
					},
				},
				root: PATH_JAVASCRIPT + '/liferay/',
			},
		},
	});
})();
