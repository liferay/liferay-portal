import BLOCKED_CUSTOM_EVENT_DEFINITIONS_QUERY, {
	BlockedCustomEventDefinitionsData,
	BlockedCustomEventDefinitionsVariables,
	HideBlockedCustomEventDefinitions,
	HideBlockedCustomEventDefinitionsData,
	HideBlockedCustomEventDefinitionsVariables,
	UnhideBlockedCustomEventDefinitions,
	UnhideBlockedCustomEventDefinitionsData
} from '../queries/BlockedCustomEventDefinitionsQuery';
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
import {addAlert, removeAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {
	BlockCustomEventDefinitionsData,
	BlockCustomEventDefinitionsVariables,
	UnblockCustomEventDefinitions
} from 'event-analysis/queries/CustomEventDefinitions';
import {BlockedCustomEvent} from 'event-analysis/utils/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {
	createOrderIOMap,
	getSortFromOrderIOMap,
	NAME
} from 'shared/util/pagination';
import {eventListColumns} from 'shared/util/table-columns';
import {get} from 'lodash';
import {LIMIT_REACHED_ALERT_ID} from './constants';
import {OrderedMap} from 'immutable';
import {RootState} from 'shared/store';
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

const EVENT_LIMIT_REACHED = /Processing request will exceed custom event definition limit/;

const connector = connect(
	(store: RootState, {groupId}: {groupId: string}) => ({
		timeZoneId: store.getIn([
			'projects',
			groupId,
			'data',
			'timeZone',
			'timeZoneId'
		])
	}),
	{addAlert, close, open, removeAlert}
);

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IBlockListCardProps extends PropsFromRedux {
	groupId: string;
	history: {push: (url: string) => void};
	timeZoneId: string;
}

