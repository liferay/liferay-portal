import ClayAlert from '@clayui/alert';
import ClayLink from '@clayui/link';
import EmptyState from 'shared/components/workspaces/EmptyState';
import JoinableWorkspacesWrapper from 'shared/components/workspaces/JoinableWorkspacesWrapper';
import Loading from 'shared/components/Loading';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import WorkspaceList from 'shared/components/workspaces/workspace-list';
import WorkspacesBasePage from 'shared/components/workspaces/BasePage';
import {ENABLE_ADD_TRIAL_WORKSPACE} from 'shared/util/constants';
import {isLDPPlan, PLANS} from 'shared/util/subscriptions';
import {isString} from 'lodash';
import {Redirect} from 'react-router';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {
	useFetchJoinableProjects,
	useFetchProjects,
} from 'shared/hooks/useProjects';
import {useIncidentAlert} from 'shared/hooks/useIncidentAlert';

type ProjectLike = {
	corpProjectUuid?: string;
	faroSubscription: {name: string};
	groupId?: string;
	[key: string]: any;
};

export const routingFn = ({projects}: {projects: ProjectLike[]}) => {
	if (projects.length === 1 && !projects[0].groupId) {
		return toRoute(Routes.WORKSPACE_ADD_WITH_CORP_PROJECT_UUID, {
			corpProjectUuid: projects[0].corpProjectUuid,
		});
	}

	return null;
};

const WorkspacesContent = ({
	joinableProjects,
	loading,
	loadingJoinableProjects,
	projects,
}: {
	joinableProjects: ProjectLike[];
	loading: boolean;
	loadingJoinableProjects: boolean;
	projects: ProjectLike[];
}) => {
	if (loading) {
		return <Loading spacer />;
	}

	const filteredProjects = projects.filter(
		({faroSubscription, groupId}) =>
			faroSubscription.name !==
				(PLANS as {[key: string]: any}).basic.name || groupId
	);

	if (!projects.length && !joinableProjects.length) {
		return <EmptyState />;
	}

	return (
		<>
			{!!filteredProjects.length && (
				<WorkspaceList
					accounts={filteredProjects}
					displayAccountHeaders
					displayPlanInfo
				/>
			)}

			{loadingJoinableProjects ? (
				<Loading spacer />
			) : (
				!!joinableProjects.length && (
					<JoinableWorkspacesWrapper
						details={Liferay.Language.get(
							'workspaces-you-can-request-access-to-based-on-your-email-domain'
						)}
						title={Liferay.Language.get('workspaces-you-can-join')}
					>
						<WorkspaceList
							accounts={joinableProjects}
							isJoinableProjects
						/>
					</JoinableWorkspacesWrapper>
				)
			)}

			{ENABLE_ADD_TRIAL_WORKSPACE && (
				<div className="mt-4">
					<ClayLink
						button
						className="button-root"
						displayType="secondary"
						href={toRoute(Routes.WORKSPACE_ADD_TRIAL)}
						small
					>
						{Liferay.Language.get('start-free-trial')}
					</ClayLink>
				</div>
			)}
		</>
	);
};

const Workspaces: any = () => {
	const {data: projects, loading} = useFetchProjects();

	const {
		data: preferences,
		loading: loadingPreferences,
		onClose,
	} = useIncidentAlert();

	const {data: joinableProjects, loading: loadingJoinableProjects} =
		useFetchJoinableProjects();

	const ldpEnabled = projects.some(({faroSubscription}) =>
		isLDPPlan(faroSubscription?.get('name'))
	);

	if (projects.length === 1 && !projects[0].groupId) {
		return toRoute(Routes.WORKSPACE_ADD_WITH_CORP_PROJECT_UUID, {
			corpProjectUuid: projects[0].corpProjectUuid,
		});
	}

	const handleDetails = () => {
		if (projects.length) {
			return [
				<p key="SELECT">
					{Liferay.Language.get('workspaces-you-have-joined')}
				</p>,
			];
		}
		else if (!loading && !projects.length && !joinableProjects.length) {
			return [
				<p key="EMPTY_STATE">
					{Liferay.Language.get(
						'you-are-not-a-part-of-any-workspaces,-lets-create-a-new-one'
					)}
				</p>,
			];
		}
	};

	const handleTitle = (): string => {
		if (projects.length || (!projects.length && !joinableProjects.length)) {
			return Liferay.Language.get('your-workspaces');
		}
		return '';
	};

	const route = routingFn({projects});

	if (isString(route)) {
		return <Redirect push to={route} />;
	}

	return (
		<div className="workspaces-root" key="Workspaces">
			<WorkspacesBasePage
				details={handleDetails()}
				ldpEnabled={ldpEnabled}
				loadingLDPEnabled={loading}
				title={handleTitle()}
			>
				{!loadingPreferences && preferences.incidentAlertEnabled && (
					<ClayAlert
						displayType="warning"
						onClose={onClose}
						symbol="info-circle"
						title={Liferay.Language.get('warning')}
						variant="inline"
					>
						{sub(
							Liferay.Language.get(
								'maintenance-is-scheduled-for-x-and-may-impact-your-workflow'
							),
							['November 13']
						)}
						<ClayLink
							className="ml-1"
							decoration="underline"
							href={URLConstants.StatusPageAnnouncements}
							target="_blank"
						>
							{Liferay.Language.get(
								'visit-our-status-page-for-more-details'
							)}
						</ClayLink>
					</ClayAlert>
				)}

				<WorkspacesContent
					joinableProjects={joinableProjects}
					loading={loading}
					loadingJoinableProjects={loadingJoinableProjects}
					projects={projects}
				/>
			</WorkspacesBasePage>
		</div>
	);
};

export default Workspaces;
