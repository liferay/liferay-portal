import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import moment from 'moment';
import React, {useEffect, useState} from 'react';
import {Text as ClayText} from '@clayui/core';
import {Data, DropdownRangeKeyIProps} from './DropdownRangeKey';
import {DEFAULT_DATE_FORMAT} from 'shared/util/date';
import {DropdownRangeKeyDatePicker} from './DatePicker';
import {DropdownRangeKeyLegacy} from './DropdownRangeKeyLegacy';
import {formatTimeRange, getFilteredItems, getSelectedItem} from './utils';
import {MomentDateRange} from 'shared/components/DateRangeInput';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useLocation} from 'react-router-dom';
import {useRetentionPeriod} from 'shared/hooks/useRetentionPeriod';

export const DropdownRangeKeyContent: React.FC<
	DropdownRangeKeyIProps & {data: Data}
> = ({
	alignmentPosition = Align.BottomRight,
	bordered = false,
	data,

	/**
	 * Legacy can be removed once we convert all uses of
	 * DropdownRangeKey to include the new values
	 */
	legacy = true,
	onRangeSelectorChange,

	/**
	 * When legacy props is true, rangeKeys will be ignored.
	 */
	rangeKeys,
	rangeSelectors,
}) => {
	const [active, setActive] = useState(false);
	const [customDateRange, setCustomDateRange] = useState<MomentDateRange>({
		end: rangeSelectors?.rangeEnd
			? moment(rangeSelectors.rangeEnd, DEFAULT_DATE_FORMAT)
			: null,
		start: rangeSelectors?.rangeStart
			? moment(rangeSelectors.rangeStart, DEFAULT_DATE_FORMAT)
			: null,
	});
	const [seeMore, setSeeMore] = useState(false);
	const [showDatePicker, setShowDatePicker] = useState(false);
	const location = useLocation();
	const retentionPeriod = useRetentionPeriod();
	const timeRange = formatTimeRange(data.timeRange);
	const filteredItems = getFilteredItems({
		legacy,
		rangeKey: rangeSelectors?.rangeKey as RangeKeyTimeRanges,
		rangeKeys,
		retentionPeriod: retentionPeriod as number,
		seeMore,
		timeRange,
	});

	let selectedItem = null;

	if (rangeSelectors) {
		selectedItem = getSelectedItem({
			rangeEnd: rangeSelectors.rangeEnd,
			rangeKey: rangeSelectors.rangeKey,
			rangeStart: rangeSelectors.rangeStart,
			timeRange,
		});
	}

	useEffect(() => {
		const query = new URLSearchParams(location.search);

		if (query.get('downloadReport')) {
			if (onRangeSelectorChange) {
				onRangeSelectorChange({
					rangeEnd: query.get('rangeEnd') || '',
					rangeKey: query.get(
						'rangeKey'
					) as any as RangeKeyTimeRanges,
					rangeStart: query.get('rangeStart') || '',
				});
			}
		}
	}, [location.search]);

	useEffect(() => {
		if (customDateRange && customDateRange.end && customDateRange.start) {
			if (onRangeSelectorChange) {
				onRangeSelectorChange({
					rangeEnd: customDateRange.end.format(DEFAULT_DATE_FORMAT),
					rangeKey: RangeKeyTimeRanges.CustomRange,
					rangeStart:
						customDateRange.start.format(DEFAULT_DATE_FORMAT),
				});
			}

			setActive(false);
		}
	}, [customDateRange]);

	return (
		<ClayDropDown
			active={active}
			alignmentPosition={alignmentPosition}
			className="dropdown-range-key-root"
			menuElementAttrs={{
				className: getCN('dropdown-range-key-menu-root', {
					'show-date-picker': showDatePicker,
				}),
			}}
			onActiveChange={(active) => {
				setActive(active);
				setShowDatePicker(false);
				setSeeMore(false);
			}}
			trigger={
				<ClayButton
					borderless={!bordered}
					className="button-root"
					displayType="secondary"
					size="sm"
				>
					<ClayIcon className="icon-root mr-2" symbol="filter" />

					{selectedItem?.label ??
						Liferay.Language.get('select-date-range')}

					<ClayIcon
						className="icon-root ml-2"
						symbol="caret-bottom"
					/>
				</ClayButton>
			}
		>
			{showDatePicker ? (
				<DropdownRangeKeyDatePicker
					customDateRange={customDateRange}
					onCustomRangeChange={setCustomDateRange}
					retentionPeriod={retentionPeriod}
				/>
			) : (
				<ClayDropDown.ItemList>
					{filteredItems.map(({description, label, value}, index) => (
						<ClayDropDown.Item
							className={getCN('c-pointer', {
								active: selectedItem?.value === value,
							})}
							key={index}
							onClick={() => {
								setActive(false);

								onRangeSelectorChange &&
									onRangeSelectorChange({
										rangeEnd: '',
										rangeKey: value,
										rangeStart: '',
									});

								setCustomDateRange({
									end: null,
									start: null,
								});
							}}
						>
							<div>{label}</div>

							<ClayText size={1}>{description}</ClayText>
						</ClayDropDown.Item>
					))}

					{!legacy && (
						<DropdownRangeKeyLegacy
							onClickSeeMore={() => setSeeMore(true)}
							onClickShowDatePicker={() =>
								setShowDatePicker(true)
							}
							seeMore={seeMore}
							selectedItem={selectedItem}
						/>
					)}
				</ClayDropDown.ItemList>
			)}
		</ClayDropDown>
	);
};
