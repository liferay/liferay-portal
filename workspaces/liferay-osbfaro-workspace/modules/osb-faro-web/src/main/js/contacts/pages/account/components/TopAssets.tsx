import * as API from 'shared/api';
import React from 'react';
import TopAssetsBaseCard from 'shared/components/TopAssetsBaseCard';
import {IAccount} from './AccountInfo';
import {pickBy} from 'lodash';

interface ITopAssetsProps {
	account?: IAccount;
	className?: string;
}

const TopAssets: React.FC<ITopAssetsProps> = ({account, className}) => {
	const accountId = account?.id;
	const accountName = account?.accountName;

	return (
		<TopAssetsBaseCard
			className={className}
			dataSourceFn={API.assets.fetchAccountTopAssets}
			dataSourceParams={{accountId: accountId!}}
			routeQueries={pickBy({accountId, accountName})}
			skipRequest={!accountId}
		/>
	);
};

export default TopAssets;
