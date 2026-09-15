import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import URLConstants from 'shared/util/url-constants';
import {Routes, toRoute} from 'shared/util/router';
import {Sizes} from 'shared/util/constants';

interface IDataSourceEmptyStateProps {
	authorized: boolean;
	description: string;
	groupId: string;
	title: string;
}

const DataSourceEmptyState: React.FC<IDataSourceEmptyStateProps> = ({
	authorized,
	description,
	groupId,
	title,
}) => (
	<NoResultsDisplay
		description={
			<>
				<p className="mb-2">{description}</p>

				<ClayLink
					className="d-block mb-3"
					decoration="underline"
					href={URLConstants.DataSourceConnection}
					target="_blank"
				>
					{Liferay.Language.get('learn-more-about-data-sources')}

					<span className="inline-item inline-item-after">
						<ClayIcon fontSize={8} symbol="shortcut" />
					</span>
				</ClayLink>
			</>
		}
		displayCard
		icon={{
			border: false,
			size: Sizes.XXXLarge,
			symbol: 'ac_satellite',
		}}
		spacer
		title={title}
	>
		{authorized ? (
			<ClayLink
				button
				className="button-root mt-1"
				displayType="primary"
				href={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId})}
			>
				{Liferay.Language.get('connect-data-source')}
			</ClayLink>
		) : undefined}
	</NoResultsDisplay>
);

export default DataSourceEmptyState;
