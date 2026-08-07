/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {DesignLibraryResourceType} from '../../types';

const ResourceTypeRenderer = ({
	itemData,
	resourceTypes = [],
}: {
	itemData?: {entryClassName?: string};
	resourceTypes?: DesignLibraryResourceType[];
}) => {
	const resourceType = resourceTypes.find(
		({entryClassName}) => entryClassName === itemData?.entryClassName
	);

	return <span>{resourceType ? resourceType.label : ''}</span>;
};

export default ResourceTypeRenderer;
