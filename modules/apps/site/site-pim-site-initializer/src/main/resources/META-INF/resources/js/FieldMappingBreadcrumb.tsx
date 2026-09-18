/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Breadcrumb} from '@liferay/site-cms-site-initializer';
import React, {ComponentProps} from 'react';

interface IActionItem {
	href: string;
	label: string;
}

interface IBreadcrumbItem {
	active?: boolean;
	href?: string;
	label: string;
}

interface IProps {
	actionItems?: IActionItem[];
	breadcrumbItems: IBreadcrumbItem[];
	hideSpace?: boolean;
	size?: ComponentProps<typeof Breadcrumb>['size'];
}

export default function FieldMappingBreadcrumb({
	actionItems,
	breadcrumbItems,
	hideSpace,
	size,
}: IProps) {
	return (
		<Breadcrumb
			actionItems={actionItems}
			breadcrumbItems={breadcrumbItems}
			hideSpace={hideSpace}
			size={size}
		/>
	);
}