const BlockListCard: React.FC<IBlockListCardProps> = ({
	addAlert,
	close,
	groupId,
	history,
	open,
	removeAlert,
	timeZoneId
}) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	const {data, error, loading, refetch} = useQuery<
		BlockedCustomEventDefinitionsData,
		BlockedCustomEventDefinitionsVariables
	>(BLOCKED_CUSTOM_EVENT_DEFINITIONS_QUERY, {
		fetchPolicy: 'network-only',
		variables: {
			keyword: query,
			page: page - 1,
			size: delta,
			sort: getSortFromOrderIOMap(orderIOMap)
		}
	});

	const [unblockCustomEventDefinitions] = useMutation<
		BlockCustomEventDefinitionsData,
		BlockCustomEventDefinitionsVariables
	>(UnblockCustomEventDefinitions);

	const [hideEventDefinitions] = useMutation<
		HideBlockedCustomEventDefinitionsData,
		HideBlockedCustomEventDefinitionsVariables
	>(HideBlockedCustomEventDefinitions, {
		onCompleted: ({
			hideBlockedEventDefinitions
		}: {
			hideBlockedEventDefinitions: BlockedCustomEvent[];
		}) => {
			if (!selectedItems.isEmpty()) {
				selectionDispatch({
					payload: {
						items: hideBlockedEventDefinitions
					},
					type: 'add'
				});
			}
		}
	});

	const [unhideEventDefinitions] = useMutation<
		UnhideBlockedCustomEventDefinitionsData,
		HideBlockedCustomEventDefinitionsVariables
	>(UnhideBlockedCustomEventDefinitions, {
		onCompleted: ({
			unhideBlockedEventDefinitions
		}: {
			unhideBlockedEventDefinitions: BlockedCustomEvent[];
		}) => {
			if (!selectedItems.isEmpty()) {
				selectionDispatch({
					payload: {
						items: unhideBlockedEventDefinitions
					},
					type: 'add'
				});
			}
		}
	});

	const currentUser = useCurrentUser();

	const handleHideEvents = (events: BlockedCustomEvent[] = []) => {
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
											[visibleEvents[0].name]
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
							visibleEvents[0].name
					  ]),
			titleIcon: 'warning'
		});
	};

	const handleUnhideEvents = (events: BlockedCustomEvent[] = []) => {
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
									[hiddenEvents[0].name]
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

	const handleUnblockEvents = (events: BlockedCustomEvent[] = []) => {
		const eventsCount = events.length;

		unblockCustomEventDefinitions({
			variables: {
				eventDefinitionIds: events.map(({id}) => id)
			}
		})
			.then(() => {
				selectionDispatch({
					type: 'clear-all'
				});

				const updatedPage = eventsCount > 1 ? 1 : Number(page);

				if (updatedPage !== Number(page)) {
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
								Routes.SETTINGS_DEFINITIONS_EVENTS_BLOCK_LIST,
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
										'x-events-have-been-returned-to-the-custom-event-list'
									),
									[eventsCount]
							  )
							: sub(
									Liferay.Language.get(
										'x-has-been-returned-to-the-custom-event-list'
									),
									[events[0].name]
							  )
				});

				removeAlert(LIMIT_REACHED_ALERT_ID);
			})
			.catch(err => {
				let message = Liferay.Language.get(
					'there-was-an-error-processing-your-request.-please-try-again'
				);

				if (EVENT_LIMIT_REACHED.test(err.message)) {
					message = sub(
						Liferay.Language.get(
							'your-workspace-is-over-the-event-limit.-please-remove-some-events-from-the-allow-list-to-continue.-visit-our-x-to-learn-more'
						),
						[
							<ClayLink
								href={URLConstants.DocumentationLink}
								key='DOCUMENTATION_LINK'
								target='_blank'
							>
								{Liferay.Language.get('documentation-fragment')}
							</ClayLink>
						],
						false
					);
				}

				addAlert({
					alertType: Alert.Types.Error,
					id: LIMIT_REACHED_ALERT_ID,
					message,
					timeout: false
				});
			});
	};

	const renderRowActions = ({data}: {data: BlockedCustomEvent}) => {
		const {hidden} = data;

		return (
			<RowActions
				quickActions={[
					{
						iconSymbol: 'undo',
						label: Liferay.Language.get('unblock-event'),
						onClick: () => {
							handleUnblockEvents([data]);
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

	const hasUnhiddenEvent = (events: OrderedMap<string, BlockedCustomEvent>) =>
		events.some(({hidden}) => !hidden);

	return (
		<Card pageDisplay>
			<CrossPageSelect
				columns={[
					eventListColumns.name,
					eventListColumns.lastSeenURL,
					eventListColumns.getLastSeenDate(timeZoneId),
					eventListColumns.hidden
				]}
				delta={delta}
				error={error}
				items={get(
					data,
					[
						'blockedCustomEventDefinitions',
						'blockedCustomEventDefinitions'
					],
					[]
				)}
				loading={loading}
				noResultsRenderer={
					<NoResultsDisplay
						description={
							<>
								{Liferay.Language.get(
									'to-block-events,-select-one-from-the-events-table'
								)}

								<ClayLink
									className='d-block mb-3'
									href={
										URLConstants.DefinitionsForEventsDocumentation
									}
									key='DOCUMENTATION'
									target='_blank'
								>
									{Liferay.Language.get(
										'access-our-documentation-to-learn-how-to-manage-custom-events'
									)}
								</ClayLink>
							</>
						}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_satellite'
						}}
						title={Liferay.Language.get(
							'there-are-no-events-blocked'
						)}
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
												handleUnblockEvents(
													selectedItems.toArray()
												);
											}}
										>
											<ClayIcon
												className='icon-root mr-2'
												symbol='undo'
											/>

											{Liferay.Language.get(
												'unblock-events'
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
				total={get(data, ['blockedCustomEventDefinitions', 'total'], 0)}
			/>
		</Card>
	);
};

export default compose<any>(withSelectionProvider, connector)(BlockListCard);
