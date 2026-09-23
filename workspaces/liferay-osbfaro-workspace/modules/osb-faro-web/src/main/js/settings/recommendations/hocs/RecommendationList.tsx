import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import Constants, {
	JobRunDataPeriods,
	JobRunFrequencies,
	JobStatuses,
	JobTypes,
	OrderByDirections,
} from 'shared/util/constants';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import Label from 'shared/components/Label';
import Nav from 'shared/components/Nav';
import React from 'react';
import RecommendationListQuery from '../queries/RecommendationListQuery';
import {
	ACTION_TYPES,
	useSelectionContext,
	withSelectionProvider,
} from 'shared/context/selection';
import {addAlert} from 'shared/actions/alerts';
import {Alert, Router} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {
	createOrderIOMap,
	getSortFromOrderIOMap,
	NAME,
} from 'shared/util/pagination';
import {formatDateToTimeZone, getCustomDateFormat} from 'shared/util/date';
import {get} from 'lodash';
import {getFormattedTitle} from 'shared/components/NoResultsDisplay';
import {
	JOB_RUN_DATA_PERIODS_LABEL_MAP,
	JOB_RUN_FREQUENCIES_LABEL_MAP,
	JOB_STATUSES_DISPLAY_MAP,
	JOB_STATUSES_LABEL_MAP,
	JOB_TYPES_LABEL_MAP,
} from '../utils/utils';
import {NameCell} from 'shared/components/table/cell-components';
import {RECOMMENDATION_DELETE_MUTATION} from '../queries/RecommendationMutation';
import {RootState} from 'shared/store';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useMutation, useQuery} from '@apollo/client';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';

const {
	pagination: {cur: defaultPage},
} = Constants;

const connector = connect(
	(store: RootState, {groupId}: {groupId: string}) => ({
		timeZoneId: store.getIn([
			'projects',
			groupId,
			'data',
			'timeZone',
			'timeZoneId',
		]),
	}),
	{addAlert, close, open}
);

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IRecommendationListProps extends PropsFromRedux {
	groupId: string;
	history: {
		push: (value: string) => void;
	};
	router: Router;
}

