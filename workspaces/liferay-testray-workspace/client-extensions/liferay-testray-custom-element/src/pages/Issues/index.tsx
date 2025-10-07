/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Container from '~/components/Layout/Container';
import ListView from '~/components/ListView';
import {useHeader} from '~/hooks';
import i18n from '~/i18n';

type JiraProjectsProps = {
	PageContainer?: React.FC;
};

const JiraProjects: React.FC<JiraProjectsProps> = ({
	PageContainer = Container,
}) => {
	useHeader({
		dropdown: [],
		headerActions: {actions: []},
		heading: [
			{
				category: i18n.translate('project'),
				title: i18n.translate('jira-project-directory'),
			},
		],
		icon: 'box-container',
	});

	return (
		<PageContainer>
			<ListView
				initialContext={{
					sort: {
						direction: 'ASC',
						key: 'name',
					},
				}}
				managementToolbarProps={{
					applyFilters: true,
					display: {columns: false},
					title: i18n.translate('jira-projects'),
				}}
				resource="/jiraprojects"
				tableProps={{
					columns: [
						{
							clickable: true,
							key: 'name',
							size: 'lg',
							sorteable: true,
							value: i18n.translate('project'),
						},
						{
							clickable: true,
							key: 'externalReferenceCode',
							size: 'lg',
							value: i18n.translate('key'),
						},
					],
					navigateTo: (jiraProject) =>
						`/issues/${jiraProject.externalReferenceCode}/initiative`,
				}}
			/>
		</PageContainer>
	);
};

export default JiraProjects;
