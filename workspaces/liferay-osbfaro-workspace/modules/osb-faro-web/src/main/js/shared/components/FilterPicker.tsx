import classNames from 'classnames';
import ClayButton from '@clayui/button';
import Loading, {Align} from 'shared/components/Loading';
import React, {useMemo, useState} from 'react';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {Icon, Option, Picker} from '@clayui/core';
import {sub} from 'shared/util/lang';
import {truncateText} from 'shared/util/util';

const MAX_LABEL_LENGTH = 35;

/**
 * Sentinel id for the "all items" entry. `Picker` keys its collection by
 * strings, so the absence of a filter is encoded rather than left as `null`.
 */

const ALL_VALUES_KEY = 'all';

/**
 * The shape `Picker` consumes and every filter endpoint already returns, so no
 * caller has to rename its payload on the way in or out.
 */

export interface IFilterPickerItem {
	disabled?: boolean;
	id: string;
	name: string;
}

interface ITriggerButtonProps
	extends React.ButtonHTMLAttributes<HTMLButtonElement> {
	buttonClassName?: string;
	filterLabel: string;
	loading?: boolean;
}

/**
 * `Picker` hands its trigger a `className` of `form-control form-control-select
 * form-control-select-secondary`, which would style this as a select and make it
 * taller than the buttons beside it. So the caller's classes arrive as
 * `buttonClassName` — a prop `Picker` passes straight through — and the
 * `className` set after the spread below replaces Clay's rather than merging.
 */

const TriggerButton = React.forwardRef<HTMLButtonElement, ITriggerButtonProps>(
	({buttonClassName, filterLabel, loading, ...rest}, ref) => (
		<ClayButton
			{...rest}
			className={classNames(

				// The trigger sits in a flex sub-header, where a wrapping label
				// would make the button twice as tall as its siblings.

				buttonClassName,
				'rounded-lg',
				'text-nowrap'
			)}
			disabled={loading}
			displayType="secondary"
			ref={ref}
			size="sm"
		>
			<Icon className="inline-item inline-item-before" symbol="filter" />

			{filterLabel}

			{loading ? (
				<Loading align={Align.Right} />
			) : (
				<Icon
					className="inline-item inline-item-after"
					symbol="caret-bottom"
				/>
			)}
		</ClayButton>
	)
);

TriggerButton.displayName = 'TriggerButton';

/**
 * Hoisted so its identity stays stable: `Picker` rebuilds its whole collection
 * whenever either `items` or `children` changes.
 */

const renderOption = (item: IFilterPickerItem) => (
	<Option disabled={item.disabled} key={item.id} textValue={item.name}>
		<div
			className="w-100"
			title={item.name.length > MAX_LABEL_LENGTH ? item.name : undefined}
		>
			{truncateText(item.name, MAX_LABEL_LENGTH, null)}
		</div>
	</Option>
);

interface IFilterPickerProps {
	className?: string;

	/**
	 * Plural name of what is being filtered, used for the "All <entity>" label
	 * and for the accessible name.
	 */

	entityLabel: string;
	items: IFilterPickerItem[];
	loading?: boolean;
	onFilterChange: (item: IFilterPickerItem | null) => void;

	/**
	 * The selected item, for callers that own the selection (a URL query, a
	 * context). Taking the whole item rather than just its id lets a selection
	 * display even when the fetched page does not contain it. Leave it
	 * undefined to let this component track the selection itself.
	 */

	selected?: IFilterPickerItem | null;
}

/**
 * The dashboard filter dropdown: a searchable `Picker` behind a secondary
 * button, prepended with an "All <entity>" entry. Shared by every filter in a
 * page sub-header so they stay visually and behaviorally identical.
 */

const FilterPicker: React.FC<IFilterPickerProps> = ({
	className,
	entityLabel,
	items,
	loading,
	onFilterChange,
	selected,
}) => {
	const [ownSelected, setOwnSelected] = useState<IFilterPickerItem | null>(
		null
	);

	const controlled = selected !== undefined;

	const selectedItem = controlled ? selected : ownSelected;

	const allValuesLabel = sub(Liferay.Language.get('all-x'), [
		entityLabel,
	]) as string;

	const options = useMemo(
		() => [{id: ALL_VALUES_KEY, name: allValuesLabel}, ...items],
		[allValuesLabel, items]
	);

	const handleSelectionChange = (key: string) => {
		const item =
			key === ALL_VALUES_KEY
				? null
				: items.find(({id}) => id === key) ?? null;

		if (!controlled) {
			setOwnSelected(item);
		}

		onFilterChange(item);
	};

	return (
		<ClayTooltipProvider>
			<div>
				<Picker
					aria-label={
						sub(Liferay.Language.get('filter-by-x'), [
							entityLabel,
						]) as string
					}
					as={TriggerButton}
					buttonClassName={className}
					filterLabel={
						selectedItem
							? truncateText(
									selectedItem.name,
									MAX_LABEL_LENGTH,
									null
								)
							: allValuesLabel
					}
					items={options}
					loading={loading}
					onSelectionChange={(key) =>
						handleSelectionChange(String(key))
					}
					searchable
					selectedKey={selectedItem?.id ?? ALL_VALUES_KEY}
					width={240}
				>
					{renderOption}
				</Picker>
			</div>
		</ClayTooltipProvider>
	);
};

export default FilterPicker;
