import BasePage from 'settings/components/base-page/BasePage';
import Constants, {JobRunStatuses} from 'shared/util/constants';
import OutputVersionsCard from '../components/OutputVersionsCard';
import DeleteRecommendationModal from 'shared/components/modals/DeleteRecommendationModal';
import ManuallyRetrainModelModal from 'shared/components/modals/ManuallyRetrainModelModal';
import React from 'react';
import RecommendationJobRunsQuery from '../queries/RecommendationJobRunsQuery';
import TrainingItemsCard from '../components/TrainingItemsCard';
import withRecommendation from 'shared/hoc/WithRecommendation';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {Filter, Job, JobParameter} from '../utils/utils';
import {get} from 'lodash';
import {getOperationName} from '@apollo/client/utilities';
import {getRecommendations} from 'shared/util/breadcrumbs';
import {
	RECOMMENDATION_DELETE_MUTATION,
	RECOMMENDATION_RUN_MUTATION,
} from '../queries/RecommendationMutation';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useModal} from '@clayui/modal';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useMutation, useQuery} from '@apollo/client';
import {useParams} from 'react-router-dom';
import {useTimeZone} from 'shared/hooks/useTimeZone';
import {withHistory} from 'shared/hoc';

const {
	pagination: {orderDescending},
} = Constants;
const connector = connect(null, {addAlert});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IViewProps extends PropsFromRedux {
	history: {
		push: (value: string) => void;
	};
	job: Job;
}

const View: React.FC<IViewProps> = ({addAlert, history, job}) => {
	const {groupId = '', jobId = ''} = useParams<{
		groupId: string;
		jobId: string;
	}>();
	const {timeZoneId} = useTimeZone();
	const currentUser = useCurrentUser();
	const deleteModal = useModal();
	const retrainModal = useModal();

	const {data: jobRuns, loading} = useQuery(RecommendationJobRunsQuery, {
		variables: {
			jobId,
			size: 1,
			sort: {
				column: 'id',
				type: orderDescending.toUpperCase(),
			},
			start: 0,
		},
	});

	const [deleteRecommendationJobs] = useMutation(
		RECOMMENDATION_DELETE_MUTATION
	);

	const jobRun =
		!!jobRuns &&
		jobRuns.jobRuns.total &&
		get(jobRuns, ['jobRuns', 'jobRuns', 0], null);

	const jobRunRunning: boolean =
		get(jobRun, 'status', null) === JobRunStatuses.Running;

	const [runRecommendationJob] = useMutation(RECOMMENDATION_RUN_MUTATION);

	const itemFilters: Filter[] = (
		get(job, 'parameters', [] as JobParameter[]) as Filter[]
	).filter(({name}) => name !== 'includePreviousPeriod');

	const name = get(job, 'name');

	return (
		<div className="row">
			<div className="col-xl-8">
				<BasePage
					breadcrumbItems={[
						getRecommendations({groupId}),
						{
							active: true,
							label: name,
						},
					]}
					pageActions={
						currentUser.isAdmin()
							? [
									{
										disabled: loading || jobRunRunning,
										label: Liferay.Language.get('retrain'),
										onClick: () => {
											retrainModal.onOpenChange(true);
										},
									},
									{
										href: toRoute(
											Routes.SETTINGS_RECOMMENDATION_EDIT,
											{
												groupId,
												jobId,
											}
										),
										label: Liferay.Language.get('edit'),
									},
									{
										label: Liferay.Language.get('delete'),
										onClick: () => {
											deleteModal.onOpenChange(true);
										},
									},
								]
							: []
					}
					pageActionsDisplayLimit={3}
					pageTitle={name}
				>
					<OutputVersionsCard
						jobId={jobId}
						nextRunDate={get(job, 'nextRunDate')}
						runFrequency={get(job, 'runFrequency')}
						timeZoneId={timeZoneId}
					/>
					<TrainingItemsCard itemFilters={itemFilters} />
				</BasePage>
			</div>

			{deleteModal.open && (
				<DeleteRecommendationModal
					description={
						sub(
							Liferay.Language.get(
								'delete-x-and-its-historical-training-output-data'
							),
							[name]
						) as string
					}
					name={name}
					observer={deleteModal.observer}
					onClose={() => deleteModal.onOpenChange(false)}
					onSubmit={() => {
						deleteRecommendationJobs({
							variables: {
								jobIds: [jobId],
							},
						})
							.then(() => {
								addAlert({
									alertType: Alert.Types.Success,
									message: sub(
										Liferay.Language.get(
											'x-has-been-deleted'
										),
										[name]
									) as string,
								});

								history.push(
									toRoute(Routes.SETTINGS_RECOMMENDATIONS, {
										groupId,
									})
								);
							})
							.catch(() => {
								addAlert({
									alertType: Alert.Types.Error,
									message: Liferay.Language.get(
										'there-was-an-error-processing-your-request.-please-try-again'
									),
									timeout: false,
								});
							});
					}}
				/>
			)}

			{retrainModal.open && (
				<ManuallyRetrainModelModal
					job={job}
					observer={retrainModal.observer}
					onClose={() => retrainModal.onOpenChange(false)}
					onSubmit={({runDataPeriod}: {runDataPeriod: string}) => {
						runRecommendationJob({
							awaitRefetchQueries: true,
							refetchQueries: [
								getOperationName(
									RecommendationJobRunsQuery
								) as string,
							],
							variables: {
								jobId,
								runDataPeriod,
							},
						})
							.then(() => {
								addAlert({
									alertType: Alert.Types.Success,
									message: Liferay.Language.get(
										'retraining-has-been-started'
									),
								});

								retrainModal.onOpenChange(false);
							})
							.catch(() => {
								addAlert({
									alertType: Alert.Types.Error,
									message: Liferay.Language.get(
										'there-was-an-error-processing-your-request.-please-try-again'
									),
									timeout: false,
								});
							});
					}}
				/>
			)}
		</div>
	);
};

export default compose<React.ComponentType<any>>(
	withRecommendation,
	withHistory,
	connector
)(View);
