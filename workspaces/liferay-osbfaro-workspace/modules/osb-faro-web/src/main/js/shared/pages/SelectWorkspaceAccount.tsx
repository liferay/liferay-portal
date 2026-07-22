import Loading from 'shared/components/Loading';
import React from 'react';
import WorkspaceList from 'shared/components/workspaces/workspace-list';
import WorkspacesBasePage from 'shared/components/workspaces/BasePage';
import {getBasicProjects, getSingleProjectRoute} from 'shared/util/projects';
import {isString} from 'lodash';
import {Navigate} from 'react-router-dom';
import {Routes, setUriQueryValue, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useFetchProjects} from 'shared/hooks/useProjects';

const checkDisabled = ({configured}: {configured: boolean}) => configured;

export const routingFn = ({projects}: {projects: any[]}) => {
	const basicProjects = getBasicProjects(projects);

	if (basicProjects.length === 1) {
		return getSingleProjectRoute(basicProjects[0]);
	}

	if (!basicProjects.some((basicProject) => !basicProject.get('groupId'))) {
		return setUriQueryValue(
			toRoute(Routes.WORKSPACES),
			'allBasicConfigured',
			true
		);
	}

	return null;
};

const SelectWorkspaceAccount = () => {
	const currentUser = useCurrentUser();
	const {data: projects, loading} = useFetchProjects();

	if (loading) {
		return <Loading />;
	}

	const route = routingFn({projects});

	if (isString(route)) {
		return <Navigate to={route} />;
	}

	return (
		<div className="select-account-root">
			<WorkspacesBasePage
				details={[
					<p key="SELECT">
						{sub(
							Liferay.Language.get(
								'weve-found-multiple-accounts-associated-with-x-.-you-can-have-one-basic-tier-workspace-of-analytics-cloud-per-account.-please-associate-this-analytics-cloud-workspace-to-an-account'
							),
							[
								<b key="emailAddress">
									{currentUser.emailAddress}
								</b>,
							],
							false
						)}
					</p>,
				]}
				title={Liferay.Language.get('select-account')}
			>
				<WorkspaceList
					accounts={getBasicProjects(projects)}
					checkDisabled={checkDisabled}
				/>
			</WorkspacesBasePage>
		</div>
	);
};

export default SelectWorkspaceAccount;
