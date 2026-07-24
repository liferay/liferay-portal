import FilterPickerTrigger from './FilterPickerTrigger';
import React, {useMemo, useState} from 'react';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {Option, Picker, Text} from '@clayui/core';
import {truncateText} from 'shared/util/util';

const MAX_NAME_LENGTH = 35;

/**
 * Sentinel key for the "all items" entry. `Picker` requires a string key, so a
 * `null` id is encoded as the string `'null'`.
 */

const ALL_ITEMS_KEY = 'null';

export type FilterPickerItem = {
	disabled?: boolean;
	id: string | null;
	name: string;
};

type DisplayItem = FilterPickerItem & {displayName: string; id: string};

interface IFilterPickerProps {
	allItemsLabel: string;
	className?: string;
	items: FilterPickerItem[];
	onFilterChange: (item: {id: string; name: string} | null) => void;

	/**
	 * Item to list on top of `items` while it is not among them — used to show
	 * a preselected filter that the fetched page does not include.
	 */

	preloadedItem?: FilterPickerItem | null;
}

/**
 * Searchable filter dropdown, prepended with an "all items" entry. Shared by
 * the filters rendered in the dashboard sub-headers.
 */

const FilterPicker: React.FC<IFilterPickerProps> = ({
	allItemsLabel,
	className,
	items,
	onFilterChange,
	preloadedItem,
}) => {
	const [selectedKey, setSelectedKey] = useState<string>(
		preloadedItem?.id ?? ALL_ITEMS_KEY
	);

	const displayItems: DisplayItem[] = useMemo(() => {
		const hasSelectedItem = items.some(
			(item) => String(item.id) === selectedKey
		);

		const preloadedItems =
			preloadedItem?.id === selectedKey && !hasSelectedItem
				? [preloadedItem as FilterPickerItem]
				: [];

		return [
			{id: null, name: allItemsLabel},
			...preloadedItems,
			...items,
		].map((item) => ({
			...item,
			displayName: truncateText(item.name, MAX_NAME_LENGTH, null),
			id: item.id === null ? ALL_ITEMS_KEY : String(item.id),
		}));
	}, [allItemsLabel, items, preloadedItem, selectedKey]);

	const handleSelectionChange = (key: string) => {
		setSelectedKey(key);

		if (key === ALL_ITEMS_KEY) {
			onFilterChange(null);

			return;
		}

		const selectedItem = displayItems.find((item) => item.id === key);

		onFilterChange(
			selectedItem ? {id: selectedItem.id, name: selectedItem.name} : null
		);
	};

	return (
		<ClayTooltipProvider>
			<div className={className}>
				<Picker
					aria-label={allItemsLabel}
					as={FilterPickerTrigger}
					className="border-light form-control-sm"
					items={displayItems}
					onSelectionChange={(key) =>
						handleSelectionChange(String(key))
					}
					searchable
					selectedKey={selectedKey}
				>
					{(item: DisplayItem) => (
						<Option
							disabled={item.disabled}
							key={item.id}
							textValue={item.name}
						>
							<div
								className="w-100"
								title={
									item.name.length > MAX_NAME_LENGTH
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

export default FilterPicker;
