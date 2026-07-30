/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';
import React from 'react';

import {openManageMembersModal} from './actions/breadcrumbActions';
import SummarySectionHeader from './components/summary/SummarySectionHeader';
import useRefreshedCount from './hooks/useRefreshedCount';

interface DesignLibraryMembersSectionHeaderProps {
	count: number;
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	ownerId: string;
	refreshDataSetIds?: string[];
}

const fetchTotalCount = (url: string) =>
	fetch(url)
		.then((response) => response.json())
		.then((data) => data.totalCount ?? 0);

export default function DesignLibraryMembersSectionHeader({
	count,
	externalReferenceCode,
	hasAssignMembersPermission,
	ownerId,
	refreshDataSetIds,
}: DesignLibraryMembersSectionHeaderProps) {
	const baseURL = `/o/headless-asset-library/v1.0/asset-libraries/${externalReferenceCode}`;

	const refreshedCount = useRefreshedCount(
		count,
		refreshDataSetIds ?? [],
		() =>
			Promise.all([
				fetchTotalCount(`${baseURL}/user-accounts?page=1&pageSize=1`),
				fetchTotalCount(`${baseURL}/user-groups?page=1&pageSize=1`),
			]).then(
				([userAccountsCount, userGroupsCount]) =>
					userAccountsCount + userGroupsCount
			)
	);

	return (
		<SummarySectionHeader
			actionLabel={
				hasAssignMembersPermission && refreshedCount
					? Liferay.Language.get('manage-members')
					: undefined
			}
			count={refreshedCount}
			onActionClick={
				hasAssignMembersPermission && refreshedCount
					? () =>
							openManageMembersModal({
								externalReferenceCode,
								hasAssignMembersPermission,
								headerTitle:
									Liferay.Language.get('manage-members'),
								ownerId,
								refreshDataSetIds,
							})
					: undefined
			}
			title={Liferay.Language.get('members')}
		/>
	);
}
