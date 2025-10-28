import BasePage from 'settings/components/BasePage';
import React from 'react';
import SearchCard from './SearchCard';
import {getDefinitions} from 'shared/util/breadcrumbs';

interface ISearchProps {
	groupId: string;
}

export const Search: React.FC<ISearchProps> = ({groupId}) => (
	<BasePage
		breadcrumbItems={[
			getDefinitions({groupId}),
			{active: true, label: Liferay.Language.get('search')}
		]}
		pageDescription={Liferay.Language.get(
			'collect-your-propertys-search-data-by-defining-search-query-parameters'
		)}
		pageTitle={Liferay.Language.get('search')}
	>
		<SearchCard groupId={groupId} />
	</BasePage>
);

export default Search;
