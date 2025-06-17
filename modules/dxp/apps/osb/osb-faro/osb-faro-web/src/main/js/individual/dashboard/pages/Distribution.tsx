import * as API from 'shared/api';
import ClayLink from '@clayui/link';
import Distribution, {CONTEXT_OPTIONS} from 'contacts/components/Distribution';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import URLConstants from 'shared/util/url-constants';
import {compose, withQuery} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {
	fetchIndividualsDistribution,
	INDIVIDUALS_DASHBOARD_DISTRUBTIONS_KEY
} from 'shared/actions/distributions';
import {get} from 'lodash';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSource} from 'shared/hooks/useDataSource';
import {useParams} from 'react-router-dom';

const connector = connect(null, {
	fetchDistribution: fetchIndividualsDistribution
});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IIndividualsDistributionProps extends PropsFromRedux {
	knownIndividualCount: number | null;
}

export const IndividualsDistribution: React.FC<IIndividualsDistributionProps> = ({
	knownIndividualCount,
	...otherProps
}) => {
	const {groupId} = useParams();
	const dataSourceStates = useDataSource();
	const currentUser = useCurrentUser();
	const authorized = currentUser.isAdmin();

	return (
		<StatesRenderer {...dataSourceStates}>
			<StatesRenderer.Empty
				description={
					<>
						{authorized
							? Liferay.Language.get(
									'connect-a-data-source-to-get-started'
							  )
							: Liferay.Language.get(
									'please-contact-your-workspace-administrator-to-add-data-sources'
							  )}

						<ClayLink
							className='d-block mb-3'
							href={URLConstants.DataSourceConnection}
							key='DOCUMENTATION'
							target='_blank'
						>
							{Liferay.Language.get(
								'access-our-documentation-to-learn-more'
							)}
						</ClayLink>

						{authorized && (
							<ClayLink
								button
								className='button-root'
								displayType='primary'
								href={toRoute(Routes.SETTINGS_ADD_DATA_SOURCE, {
									groupId
								})}
							>
								{Liferay.Language.get('connect-data-source')}
							</ClayLink>
						)}
					</>
				}
				displayCard
				title={Liferay.Language.get('no-data-sources-connected')}
			/>

			<StatesRenderer.Success>
				<div className='individuals-dashboard-distribution-root container-fluid'>
					<div className='row'>
						<div className='col-xl-12'>
							<Distribution
								contextOptions={[CONTEXT_OPTIONS[0]]}
								distributionsKey={
									INDIVIDUALS_DASHBOARD_DISTRUBTIONS_KEY
								}
								knownIndividualCount={knownIndividualCount}
								pageContainer
								{...otherProps}
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
														URLConstants.IndividualsDashboardBreakdownDocumentation
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
							/>
						</div>
					</div>
				</div>
			</StatesRenderer.Success>
		</StatesRenderer>
	);
};

export default compose<any>(
	withQuery(
		({channelId, groupId}) =>
			API.individuals.search({
				channelId,
				groupId,
				includeAnonymousUsers: false
			}),
		val => val,
		({data, error}) => ({
			knownIndividualCount: error ? 0 : get(data, 'total', null)
		})
	),
	connector
)(IndividualsDistribution);
