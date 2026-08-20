/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DateTimeRenderer} from '@liferay/frontend-data-set-web';
import React from 'react';

export default function ProcessEndDateRenderer({value}: {value?: string}) {
	if (!value) {
		return <>{Liferay.Language.get('no-end-date')}</>;
	}

	return DateTimeRenderer({value});
}
