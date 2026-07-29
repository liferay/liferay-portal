/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {config} from '../config';
import {PageVersion} from '../types/PageVersion';
import ApiHelper from './ApiHelper';

async function getPageVersions(signal?: AbortSignal) {
	return await ApiHelper.get<{items: PageVersion[]}>(
		config.pageSpecificationVersionsURL,
		signal
	);
}

export default {getPageVersions};
