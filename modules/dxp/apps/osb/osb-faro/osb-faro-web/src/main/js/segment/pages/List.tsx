import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import LinkCell from 'shared/components/table/cell-components/LinkCell';
import Nav from 'shared/components/Nav';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useContext, useEffect, useRef, useState} from 'react';
import RowActions from 'shared/components/RowActions';
import SearchableEntityTable from 'shared/components/SearchableEntityTable';
import ToThousandsCell from 'shared/components/table/cell-components/ToThousandsCell';
import URLConstants from 'shared/util/url-constants';
import UserCell from 'shared/components/table/cell-components/UserCell';
import {
	ActionType,
	UnassignedSegmentsContext
} from 'shared/context/unassignedSegments';
import {
	ActionTypes,
	useSelectionContext,
	withSelectionProvider
} from 'shared/context/selection';
import {addAlert} from 'shared/actions/alerts';
import {Alert, FilterByType} from 'shared/types';
import {ALERT_CONFIG_MAP, AlertTypes} from 'shared/components/Alert';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {createOrderIOMap} from 'shared/util/pagination';
import {
	DATE_MODIFIED,
	INDIVIDUAL_COUNT,
	LAST_MEMBERSHIP_UPDATE_DATE,
	Routes,
	SEGMENT_STATE,
	SEGMENT_TYPE,
	SEGMENTS,
	setUriQueryValue,
	toRoute,
	USER_NAME
} from 'shared/util/router';
import {DateCell} from 'shared/components/table/cell-components';
import {formatDateToTimeZone} from 'shared/util/date';
import {
	getDefaultSortOrder,
	NAME,
	paginationDefaults
} from 'shared/util/pagination';
import {Link} from 'react-router-dom';
import {OrderedMap} from 'immutable';
import {OrderParams} from 'shared/util/records';
import {SegmentStates, SegmentTypes, Sizes} from 'shared/util/constants';
import {setUriQueryValues} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';

export interface FetchSegmentsParams {
	channelId: string;
	delta?: number;
	filterBy: FilterByType;
	groupId: string;
	orderIOMap: OrderedMap<string, OrderParams>;
	page: number;
	query: string;
}

function fetchDisabledSegments(
	channelId: string,
	groupId: string,
	orderIOMap: OrderedMap<string, OrderParams>
): any {
	return API.individualSegment.search({
		channelId,
		delta: 1,
		groupId,
		orderIOMap,
		state: SegmentStates.Disabled
	});
}

