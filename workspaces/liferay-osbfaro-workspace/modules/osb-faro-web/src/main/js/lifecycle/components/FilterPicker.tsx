import * as API from 'shared/api';
import React from 'react';
import SharedFilterPicker, {
	IFilterPickerItem,
} from 'shared/components/FilterPicker';
import {useLifecycle} from '../context/LifecycleContext';
import {useParams} from 'react-router-dom';

// Field values are plain strings, so each one is its own id and name.

const normalizeFieldValues = (data: {items?: string[]}): IFilterPickerItem[] =>
	(data?.items ?? []).map((item) => ({id: item, name: item}));

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

	const selectedValue = filters[filterKey];

	return (
		<SharedFilterPicker
			className={className}
			dataSourceFn={API.accounts.fetchFieldValues}
			entityLabel={entityLabel}
			normalize={normalizeFieldValues}
			onFilterChange={(item) =>
				updateFilters({[filterKey]: item?.id ?? ''})
			}
			selected={
				selectedValue ? {id: selectedValue, name: selectedValue} : null
			}
			variables={{
				channelId,
				fieldMappingFieldName,
				groupId,
			}}
		/>
	);
};

export default FilterPicker;
