/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect} from 'react';
import {Outlet, useLocation, useParams} from 'react-router-dom';
import PageRenderer from '~/components/PageRenderer';
import {testrayJiraProjectImpl} from '~/services/rest/TestrayJiraProject';

import {useFetch} from '../../hooks/useFetch';
import useHeader from '../../hooks/useHeader';
import i18n from '../../i18n';
import {APIResponse, TestrayJiraProject} from '../../services/rest';

const IssuesOutlet = () => {
	const {jiraProjectERC, ...otherParams} = useParams();
	const {pathname} = useLocation();
	const shouldUpdate = !Object.keys(otherParams).length;

	const {setDropdown, setHeading, setTabs} = useHeader({
		shouldUpdate,
	});

	const {
		data: testrayJiraProject,
		error,
		loading,
		mutate,
	} = useFetch<TestrayJiraProject>(
		testrayJiraProjectImpl.getResourceByExternalReferenceCode(
			jiraProjectERC as string
		),
		{
			transformData: (response) =>
				testrayJiraProjectImpl.transformData(response),
		}
	);

	const getPath = useCallback(
		(path: string) => {
			const relativePath = `/issues/${jiraProjectERC}/${path}`;

			return {
				active: relativePath === pathname,
				path: relativePath,
			};
		},
		[jiraProjectERC, pathname]
	);

	const {data: testrayJiraProjects} = useFetch<
		APIResponse<TestrayJiraProject>
	>('/jiraprojects', {
		params: {
			fields: 'externalReferenceCode,name',
			pageSize: 100,
		},
	});

	const jiraProjects = testrayJiraProjects?.items;

	useEffect(() => {
		if (jiraProjects) {
			setDropdown([
				{
					items: [
						{
							divider: true,
							label: i18n.translate('jira-directory'),
							path: '/issues',
						},
						...jiraProjects.map((jiraProject) => ({
							label: jiraProject.name,
							path: `/issues/${jiraProject.externalReferenceCode}/initiative`,
						})),
					],
				},
			]);
		}
	}, [setDropdown, jiraProjects]);

	const hasOtherParams = !!Object.values(otherParams).length;

	useEffect(() => {
		if (testrayJiraProject) {
			setTimeout(() => {
				setHeading([
					{
						category: i18n.translate('project').toUpperCase(),
						path: `/issues/${testrayJiraProject.externalReferenceCode}/initiative`,
						title: testrayJiraProject.name,
					},
				]);
			}, 0);
		}
	}, [setHeading, testrayJiraProject, hasOtherParams]);

	useEffect(() => {
		if (!hasOtherParams) {
			setTimeout(() => {
				setTabs([
					{
						...getPath('initiative'),
						title: i18n.translate('initiatives'),
					},
					{
						...getPath('epic'),
						title: i18n.translate('epics'),
					},
					{
						...getPath('story'),
						title: i18n.translate('stories'),
					},
				]);
			}, 0);
		}
	}, [getPath, setTabs, hasOtherParams]);

	return (
		<PageRenderer error={error} loading={loading}>
			<Outlet
				context={{
					actions: testrayJiraProject?.actions,
					mutateTestrayProject: mutate,
					testrayJiraProject,
				}}
			/>
		</PageRenderer>
	);
};

export default IssuesOutlet;
