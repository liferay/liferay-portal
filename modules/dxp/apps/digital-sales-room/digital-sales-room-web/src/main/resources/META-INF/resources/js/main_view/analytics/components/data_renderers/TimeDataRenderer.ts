/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import moment from 'moment';

import {TLatestActivity} from '../../../../common/utils/types';

export function TimeDataRenderer({itemData}: {itemData: TLatestActivity}) {
	const {createDate = ''} = itemData;

	return moment(createDate).fromNow(false);
}
