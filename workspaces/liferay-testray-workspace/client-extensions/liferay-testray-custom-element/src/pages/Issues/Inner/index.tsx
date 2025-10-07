/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import {useOutletContext} from 'react-router-dom';
import Container from '~/components/Layout/Container';
import ListView from '~/components/ListView';
import ProgressBar from '~/components/ProgressBar';
import i18n from '~/i18n';
import {TestrayBuild, TestrayJiraIssue} from '~/services/rest';
import getJiraIconImage from '~/util/icons';

type OutletContext = {
	testrayBuild?: TestrayBuild;
	testrayJiraIssue?: TestrayJiraIssue;
};

const ChildIssues = () => {
	const {testrayBuild, testrayJiraIssue}: OutletContext = useOutletContext();

	const clickable =
		testrayJiraIssue?.issueType.key !== 'STORY' &&
		testrayJiraIssue?.issueType.key !== 'TASK';

	return (
		<Container className="mt-4">
			<ListView
				initialContext={{
					columns: {
						inprogress: false,
						passed: false,
						testfix: false,
						total: false,
						untested: false,
					},
					columnsFixed: ['testrayIssueTitle'],
				}}
				managementToolbarProps={{
					applyFilters: true,
					display: {columns: true},
					title: i18n.translate('jira-child-issues'),
				}}
				resource={`/testray-status-metrics/by-testray-jiraIssueId/${testrayJiraIssue?.id}/testray-issues-metrics?testrayBuildId=${testrayBuild?.id}`}
				tableProps={{
					columns: [
						{
							clickable,
							key: 'testrayIssueKey',
							size: 'sm',
							value: i18n.translate('issue-key'),
						},
						{
							clickable,
							key: 'testrayIssueType',
							render: (_, {testrayIssueType}) => (
								<>
									{testrayIssueType && (
										<img
											alt={testrayIssueType}
											src={getJiraIconImage(
												testrayIssueType
											)}
											style={{
												height: 16,
												verticalAlign: 'middle',
												width: 16,
											}}
										/>
									)}
									<ClayLabel
										className="ml-2"
										displayType="info"
									>
										{testrayIssueType}
									</ClayLabel>
								</>
							),
							size: 'sm',
							value: i18n.translate('issue-type'),
						},
						{
							clickable,
							key: 'testrayIssueTitle',
							size: 'lg',
							value: i18n.translate('title'),
						},
						{
							clickable,
							key: 'untested',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.untested,
							value: i18n.translate('untested'),
						},
						{
							clickable,
							key: 'in-progress',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.inProgress,
							value: i18n.translate('in-progress'),
						},
						{
							clickable,
							key: 'passed',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.passed,
							value: i18n.translate('passed'),
						},
						{
							clickable,
							key: 'failed',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.failed,
							value: i18n.translate('failed'),
						},
						{
							clickable,
							key: 'blocked',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.blocked,
							value: i18n.translate('blocked'),
						},
						{
							clickable,
							key: 'test-fix',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.testfix,
							value: i18n.translate('test-fix'),
						},
						{
							clickable,
							key: 'total',
							render: (_, {testrayStatusMetric}) =>
								testrayStatusMetric.total,
							value: i18n.translate('total'),
						},
						{
							clickable,
							key: 'testrayStatusMetric',
							render: (testrayStatusMetric) => (
								<ProgressBar
									chartOrder={[
										'passed',
										'failed',
										'blocked',
										'test_fix',
										'incomplete',
									]}
									items={{
										blocked: testrayStatusMetric?.blocked,
										failed: testrayStatusMetric?.failed,
										incomplete:
											testrayStatusMetric?.incomplete +
											testrayStatusMetric?.untested,
										passed: testrayStatusMetric?.passed,
										test_fix: testrayStatusMetric?.testfix,
									}}
								/>
							),
							value: i18n.translate('metrics'),
							width: '300',
						},
					],
					navigateTo: (issue) => `../${issue.testrayIssueKey}`,
					rowWrap: true,
				}}
			/>
		</Container>
	);
};

export default ChildIssues;
