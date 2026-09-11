import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import DownloadPDFReport from 'shared/components/download-report/DownloadPDFReport';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import ErrorPage from 'shared/pages/ErrorPage';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {
	lazy,
	Suspense,
	useContext,
	useEffect,
	useMemo,
	useState
} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect} from 'react-redux';
import {CSVType} from 'shared/components/download-report/utils';
import {DownloadStaticCSVReport} from 'shared/components/download-report/DownloadStaticCSVReport';
import {getMatchedRoute, Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {getSegmentAlerts} from 'segment/utils/alerts';
import {
	Route,
	Routes as RouterRoutes,
	useNavigate,
	useParams
} from 'react-router-dom';
import {Segment} from 'shared/util/records';
import {
	SegmentCategories,
	SegmentStates,
	SegmentTypes
} from 'shared/util/constants';
import {useDeleteSegments} from 'segment/hooks/useDeleteSegments';
import {useRequest} from 'shared/hooks/useRequest';

const AccountProfile = lazy(() =>
	import(/* webpackChunkName: "SegmentAccountProfile" */ './AccountProfile')
);
const Overview = lazy(() =>
	import(/* webpackChunkName: "SegmentOverview" */ './Overview')
);
const OverviewRealTime = lazy(() =>
	import(/* webpackChunkName: "SegmentOverview" */ './OverviewRealTime')
);
const Membership = lazy(() =>
	import(/* webpackChunkName: "SegmentMembership" */ './Membership')
);
const Interests = lazy(() =>
	import(/* webpackChunkName: "SegmentInterests" */ './Interests')
);
const InterestDetails = lazy(() =>
	import(/* webpackChunkName: "SegmentInterestDetails" */ './InterestDetails')
);
const Distribution = lazy(() =>
	import(/* webpackChunkName: "SegmentDistribution" */ './Distribution')
);

const NAV_ITEMS = [
	{
		exact: true,
		label: Liferay.Language.get('overview'),
		route: Routes.CONTACTS_SEGMENT
	},
	{
		exact: true,
		label: Liferay.Language.get('membership'),
		route: Routes.CONTACTS_SEGMENT_MEMBERSHIP
	},
	{
		exact: false,
		label: Liferay.Language.get('interests'),
		route: Routes.CONTACTS_SEGMENT_INTERESTS
	},
	{
		exact: true,
		label: Liferay.Language.get('distribution'),
		route: Routes.CONTACTS_SEGMENT_DISTRIBUTION
	}
];

const SEGMENTS_LANGUAGE_MAP = {
	[SegmentTypes.Batch]: Liferay.Language.get('individual-batch-segment'),
	[SegmentTypes.RealTime]: Liferay.Language.get(
		'individual-real-time-segment'
	)
};

export const SegmentProfileRoutes = ({close, open}) => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, id} = useParams();

	const navigate = useNavigate();

	const deleteSegments = useDeleteSegments(groupId);

	const {data, error, loading} = useRequest({
		dataSourceFn: API.individualSegment.fetch,
		variables: {
			groupId,
			includeReferencedObjects: true,
			segmentId: id
		}
	});

	const segment = useMemo(() => new Segment(data), [data]);

	const [segmentDetails, setSegmentDetails] = useState({
		name: segment.name,
		segmentType: segment.segmentType
	});

	useEffect(() => {
		if (data && !loading) {
			setSegmentDetails({
				name: segment.name,
				segmentType: segment.segmentType
			});
		}
	}, [data, loading]);

	if (loading && !segmentDetails.name) {
		return <Loading />;
	}

	const title = segmentDetails.name || Liferay.Language.get('unknown');

	if (error) {
		return (
			<ErrorPage
				href={toRoute(Routes.CONTACTS_LIST_ENTITY, {
					channelId,
					groupId,
					type: SEGMENTS
				})}
				linkLabel={Liferay.Language.get('go-to-segments')}
				message={Liferay.Language.get(
					'the-segment-you-are-looking-for-does-not-exist'
				)}
				subtitle={Liferay.Language.get('segment-not-found')}
			/>
		);
	}

	if (segment.segmentCategory === SegmentCategories.Account) {
		return (
			<Suspense fallback={<Loading />}>
				<AccountProfile
					channelId={channelId}
					groupId={groupId}
					segment={segment}
				/>
			</Suspense>
		);
	}

	const checkDisabled = () => segment.state === SegmentStates.Disabled;

	const isBatch = segmentDetails.segmentType === SegmentTypes.Batch;

	const handleDeleteSegment = () => {
		deleteSegments({
			ids: [id],
			name: segmentDetails.name,
			onSuccess: () =>
				navigate(
					toRoute(Routes.CONTACTS_LIST_ENTITY, {
						channelId,
						groupId,
						type: SEGMENTS
					})
				)
		});
	};

	return (
		<BasePage
			className={getCN(
				'segment-profile-root',
				'segment-overview-root',
				'overview-root'
			)}
			documentTitle={`${segmentDetails.name} - ${Liferay.Language.get(
				'segment'
			)}`}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel && selectedChannel.name
					}),
					breadcrumbs.getSegments({channelId, groupId}),
					breadcrumbs.getEntityName({label: segmentDetails.name})
				]}
				groupId={groupId}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection
						className='mb-3'
						subtitle={`${Liferay.Language.get('erc')}: ${
							segment.externalReferenceCode
						}`}
						title={title}
						topLabel={
							SEGMENTS_LANGUAGE_MAP[segmentDetails.segmentType]
						}
					>
						<BasePage.Header.PageActionsToolbar
							actions={[
								{
									href: toRoute(
										Routes.CONTACTS_SEGMENT_EDIT,
										{channelId, groupId, id, type: SEGMENTS}
									),
									icon: {symbol: 'pencil'},
									label: Liferay.Language.get(
										'edit-segment'
									)
								},
								...(isBatch &&
								getMatchedRoute(NAV_ITEMS) ===
									Routes.CONTACTS_SEGMENT
									? [
											{
												icon: {symbol: 'download'},
												label: Liferay.Language.get(
													'download-pdf'
												),
												renderItem: (item) => (
													<DownloadPDFReport
														disabled={false}
														showDateRange={false}
														subtitle={
															selectedChannel?.name
														}
														title={title}
													>
														{item}
													</DownloadPDFReport>
												)
											}
										]
									: []),
								...(isBatch &&
								getMatchedRoute(NAV_ITEMS) ===
									Routes.CONTACTS_SEGMENT_MEMBERSHIP
									? [
											{
												icon: {symbol: 'download'},
												label: Liferay.Language.get(
													'download-csv'
												),
												renderItem: (item) => (
													<DownloadStaticCSVReport
														disabled={checkDisabled()}
														segmentId={segment.id}
														type={CSVType.Membership}
														typeLang={Liferay.Language.get(
															'segment-membership'
														)}
													>
														{item}
													</DownloadStaticCSVReport>
												)
											}
										]
									: []),
								{
									icon: {symbol: 'bell-on'},
									label: Liferay.Language.get(
										'manage-notifications'
									),
									onClick: () =>
										open(
											modalTypes.MANAGE_SEGMENT_NOTIFICATIONS_MODAL,
											{onClose: close}
										)
								},
								{
									className: 'text-danger',
									icon: {symbol: 'trash'},
									label: Liferay.Language.get('delete'),
									onClick: handleDeleteSegment
								}
							]}
						/>
					</BasePage.Header.TitleSection>
				</BasePage.Row>

				{isBatch && (
					<BasePage.Header.NavBar
						items={NAV_ITEMS}
						routeParams={{channelId, groupId, id}}
					/>
				)}
			</BasePage.Header>

			<EmbeddedAlertList alerts={getSegmentAlerts(segment)} />

			<BasePage.Body disabled={checkDisabled()}>
				{segment.id ? (
					<Suspense fallback={<Loading />}>
						<RouterRoutes>
							<Route
								element={
									<BundleRouter
										componentProps={{segment}}
										data={Membership}
									/>
								}
								path='membership'
							/>

							<Route
								element={
									<BundleRouter
										componentProps={{segment}}
										data={InterestDetails}
									/>
								}
								path='interests/:interestId/:tabId?'
							/>

							<Route
								element={
									<BundleRouter
										componentProps={{segment}}
										data={Interests}
										destructured={false}
									/>
								}
								path='interests'
							/>

							<Route
								element={
									<BundleRouter
										componentProps={{segment}}
										data={Distribution}
									/>
								}
								path='distribution'
							/>

							<Route
								element={
									<BundleRouter
										componentProps={{segment}}
										data={isBatch ? Overview : OverviewRealTime}
									/>
								}
								index
							/>

							<Route element={<ErrorPage />} path='*' />
						</RouterRoutes>
					</Suspense>
				) : (
					<Loading />
				)}
			</BasePage.Body>
		</BasePage>
	);
};

export default connect(null, {close, open})(SegmentProfileRoutes);
