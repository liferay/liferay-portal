import ClayEmptyState from '@clayui/empty-state';
import ClayLink from '@clayui/link';
import LifecyclesPage from 'lifecycle/components/LifecyclesPage';
import React from 'react';
import {Routes, toRoute} from 'shared/util/router';
import {useParams} from 'react-router-dom';

const LifecycleNotFound = () => {
	const {channelId, groupId} = useParams();

	return (
		<LifecyclesPage
			documentTitle={Liferay.Language.get('dashboard-not-found')}
			showTitle={false}
		>
			<div className="card mt-4 py-8">
				<p className="display-1 font-weight-bold mb-0 text-center text-secondary">
					{'404'}
				</p>

				<ClayEmptyState
					description={Liferay.Language.get(
						'the-lifecycle-dashboard-you-are-looking-for-does-not-exist'
					)}
					title={Liferay.Language.get('dashboard-not-found')}
				>
					<ClayLink
						button
						displayType="secondary"
						href={toRoute(Routes.LIFECYCLE, {
							channelId,
							groupId,
						})}
					>
						{Liferay.Language.get('go-to-lifecycles')}
					</ClayLink>
				</ClayEmptyState>
			</div>
		</LifecyclesPage>
	);
};

export default LifecycleNotFound;
