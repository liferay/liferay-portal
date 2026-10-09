import BasePage from 'shared/components/base-page';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import InterestDetails from 'shared/components/InterestDetails';
import React from 'react';
import {isNil, pickBy} from 'lodash';
import {Router} from 'shared/types';
import {Routes, setUriQueryValues, toRoute} from 'shared/util/router';

interface IInterestDetailsProps extends React.HTMLAttributes<HTMLDivElement> {
	router: Router;
}

const InterestDetailsPage: React.FC<IInterestDetailsProps> = ({router}) => {
	const {
		params: {channelId, groupId},
		query: {rangeKey},
	} = router;

	return (
		<BasePage.Body
			className="individuals-dashboard-interest-details-root"
			pageContainer
		>
			<div className="back-button-root mb-2">
				<ClayLink
					borderless
					button
					displayType="secondary"
					href={setUriQueryValues(
						pickBy({rangeKey}, (param) => !isNil(param)),

						toRoute(Routes.CONTACTS_INDIVIDUALS_INTERESTS, {
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

			<InterestDetails router={router} />
		</BasePage.Body>
	);
};

export default InterestDetailsPage;