const connector = connect(null, {addAlert, close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IListProps extends PropsFromRedux {
	channelId: string;
	groupId: string;
	history: any;
}

const SEGMENT_TYPES_LABEL_MAP = {
	[SegmentTypes.Batch]: Liferay.Language.get('batch'),
	[SegmentTypes.RealTime]: Liferay.Language.get('real-time')
};

const FILTER_BY_OPTIONS = [
	{
		key: SEGMENT_TYPE,
		label: Liferay.Language.get('segment-type'),
		values: [
			{
				label: SEGMENT_TYPES_LABEL_MAP[SegmentTypes.Batch],
				value: SegmentTypes.Batch
			},
			{
				label: SEGMENT_TYPES_LABEL_MAP[SegmentTypes.RealTime],
				value: SegmentTypes.RealTime
			}
		]
	}
];

const ORDER_BY_OPTIONS = [
	{
		label: Liferay.Language.get('name'),
		value: NAME
	},
	{
		label: Liferay.Language.get('type'),
		value: SEGMENT_TYPE
	},
	{
		label: Liferay.Language.get('segment-membership'),
		value: INDIVIDUAL_COUNT
	},
	{
		label: Liferay.Language.get('modified'),
		value: DATE_MODIFIED
	},
	{
		label: Liferay.Language.get('last-modified-by'),
		value: USER_NAME
	},
	{
		label: Liferay.Language.get('last-membership-update'),
		value: LAST_MEMBERSHIP_UPDATE_DATE
	}
];

export const List: React.FC<IListProps> = ({
	addAlert,
	channelId,
	close,
	groupId,
	history,
	open
}) => {
	const currentUser = useCurrentUser();
	const {selectedChannel} = useChannelContext();
	const _tableRef = useRef<HTMLDivElement & SearchableEntityTable>();

	const {selectedItems, selectionDispatch} = useSelectionContext();
	const {delta, filterBy, orderIOMap, page, query} = useQueryPagination({
		filterFields: [SEGMENT_TYPE],
		initialDelta: paginationDefaults.delta,
		initialOrderIOMap: createOrderIOMap(NAME, getDefaultSortOrder(NAME)),
		initialPage: paginationDefaults.page,
		initialQuery: paginationDefaults.query
	});

	const [alerts, setAlerts] = useState([]);

	const _disableSegmentsRequestRef = useRef<Promise<any>>();
	const {
		showUnassignedAlert,
		unassignedSegments,
		unassignedSegmentsDispatch
	} = useContext(UnassignedSegmentsContext);

	useEffect(() => {
		const abortController = new AbortController();

		_disableSegmentsRequestRef.current = getDisabledSegmentsAlert(
			abortController.signal
		);

		return () => {
			abortController.abort();
		};
	}, []);

	const selectedSegmentTypes = filterBy.get(SEGMENT_TYPE)?.toArray() || [];

	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.individualSegment.search,
		variables: {
			channelId,
			delta,
			groupId,
			orderIOMap,
			page,
			query,
			segmentTypes: selectedSegmentTypes.length
				? selectedSegmentTypes
				: undefined
		}
	});

	const getDisabledSegmentsAlert = (abortSignal: AbortSignal) =>
		fetchDisabledSegments(channelId, groupId, orderIOMap).then(
			({total}) => {
				if (abortSignal.aborted) {
					return;
				}
				if (total) {
					setAlerts(() => handleDisabledSegmentsAlert());
				}
			}
		);

	const getAlerts = () =>
		[
			...alerts,
			showUnassignedAlert &&
				unassignedSegments.length &&
				handleUnassignedSegmentsAlert()
		].filter(Boolean);

	const handleDisabledSegmentsAlert = () => [
		{
			message: sub(
				Liferay.Language.get(
					'some-of-your-segments-are-disabled-because-a-data-source-has-been-removed-x'
				),
				[
					<Link
						key='DISABLED_SEGMENTS'
						to={setUriQueryValue(
							window.location.href,
							SEGMENT_STATE,
							SegmentStates.Disabled
						)}
					>
						{Liferay.Language.get('view-disabled-segments')}
					</Link>
				],
				false
			),
			onClose: () => setAlerts(() => []),
			...ALERT_CONFIG_MAP[AlertTypes.Warning]
		}
	];

	const handleUnassignedSegmentsAlert = () => {
		const openModal = () => {
			open(
				modalTypes.UNASSIGNED_SEGMENTS_MODAL,
				{
					groupId,
					onClose: close
				},
				{closeOnBlur: false}
			);
		};

		return {
			message: sub(
				Liferay.Language.get(
					'there-are-existing-segments-that-have-not-been-assigned-to-a-property-x'
				),
				[
					<ClayButton
						className='button-root p-0'
						displayType='unstyled'
						key='UNASSIGNED_SEGMENTS'
						onClick={openModal}
						small
					>
						{Liferay.Language.get('view-unassigned-segments')}
					</ClayButton>
				],
				false
			),
			onClose: () =>
				unassignedSegmentsDispatch({type: ActionType.updateShowAlert}),
			...ALERT_CONFIG_MAP[AlertTypes.Warning]
		};
	};

	const handleDeleteSegments = ({ids, items, name = undefined}) => {
		const isMultiple = ids.length > 1;

		const MODAL_MESSAGES = {
			confirmation: isMultiple
				? Liferay.Language.get(
						'are-you-sure-you-want-to-delete-the-selected-segments'
				  )
				: Liferay.Language.get(
						'are-you-sure-you-want-to-delete-this-segment'
				  ),
			subtitle: isMultiple
				? Liferay.Language.get(
						'you-will-lose-all-data-related-to-these-segments.-you-will-not-be-able-to-undo-this-operation'
				  )
				: Liferay.Language.get(
						'you-will-lose-all-data-related-to-this-segment.-you-will-not-be-able-to-undo-this-operation'
				  ),
			title: isMultiple
				? Liferay.Language.get('delete-segments')
				: sub(Liferay.Language.get('deleting-x'), [name])
		};

		open(modalTypes.CONFIRMATION_MODAL, {
			message: (
				<div>
					<div className='h4 text-secondary'>
						{MODAL_MESSAGES.confirmation}
					</div>

					<p>{MODAL_MESSAGES.subtitle}</p>
				</div>
			),
			modalVariant: 'modal-warning',
			onClose: close,
			onSubmit: () =>
				API.individualSegment
					.delete({
						groupId,
						ids
					})
					.then(() => {
						_tableRef?.current?.reload();

						addAlert({
							alertType: Alert.Types.Success,
							message: Liferay.Language.get(
								'the-segment-has-been-deleted'
							)
						});

						if (items.length === 1 && page !== 1) {
							history.push(
								setUriQueryValue(
									window.location.href,
									'page',
									Number(page) - 1
								)
							);
						}
						selectionDispatch({type: ActionTypes.ClearAll});

						refetch();
					})
					.catch(() => {
						addAlert({
							alertType: Alert.Types.Error,
							message: Liferay.Language.get('error')
						});
					}),
			submitButtonDisplay: 'warning',
			submitMessage: Liferay.Language.get('delete'),
			title: MODAL_MESSAGES.title,
			titleIcon: 'warning-full'
		});
	};
	const renderRowActions = ({data: {id, name}, items}) => {
		const commonActions = [
			{
				href: toRoute(Routes.CONTACTS_SEGMENT, {
					channelId,
					groupId,
					id,
					type: SEGMENTS
				}),
				iconSymbol: 'view',
				label: Liferay.Language.get('view')
			},
			{
				href: toRoute(Routes.CONTACTS_SEGMENT_EDIT, {
					channelId,
					groupId,
					id,
					type: SEGMENTS
				}),
				iconSymbol: 'pencil',
				label: Liferay.Language.get('edit')
			},
			{
				className: 'text-danger',
				iconSymbol: 'trash',
				label: Liferay.Language.get('delete'),
				onClick: () => handleDeleteSegments({ids: [id], items, name})
			}
		];

		const actions = commonActions.map(
			({className, href, iconSymbol, label, onClick}) => ({
				className,
				href,
				iconSymbol,
				label,
				onClick
			})
		);
		return <RowActions actions={actions} quickActions={commonActions} />;
	};

	const pageActions = [
		{
			href: setUriQueryValues(
				{type: SegmentTypes.Batch},
				toRoute(Routes.CONTACTS_SEGMENT_CREATE, {
					channelId,
					groupId
				})
			),
			label: Liferay.Language.get('batch-segment')
		},
		{
			href: setUriQueryValues(
				{type: SegmentTypes.RealTime},
				toRoute(Routes.CONTACTS_SEGMENT_CREATE, {
					channelId,
					groupId
				})
			),
			label: Liferay.Language.get('real-time-segment')
		}
	];

	const pageActionsLabel = Liferay.Language.get('new-segment');

	const renderNav = () => {
		if (selectedItems.isEmpty()) {
			return (
				<Nav>
					<Nav.Item>
						<ClayDropDown
							alignmentPosition={Align.BottomRight}
							trigger={
								<ClayButton
									aria-label={
										pageActionsLabel &&
										Liferay.Language.get('menu')
									}
									className='button-root nav-btn p-2'
									disabled={error || loading}
									displayType='primary'
								>
									<>
										<span>{pageActionsLabel}</span>
										<ClayIcon
											className='icon-root ml-2'
											symbol='caret-bottom'
										/>
									</>
								</ClayButton>
							}
						>
							{pageActions.map(({label, ...props}) => (
								<ClayDropDown.Item key={label} {...props}>
									{label}
								</ClayDropDown.Item>
							))}
						</ClayDropDown>
					</Nav.Item>
				</Nav>
			);
		}
		return (
			<Nav>
				<ClayButton
					aria-label={Liferay.Language.get('delete')}
					borderless
					className='button-root text-danger'
					displayType='primary'
					onClick={() => {
						handleDeleteSegments({
							ids: selectedItems.map(item => item.id).toArray(),
							items: selectedItems.toArray(),
							name:
								selectedItems.size === 1
									? selectedItems.first().name
									: undefined
						});
					}}
					outline
				>
					<ClayIcon className='icon-root mr-2' symbol='trash' />
					<span>{Liferay.Language.get('delete')}</span>
				</ClayButton>
			</Nav>
		);
	};

	return (
		<BasePage
			className='segment-list-root'
			documentTitle={Liferay.Language.get('segments')}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel && selectedChannel.name
					})
				]}
				groupId={groupId}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection
						title={Liferay.Language.get('segments')}
					/>
					<BasePage.Header.Section>
						<BasePage.Header.PageActions
							disabled={error || loading}
							label={pageActionsLabel}
						/>
					</BasePage.Header.Section>
				</BasePage.Row>
			</BasePage.Header>
			<BasePage.Body>
				<Card pageDisplay>
					<Card.Body noPadding>
						<CrossPageSelect
							alerts={getAlerts()}
							className='segment-list-root'
							columns={[
								{
									accessor: 'name',
									cellRenderer: LinkCell,
									cellRendererProps: {
										hrefFormatter: data =>
											toRoute(Routes.CONTACTS_SEGMENT, {
												channelId,
												groupId,
												id: data.id,
												type: SEGMENTS
											})
									},
									className: 'table-cell-expand',
									label: Liferay.Language.get('segment-name'),
									title: true
								},
								{
									accessor: 'segmentType',
									cellRenderer: item => {
										const segmentTypeMap = {
											BATCH: Liferay.Language.get(
												'batch'
											),
											REAL_TIME: Liferay.Language.get(
												'real-time'
											)
										};

										return (
											<td>
												{
													segmentTypeMap[
														item.data.segmentType
													]
												}
											</td>
										);
									},
									label: Liferay.Language.get('type')
								},
								{
									accessor: 'individualCount',
									cellRenderer: ToThousandsCell,
									label: Liferay.Language.get(
										'segment-membership'
									)
								},
								{
									accessor: 'lastMembershipUpdateDate',
									cellRenderer: DateCell,
									cellRendererProps: {
										dateFormatter: date =>
											formatDateToTimeZone(date, 'lll'),
										datePath: 'lastMembershipUpdateDate'
									},
									className: 'table-column-text-start',
									label: Liferay.Language.get(
										'last-membership-update'
									)
								},
								{
									accessor: 'userName',
									cellRenderer: UserCell,
									label: Liferay.Language.get(
										'last-modified-by'
									)
								},
								{
									accessor: 'dateModified',
									cellRenderer: DateCell,
									cellRendererProps: {
										dateFormatter: date =>
											formatDateToTimeZone(date, 'll'),
										datePath: 'dateModified'
									},
									className: 'table-column-text-start',
									label: Liferay.Language.get('modified-date')
								}
							]}
							currentUser={currentUser}
							delta={delta}
							filterBy={filterBy}
							filterByOptions={FILTER_BY_OPTIONS}
							items={data?.items}
							loading={loading}
							noResultsRenderer={
								<NoResultsDisplay
									description={
										<>
											{Liferay.Language.get(
												'create-a-segment-to-get-started'
											)}

											<ClayLink
												className='d-block mb-3'
												href={
													URLConstants.SegmentsDocumentationLink
												}
												key='DOCUMENTATION'
												target='_blank'
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
										symbol: 'ac_satellite'
									}}
									title={Liferay.Language.get(
										'there-are-no-segments-found'
									)}
								/>
							}
							orderByOptions={ORDER_BY_OPTIONS}
							orderIOMap={orderIOMap}
							page={page}
							query={query}
							renderInlineRowActions={renderRowActions}
							renderNav={renderNav}
							total={data?.total}
						/>
					</Card.Body>
				</Card>
			</BasePage.Body>
		</BasePage>
	);
};
export default compose(connector, withSelectionProvider)(List);