const RecommendationList: React.FC<IRecommendationListProps> = ({
	addAlert,
	close,
	groupId,
	history,
	open,
	timeZoneId,
}: IRecommendationListProps) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {data, error, loading, refetch} = useQuery(RecommendationListQuery, {
		fetchPolicy: 'no-cache',
		variables: {
			keywords: query,
			size: delta,
			sort: getSortFromOrderIOMap(orderIOMap),
			start: (page - 1) * delta,
		},
	});

	const [deleteRecommendationJobs] = useMutation(
		RECOMMENDATION_DELETE_MUTATION
	);

	const currentUser = useCurrentUser();

	const singleSelectedItem =
		selectedItems.size === 1 ? selectedItems.first() : null;

	const selectedItemsCount = selectedItems.size;

	const confirmationMessage = singleSelectedItem
		? sub(
				Liferay.Language.get(
					'delete-x-and-its-historical-training-output-data'
				),
				[singleSelectedItem.name]
			)
		: sub(
				Liferay.Language.get(
					'delete-x-models-and-their-historical-training-output-data'
				),
				[selectedItemsCount]
			);

	const handleSubmit = () => {
		deleteRecommendationJobs({
			variables: {
				jobIds: selectedItems.map(({id}) => id).toArray(),
			},
		})
			.then(() => {
				const successMessage = singleSelectedItem
					? sub(Liferay.Language.get('x-has-been-deleted'), [
							singleSelectedItem.name,
						])
					: sub(Liferay.Language.get('x-models-have-been-deleted'), [
							selectedItemsCount,
						]);

				addAlert({
					alertType: Alert.Types.Success,
					message: successMessage as string,
				});

				selectionDispatch?.({type: ACTION_TYPES.clearAll});

				refetch?.();

				history.push(
					setUriQueryValues(
						{
							field: NAME,
							keywords: '',
							page: defaultPage,
							sortOrder: OrderByDirections.Descending,
						},
						toRoute(Routes.SETTINGS_RECOMMENDATIONS, {
							groupId,
						})
					)
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
	};

	const renderNav = () => {
		if (!currentUser.isAdmin()) {
			return null;
		}

		if (selectedItemsCount) {
			return (
				<Nav>
					<Nav.Item>
						{
							<ClayButton
								borderless
								className="button-root"
								displayType="secondary"
								onClick={() => {
									open(modalTypes.CONFIRMATION_MODAL, {
										message: (
											<div>
												<div className="h4 text-secondary">
													{confirmationMessage}
												</div>

												<p>
													{singleSelectedItem
														? Liferay.Language.get(
																'components-using-this-model-will-need-to-be-reconfigured'
															)
														: Liferay.Language.get(
																'components-using-these-models-will-need-to-be-reconfigured'
															)}
												</p>
											</div>
										),
										modalVariant: 'modal-warning',
										onClose: close,
										onSubmit: handleSubmit,
										submitButtonDisplay: 'warning',
										submitMessage:
											Liferay.Language.get('delete'),
										title: sub(
											Liferay.Language.get('deleting-x'),
											[
												singleSelectedItem
													? singleSelectedItem.name
													: sub(
															Liferay.Language.get(
																'x-models'
															),
															[selectedItemsCount]
														),
											]
										),
										titleIcon: 'warning-full',
									});
								}}
								outline
							>
								{Liferay.Language.get('delete')}
							</ClayButton>
						}
					</Nav.Item>
				</Nav>
			);
		}

		return (
			<Nav>
				<Nav.Item>
					{
						<ClayLink
							button
							className="nav-btn"
							href={toRoute(
								Routes.SETTINGS_RECOMMENDATIONS_CREATE_ITEM_SIMILARITY_MODEL,
								{groupId}
							)}
						>
							{Liferay.Language.get('new-model')}
						</ClayLink>
					}
				</Nav.Item>
			</Nav>
		);
	};

	return (
		<Card className="recommendations-list-root" pageDisplay>
			<CrossPageSelect
				columns={[
					{
						accessor: 'name',
						cellRenderer: NameCell,
						cellRendererProps: {
							routeFn: ({data: {id}}: {data: {id: string}}) =>
								toRoute(
									Routes.SETTINGS_RECOMMENDATION_MODEL_VIEW,
									{
										groupId,
										jobId: id,
									}
								),
						},
						className: 'table-cell-expand',
						label: Liferay.Language.get('name'),
					},
					{
						accessor: 'type',
						dataFormatter: (type: JobTypes) =>
							JOB_TYPES_LABEL_MAP[type],
						label: Liferay.Language.get('training-model'),
					},
					{
						accessor: 'runDataPeriod',
						dataFormatter: (type: JobRunDataPeriods) =>
							JOB_RUN_DATA_PERIODS_LABEL_MAP[type],
						label: Liferay.Language.get('training-period'),
					},
					{
						accessor: 'runFrequency',
						dataFormatter: (type: JobRunFrequencies) =>
							JOB_RUN_FREQUENCIES_LABEL_MAP[type],
						label: Liferay.Language.get('training-frequency'),
					},
					{
						accessor: 'runDate',
						dataFormatter: (date: string) =>
							formatDateToTimeZone(
								date,
								getCustomDateFormat(),
								timeZoneId
							),
						label: Liferay.Language.get('last-trained'),
					},
					{
						accessor: 'status',
						cellRenderer: ({
							className,
							data: {status},
						}: {
							className: string;
							data: {status: JobStatuses};
						}) => (
							<td className={className}>
								<Label
									className="status"
									display={JOB_STATUSES_DISPLAY_MAP[status]}
									size="lg"
									uppercase
								>
									{JOB_STATUSES_LABEL_MAP[status]}
								</Label>
							</td>
						),
						label: Liferay.Language.get('status'),
					},
				]}
				delta={delta}
				emptyTitle={getFormattedTitle(
					Liferay.Language.get('recommendations').toLowerCase()
				)}
				entityLabel={Liferay.Language.get('recommendations')}
				error={error}
				items={get(data, ['jobs', 'jobs'], [])}
				loading={loading}
				orderIOMap={orderIOMap}
				page={page}
				primary
				query={query}
				refetch={refetch}
				renderNav={renderNav}
				rowIdentifier="id"
				total={get(data, ['jobs', 'total'], 0)}
			/>
		</Card>
	);
};

export default compose<React.ComponentType<any>>(
	withSelectionProvider,
	connector
)(RecommendationList);
