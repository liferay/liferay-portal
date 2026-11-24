import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import EmptyState from '@clayui/empty-state';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useParams} from 'react-router-dom';

interface ICriteriaSidebarDataSourceEmptyStateProps {
	description: string;
	title: string;
}

/**
 * Shown in a Conditions Library section whose data comes from a data source
 * that has not synced it. Only administrators can connect a data source, so
 * only they get the button.
 */
const CriteriaSidebarDataSourceEmptyState: React.FC<
	ICriteriaSidebarDataSourceEmptyStateProps
> = ({description, title}) => {
	const {groupId} = useParams();
	const currentUser = useCurrentUser();

	return (
		<div className="align-items-center d-flex empty-message h-100 justify-content-center">
			<EmptyState
				className="text-center"
				description={description}
				title={title}
			>
				<ClayLink
					decoration="underline"
					href={URLConstants.HelpConnectDxp}
					target="_blank"
				>
					{Liferay.Language.get('learn-more-about-data-sources')}

					<span className="inline-item inline-item-after">
						<ClayIcon fontSize={10} symbol="shortcut" />
					</span>
				</ClayLink>

				{currentUser.isAdmin() && (
					<ClayLink
						button
						className="button-root mt-3"
						displayType="secondary"
						href={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
							groupId,
						})}
					>
						{Liferay.Language.get('connect-data-source')}
					</ClayLink>
				)}
			</EmptyState>
		</div>
	);
};

export default CriteriaSidebarDataSourceEmptyState;
