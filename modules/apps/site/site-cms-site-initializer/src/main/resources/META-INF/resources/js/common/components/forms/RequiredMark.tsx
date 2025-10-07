/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

const RequiredMark = () => (
	<>
		<span className="inline-item-after ml-1 reference-mark text-warning">
			<ClayIcon symbol="asterisk" />
		</span>

		<span className="hide-accessible sr-only">
			{Liferay.Language.get('required')}
		</span>
	</>
);

export default RequiredMark;
