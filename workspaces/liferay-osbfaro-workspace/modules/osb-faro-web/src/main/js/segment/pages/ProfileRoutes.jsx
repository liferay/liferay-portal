import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import DownloadPDFReport from 'shared/components/download-report/DownloadPDFReport';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import ErrorPage from 'shared/pages/ErrorPage';
import getCN from 'classnames';
import Label from 'shared/components/Label';
import Loading from 'shared/components/Loading';
import React, {
	lazy,
	Suspense,
	useContext,
	useEffect,
	useMemo,
	useState
} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {AlertTypes} from 'shared/components/Alert';
import {ChannelContext} from 'shared/context/channel';
import {CSVType} from 'shared/components/download-report/utils';
import {DownloadStaticCSVReport} from 'shared/components/download-report/DownloadStaticCSVReport';
import {getMatchedRoute, Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {Segment} from 'shared/util/records';
import {
	SegmentCategories,
	SegmentStates,
	SegmentTypes
} from 'shared/util/constants';
import {Switch, useParams} from 'react-router-dom';
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
	[SegmentTypes.Batch]: Liferay.Language.get('batch-segment'),
	[SegmentTypes.RealTime]: Liferay.Language.get('real-time-segment')
};

export const SegmentProfileRoutes = () => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, id} = useParams();

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

	const getAlerts = () => {
		if (segment.state === SegmentStates.InProgress) {
			return [
				{
					alertType: AlertTypes.Info,
					message: Liferay.Language.get(
						'segment-data-is-processing-please-check-back-later'
					),
					stripe: true
				}
			];
		} else if (checkDisabled()) {
			return [
				{
					alertType: AlertTypes.Danger,
					message: Liferay.Language.get(
						'this-segment-is-disabled-because-some-criteria-has-been-affected-by-removal-of-a-data-source.-to-continue-using-this-segment-please-update-the-criteria'
					),
					stripe: true
				}
			];
		}
	};

	const isBatch = segmentDetails.segmentType === SegmentTypes.Batch;

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
					>
						<Label display='secondary' size='lg' uppercase>
							{SEGMENTS_LANGUAGE_MAP[segmentDetails.segmentType]}
						</Label>
					</BasePage.Header.TitleSection>

					<BasePage.Header.Section>
						<BasePage.Header.PageActions
							actions={[
								{
									button: true,
									displayType: 'secondary',
									href: toRoute(Routes.CONTACTS_SEGMENT_EDIT, {
										channelId,
										groupId,
										id,
										type: SEGMENTS
									}),
									label: Liferay.Language.get('edit-segment')
								}
							]}
						/>
					</BasePage.Header.Section>
				</BasePage.Row>

				{isBatch && (
					<BasePage.Header.NavBar
						items={NAV_ITEMS}
						routeParams={{channelId, groupId, id}}
					/>
				)}
			</BasePage.Header>

			{isBatch &&
				getMatchedRoute(NAV_ITEMS) === Routes.CONTACTS_SEGMENT && (
					<BasePage.SubHeader>
						<div className='d-flex justify-content-end w-100'>
							<DownloadPDFReport
								disabled={false}
								showDateRange={false}
								subtitle={selectedChannel?.name}
								title={title}
							/>
						</div>
					</BasePage.SubHeader>
				)}

			{isBatch &&
				getMatchedRoute(NAV_ITEMS) ===
					Routes.CONTACTS_SEGMENT_MEMBERSHIP && (
					<BasePage.SubHeader>
						<div className='d-flex justify-content-end w-100'>
							<DownloadStaticCSVReport
								disabled={checkDisabled()}
								segmentId={segment.id}
								type={CSVType.Membership}
								typeLang={Liferay.Language.get(
									'segment-membership'
								)}
							/>
						</div>
					</BasePage.SubHeader>
				)}

			<EmbeddedAlertList alerts={getAlerts()} />

			<BasePage.Body disabled={checkDisabled()}>
				{segment.id ? (
					<Suspense fallback={<Loading />}>
						<Switch>
							<BundleRouter
								componentProps={{segment}}
								data={Membership}
								exact
								path={Routes.CONTACTS_SEGMENT_MEMBERSHIP}
							/>

							<BundleRouter
								componentProps={{segment}}
								data={InterestDetails}
								exact
								path={Routes.CONTACTS_SEGMENT_INTEREST_DETAILS}
							/>

							<BundleRouter
								componentProps={{segment}}
								data={Interests}
								destructured={false}
								exact
								path={Routes.CONTACTS_SEGMENT_INTERESTS}
							/>

							<BundleRouter
								componentProps={{segment}}
								data={Distribution}
								exact
								path={Routes.CONTACTS_SEGMENT_DISTRIBUTION}
							/>

							<BundleRouter
								componentProps={{segment}}
								data={isBatch ? Overview : OverviewRealTime}
								exact
								path={Routes.CONTACTS_SEGMENT}
							/>

							<RouteNotFound />
						</Switch>
					</Suspense>
				) : (
					<Loading />
				)}
			</BasePage.Body>
		</BasePage>
	);
};

export default SegmentProfileRoutes;
