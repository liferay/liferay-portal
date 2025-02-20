/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

import {
	HEADLESS_BATCH_PLANNER_URL,
	TEMPLATE_SELECTED_EVENT,
} from '../constants';

export async function fetchTemplateDetails(templateId) {
	if (!templateId) {
		return Liferay.fire(TEMPLATE_SELECTED_EVENT, {
			template: null,
		});
	}

	try {
		const request = await fetch(
			`${HEADLESS_BATCH_PLANNER_URL}/plans/${templateId}`
		);

		if (!request.ok) {
			throw new Error();
		}

		return await request.json();
	}
	catch (error) {
		openToast({
			message: Liferay.Language.get('unexpected-error'),
			type: 'danger',
		});
	}
}
