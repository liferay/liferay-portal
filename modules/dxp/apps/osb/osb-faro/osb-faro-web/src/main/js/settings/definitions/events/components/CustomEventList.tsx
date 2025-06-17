import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
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
import NotificationAlertList, {
	useNotificationsAPI
} from 'shared/components/NotificationAlertList';
import React from 'react';
import RowActions from 'shared/components/RowActions';
import URLConstants from 'shared/util/url-constants';
import {addAlert, removeAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {
	BlockCustomEventDefinitions,
	BlockCustomEventDefinitionsData,
	BlockCustomEventDefinitionsVariables
} from 'event-analysis/queries/CustomEventDefinitions';
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
import {LIMIT_REACHED_ALERT_ID} from './constants';
import {NotificationSubtypes} from 'shared/util/records/Notification';
import {OrderedMap} from 'immutable';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useMutation, useQuery} from '@apollo/react-hooks';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {
	useSelectionContext,
	withSelectionProvider
} from 'shared/context/selection';

const connector = connect(null, {addAlert, close, open, removeAlert});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface ICustomEventListProps extends PropsFromRedux {
	groupId: string;
	history: {push: (url: string) => void};
}

const CustomEventList: React.FC<ICustomEventListProps> = ({
	addAlert,
	close,
	groupId,
	history,
	open,
	removeAlert
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
			blocked: false,
			eventType: EventTypes.Custom,
			keyword: query,
			page: page - 1,
			size: delta,
			sort: getSortFromOrderIOMap(orderIOMap)
		}
	});

	const [blockCustomEventDefinitions] = useMutation<
		BlockCustomEventDefinitionsData,
		BlockCustomEventDefinitionsVariables
	>(BlockCustomEventDefinitions);

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

	const notificationResponse = useNotificationsAPI(groupId);

	const currentUser = useCurrentUser();

	const handleBlockEvents = (events: Event[] = []) => {
		const eventsCount = events.length;

		open(modalTypes.CONFIRMATION_MODAL, {
			message: (
				<p className='text-secondary'>
					{eventsCount > 1
						? Liferay.Language.get(
								'blocking-events-will-result-in-the-deletion-of-their-display-names-and-descriptions.-you-must-reassign-these-values-if-you-wish-to-unblock-these-events-in-the-future'
						  )
						: sub(
								Liferay.Language.get(
									'blocking-x-will-result-in-the-deletion-of-its-display-name-and-description.-you-must-reassign-these-values-if-you-wish-to-unblock-the-event-in-the-future'
								),
								[events[0].displayName]
						  )}
				</p>
			),
			modalVariant: 'modal-warning',
			onClose: close,
			onSubmit: () => {
				blockCustomEventDefinitions({
					variables: {
						eventDefinitionIds: events.map(({id}) => id)
					}
				})
					.then(() => {
						selectionDispatch({
							type: 'clear-all'
						});

						const updatedPage = eventsCount > 1 ? 1 : page;

						if (updatedPage !== page) {
							const orderParams = orderIOMap.first();

							const {field, sortOrder} = orderParams;

							history.push(
								setUriQueryValues(
									{
										field,
										page: updatedPage,
										sortOrder
									},
									toRoute(
										Routes.SETTINGS_DEFINITIONS_EVENTS_CUSTOM,
										{
											groupId
										}
									)
								)
							);
						} else {
							refetch();
						}

						addAlert({
							alertType: Alert.Types.Success,
							message:
								eventsCount > 1
									? sub(
											Liferay.Language.get(
												'x-events-have-been-added-to-the-block-list'
											),
											[eventsCount]
									  )
									: sub(
											Liferay.Language.get(
												'x-has-been-added-to-the-block-list'
											),
											[events[0].displayName]
									  )
						});

						removeAlert(LIMIT_REACHED_ALERT_ID);

						notificationResponse.refetch();
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
			submitMessage: Liferay.Language.get('block'),
			title:
				eventsCount > 1
					? Liferay.Language.get('block-events')
					: Liferay.Language.get('block-event'),
			titleIcon: 'warning'
		});
	};

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
												'x-has-been-set-to-hide'
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

	const renderRowActions = ({data}: {data: Event}) => {
		const {hidden} = data;

		return (
			<RowActions
				quickActions={[
					{
						iconSymbol: 'ac_block',
						label: Liferay.Language.get('block-event'),
						onClick: () => {
							handleBlockEvents([data]);
						}
					},
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
	};

	const authorized = currentUser.isAdmin();

	const hasUnhiddenEvent = (events: OrderedMap<string, Event>) =>
		events.some(({hidden}) => !hidden);

	return (
		<>
			<div className='mx-4'>
				<NotificationAlertList
					{...notificationResponse}
					groupId={groupId}
					subtypes={[NotificationSubtypes.BlockedEventsLimit]}
				/>
			</div>

			<CrossPageSelect
				columns={[
					eventListColumns.getName({groupId}),
					eventListColumns.displayName,
					eventListColumns.description,
					eventListColumns.hidden
				]}
				delta={delta}
				emptyDescription={Liferay.Language.get(
					'visit-our-documentation-to-learn-how-to-add-custom-events-on-your-site'
				)}
				emptyTitle={Liferay.Language.get('create-some-custom-events')}
				error={error}
				items={get(data, ['eventDefinitions', 'eventDefinitions'], [])}
				loading={loading}
				noResultsRenderer={
					<NoResultsDisplay
						description={
							<>
								{Liferay.Language.get(
									'create-some-custom-events-to-get-started'
								)}

								<ClayLink
									className='d-block mb-3'
									href={
										URLConstants.CustomEventsDocumentation
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'learn-how-to-add-custom-events-on-your-site'
									)}
								</ClayLink>
							</>
						}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_satellite'
						}}
						title={Liferay.Language.get('no-custom-events-found')}
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
												handleBlockEvents(
													selectedItems.toArray()
												);
											}}
										>
											<ClayIcon
												className='icon-root mr-2'
												symbol='ac_block'
											/>

											{Liferay.Language.get(
												'block-events'
											)}
										</ClayButton>

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
												className='icon-root mr-2'
												symbol={
													hasUnhiddenEvent(
														selectedItems
													)
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
				renderRowActions={
					authorized && !selectedItems.size ? renderRowActions : null
				}
				rowIdentifier='id'
				showCheckbox={authorized}
				total={get(data, ['eventDefinitions', 'total'], 0)}
			/>
		</>
	);
};

export default compose<any>(withSelectionProvider, connector)(CustomEventList);
