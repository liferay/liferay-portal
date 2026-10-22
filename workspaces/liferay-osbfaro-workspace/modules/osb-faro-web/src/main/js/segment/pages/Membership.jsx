import * as API from 'shared/api';
import Card from 'shared/components/Card';
import React from 'react';
import {isNil} from 'lodash';
import SegmentGrowthWithList from 'segment/components/Growth';
import {connect} from 'react-redux';
import {mapGrowthHistory} from 'shared/hoc/mappers/segment';
import {PropTypes} from 'prop-types';
import {ReportContainer} from 'shared/components/download-report/DownloadPDFReport';
import {Segment} from 'shared/util/records';
import {SegmentTypes} from 'shared/util/constants';
import {withRequest} from 'shared/hoc';

export const MembershipChart = withRequest(
	API.individualSegment.fetchMembershipChangesAggregations,
	mapGrowthHistory,
	{
		alignCenter: true,
		page: false
	}
)(
	class extends React.Component {
		static propTypes = {
			channelId: PropTypes.string,
			data: PropTypes.array,
			groupId: PropTypes.string.isRequired,
			id: PropTypes.string.isRequired,
			includeAnonymousUsers: PropTypes.bool,
			individualCounts: PropTypes.shape({
				anonymousCount: PropTypes.number,
				knownCount: PropTypes.number
			}),
			membershipProcessing: PropTypes.bool,
			segmentType: PropTypes.oneOf(Object.values(SegmentTypes)),
			timeZoneId: PropTypes.string
		};

		render() {
			const {
				channelId,
				data,
				groupId,
				id,
				includeAnonymousUsers,
				individualCounts,
				membershipProcessing,
				segmentType,
				timeZoneId
			} = this.props;

			return (
				<SegmentGrowthWithList
					channelId={channelId}
					data={data}
					groupId={groupId}
					id={id}
					includeAnonymousUsers={includeAnonymousUsers}
					individualCounts={individualCounts}
					membershipProcessing={membershipProcessing}
					shouldShowMembershipList={
						segmentType !== SegmentTypes.RealTime
					}
					timeZoneId={timeZoneId}
				/>
			);
		}
	}
);

@connect((store, {groupId}) => ({
	timeZoneId: store.getIn([
		'projects',
		groupId,
		'data',
		'timeZone',
		'timeZoneId'
	])
}))
export default class Membership extends React.Component {
	static propTypes = {
		groupId: PropTypes.string.isRequired,
		segment: PropTypes.instanceOf(Segment).isRequired,
		timeZoneId: PropTypes.string
	};

	render() {
		const {
			channelId,
			groupId,
			segment: {
				anonymousIndividualCount,
				id,
				includeAnonymousUsers,
				knownIndividualCount,
				lastMembershipUpdateDate,
				segmentType
			},
			timeZoneId
		} = this.props;

		return (
			<Card
				className='segment-membership-root'
				reportContainer={ReportContainer.SegmentMembershipCard}
			>
				<Card.Header className='align-items-center d-flex justify-content-between'>
					<Card.Title>
						{Liferay.Language.get('segment-membership-trend')}
					</Card.Title>
					<span className='text-secondary text-uppercase'>
						<strong>{Liferay.Language.get('last-30-days')}</strong>
					</span>
				</Card.Header>

				<MembershipChart
					anonymousIndividualCount
					channelId={channelId}
					groupId={groupId}
					id={id}
					includeAnonymousUsers={includeAnonymousUsers}
					individualCounts={{
						anonymousCount: anonymousIndividualCount,
						knownCount: knownIndividualCount
					}}
					membershipProcessing={isNil(lastMembershipUpdateDate)}
					segmentType={segmentType}
					timeZoneId={timeZoneId}
				/>
			</Card>
		);
	}
}
