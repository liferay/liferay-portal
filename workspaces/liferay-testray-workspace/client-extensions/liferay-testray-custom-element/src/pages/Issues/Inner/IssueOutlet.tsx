/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';
import {
	Outlet,
	useLocation,
	useOutletContext,
	useParams,
} from 'react-router-dom';
import {mutate} from 'swr';
import PageRenderer from '~/components/PageRenderer';
import {testrayJiraIssueImpl} from '~/services/rest/TestrayJiraIssue';

import {useFetch} from '../../../hooks/useFetch';
import useHeader from '../../../hooks/useHeader';
import i18n from '../../../i18n';
import {
	APIResponse,
	TestrayBuild,
	TestrayJiraIssue,
	TestrayJiraProject,
} from '../../../services/rest';
import IssueOverview from './IssueOverview';

type OutletContext = {
	testrayJiraProject: TestrayJiraProject;
};

const IssueOutlet = () => {
	const {jiraIssueERC, jiraProjectERC, ...otherParams} = useParams();
	const {pathname} = useLocation();
	const {testrayJiraProject}: OutletContext = useOutletContext();

	const {data: testrayJiraIssue} = useFetch<TestrayJiraIssue>(
		testrayJiraIssueImpl.getResourceByExternalReferenceCode(
			jiraIssueERC as string
		),
		{
			transformData: (response) =>
				testrayJiraIssueImpl.transformData(response),
		}
	);

	const hasOtherParams = !!Object.values(otherParams).length;

	const basePath = `/issues/${jiraProjectERC}/${jiraIssueERC}`;

	const {setHeading, setTabs} = useHeader({
		shouldUpdate: !hasOtherParams,
		timeout: 100,
	});

	useEffect(() => {
		const heading = [
			{
				category: i18n.translate('project').toUpperCase(),
				path: `/issues/${jiraProjectERC}/initiative`,
				title: testrayJiraProject.name,
			},
		];

		if (testrayJiraIssue?.initiativeERC) {
			heading.push({
				category: 'INITIATIVE',
				path: `/issues/${jiraProjectERC}/${testrayJiraIssue.initiativeERC}`,
				title: testrayJiraIssue.initiativeERC,
			});
		}

		if (testrayJiraIssue?.epicERC) {
			heading.push({
				category: 'EPIC',
				path: `/issues/${jiraProjectERC}/${testrayJiraIssue.epicERC}`,
				title: testrayJiraIssue.epicERC,
			});
		}

		if (testrayJiraIssue?.storyERC) {
			heading.push({
				category: 'STORY',
				path: `/issues/${jiraProjectERC}/${testrayJiraIssue.storyERC}`,
				title: testrayJiraIssue.storyERC,
			});
		}

		if (testrayJiraIssue?.externalReferenceCode) {
			heading.push({
				category: testrayJiraIssue.issueType.name,
				path: `/issues/${jiraProjectERC}/${testrayJiraIssue.externalReferenceCode}`,
				title: testrayJiraIssue.externalReferenceCode,
			});
		}

		setHeading(heading);
	}, [setHeading, testrayJiraIssue, jiraProjectERC, testrayJiraProject]);

	useEffect(() => {
		if (!hasOtherParams) {
			setTabs([
				{
					active: pathname === basePath,
					path: basePath,
					title: i18n.translate('current'),
				},
				{
					active: pathname === `${basePath}/results`,
					path: `${basePath}/results`,
					title: i18n.translate('results'),
				},
			]);
		}
	}, [basePath, pathname, hasOtherParams, setTabs]);

	const {
		data: testrayBuilds,
		error,
		loading,
	} = useFetch<APIResponse<TestrayBuild>>('/builds', {
		params: {
			filter: `routineId eq '${testrayJiraProject.r_routineToJiraProject_c_routineId}'`,
			pageSize: 1,
			sort: 'dateCreated:desc',
		},
	});

	return (
		<PageRenderer error={error} loading={loading}>
			<>
				{testrayJiraIssue && (
					<IssueOverview
						testrayBuild={testrayBuilds?.items[0]}
						testrayJiraIssue={testrayJiraIssue}
					/>
				)}

				<Outlet
					context={{
						actions: testrayJiraIssue?.actions,
						mutate,
						testrayBuild: testrayBuilds?.items[0],
						testrayJiraIssue,
						testrayJiraProject,
					}}
				/>
			</>
		</PageRenderer>
	);
};

export default IssueOutlet;
