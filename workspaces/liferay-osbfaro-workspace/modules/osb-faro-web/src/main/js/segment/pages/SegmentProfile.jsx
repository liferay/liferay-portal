import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import DownloadPDFReport from 'shared/components/download-report/DownloadPDFReport';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import ErrorPage from 'shared/pages/ErrorPage';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import PropTypes from 'prop-types';
import ManageSegmentNotificationsModal from 'shared/components/modals/ManageSegmentNotificationsModal';
import React, {
	lazy,
	Suspense,
	useContext,
	useEffect,
	useMemo,
	useState
} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {CSVType} from 'shared/components/download-report/utils';
import {DownloadStaticCSVReport} from 'shared/components/download-report/DownloadStaticCSVReport';
import {getSegmentAlerts} from 'segment/utils/alerts';
import {Segment} from 'shared/util/records';
import {
	SegmentCategories,
	SegmentStates,
	SegmentTypes
} from 'shared/util/constants';
import {useDeleteSegments} from 'segment/hooks/useDeleteSegments';
import {useModal} from '@clayui/modal';
import {useRequest} from 'shared/hooks/useRequest';
import {config} from 'shared/util/config';
import {setUriQueryValues} from 'shared/util/url';

const AccountProfile = lazy(() =>
	import(/* webpackChunkName: "SegmentAccountProfile" */ './AccountProfile')
);
const SEGMENTS_LANGUAGE_MAP = {
	[SegmentTypes.Batch]: Liferay.Language.get('individual-batch-segment'),
	[SegmentTypes.RealTime]: Liferay.Language.get(
		'individual-real-time-segment'
	)
};

const SegmentProfile = ({renderContent}) => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId} = config;

	const {id} = config.params;


	const {deleteSegments, deleteSegmentsModal} = useDeleteSegments(groupId);
	const notificationsModal = useModal();
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
				href={config.segmentsURL}
				linkLabel={Liferay.Language.get('go-to-segments')}
				message={Liferay.Language.get(
					'the-segment-you-are-looking-for-does-not-exist'
				)}
				subtitle={Liferay.Language.get('segment-was-not-found')}
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
			onSuccess: () => window.location.assign(config.segmentsURL)
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
					breadcrumbs.getSegments(config.segmentsURL),
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
									href: setUriQueryValues({id}, config.editSegmentURL),
									icon: {symbol: 'pencil'},
									label: Liferay.Language.get(
										'edit-segment'
									)
								},
								...(isBatch && config.friendlyURL === '/segment'
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
								config.friendlyURL ===
									'/segment-membership'
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
										notificationsModal.onOpenChange(true)
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

				{isBatch && <BasePage.Header.NavBar />}
			</BasePage.Header>

			<EmbeddedAlertList alerts={getSegmentAlerts(segment)} />

			<BasePage.Body disabled={checkDisabled()}>
				{segment.id ? (
					<Suspense fallback={<Loading />}>
						{renderContent({isBatch, segment})}
					</Suspense>
				) : (
					<Loading />
				)}
			</BasePage.Body>

			{deleteSegmentsModal}

			{notificationsModal.open && (
				<ManageSegmentNotificationsModal
					groupId={groupId}
					observer={notificationsModal.observer}
					onClose={() => notificationsModal.onOpenChange(false)}
					segmentId={id}
				/>
			)}
		</BasePage>
	);
};

SegmentProfile.propTypes = {
	renderContent: PropTypes.func.isRequired
};

export default SegmentProfile;
