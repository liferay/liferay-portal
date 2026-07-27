import FilterPicker, {IFilterPickerItem} from 'shared/components/FilterPicker';
import React from 'react';
import {fetchPageExperience} from 'shared/api/experiences';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const NO_ITEMS: IFilterPickerItem[] = [];

interface IExperienceDropdownProps {
	className?: string;
	onChange: (experienceId: string | null) => void;
}

const ExperienceDropdown: React.FC<IExperienceDropdownProps> = ({
	className,
	onChange,
}) => {
	const {channelId, groupId, title, touchpoint} = useParams();

	const {data, loading} = useRequest({
		dataSourceFn: fetchPageExperience,
		variables: {
			canonicalUrl: touchpoint!,
			channelId: channelId!,
			groupId: groupId!,
			pageTitle: title!,
		},
	});

	return (
		<FilterPicker
			className={className}
			entityLabel={Liferay.Language.get('experiences')}
			items={Array.isArray(data) ? data : NO_ITEMS}
			loading={loading}
			onFilterChange={(item) => onChange(item?.id ?? null)}
		/>
	);
};

export default ExperienceDropdown;
