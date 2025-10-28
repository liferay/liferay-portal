import BasePage from 'settings/components/BasePage';
import Constants, {JobRunStatuses} from 'shared/util/constants';
import OutputVersionsCard from '../components/OutputVersionsCard';
import React from 'react';
import RecommendationJobRunsQuery from '../queries/RecommendationJobRunsQuery';
import TrainingItemsCard from '../components/TrainingItemsCard';
import withRecommendation from 'shared/hoc/WithRecommendation';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {Filter, Job} from '../utils/utils';
import {get} from 'lodash';
import {getOperationName} from 'apollo-link';
import {getRecommendations} from 'shared/util/breadcrumbs';
import {
	RECOMMENDATION_DELETE_MUTATION,
	RECOMMENDATION_RUN_MUTATION
} from '../queries/RecommendationMutation';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useMutation, useQuery} from '@apollo/react-hooks';
import {useParams} from 'react-router-dom';
import {useTimeZone} from 'shared/hooks/useTimeZone';
import {withHistory} from 'shared/hoc';

const {
	pagination: {orderDescending}
} = Constants;
const connector = connect(null, {addAlert, close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IViewProps extends PropsFromRedux {
	history: {
		push: (value: string) => void;
	};
	job: Job;
}

const View: React.FC<IViewProps> = ({addAlert, close, history, job, open}) => {
	const {groupId, jobId} = useParams();
	const {timeZoneId} = useTimeZone();
	const currentUser = useCurrentUser();

	const {data: jobRuns, loading} = useQuery(RecommendationJobRunsQuery, {
		variables: {
			jobId,
			size: 1,
			sort: {
				column: 'id',
				type: orderDescending.toUpperCase()
			},
			start: 0
		}
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

	const itemFilters: Filter[] = get(job, 'parameters', []).filter(
		({name}) => name !== 'includePreviousPeriod'
	);

	const name = get(job, 'name');

	return (
		<div className='row'>
			<div className='col-xl-8'>
				<BasePage
					breadcrumbItems={[
						getRecommendations({groupId}),
						{
							active: true,
							label: name
						}
					]}
					pageActions={
						currentUser.isAdmin()
							? [
									{
										disabled: loading || jobRunRunning,
										label: Liferay.Language.get('retrain'),
										onClick: () => {
											open(
												modalTypes.MANUALLY_RETRAIN_MODEL_MODAL,
												{
													job,
													onClose: close,
													onSubmit: ({
														runDataPeriod
													}) => {
														runRecommendationJob({
															awaitRefetchQueries: true,
															refetchQueries: [
																getOperationName(
																	RecommendationJobRunsQuery
																)
															],
															variables: {
																jobId,
																runDataPeriod
															}
														})
															.then(() => {
																addAlert({
																	alertType:
																		Alert
																			.Types
																			.Success,
																	message: Liferay.Language.get(
																		'retraining-has-been-started'
																	)
																});

																close();
															})
															.catch(() => {
																addAlert({
																	alertType:
																		Alert
																			.Types
																			.Error,
																	message: Liferay.Language.get(
																		'there-was-an-error-processing-your-request.-please-try-again'
																	),
																	timeout: false
																});
															});
													},
													trainingPeriod: get(
														job,
														'trainingPeriod'
													)
												}
											);
										}
									},
									{
										href: toRoute(
											Routes.SETTINGS_RECOMMENDATION_EDIT,
											{
												groupId,
												jobId
											}
										),
										label: Liferay.Language.get('edit')
									},
									{
										label: Liferay.Language.get('delete'),
										onClick: () => {
											open(
												modalTypes.CONFIRMATION_MODAL,
												{
													message: (
														<div>
															<div className='h4 text-secondary'>
																{sub(
																	Liferay.Language.get(
																		'delete-x-and-its-historical-training-output-data'
																	),
																	[name]
																)}
															</div>

															<p>
																{Liferay.Language.get(
																	'components-using-this-model-will-need-to-be-reconfigured'
																)}
															</p>
														</div>
													),
													modalVariant:
														'modal-warning',
													onClose: close,
													onSubmit: () => {
														deleteRecommendationJobs(
															{
																variables: {
																	jobIds: [
																		jobId
																	]
																}
															}
														)
															.then(() => {
																addAlert({
																	alertType:
																		Alert
																			.Types
																			.Success,
																	message: sub(
																		Liferay.Language.get(
																			'x-has-been-deleted'
																		),
																		[name]
																	) as string
																});

																history.push(
																	toRoute(
																		Routes.SETTINGS_RECOMMENDATIONS,
																		{
																			groupId
																		}
																	)
																);
															})
															.catch(() => {
																addAlert({
																	alertType:
																		Alert
																			.Types
																			.Error,
																	message: Liferay.Language.get(
																		'there-was-an-error-processing-your-request.-please-try-again'
																	),
																	timeout: false
																});
															});
													},
													submitButtonDisplay:
														'warning',
													submitMessage: Liferay.Language.get(
														'delete'
													),
													title: sub(
														Liferay.Language.get(
															'deleting-x'
														),
														[name]
													),
													titleIcon: 'warning-full'
												}
											);
										}
									}
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
		</div>
	);
};

export default compose<any>(withRecommendation, withHistory, connector)(View);
