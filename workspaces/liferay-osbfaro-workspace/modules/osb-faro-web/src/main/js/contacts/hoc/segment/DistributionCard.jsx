import ClayLink from '@clayui/link';
import DistributionCard from 'contacts/components/distribution-card';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {connect} from 'react-redux';
import {fetchDistribution} from 'shared/actions/distributions';
import {Routes, toRoute} from 'shared/util/router';

const SegmentDistributionCard = ({channelId, groupId, id, ...otherProps}) => (
	<DistributionCard
		channelId={channelId}
		distributionKey={id}
		groupId={groupId}
		id={id}
		noResultsRenderer={() => (
			<NoResultsDisplay
				description={
					<>
						{Liferay.Language.get(
							'try-choosing-a-different-breakdown'
						)}

						<ClayLink
							className='d-block'
							href={
								URLConstants.SegmentsDistributionDocumentationLink
							}
							key='DOCUMENTATION'
							target='_blank'
						>
							{Liferay.Language.get(
								'learn-more-about-distribution'
							)}
						</ClayLink>
					</>
				}
				title={Liferay.Language.get('no-results-were-found')}
			/>
		)}
		showContext
		viewAllLink={toRoute(Routes.CONTACTS_SEGMENT_DISTRIBUTION, {
			channelId,
			groupId,
			id
		})}
		{...otherProps}
	/>
);

export default connect(null, {fetchDistribution})(SegmentDistributionCard);
