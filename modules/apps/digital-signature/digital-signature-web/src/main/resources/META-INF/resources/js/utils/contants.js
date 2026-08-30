/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const DOCUSIGN_STATUS = {
	completed: {
		color: 'success',
		label: Liferay.Language.get('completed'),
	},
	correct: {
		color: 'info',
		label: Liferay.Language.get('correcting'),
	},
	declined: {
		color: 'danger',
		label: Liferay.Language.get('declined'),
	},
	deleted: {
		color: 'danger',
		label: Liferay.Language.get('deleted'),
	},
	delivered: {
		color: 'success',
		label: Liferay.Language.get('delivered'),
	},
	sent: {
		color: 'primary',
		label: Liferay.Language.get('sent'),
	},
	voided: {
		color: 'warning',
		label: Liferay.Language.get('void'),
	},
};
