import * as API from 'shared/api';
import React, {useCallback} from 'react';
import TopCategoriesAndTagsBaseCard, {
	ITopCategoriesAndTagsRequestVariables,
} from 'shared/components/TopCategoriesAndTagsBaseCard';
import {IAccount} from './AccountInfo';

interface ITopCategoriesAndTagsProps {
	account?: IAccount;
	className?: string;
}

const TopCategoriesAndTags: React.FC<ITopCategoriesAndTagsProps> = ({
	account,
	className,
}) => {
	const accountId = account?.id;

	const dataSourceFn = useCallback(
		({
			isCategory,
			...variables
		}: ITopCategoriesAndTagsRequestVariables & {accountId: string}) =>
			isCategory
				? API.categories.fetchAccountTopCategories(variables)
				: API.tags.fetchAccountTopTags(variables),
		[]
	);

	return (
		<TopCategoriesAndTagsBaseCard
			className={className}
			dataSourceFn={dataSourceFn}
			dataSourceParams={{accountId: accountId!}}
			skipRequest={!accountId}
		/>
	);
};

export default TopCategoriesAndTags;
