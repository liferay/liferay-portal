import * as API from 'shared/api';
import AssociatedSegmentsCard from 'contacts/components/AssociatedSegmentsCard';
import ClayLink from '@clayui/link';
import DetailsCard from '../components/DetailsCard';
import IndividualProfileCard from '../hoc/ProfileCard';
import InterestsCard from '../components/InterestsCard';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {connect} from 'react-redux';
import {createOrderIOMap, INDIVIDUAL_COUNT} from 'shared/util/pagination';
import {EntityTypes, OrderByDirections} from 'shared/util/constants';
import {Individual} from 'shared/util/records';
import {INDIVIDUALS} from 'shared/util/router';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';

const ITEMS_PER_CARD = 5;

function fetchAssociatedSegments({channelId, groupId, id, searchValue}) {
	return API.individualSegment.search({
		channelId,
		contactsEntityId: id,
		contactsEntityType: EntityTypes.Individual,
		delta: ITEMS_PER_CARD,
		groupId,
		orderIOMap: createOrderIOMap(
			INDIVIDUAL_COUNT,
			OrderByDirections.Descending
		),
		query: searchValue
	});
}

export class Overview extends React.Component {
	static propTypes = {
		channelId: PropTypes.string,
		groupId: PropTypes.string.isRequired,
		id: PropTypes.string.isRequired,
		individual: PropTypes.instanceOf(Individual).isRequired,
		tabId: PropTypes.string,
		timeZoneId: PropTypes.string
	};

	render() {
		const {channelId, groupId, id, individual, tabId, timeZoneId} =
			this.props;

		return (
			<>
				<div className='overview-layout'>
					<div className='overview-column-main'>
						<IndividualProfileCard
							channelId={channelId}
							entity={individual}
							groupId={groupId}
							tabId={tabId}
							timeZoneId={timeZoneId}
						/>
					</div>

					<div className='overview-column-side'>
						<DetailsCard
							channelId={channelId}
							entity={individual}
							groupId={groupId}
							timeZoneId={timeZoneId}
						/>

						<InterestsCard
							channelId={channelId}
							compact
							entity={individual}
							groupId={groupId}
							showFilter
							type={INDIVIDUALS}
						/>

						<AssociatedSegmentsCard
							channelId={channelId}
							dataSourceFn={fetchAssociatedSegments}
							groupId={groupId}
							id={id}
							noResultsRenderer={() => (
								<NoResultsDisplay
									description={
										<>
											{Liferay.Language.get(
												'create-a-segment-to-get-started'
											)}

											<ClayLink
												className='d-block'
												href={
													URLConstants.IndividualProfilesDocumentSegments
												}
												key='DOCUMENTATION'
												target='_blank'
											>
												{Liferay.Language.get(
													'learn-more-about-segments'
												)}
											</ClayLink>
										</>
									}
									spacer
									title={Liferay.Language.get(
										'no-segments-were-found'
									)}
								/>
							)}
							pageUrl={toRoute(
								Routes.CONTACTS_INDIVIDUAL_SEGMENTS,
								{
									channelId,
									groupId,
									id
								}
							)}
						/>
					</div>
				</div>
			</>
		);
	}
}

export default connect((store, {groupId}) => ({
	timeZoneId: store.getIn([
		'projects',
		groupId,
		'data',
		'timeZone',
		'timeZoneId'
	])
}))(Overview);
