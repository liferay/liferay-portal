import BasePage from 'settings/components/BasePage';
import React from 'react';
import RecommendationStepCard from '../components/recommendation-step-card';
import withRecommendation from 'shared/hoc/WithRecommendation';
import {compose} from 'redux';
import {get} from 'lodash';
import {getRecommendations} from 'shared/util/breadcrumbs';
import {Job} from '../utils/utils';
import {Router} from 'shared/types';
import {Routes, toRoute} from 'shared/util/router';
import {withAdminPermission} from 'shared/hoc';

interface IEditProps {
	job: Job;
	router: Router;
}

const Edit: React.FC<IEditProps> = ({job, router}) => {
	const {groupId, jobId} = router.params;

	const name = get(job, 'name');

	return (
		<BasePage
			breadcrumbItems={[
				getRecommendations({groupId}),
				{
					active: true,
					label: name
				}
			]}
			pageDescription={Liferay.Language.get(
				'item-similarity-model-uses-items-and-iteractions-for-training'
			)}
			pageTitle={name}
		>
			<div className='row'>
				<div className='col-xl-8'>
					<RecommendationStepCard
						cancelHref={toRoute(
							Routes.SETTINGS_RECOMMENDATION_MODEL_VIEW,
							{
								groupId,
								jobId
							}
						)}
						job={job}
						router={router}
					/>
				</div>
			</div>
		</BasePage>
	);
};

export default compose<any>(withAdminPermission, withRecommendation)(Edit);
