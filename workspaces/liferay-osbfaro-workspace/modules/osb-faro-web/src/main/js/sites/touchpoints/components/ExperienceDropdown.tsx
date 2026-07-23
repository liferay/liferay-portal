import classNames from 'classnames';
import FilterPickerTrigger from 'shared/components/FilterPickerTrigger';
import React, {useMemo, useState} from 'react';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {fetchPageExperience} from 'shared/api/experiences';
import {Option, Picker, Text} from '@clayui/core';
import {truncateText} from 'shared/util/util';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

interface IExperienceItem {
	id: string | null;
	name: string;
	displayName?: string;
}

const ALL_EXPERIENCES_ITEM: IExperienceItem = {
	id: null,
	name: Liferay.Language.get('all-experiences'),
};

interface IExperienceDropdownProps {
	className?: string;
	onChange: (experienceId: string | null) => void;
}

const ExperienceDropdown: React.FC<IExperienceDropdownProps> = ({
	className,
	onChange,
}) => {
	const {channelId, groupId, title, touchpoint} = useParams();

	const [selectedKey, setSelectedKey] = useState<string>('null');

	const {data} = useRequest({
		dataSourceFn: fetchPageExperience,
		variables: {
			canonicalUrl: touchpoint!,
			channelId: channelId!,
			groupId: groupId!,
			pageTitle: title!,
		},
	});

	const displayItems = useMemo(() => {
		const apiItems: IExperienceItem[] = Array.isArray(data) ? data : [];

		return [ALL_EXPERIENCES_ITEM, ...apiItems].map((item) => ({
			...item,
			displayName: truncateText(item.name, 35, null),
			id: item.id === null ? 'null' : String(item.id),
		}));
	}, [data]);

	const handleSelectionChange = (key: string) => {
		setSelectedKey(key);
		const valueForBackend = key === 'null' ? null : key;
		onChange(valueForBackend);
	};

	return (
		<ClayTooltipProvider>
			<div className={classNames('experience-dropdown', className)}>
				<Picker
					aria-label={Liferay.Language.get('all-experiences')}
					as={FilterPickerTrigger}
					className="border-light form-control-sm"
					items={displayItems}
					onSelectionChange={(key) =>
						handleSelectionChange(String(key))
					}
					searchable
					selectedKey={selectedKey}
				>
					{(item: IExperienceItem) => (
						<Option key={String(item.id)} textValue={item.name}>
							<div
								className="w-100"
								title={
									item.name.length > 35
										? item.name
										: undefined
								}
							>
								<Text size={3}>{item.displayName}</Text>
							</div>
						</Option>
					)}
				</Picker>
			</div>
		</ClayTooltipProvider>
	);
};

export default ExperienceDropdown;
