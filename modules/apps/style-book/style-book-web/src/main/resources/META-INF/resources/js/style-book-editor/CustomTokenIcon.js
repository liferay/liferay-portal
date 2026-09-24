/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

export default function CustomTokenIcon() {
	const label = Liferay.Language.get('style-book-custom-token');

	return (
		<span aria-label={label} className="ml-2" role="img" title={label}>
			<ClayIcon symbol="format" />
		</span>
	);
}
