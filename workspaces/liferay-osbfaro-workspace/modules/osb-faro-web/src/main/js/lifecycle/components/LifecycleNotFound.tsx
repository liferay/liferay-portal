import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import ClayEmptyState from '@clayui/empty-state';
import ClayLink from '@clayui/link';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {Routes, toRoute} from 'shared/util/router';
import {useParams} from 'react-router-dom';

const LifecycleNotFound = () => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId} = useParams();

	return (
		<BasePage documentTitle={Liferay.Language.get('dashboard-not-found')}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId: channelId!,
						groupId: groupId!,
						label: selectedChannel?.name,
					}),
					breadcrumbs.getLifecycles({
						channelId: channelId!,
						groupId: groupId!,
					}),
				]}
				fluid
				groupId={groupId!}
			/>

			<BasePage.Body fluid>
				<NoResultsDisplay displayCard title="">
					<>
						<p className="lifecycle-not-found-code">404</p>

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
					</>
				</NoResultsDisplay>
			</BasePage.Body>
		</BasePage>
	);
};

export default LifecycleNotFound;
