import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountsDataSet from 'shared/components/accounts-data-set/AccountsDataSet';
import BasePage from 'shared/components/base-page';
import CriteriaCard from 'segment/components/criteria-card';
import EmbeddedAlertList from 'shared/components/EmbeddedAlertList';
import React from 'react';
import {CSVType} from 'shared/components/download-report/utils';
import {DownloadStaticCSVReport} from 'shared/components/download-report/DownloadStaticCSVReport';
import {ReferencedObjectsProvider} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {Routes, SEGMENTS, toRoute} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {getSegmentAlerts} from 'segment/utils/alerts';
import {Segment} from 'shared/util/records';
import {SegmentStates, SegmentTypes} from 'shared/util/constants';
import {useChannelContext} from 'shared/context/channel';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface IAccountProfileProps {
	channelId: string;
	groupId: string;
	segment: Segment;
}

const AccountProfile: React.FC<IAccountProfileProps> = ({
	channelId,
	groupId,
	segment,
}) => {
	const {selectedChannel} = useChannelContext();
	const {timeZoneId} = useTimeZone();

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
					/>

					<BasePage.Header.Section>
						<BasePage.Header.PageActions
							actions={[
								{
									disabled: false,
									href: toRoute(
										Routes.CONTACTS_SEGMENT_EDIT,
										{
											channelId,
											groupId,
											id: segment.id,
											type: SEGMENTS,
										}
									),
									label: Liferay.Language.get('edit-segment'),
								},
							]}
						/>
					</BasePage.Header.Section>
				</BasePage.Row>
			</BasePage.Header>

			<BasePage.SubHeader>
				<div className="d-flex justify-content-end w-100">
					<DownloadStaticCSVReport
						disabled={disabled}
						segmentId={segment.id}
						type={CSVType.Membership}
						typeLang={Liferay.Language.get('segment-membership')}
					/>
				</div>
			</BasePage.SubHeader>

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

export default AccountProfile;
