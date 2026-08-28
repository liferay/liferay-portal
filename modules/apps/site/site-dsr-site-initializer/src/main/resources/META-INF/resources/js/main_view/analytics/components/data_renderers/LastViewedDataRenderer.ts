/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TRoomDocumentsStatistics} from '../../../../common/utils/types';

export function LastViewedDataRenderer({
	itemData,
}: {
	itemData: TRoomDocumentsStatistics;
}) {
	const {lastViewed = ''} = itemData;

	const lastViewedDate = new Date(lastViewed);

	if (Number.isNaN(lastViewedDate.getTime())) {
		return '-';
	}

	return new Intl.DateTimeFormat(Liferay.ThemeDisplay.getBCP47LanguageId(), {
		dateStyle: 'medium',
	}).format(lastViewedDate);
}
