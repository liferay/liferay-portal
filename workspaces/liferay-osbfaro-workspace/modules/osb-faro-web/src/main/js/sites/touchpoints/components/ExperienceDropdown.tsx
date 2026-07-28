import FilterPicker from 'shared/components/FilterPicker';
import React from 'react';
import {fetchPageExperience} from 'shared/api/experiences';
import {useParams} from 'react-router-dom';

interface IExperienceDropdownProps {
	className?: string;
	onChange: (experienceId: string | null) => void;
}

const ExperienceDropdown: React.FC<IExperienceDropdownProps> = ({
	className,
	onChange,
}) => {
	const {channelId, groupId, title, touchpoint} = useParams();

	return (
		<FilterPicker
			className={className}
			dataSourceFn={fetchPageExperience}
			entityLabel={Liferay.Language.get('experiences')}
			onFilterChange={(item) => onChange(item?.id ?? null)}
			variables={{
				canonicalUrl: touchpoint!,
				channelId: channelId!,
				groupId: groupId!,
				pageTitle: title!,
			}}
		/>
	);
};

export default ExperienceDropdown;
