import * as API from 'shared/api';
import BasePage from 'shared/components/base-page';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import Loading from 'shared/components/Loading';
import Nav from 'shared/components/Nav';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import URLConstants from 'shared/util/url-constants';
import {
	ACTION_TYPES,
	useSelectionContext,
	withSelectionProvider
} from 'shared/context/selection';
import {
	ACTIVITIES_COUNT,
	createOrderIOMap,
	JOB_TITLE,
	LAST_ACTIVITY_DATE,
	NAME
} from 'shared/util/pagination';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {EntityTypes, SegmentTypes, Sizes} from 'shared/util/constants';
import {individualsListColumns} from 'shared/util/table-columns';
import {isNil} from 'lodash';
import {List} from 'immutable';
import {OrderByDirections} from 'shared/util/constants';
import {Routes, toRoute} from 'shared/util/router';
import {Segment, User} from 'shared/util/records';
import {sub} from 'shared/util/lang';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSource} from 'shared/hooks/useDataSource';
import {useParams} from 'react-router-dom';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const connector = connect(null, {addAlert, close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IKnownIndividualsProps
	extends React.HTMLAttributes<HTMLDivElement>,
		PropsFromRedux {
	currentUser: User;
}

const KnownIndividuals: React.FC<IKnownIndividualsProps> = ({
	addAlert,
	close,
	open
}) => {
	const {channelId, groupId} = useParams();
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME)
	});

	const {data: dataSourceData, loading: dataSourceLoading} = useRequest({
		dataSourceFn: API.dataSource.search,
		variables: {
			delta: 1,
			groupId
		}
	});

	const dataSourceStates = useDataSource();

	const currentUser = useCurrentUser();

	const authorized = currentUser.isAdmin();

	const {timeZoneId} = useTimeZone();

	const addToSegment = (
		selectedSegmentsList: List<Segment>,
		idsArray: string[]
	) => {
		const selectedSegmentId = selectedSegmentsList[0].id;

		return API.individualSegment
			.addIndividuals({
				groupId,
				individualIds: idsArray,
				selectedSegmentId
			})
			.then(() => {
				addAlert({
					alertType: Alert.Types.Success,
					message: sub(
						Liferay.Language.get(
							'x-individuals-have-been-added-to-this-static-segment'
						),
						[idsArray.length]
					)
				});

				selectionDispatch({type: ACTION_TYPES.clearAll});
			})
			.catch((error: Error) => {
				addAlert({
					alertType: Alert.Types.Error,
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					)
				});

				return error;
			});
	};

	const getStaticIndividualSegments = ({delta, orderIOMap, page, query}) =>
		API.individualSegment.search({
			channelId,
			delta,
			groupId,
			orderIOMap,
			page,
			query,
			segmentType: SegmentTypes.Static
		});

	const handleAddIndividualsToSegmentModal = (idsArray: string[]) => () =>
		open(modalTypes.SELECT_ITEMS_MODAL, {
			countLabel: Liferay.Language.get('x-segments'),
			dataSourceFn: getStaticIndividualSegments,
			entityType: EntityTypes.IndividualsSegment,
			groupId,
			initialOrderIOMap: createOrderIOMap(
				NAME,
				OrderByDirections.Ascending
			),
			noResultsIcon: 'ac_segment',
			noResultsName: Liferay.Language.get('static-segments'),
			onClose: close,
			onSubmit: (selectedSegmentsList: List<Segment>) =>
				addToSegment(selectedSegmentsList, idsArray),
			selectMultiple: false,
			submitMessage: Liferay.Language.get('add'),
			title: Liferay.Language.get('add-to-static-segment')
		});

	const renderNav = () => {
		if (dataSourceData?.total > 0 && !selectedItems.isEmpty()) {
			return (
				<Nav>
					<Nav.Item key='PRIMARY_ACTION'>
						<ClayButton
							className='button-root nav-btn'
							displayType='primary'
							onClick={handleAddIndividualsToSegmentModal(
								selectedItems.keySeq().toArray()
							)}
						>
							{Liferay.Language.get('add-to-static-segment')}
						</ClayButton>
					</Nav.Item>
				</Nav>
			);
		}
	};

	const renderNoResults = () => {
		const createDataSourceButton = (
			<ClayLink
				button
				className='button-root'
				displayType='primary'
				href={toRoute(Routes.SETTINGS_ADD_DATA_SOURCE, {
					groupId
				})}
			>
				{Liferay.Language.get('connect-data-source')}
			</ClayLink>
		);

		if (dataSourceLoading || isNil(dataSourceData?.total)) {
			return (
				<NoResultsDisplay>
					<Loading key='LOADING' />
				</NoResultsDisplay>
			);
		} else if (dataSourceData?.total === 0) {
			return (
				<NoResultsDisplay
					description={
						authorized
							? Liferay.Language.get(
									'connect-a-data-source-with-people-data'
							  )
							: Liferay.Language.get(
									'please-contact-your-site-administrator-to-add-people-data-sources'
							  )
					}
					primary
					title={Liferay.Language.get('no-data-sources-connected')}
				>
					{authorized && createDataSourceButton}
				</NoResultsDisplay>
			);
		} else {
			return (
				<NoResultsDisplay
					description={
						<>
							{authorized
								? Liferay.Language.get(
										'connect-a-data-source-with-people-data'
								  )
								: Liferay.Language.get(
										'please-contact-your-site-administrator-to-add-people-data-sources'
								  )}

							<ClayLink
								className='d-block mb-3'
								href={URLConstants.DataSourceConnection}
								key='DOCUMENTATION'
								target='_blank'
							>
								{Liferay.Language.get(
									'access-our-documentation-to-learn-more'
								)}
							</ClayLink>

							{authorized && (
								<ClayLink
									button
									className='button-root'
									displayType='primary'
									href={toRoute(
										Routes.SETTINGS_ADD_DATA_SOURCE,
										{
											groupId
										}
									)}
								>
									{Liferay.Language.get(
										'connect-data-source'
									)}
								</ClayLink>
							)}
						</>
					}
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite'
					}}
					primary
					title={Liferay.Language.get(
						'no-individuals-synced-from-data-sources'
					)}
				/>
			);
		}
	};

	const {data, error, loading} = useRequest({
		dataSourceFn: API.individuals.search,
		variables: {
			channelId,
			delta,
			groupId,
			orderIOMap,
			page,
			query
		}
	});

	return (
		<BasePage.Body pageContainer>
			<StatesRenderer {...dataSourceStates}>
				<StatesRenderer.Empty
					description={
						<>
							{authorized
								? Liferay.Language.get(
										'connect-a-data-source-to-get-started'
								  )
								: Liferay.Language.get(
										'please-contact-your-workspace-administrator-to-add-data-sources'
								  )}

							<ClayLink
								className='d-block mb-3'
								href={URLConstants.DataSourceConnection}
								key='DOCUMENTATION'
								target='_blank'
							>
								{Liferay.Language.get(
									'access-our-documentation-to-learn-more'
								)}
							</ClayLink>

							{authorized && (
								<ClayLink
									button
									className='button-root'
									displayType='primary'
									href={toRoute(
										Routes.SETTINGS_ADD_DATA_SOURCE,
										{
											groupId
										}
									)}
								>
									{Liferay.Language.get(
										'connect-data-source'
									)}
								</ClayLink>
							)}
						</>
					}
					displayCard
					title={Liferay.Language.get('no-data-sources-connected')}
				/>

				<StatesRenderer.Success>
					<div className='individuals-dashboard-known-individuals-root'>
						<Card pageDisplay>
							<CrossPageSelect
								columns={[
									individualsListColumns.getNameEmail({
										channelId,
										groupId
									}),
									individualsListColumns.jobTitle,
									individualsListColumns.activitiesCount,
									individualsListColumns.getLastActivityDate(
										timeZoneId
									)
								]}
								currentUser={currentUser}
								delta={delta}
								entityLabel={Liferay.Language.get(
									'individuals'
								)}
								error={error}
								items={data?.items}
								loading={loading}
								noResultsRenderer={renderNoResults()}
								orderByOptions={[
									{
										label: Liferay.Language.get('name'),
										value: NAME
									},
									{
										label: Liferay.Language.get(
											'job-title'
										),
										value: JOB_TITLE
									},
									{
										label: Liferay.Language.get(
											'total-activities'
										),
										value: ACTIVITIES_COUNT
									},
									{
										label: Liferay.Language.get(
											'last-activity'
										),
										value: LAST_ACTIVITY_DATE
									}
								]}
								orderIOMap={orderIOMap}
								page={page}
								query={query}
								renderNav={renderNav}
								showCheckbox
								total={data?.total}
							/>
						</Card>
					</div>
				</StatesRenderer.Success>
			</StatesRenderer>
		</BasePage.Body>
	);
};

export default compose<any>(connector, withSelectionProvider)(KnownIndividuals);
