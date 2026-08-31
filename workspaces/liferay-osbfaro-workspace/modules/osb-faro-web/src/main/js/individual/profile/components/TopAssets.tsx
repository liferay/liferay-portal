import * as API from 'shared/api';
import React from 'react';
import TopAssetsBaseCard from 'shared/components/TopAssetsBaseCard';
import {pickBy} from 'lodash';

interface ITopAssetsProps {
	className?: string;
	individualId?: string;
	individualName?: string;
}

/**
 * The asset pages do not offer an individual filter yet, so the `individualId`
 * and `individualName` route queries mirror what the account card sends and are
 * ignored by the pages they land on.
 */

const TopAssets: React.FC<ITopAssetsProps> = ({
	className,
	individualId,
	individualName,
}) => (
	<TopAssetsBaseCard
		className={className}
		dataSourceFn={API.assets.fetchIndividualTopAssets}
		dataSourceParams={{individualId: individualId!}}
		routeQueries={pickBy({individualId, individualName})}
		showViewAll={false}
		skipRequest={!individualId}
	/>
);

export default TopAssets;
