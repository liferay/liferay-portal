/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLocation, useOutletContext} from 'react-router-dom';
import Container from '~/components/Layout/Container';
import ListView from '~/components/ListView';
import i18n from '~/i18n';
import {TestrayJiraProject} from '~/services/rest';

type IssuesProps = {
	PageContainer?: React.FC;
};

type OutletContext = {
	testrayJiraProject: TestrayJiraProject;
};

const Issues: React.FC<IssuesProps> = ({PageContainer = Container}) => {
	const {testrayJiraProject}: OutletContext = useOutletContext();
	const {pathname} = useLocation();

	const filter = `jiraProjectId eq '${testrayJiraProject.id}' and issueType eq '${pathname.split('/').pop()?.toUpperCase()}'`;

	return (
		<PageContainer>
			<ListView
				initialContext={{
					sort: {
						direction: 'ASC',
						key: 'title',
					},
				}}
				managementToolbarProps={{
					applyFilters: true,
					display: {columns: false},
					filterSchema: 'issues',
					title: i18n.translate(
						'' + pathname.split('/').filter(Boolean).pop()
					),
				}}
				resource="/jiraissues"
				tableProps={{
					columns: [
						{
							clickable: true,
							key: 'externalReferenceCode',
							size: 'sm',
							value: i18n.translate('issue-key'),
						},
						{
							clickable: true,
							key: 'title',
							size: 'lg',
							sorteable: true,
							value: i18n.translate('title'),
						},
					],
					navigateTo: (issue) => `../${issue.externalReferenceCode}`,
				}}
				variables={{
					filter,
				}}
			/>
		</PageContainer>
	);
};

export default Issues;
