import * as API from 'shared/api';
import Card from 'shared/components/Card';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import React from 'react';
import {isNil} from 'lodash';
import {mapGrowthHistory} from 'shared/hoc/mappers/segment';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {Routes, toRoute} from 'shared/util/router';
import {Segment} from 'shared/util/records';
import {SegmentGrowthChart} from './Growth';
import {withRequest} from 'shared/hoc';

const MembershipChart = withRequest(
	API.individualSegment.fetchMembershipChangesAggregations,
	mapGrowthHistory,
	{
		page: false,
	}
)((props: any) => <SegmentGrowthChart {...props} />);

interface ISegmentProfileCardProps extends React.HTMLAttributes<HTMLElement> {
	channelId: string;
	groupId: string;
	id: string;
	segment: Segment;
}

const SegmentProfileCard: React.FC<ISegmentProfileCardProps> = ({
	channelId,
	groupId,
	id,
	segment: {
		anonymousIndividualCount,
		includeAnonymousUsers,
		knownIndividualCount,
		lastMembershipUpdateDate,
	},
}) => (
	<Card
		className="segment-profile-card-root"
		reportContainer={ReportContainer.SegmentMembershipCard}
	>
		<Card.Header className="align-items-center d-flex justify-content-between">
			<Card.Title>
				{Liferay.Language.get('segment-membership-trend')}
			</Card.Title>
			<span className="text-secondary text-uppercase">
				<strong>{Liferay.Language.get('last-30-days')}</strong>
			</span>
		</Card.Header>

		<Card.Body>
			<MembershipChart
				channelId={channelId}
				groupId={groupId}
				id={id}
				includeAnonymousUsers={includeAnonymousUsers}
				individualCounts={{
					anonymousCount: anonymousIndividualCount,
					knownCount: knownIndividualCount,
				}}
				membershipProcessing={isNil(lastMembershipUpdateDate)}
			/>
		</Card.Body>

		<Card.Footer>
			<ClayLink
				borderless
				button
				className="button-root"
				displayType="secondary"
				href={toRoute(Routes.CONTACTS_SEGMENT_MEMBERSHIP, {
					channelId,
					groupId,
					id,
				})}
				small
			>
				{Liferay.Language.get('view-members')}

				<ClayIcon
					className="icon-root ml-2"
					symbol="angle-right-small"
				/>
			</ClayLink>
		</Card.Footer>
	</Card>
);

export default SegmentProfileCard;
