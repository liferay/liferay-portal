/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useOutletContext} from 'react-router-dom';
import JiraLink from '~/components/JiraLink';
import Container from '~/components/Layout/Container';
import ListView from '~/components/ListView';
import StatusBadge from '~/components/StatusBadge';
import {StatusBadgeType} from '~/components/StatusBadge/StatusBadge';
import i18n from '~/i18n';
import {TestrayBuild, TestrayJiraIssue} from '~/services/rest';

type OutletContext = {
	testrayBuild: TestrayBuild;
	testrayJiraIssue: TestrayJiraIssue;
};

const IssueResults = () => {
	const {testrayBuild, testrayJiraIssue}: OutletContext = useOutletContext();

	const filter = `caseDetailsToJiraIssues/r_${testrayJiraIssue.issueType.key.toLowerCase()}_c_jiraIssueId eq '${testrayJiraIssue.id}' and r_buildToCaseDetail_c_buildId eq '${testrayBuild.id}'`;

	return (
		<Container>
			<ListView
				initialContext={{
					pageSize: 50,
					sort: {
						direction: 'ASC',
						key: 'dueStatus',
					},
				}}
				managementToolbarProps={{
					applyFilters: true,
					display: {columns: false},
					filterSchema: 'issueResults',
					title: i18n.translate('jira-issue-results'),
				}}
				resource="/casedetails/?nestedFields=caseToCaseDetails,caseDetailsToJiraIssues"
				tableProps={{
					columns: [
						{
							clickable: true,
							key: 'flaky',
							render: (_, {r_caseToCaseDetails_c_case}) => (
								<>
									{r_caseToCaseDetails_c_case.flaky && (
										<ClayTooltipProvider>
											<span
												className="tr-table__row__flaky-icon"
												data-tooltip-align="top"
												title={i18n.translate(
													'this-is-a-possible-flaky-test'
												)}
											>
												<ClayIcon symbol="flag-full" />
											</span>
										</ClayTooltipProvider>
									)}
									{r_caseToCaseDetails_c_case.name}
								</>
							),
							size: 'md',
							value: i18n.translate('case'),
							width: '350',
						},
						{
							clickable: true,
							key: 'name',
							size: 'md',
							value: i18n.translate('test'),
						},
						{
							clickable: true,
							key: 'priority',
							render: (_, {r_caseToCaseDetails_c_case}) =>
								r_caseToCaseDetails_c_case.priority,
							size: 'sm',
							value: i18n.translate('priority'),
						},
						{
							clickable: true,
							key: 'dueStatus',
							render: (_, {dueStatus}) => (
								<StatusBadge
									type={dueStatus.key as StatusBadgeType}
								>
									{dueStatus.key}
								</StatusBadge>
							),
							value: i18n.translate('status'),
						},
						{
							key: 'issues',
							render: (_, {caseDetailsToJiraIssues}) => (
								<>
									{caseDetailsToJiraIssues.map(
										(
											issue: TestrayJiraIssue,
											_: number
										) => (
											<JiraLink
												displayViewInJira={false}
												issue={
													issue.externalReferenceCode
												}
												key={issue.id}
											/>
										)
									)}
								</>
							),
							value: i18n.translate('issues'),
						},
					],
					navigateTo: (caseDetail) =>
						`/project/${caseDetail.r_caseToCaseDetails_c_case?.r_projectToCases_c_projectId}/cases/${caseDetail.r_caseToCaseDetails_c_case?.id}`,
				}}
				variables={{
					filter,
				}}
			/>
		</Container>
	);
};

export default IssueResults;
