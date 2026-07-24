/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {openManageMembersModal} from './actions/breadcrumbActions';
import SummarySectionHeader from './components/summary/SummarySectionHeader';

interface DesignLibraryMembersSectionHeaderProps {
	count: number;
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	ownerId: string;
}

export default function DesignLibraryMembersSectionHeader({
	count,
	externalReferenceCode,
	hasAssignMembersPermission,
	ownerId,
}: DesignLibraryMembersSectionHeaderProps) {
	return (
		<SummarySectionHeader
			actionLabel={
				hasAssignMembersPermission && count
					? Liferay.Language.get('manage-members')
					: undefined
			}
			count={count}
			onActionClick={
				hasAssignMembersPermission && count
					? () =>
							openManageMembersModal({
								externalReferenceCode,
								hasAssignMembersPermission,
								headerTitle:
									Liferay.Language.get('manage-members'),
								ownerId,
							})
					: undefined
			}
			title={Liferay.Language.get('members')}
		/>
	);
}
