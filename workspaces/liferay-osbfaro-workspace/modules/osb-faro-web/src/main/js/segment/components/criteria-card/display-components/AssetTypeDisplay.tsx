import * as API from 'shared/api';
import React from 'react';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

interface IAssetTypeDisplayProps {
	objectDefinitionName: string;
}

const AssetTypeDisplay: React.FC<IAssetTypeDisplayProps> = ({
	objectDefinitionName,
}) => {
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const {data, loading} = useRequest<
		{
			channelId: string;
			groupId: string;
			page: number;
			pageSize: number;
			rangeKey: null;
		},
		{items: Array<{id: string; name: string}>}
	>({
		dataSourceFn: (variables) => API.assets.searchTypes(variables),
		variables: {
			channelId,
			groupId,
			page: 1,
			pageSize: 100,
			rangeKey: null,
		},
	});

	if (loading) {
		return null;
	}

	const assetType = data?.items.find(({id}) => id === objectDefinitionName);

	return <span>{assetType?.name ?? objectDefinitionName}</span>;
};

export default AssetTypeDisplay;
