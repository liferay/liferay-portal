import AppliedFilters from 'shared/components/filter/AppliedFilters';
import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import DropdownMenu from 'shared/components/filter/DropdownMenu';
import React, {useEffect, useRef, useState} from 'react';
import remove from 'lodash/remove';

interface IFilterProps {
	items?: Item[];
	onChange: (param: SelectedItems) => void;
}

type Item = {
	category: string;
	checked?: boolean;
	items: Item[] | null;
	hasSearch: boolean;
	inputType: string;
	label: string;
	value: string;
};

type SelectedItems = {[key: string]: string[]};

const Filter: React.FC<IFilterProps> = ({
	items: initialItems = [],
	onChange,
}) => {
	const [selectedItems, setSelectedItems] = useState<SelectedItems>({});
	const [showDropdown, setShowDropdown] = useState(false);

	const [items, setItems] = useState(initialItems);

	const _elementRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		document.addEventListener('click', handleDocClick);

		return () => {
			document.removeEventListener('click', handleDocClick);
		};
	}, []);

	useEffect(() => {
		setItems(getCheckedItems(initialItems));
	}, [initialItems]);

	const getCheckedItems = (parentItems: Item[]): Item[] =>
		parentItems.map((item) => {
			const categoryItems = selectedItems[item.category];

			let childItems = null;

			if (item.items) {
				childItems = getCheckedItems(item.items);
			}

			if (categoryItems && categoryItems.indexOf(item.label) > -1) {
				return {
					...item,
					checked: true,
					items: childItems,
				};
			}

			return {...item, items: childItems};
		});

	const updateRadioItems = ({category, label}: Item): void => {
		handleUpdateFilters({...selectedItems, [category]: [label]});
	};

	const updateCheckboxItems = ({category, checked, label}: Item): void => {
		const categoryItems = selectedItems[category] || [];

		if (checked) {
			categoryItems.push(label);
		}
		else {
			remove(categoryItems, (n) => n === label);
		}

		selectedItems[category] = categoryItems;

		handleUpdateFilters({...selectedItems});
	};

	const handleChangeDropdownItem = ({
		dropdownItem,
	}: {
		dropdownItem: Item;
	}): void => {
		if (dropdownItem.inputType == 'radio') {
			updateRadioItems(dropdownItem);
		}
		else if (dropdownItem.inputType == 'checkbox') {
			updateCheckboxItems(dropdownItem);
		}
	};

	const handleClickToggleDropdown = (): void => {
		setShowDropdown(!showDropdown);
	};

	const handleDocClick = ({target}: Event): void => {
		if (!_elementRef.current) return;

		const dropdown = _elementRef.current.querySelector(
			'.analytics-dropdown'
		);
		const dropdownMenu: Element[] = Object.assign(
			[],
			document.querySelectorAll('.analytics-dropdown-menu')
		);

		if (
			(dropdown && dropdown.contains(target as Node)) ||
			dropdownMenu.find((menu) => menu.contains(target as Node))
		)
			return;

		setShowDropdown(false);
	};

	const handleUpdateFilters = (selectedItems: SelectedItems): void => {
		onChange(selectedItems);

		setSelectedItems(selectedItems);
	};

	return (
		<div className="analytics-filter" ref={_elementRef}>
			<div className="analytics-dropdown dropdown btn-group border-0">
				<ClayButton
					aria-label="Dropdown Filter"
					className="dropdown-toggle btn-outline-borderless"
					displayType="secondary"
					onClick={handleClickToggleDropdown}
					size="sm"
				>
					{Liferay.Language.get('filter')}

					<ClayIcon
						className="icon-root ml-2"
						symbol="caret-bottom"
					/>
				</ClayButton>
			</div>

			<DropdownMenu
				items={getCheckedItems(items)}
				onSelectItemsChange={handleChangeDropdownItem}
				show={showDropdown}
			/>

			<AppliedFilters
				filters={selectedItems}
				onChange={handleUpdateFilters}
			/>
		</div>
	);
};

export {Filter};
export default Filter;
