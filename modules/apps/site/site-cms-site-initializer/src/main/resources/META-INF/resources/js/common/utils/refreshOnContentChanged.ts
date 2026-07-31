/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CONTENT_CHANGED_EVENT} from '@liferay/ai-hub-cell-js-components-web';

import {FDS_EVENT_UPDATE_DISPLAY} from './constants';

const dataSetIds = new Set<string>();

function handleContentChanged() {
	dataSetIds.forEach((dataSetId) => {
		Liferay.fire(FDS_EVENT_UPDATE_DISPLAY, {id: dataSetId});
	});
}

export default function refreshOnContentChanged(dataSetId?: string) {
	if (!dataSetId) {
		return;
	}

	if (!dataSetIds.size) {
		Liferay.on(CONTENT_CHANGED_EVENT, handleContentChanged);
	}

	dataSetIds.add(dataSetId);
}
