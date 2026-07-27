import * as API from 'shared/api';
import React, {useMemo} from 'react';
import SharedFilterPicker from 'shared/components/FilterPicker';
import {useLifecycle} from '../context/LifecycleContext';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

interface IProps {
	className?: string;
	entityLabel: string;
	fieldMappingFieldName: string;
	filterKey: 'countryFilter' | 'industryFilter';
}

const FilterPicker = ({
	className,
	entityLabel,
	fieldMappingFieldName,
	filterKey,
}: IProps) => {
	const {filters, updateFilters} = useLifecycle();

	const {channelId, groupId} = useParams();

	const {data, loading} = useRequest({
		dataSourceFn: API.accounts.fetchFieldValues,
		variables: {
			channelId,
			fieldMappingFieldName,
			groupId,
			query: '',
		},
	});

	// Field values are plain strings, so each one is its own id and name.

	const items = useMemo(
		() =>
			((data?.items ?? []) as string[]).map((item) => ({
				id: item,
				name: item,
			})),
		[data]
	);

	const selectedValue = filters[filterKey];

	return (
		<SharedFilterPicker
			className={className}
			entityLabel={entityLabel}
			items={items}
			loading={loading}
			onFilterChange={(item) =>
				updateFilters({[filterKey]: item?.id ?? ''})
			}
			selected={
				selectedValue ? {id: selectedValue, name: selectedValue} : null
			}
		/>
	);
};

export default FilterPicker;
