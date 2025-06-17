import ClayLink from '@clayui/link';
import Distribution from 'contacts/components/Distribution';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {connect} from 'react-redux';
import {fetchDistribution} from 'shared/actions/distributions';
import {Sizes} from 'shared/util/constants';

const SegmentDistribution = ({segment, ...otherProps}) => (
	<div className='segment-distribution-root container-fluid'>
		<div className='row'>
			<div className='col-xl-12'>
				<Distribution
					distributionsKey={segment.id}
					knownIndividualCount={segment.knownIndividualCount}
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
							icon={{
								border: false,
								size: Sizes.XXXLarge,
								symbol: 'ac_satellite'
							}}
							title={Liferay.Language.get(
								'there-are-no-results-found'
							)}
						/>
					)}
					pageContainer
					{...otherProps}
				/>
			</div>
		</div>
	</div>
);

export default connect(null, {fetchDistribution})(SegmentDistribution);
