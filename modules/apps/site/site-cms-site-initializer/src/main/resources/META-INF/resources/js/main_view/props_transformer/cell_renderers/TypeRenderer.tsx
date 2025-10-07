/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

const CMS_TYPES: {[key: string]: string} = {
	L_CMS_CONTENT_STRUCTURES: Liferay.Language.get('content'),
	L_CMS_FILE_TYPES: Liferay.Language.get('file'),
};

const TypeRenderer = ({value}: {value: string}) => {
	return <>{CMS_TYPES[value]}</>;
};

export default TypeRenderer;
