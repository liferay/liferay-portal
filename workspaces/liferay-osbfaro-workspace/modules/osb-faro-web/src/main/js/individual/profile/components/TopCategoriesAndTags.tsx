import * as API from 'shared/api';
import React, {useCallback} from 'react';
import TopCategoriesAndTagsBaseCard, {
	ITopCategoriesAndTagsRequestVariables,
} from 'shared/components/TopCategoriesAndTagsBaseCard';

interface ITopCategoriesAndTagsProps {
	className?: string;
	individualId?: string;
}

const TopCategoriesAndTags: React.FC<ITopCategoriesAndTagsProps> = ({
	className,
	individualId,
}) => {
	const dataSourceFn = useCallback(
		({
			isCategory,
			...variables
		}: ITopCategoriesAndTagsRequestVariables & {individualId: string}) =>
			isCategory
				? API.categories.fetchIndividualTopCategories(variables)
				: API.tags.fetchIndividualTopTags(variables),
		[]
	);

	return (
		<TopCategoriesAndTagsBaseCard
			className={className}
			dataSourceFn={dataSourceFn}
			dataSourceParams={{individualId: individualId!}}
			skipRequest={!individualId}
		/>
	);
};

export default TopCategoriesAndTags;
