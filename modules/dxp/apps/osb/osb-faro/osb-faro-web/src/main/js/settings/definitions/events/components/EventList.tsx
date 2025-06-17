import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import EventDefinitionsQuery, {
	EventDefinitionsData,
	EventDefinitionsVariables,
	HideEventDefinitions,
	HideEventDefinitionsData,
	HideEventDefinitionsVariables,
	UnhideEventDefinitions,
	UnhideEventDefinitionsData
} from 'event-analysis/queries/EventDefinitionsQuery';
import Nav from 'shared/components/Nav';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import RowActions from 'shared/components/RowActions';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {
	createOrderIOMap,
	getSortFromOrderIOMap,
	NAME
} from 'shared/util/pagination';
import {Event, EventTypes} from 'event-analysis/utils/types';
import {eventListColumns} from 'shared/util/table-columns';
import {get} from 'lodash';
import {OrderedMap} from 'immutable';
import {Sizes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useMutation, useQuery} from '@apollo/react-hooks';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {
	useSelectionContext,
	withSelectionProvider
} from 'shared/context/selection';

const connector = connect(null, {addAlert, close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IEventListProps extends PropsFromRedux {
	groupId: string;
}

const EventList: React.FC<IEventListProps> = ({
	addAlert,
	close,
	groupId,
	open
}) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	const {data, error, loading, refetch} = useQuery<
		EventDefinitionsData,
		EventDefinitionsVariables
	>(EventDefinitionsQuery, {
		fetchPolicy: 'network-only',
		variables: {
			eventType: EventTypes.Default,
			keyword: query,
			page: page - 1,
			size: delta,
			sort: getSortFromOrderIOMap(orderIOMap)
		}
	});

	const [hideEventDefinitions] = useMutation<
		HideEventDefinitionsData,
		HideEventDefinitionsVariables
	>(HideEventDefinitions, {
		onCompleted: ({
			hideEventDefinitions
		}: {
			hideEventDefinitions: Event[];
		}) => {
			if (!selectedItems.isEmpty()) {
				selectionDispatch({
					payload: {
						items: hideEventDefinitions
					},
					type: 'add'
				});
			}
		}
	});

	const [unhideEventDefinitions] = useMutation<
		UnhideEventDefinitionsData,
		HideEventDefinitionsVariables
	>(UnhideEventDefinitions, {
		onCompleted: ({
			unhideEventDefinitions
		}: {
			unhideEventDefinitions: Event[];
		}) => {
			if (!selectedItems.isEmpty()) {
				selectionDispatch({
					payload: {
						items: unhideEventDefinitions
					},
					type: 'add'
				});
			}
		}
	});

	const currentUser = useCurrentUser();

	const handleHideEvents = (events: Event[] = []) => {
		const visibleEvents = events.filter(({hidden}) => !hidden);

		const visibleEventsCount = visibleEvents.length;

		open(modalTypes.CONFIRMATION_MODAL, {
			message: (
				<p className='text-secondary'>
					{Liferay.Language.get(
						'hiding-events-in-the-interface-may-require-reconfiguration-of-segments-and-other-analysis-using-this-event.-hidden-events-will-be-available-for-calculating-metrics'
					)}
				</p>
			),
			modalVariant: 'modal-warning',
			onClose: close,
			onSubmit: () => {
				hideEventDefinitions({
					variables: {
						eventDefinitionIds: events.map(({id}) => id)
					}
				})
					.then(() => {
						addAlert({
							alertType: Alert.Types.Success,
							message:
								visibleEventsCount > 1
									? sub(
											Liferay.Language.get(
												'x-events-have-been-set-to-hide'
											),
											[visibleEventsCount]
									  )
									: sub(
											Liferay.Language.get(
												'x-set-to-hide'
											),
											[visibleEvents[0].displayName]
									  )
						});
					})
					.catch(() =>
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get(
								'there-was-an-error-processing-your-request.-please-try-again'
							),
							timeout: false
						})
					);
			},
			submitButtonDisplay: 'warning',
			submitMessage: Liferay.Language.get('hide'),
			title:
				visibleEventsCount > 1
					? Liferay.Language.get('hide-events')
					: sub(Liferay.Language.get('hide-x'), [
							visibleEvents[0].displayName
					  ]),
			titleIcon: 'warning'
		});
	};

	const handleUnhideEvents = (events: Event[] = []) => {
		const hiddenEvents = events.filter(({hidden}) => hidden);

		const hiddenEventsCount = hiddenEvents.length;

		unhideEventDefinitions({
			variables: {
				eventDefinitionIds: events.map(({id}) => id)
			}
		})
			.then(() => {
				addAlert({
					alertType: Alert.Types.Success,
					message:
						hiddenEventsCount > 1
							? sub(
									Liferay.Language.get(
										'x-events-have-been-set-to-show'
									),
									[hiddenEventsCount]
							  )
							: sub(
									Liferay.Language.get(
										'x-has-been-set-to-show'
									),
									[hiddenEvents[0].displayName]
							  )
				});
			})
			.catch(() =>
				addAlert({
					alertType: Alert.Types.Error,
					message: Liferay.Language.get(
						'there-was-an-error-processing-your-request.-please-try-again'
					),
					timeout: false
				})
			);
	};

	const authorized = currentUser.isAdmin();

	const renderRowActions = ({data}: {data: Event}) => {
		const {hidden} = data;

		if (authorized && !selectedItems.size) {
			return (
				<RowActions
					quickActions={[
						{
							iconSymbol: hidden ? 'view' : 'ac_hidden',
							label: hidden
								? Liferay.Language.get('set-to-show')
								: Liferay.Language.get('set-to-hide'),
							onClick: () => {
								const hideEventFn = hidden
									? handleUnhideEvents
									: handleHideEvents;

								hideEventFn([data]);
							}
						}
					]}
				/>
			);
		}
	};

	const hasUnhiddenEvent = (events: OrderedMap<string, Event>) =>
		events.some(({hidden}) => !hidden);

	return (
		<CrossPageSelect
			columns={[
				eventListColumns.getName({groupId}),
				eventListColumns.displayName,
				eventListColumns.description,
				eventListColumns.hidden
			]}
			delta={delta}
			emptyTitle={Liferay.Language.get('there-are-no-events-found')}
			error={error}
			items={get(data, ['eventDefinitions', 'eventDefinitions'], [])}
			loading={loading}
			noResultsRenderer={
				<NoResultsDisplay
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite'
					}}
				/>
			}
			orderIOMap={orderIOMap}
			page={page}
			query={query}
			refetch={refetch}
			renderNav={
				authorized && selectedItems.size
					? () => (
							<Nav>
								<Nav.Item>
									<ClayButton
										borderless
										className='button-root nav-btn'
										displayType='secondary'
										onClick={() => {
											const hideEventFn = hasUnhiddenEvent(
												selectedItems
											)
												? handleHideEvents
												: handleUnhideEvents;

											hideEventFn(
												selectedItems.toArray()
											);
										}}
									>
										<ClayIcon
											className='mr-2'
											symbol={
												hasUnhiddenEvent(selectedItems)
													? 'ac_hidden'
													: 'view'
											}
										/>

										{hasUnhiddenEvent(selectedItems)
											? Liferay.Language.get('hide')
											: Liferay.Language.get('show')}
									</ClayButton>
								</Nav.Item>
							</Nav>
					  )
					: null
			}
			renderRowActions={renderRowActions}
			rowIdentifier='id'
			showCheckbox={authorized}
			showDropdownRangeKey={false}
			total={get(data, ['eventDefinitions', 'total'], 0)}
		/>
	);
};

export default compose<any>(withSelectionProvider, connector)(EventList);
