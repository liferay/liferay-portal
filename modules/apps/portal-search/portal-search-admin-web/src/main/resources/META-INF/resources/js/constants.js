/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const EXECUTION_MODES = {
	CONCURRENT: {
		description: Liferay.Language.get(
			'reindex-mode-concurrent-description'
		),
		label: Liferay.Language.get('concurrent'),
		symbol: 'change-list',
		value: 'concurrent',
	},
	FULL: {
		description: Liferay.Language.get('reindex-mode-full-description'),
		label: Liferay.Language.get('full'),
		symbol: 'globe-lines',
		value: 'full',
	},
	SYNC: {
		description: Liferay.Language.get('reindex-mode-sync-description'),
		label: Liferay.Language.get('sync[mode]'),
		symbol: 'reload',
		value: 'sync',
	},
};

export const INTERVAL_RENDER_IN_PROGRESS = 2000;

export const SCOPES = {
	ALL: 'all',
	SELECTED: 'selected',
};

export const PORTAL_TOOLTIP_TRIGGER_CLASS = 'lfr-portal-tooltip';
