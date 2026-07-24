/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SpaceSummaryHeader} from '@liferay/site-cms-site-initializer';
import React, {ComponentProps} from 'react';

import manageMembersAction from '../props_transformer/actions/manageMembersAction';

export default function ProjectSummaryHeader(
	props: ComponentProps<typeof SpaceSummaryHeader>
) {
	return (
		<SpaceSummaryHeader
			{...props}
			onOpenMembersModal={manageMembersAction}
		/>
	);
}
