import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import InterestDetails from 'shared/components/InterestDetails';
import React from 'react';
import {pickBy} from 'lodash';
import {Router} from 'shared/types';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';
import {useParams} from 'react-router-dom';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

interface IInterestDetailsProps extends React.HTMLAttributes<HTMLDivElement> {
	router: Router;
}

const InterestDetailsPage: React.FC<IInterestDetailsProps> = ({router}) => {
	const {channelId, groupId} = useParams();
	const rangeSelectors = useQueryRangeSelectors();
	const {accountId, accountName} = router.query;

	return (
		<div className="sites-dashboard-interest-details-root">
			<div className="row">
				<div className="col-xl-12">
					<div className="back-button-root mb-2">
						<ClayLink
							borderless
							button
							displayType="secondary"
							href={setUriQueryValues(
								pickBy({
									accountId,
									accountName,
									...rangeSelectors,
								}),

								toRoute(Routes.SITES_INTERESTS, {
									channelId,
									groupId,
								})
							)}
						>
							<ClayIcon
								className="icon-root mr-2"
								symbol="angle-left-small"
							/>

							{Liferay.Language.get('back-to-interests')}
						</ClayLink>
					</div>

					<InterestDetails
						className="sites-interest-details-root"
						router={router}
					/>
				</div>
			</div>
		</div>
	);
};

export default InterestDetailsPage;
