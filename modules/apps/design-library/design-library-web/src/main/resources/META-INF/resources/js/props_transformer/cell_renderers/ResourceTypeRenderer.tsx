/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {FRAGMENT_COLLECTION_ENTRY_CLASS_NAME} from '../../constants';

const ResourceTypeRenderer = ({
	itemData,
}: {
	itemData?: {entryClassName?: string};
}) => {
	if (itemData?.entryClassName === FRAGMENT_COLLECTION_ENTRY_CLASS_NAME) {
		return <span>{Liferay.Language.get('fragment-set')}</span>;
	}

	return <span>{Liferay.Language.get('style-book')}</span>;
};

export default ResourceTypeRenderer;
