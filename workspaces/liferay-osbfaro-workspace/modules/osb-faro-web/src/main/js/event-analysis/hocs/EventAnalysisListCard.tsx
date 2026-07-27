import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import Nav from 'shared/components/Nav';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import RowActions from 'shared/components/RowActions';
import URLConstants from 'shared/util/url-constants';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {
	CREATED_BY_USER_NAME,
	createOrderIOMap,
	getGraphQLVariablesFromPagination,
	NAME,
} from 'shared/util/pagination';
import {CreatedByCell} from 'shared/components/table/cell-components';
import {
	DeleteEventAnalysisData,
	DeleteEventAnalysisMutation,
	DeleteEventAnalysisVariables,
	EventAnalysisListData,
	EventAnalysisListQuery,
	EventAnalysisListVariables,
} from '../queries/EventAnalysisQuery';
import {getPluralMessage} from 'shared/util/lang';
import {mapListResultsToProps} from 'shared/util/mappers';
import {NameCell} from 'shared/components/table/cell-components';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {useMutation, useQuery} from '@apollo/client';
import {useParams} from 'react-router-dom';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {
	useSelectionContext,
	withSelectionProvider,
} from 'shared/context/selection';

const connector = connect(null, {addAlert, close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface EventAnalysisListCardProps extends PropsFromRedux {
	onItemsChange: () => void;
}

const EventAnalysisListCard: React.FC<EventAnalysisListCardProps> = ({
	addAlert,
	close,
	onItemsChange,
	open,
}) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();
	const rangeSelectors = useQueryRangeSelectors();

	const paginationVariables = getGraphQLVariablesFromPagination({
		delta,
		orderIOMap,
		page,
		query,
	});

	const {keywords, size, sort} = {
		keywords: paginationVariables.keywords ?? '',
		size: paginationVariables.size,
		sort: paginationVariables.sort ?? {
			column: NAME,
			type: 'ASCENDING',
		},
	};

	const response = useQuery<
		EventAnalysisListData,
		EventAnalysisListVariables
	>(EventAnalysisListQuery, {
		fetchPolicy: 'network-only',
		variables: {
			channelId,
			keywords,
			page: page - 1,
			size,
			sort,
		},
	});

	const [deleteEventAnalysis] = useMutation<
		DeleteEventAnalysisData,
		DeleteEventAnalysisVariables
	>(DeleteEventAnalysisMutation);

	const handleDeleteEventAnalysis = (eventAnalysisIds: Array<string>) => {
		const {refetch} = response;

		const message = (
			<div>
				<div className="h4 text-secondary">
					{getPluralMessage(
						Liferay.Language.get(
							'are-you-sure-you-want-to-delete-this-analysis'
						),
						Liferay.Language.get(
							'are-you-sure-you-want-to-delete-these-analyses'
						),
						eventAnalysisIds.length
					)}
				</div>

				<p>
					{getPluralMessage(
						Liferay.Language.get(
							'you-will-lose-all-data-related-to-this-analysis.-you-will-not-be-able-to-undo-this-operation'
						),
						Liferay.Language.get(
							'you-will-lose-all-data-related-to-these-analyses.-you-will-not-be-able-to-undo-this-operation'
						),
						eventAnalysisIds.length
					)}
				</p>
			</div>
		);

		const onSubmit = () => {
			deleteEventAnalysis({
				variables: {
					eventAnalysisIds,
				},
			})
				.then(() => {
					addAlert({
						alertType: Alert.Types.Success,
						message: Liferay.Language.get(
							'the-analysis-has-been-deleted'
						),
					});

					selectionDispatch?.({
						type: 'clear-all',
					});

					if (refetch) {
						refetch();
					}

					onItemsChange();
				})
				.catch(() => {
					addAlert({
						alertType: Alert.Types.Error,
						message: Liferay.Language.get('error'),
						timeout: false,
					});
				});
		};

		open(modalTypes.CONFIRMATION_MODAL, {
			message,
			modalVariant: 'modal-warning',
			onClose: close,
			onSubmit,
			submitButtonDisplay: 'warning',
			submitMessage: Liferay.Language.get('delete'),
			title: Liferay.Language.get('deleting-analysis'),
			titleIcon: 'warning-full',
		});
	};

	const renderNav = () => {
		if (!selectedItems.isEmpty()) {
			const eventAnalysisIds = selectedItems.keySeq().toArray();

			return (
				<Nav>
					<ClayButton
						aria-label={Liferay.Language.get('delete')}
						borderless
						className="button-root"
						displayType="secondary"
						onClick={() =>
							handleDeleteEventAnalysis(eventAnalysisIds)
						}
						outline
					>
						<ClayIcon className="icon-root" symbol="trash" />
					</ClayButton>
				</Nav>
			);
		}
	};

	const renderRowActions = ({data: {id}}: {data: {id: string}}) => {
		if (selectedItems.isEmpty()) {
			return (
				<RowActions
					quickActions={[
						{
							iconSymbol: 'trash',
							label: Liferay.Language.get('delete'),
							onClick: () => handleDeleteEventAnalysis([id]),
						},
					]}
				/>
			);
		}
	};

	return (
		<Card className="event-analysis-list-root" pageDisplay>
			<CrossPageSelect
				{...mapListResultsToProps(response, (result: any) => ({
					items: result.eventAnalyses.eventAnalyses,
					total: result.eventAnalyses.total,
				}))}
				columns={[
					{
						accessor: NAME,
						cellRenderer: NameCell,
						cellRendererProps: {
							routeFn: ({data: {id}}: {data: {id: string}}) =>
								toRoute(Routes.EVENT_ANALYSIS_EDIT, {
									channelId,
									groupId,
									id,
								}),
						},
						className: 'table-cell-expand',
						label: Liferay.Language.get('name'),
					},
					{
						accessor: CREATED_BY_USER_NAME,
						cellRenderer: CreatedByCell,
						label: Liferay.Language.get('created-by'),
					},
				]}
				delta={delta}
				entityLabel={Liferay.Language.get('event-analysis')}
				legacyDropdownRangeKey={false}
				noResultsRenderer={
					<NoResultsDisplay
						description={
							<>
								{Liferay.Language.get(
									'create-an-analysis-to-get-started'
								)}

								<ClayLink
									className="d-block"
									href={
										URLConstants.EventAnalysisDocumentationLink
									}
									key="DOCUMENTATION"
									target="_blank"
								>
									{Liferay.Language.get(
										'access-our-documentation-to-learn-more'
									)}
								</ClayLink>
							</>
						}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_satellite',
						}}
						title={Liferay.Language.get('no-analyses-were-found')}
					/>
				}
				orderByOptions={[
					{
						label: Liferay.Language.get('name'),
						value: NAME,
					},
					{
						label: Liferay.Language.get('created-by'),
						value: CREATED_BY_USER_NAME,
					},
				]}
				orderIOMap={orderIOMap}
				page={page}
				query={query}
				rangeSelectors={rangeSelectors}
				renderNav={renderNav}
				renderRowActions={renderRowActions}
				rowIdentifier="id"
				showCheckbox
				showFilterAndOrder
			/>
		</Card>
	);
};

export default compose<React.ComponentType<any>>(
	withSelectionProvider,
	connector
)(EventAnalysisListCard);
