import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountsDataSet from 'shared/components/accounts-data-set/AccountsDataSet';
import BasePage from 'shared/components/base-page';
import CriteriaCard from 'segment/components/criteria-card';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import React from 'react';
import {close, modalTypes, open} from 'shared/actions/modals';
import {connect, ConnectedProps} from 'react-redux';
import {CSVType} from 'shared/components/download-report/utils';
import {DownloadStaticCSVReport} from 'shared/components/download-report/DownloadStaticCSVReport';
import {ReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {getSegmentAlerts} from 'segment/utils/alerts';
import {Segment} from 'shared/util/records';
import {SegmentStates, SegmentTypes} from 'shared/util/constants';
import {useChannelContext} from 'shared/context/channel';
import {useDeleteSegments} from 'segment/hooks/useDeleteSegments';
import {useNavigate} from 'react-router-dom';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const connector = connect(null, {close, open});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IAccountProfileProps extends PropsFromRedux {
	channelId: string;
	groupId: string;
	segment: Segment;
}

const AccountProfile: React.FC<IAccountProfileProps> = ({
	channelId,
	close,
	groupId,
	open,
	segment,
}) => {
	const {selectedChannel} = useChannelContext();
	const {timeZoneId} = useTimeZone();

	const navigate = useNavigate();

	const deleteSegments = useDeleteSegments(groupId);

	const {name} = segment;

	const disabled = segment.state === SegmentStates.Disabled;

	return (
		<BasePage
			className="segment-profile-root"
			documentTitle={`${name} - ${Liferay.Language.get('segment')}`}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannel && selectedChannel.name,
					}),
					breadcrumbs.getSegments({channelId, groupId}),
					breadcrumbs.getEntityName({label: name}),
				]}
				groupId={groupId}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection
						className="mb-3"
						subtitle={`${Liferay.Language.get(
							'erc'
						)}: ${segment.externalReferenceCode}`}
						title={name}
						topLabel={Liferay.Language.get('account-batch-segment')}
					>
						<BasePage.Header.PageActionsToolbar
							actions={[
								{
									href: toRoute(
										Routes.CONTACTS_SEGMENT_EDIT,
										{
											channelId,
											groupId,
											id: segment.id,
											type: SEGMENTS,
										}
									),
									icon: {symbol: 'pencil'},
									label: Liferay.Language.get('edit-segment'),
								},
								{
									icon: {symbol: 'download'},
									label: Liferay.Language.get('download-csv'),
									renderItem: (item) => (
										<DownloadStaticCSVReport
											disabled={disabled}
											segmentId={segment.id}
											type={CSVType.Membership}
											typeLang={Liferay.Language.get(
												'segment-membership'
											)}
										>
											{item}
										</DownloadStaticCSVReport>
									),
								},
								{
									icon: {symbol: 'bell-on'},
									label: Liferay.Language.get(
										'manage-notifications'
									),
									onClick: () =>
										open(
											modalTypes.MANAGE_SEGMENT_NOTIFICATIONS_MODAL,
											{onClose: close}
										),
								},
								{
									className: 'text-danger',
									icon: {symbol: 'trash'},
									label: Liferay.Language.get('delete'),
									onClick: () =>
										deleteSegments({
											ids: [segment.id],
											name,
											onSuccess: () =>
												navigate(
													toRoute(
														Routes.CONTACTS_LIST_ENTITY,
														{
															channelId,
															groupId,
															type: SEGMENTS,
														}
													)
												),
										}),
								},
							]}
						/>
					</BasePage.Header.TitleSection>
				</BasePage.Row>
			</BasePage.Header>

			<EmbeddedAlertList alerts={getSegmentAlerts(segment)} />

			<BasePage.Body disabled={disabled}>
				<SectionHeader
					icon="analytics"
					title={Liferay.Language.get('details')}
				/>

				<ReferencedObjectsProvider segment={segment}>
					<CriteriaCard
						channelId={channelId}
						criteriaString={segment.criteriaString ?? ''}
						groupId={groupId}
						includeAnonymousUsers={segment.includeAnonymousUsers}
						segmentType={SegmentTypes.Batch}
						sequential={false}
						timeZoneId={timeZoneId}
					/>
				</ReferencedObjectsProvider>

				<SectionHeader
					icon="users"
					title={Liferay.Language.get('segment-membership')}
				/>

				<AccountsDataSet
					apiURL={`/o/faro/contacts/${groupId}/account/search?channelId=${channelId}&segmentId=${segment.id}`}
					channelId={channelId}
					dataSetId="segment-accounts-dataset"
					groupId={groupId}
				/>
			</BasePage.Body>
		</BasePage>
	);
};

export default connector(AccountProfile);
