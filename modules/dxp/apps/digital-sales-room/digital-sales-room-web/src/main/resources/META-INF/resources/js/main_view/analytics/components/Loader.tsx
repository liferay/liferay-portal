/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

function Loader({className = ''}) {
	return (
		<span
			aria-hidden="true"
			className={`loading-animation ${className}`.trim()}
		/>
	);
}

export default Loader;
