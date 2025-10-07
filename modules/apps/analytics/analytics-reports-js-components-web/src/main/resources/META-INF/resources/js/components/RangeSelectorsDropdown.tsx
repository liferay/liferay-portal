/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayDatePicker from '@clayui/date-picker';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {dateUtils} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

type Item = {
	description?: string;
	hasChildren?: boolean;
	label: string;
	value: string;
};

enum View {
	CustomRange = 'custom-range',
	Default = 'default',
}

export enum RangeSelectors {
	Last24Hours = '0',
	Last28Days = '28',
	Last30Days = '30',
	Last7Days = '7',
	Last90Days = '90',
	Yesterday = '1',
	CustomRange = 'custom',
}

export type RangeSelector = {
	rangeEnd: string;
	rangeKey: RangeSelectors;
	rangeStart: string;
};

export interface IRangeSelectorsDropdown {
	activeRangeSelector: RangeSelector;
	availableRangeKeys: RangeSelectors[];
	className?: string;
	onChange: (rangeSelector: RangeSelector) => void;
	showDescription?: boolean;
	showIcon?: boolean;
	size?: 'sm' | 'xs';
}

const LAST_24_HOURS = {
	description: `${formatUTCDate(new Date(getDateByRangeSelector(RangeSelectors.Yesterday)).toISOString(), true)} - ${formatUTCDate(
		new Date().toISOString(),
		true
	)}`,
	label: Liferay.Util.sub(Liferay.Language.get('last-x-hours'), [24]),
	value: RangeSelectors.Last24Hours,
};

const LAST_7_DAYS = {
	description: formatDateRange(RangeSelectors.Last7Days),
	label: Liferay.Util.sub(Liferay.Language.get('last-x-days'), [7]),
	value: RangeSelectors.Last7Days,
};

const LAST_28_DAYS = {
	description: formatDateRange(RangeSelectors.Last28Days),
	label: Liferay.Util.sub(Liferay.Language.get('last-x-days'), [28]),
	value: RangeSelectors.Last28Days,
};

const LAST_30_DAYS = {
	description: formatDateRange(RangeSelectors.Last30Days),
	label: Liferay.Util.sub(Liferay.Language.get('last-x-days'), [30]),
	value: RangeSelectors.Last30Days,
};

const LAST_90_DAYS = {
	description: formatDateRange(RangeSelectors.Last90Days),
	label: Liferay.Util.sub(Liferay.Language.get('last-x-days'), [90]),
	value: RangeSelectors.Last90Days,
};

const buildRangeSelectors = (showDescription = true): Item[] => [
	{
		...LAST_24_HOURS,
		description: showDescription ? LAST_24_HOURS.description : undefined,
	},
	{
		...LAST_7_DAYS,
		description: showDescription ? LAST_7_DAYS.description : undefined,
	},
	{
		...LAST_28_DAYS,
		description: showDescription ? LAST_28_DAYS.description : undefined,
	},
	{
		...LAST_30_DAYS,
		description: showDescription ? LAST_30_DAYS.description : undefined,
	},
	{
		...LAST_90_DAYS,
		description: showDescription ? LAST_90_DAYS.description : undefined,
	},
	{
		label: Liferay.Language.get('custom-range'),
		value: RangeSelectors.CustomRange,
	},
];

interface IView {
	activeRangeSelector: RangeSelector;
	availableRangeSelectors: Item[];
	onActiveChange: (active: boolean) => void;
	onChange: (rangeSelector: RangeSelector) => void;
	onViewChange: (view: View) => void;
	triggerRef: React.RefObject<HTMLButtonElement> | null;
}

const CustomRangeView: React.FC<Omit<IView, 'availableRangeSelectors'>> = ({
	onActiveChange,
	onChange,
	onViewChange,
	triggerRef,
}) => {
	const [rangeStart, setRangeStart] = useState('');
	const [rangeEnd, setRangeEnd] = useState('');

	return (
		<>
			<div className="align-items-center d-flex dropdown-header pl-3">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('cancel')}
					borderless
					className="mr-2"
					data-testid="cancel-button"
					displayType="secondary"
					monospaced
					onClick={() => onViewChange(View.Default)}
					size="sm"
					symbol="angle-left"
				/>

				<span className="text-uppercase">
					<Text color="secondary" size={3} weight="semi-bold">
						{Liferay.Language.get('create-date-range')}
					</Text>
				</span>
			</div>

			<ClayDropdown.Item>
				<div data-testid="range-start">
					<label htmlFor="rangeStartId">
						{Liferay.Language.get('from')}
					</label>

					<ClayDatePicker
						ariaLabels={{
							buttonChooseDate: `${Liferay.Language.get(
								'select-date'
							)}`,
							buttonDot: `${Liferay.Language.get(
								'select-current-date'
							)}`,
							buttonNextMonth: `${Liferay.Language.get(
								'select-next-month'
							)}`,
							buttonPreviousMonth: `${Liferay.Language.get(
								'select-previous-month'
							)}`,
							dialog: `${Liferay.Language.get('select-date')}`,
							selectMonth: `${Liferay.Language.get('select-a-month')}`,
							selectYear: `${Liferay.Language.get('select-a-year')}`,
						}}
						dateFormat="yyyy-MM-dd"
						firstDayOfWeek={dateUtils.getFirstDayOfWeek()}
						inputName="rangeStartId"
						months={[
							`${Liferay.Language.get('january')}`,
							`${Liferay.Language.get('february')}`,
							`${Liferay.Language.get('march')}`,
							`${Liferay.Language.get('april')}`,
							`${Liferay.Language.get('may')}`,
							`${Liferay.Language.get('june')}`,
							`${Liferay.Language.get('july')}`,
							`${Liferay.Language.get('august')}`,
							`${Liferay.Language.get('september')}`,
							`${Liferay.Language.get('october')}`,
							`${Liferay.Language.get('november')}`,
							`${Liferay.Language.get('december')}`,
						]}
						onChange={setRangeStart}
						onClick={() => triggerRef?.current?.focus()}
						placeholder="YYYY-MM-DD"
						value={rangeStart}
						weekdaysShort={dateUtils.getWeekdaysShort()}
						years={{
							end: new Date().getFullYear() + 25,
							start: new Date().getFullYear() - 50,
						}}
					/>
				</div>

				<div data-testid="range-end">
					<label className="mt-2" htmlFor="rangeEndId">
						{Liferay.Language.get('to[date-time]')}
					</label>

					<ClayDatePicker
						dateFormat="yyyy-MM-dd"
						inputName="rangeEndId"
						onChange={setRangeEnd}
						placeholder="YYYY-MM-DD"
						value={rangeEnd}
						years={{
							end: new Date().getFullYear() + 25,
							start: new Date().getFullYear() - 50,
						}}
					/>
				</div>
			</ClayDropdown.Item>

			<ClayDropdown.Divider />

			<ClayDropdown.Caption>
				<ClayButton
					block
					disabled={!rangeStart || !rangeEnd}
					onClick={() => {
						onViewChange(View.Default);

						onChange({
							rangeEnd,
							rangeKey: RangeSelectors.CustomRange,
							rangeStart,
						});

						onActiveChange(false);

						triggerRef?.current?.focus();
					}}
				>
					{Liferay.Language.get('add-filter')}
				</ClayButton>
			</ClayDropdown.Caption>
		</>
	);
};

const DefaultView: React.FC<IView> = ({
	activeRangeSelector,
	availableRangeSelectors,
	onActiveChange,
	onChange,
	onViewChange,
	triggerRef,
}) => {
	const hasCustomRange = availableRangeSelectors.some(
		(item) => item.value === RangeSelectors.CustomRange
	);

	return (
		<>
			{availableRangeSelectors.map((item) => {
				if (item.value === RangeSelectors.CustomRange) {
					return null;
				}

				return (
					<ClayDropdown.Item
						active={item.value === activeRangeSelector.rangeKey}
						data-testid={`filter-item-${item.value}`}
						key={item.value}
						onClick={() => {
							onChange({
								rangeEnd: '',
								rangeKey: item.value as RangeSelectors,
								rangeStart: '',
							});

							onActiveChange(false);
							triggerRef?.current?.focus();
						}}
						symbolLeft={
							item.value === activeRangeSelector.rangeKey
								? 'check'
								: ''
						}
					>
						<div>
							<Text size={4}>{item.label}</Text>
						</div>

						{item.description && (
							<Text size={1}>
								<span className="text-uppercase">
									{item.description}
								</span>
							</Text>
						)}
					</ClayDropdown.Item>
				);
			})}

			{hasCustomRange && (
				<ClayDropdown.Item
					onClick={() => onViewChange(View.CustomRange)}
					symbolLeft={
						activeRangeSelector.rangeKey ===
						RangeSelectors.CustomRange
							? 'check'
							: ''
					}
					symbolRight="angle-right"
				>
					<div>
						<Text size={4}>
							{Liferay.Language.get('custom-range')}
						</Text>
					</div>
				</ClayDropdown.Item>
			)}
		</>
	);
};

const Views = {
	[View.CustomRange]: CustomRangeView,
	[View.Default]: DefaultView,
};

const RangeSelectorsDropdown: React.FC<IRangeSelectorsDropdown> = ({
	activeRangeSelector,
	availableRangeKeys,
	className,
	onChange,
	showDescription = true,
	showIcon = false,
	size = 'sm',
}) => {
	const [dropdownActive, setDropdownActive] = useState(false);
	const [view, setView] = useState<View>(View.Default);

	const allSelectors = buildRangeSelectors(showDescription);
	const filteredSelectors = availableRangeKeys
		? allSelectors.filter((item) =>
				availableRangeKeys.includes(item.value as RangeSelectors)
			)
		: allSelectors;

	const triggerLabel = () => {
		if (activeRangeSelector.rangeKey === RangeSelectors.CustomRange) {
			return `${activeRangeSelector.rangeStart} - ${activeRangeSelector.rangeEnd}`;
		}

		return (
			filteredSelectors.find(
				({value}) => value === activeRangeSelector.rangeKey
			)?.label ?? ''
		);
	};

	const triggerRef = useRef<HTMLButtonElement | null>(null);

	const ViewComponent = Views[view];

	return (
		<ClayDropdown
			active={dropdownActive}
			className={classNames('range-selector-dropdown', className)}
			closeOnClick={false}
			closeOnClickOutside
			hasLeftSymbols={view === View.Default}
			onActiveChange={setDropdownActive}
			trigger={
				<ClayButton
					aria-label={triggerLabel()}
					borderless
					data-testid="rangeSelectors"
					displayType="secondary"
					ref={(node: HTMLButtonElement) => {
						triggerRef.current = node;
					}}
					size={size}
				>
					<span className="align-items-center d-flex ml-2 range-selector-dropdown__trigger-label">
						{showIcon && (
							<ClayIcon className="mr-2" symbol="date" />
						)}

						{triggerLabel()}

						<ClayIcon className="ml-2" symbol="caret-bottom" />
					</span>
				</ClayButton>
			}
		>
			<ViewComponent
				activeRangeSelector={activeRangeSelector}
				availableRangeSelectors={filteredSelectors}
				onActiveChange={setDropdownActive}
				onChange={onChange}
				onViewChange={setView}
				triggerRef={triggerRef}
			/>
		</ClayDropdown>
	);
};

function formatUTCDate(dateString: string, displayTime: boolean = false) {
	const date = new Date(dateString);

	const months = [
		'JAN',
		'FEB',
		'MAR',
		'APR',
		'MAY',
		'JUN',
		'JUL',
		'AUG',
		'SEP',
		'OCT',
		'NOV',
		'DEC',
	];

	const day = String(date.getUTCDate()).padStart(2, '0');
	const month = months[date.getUTCMonth()];

	let hour = date.getUTCHours();

	const suffix = hour >= 12 ? 'P.M.' : 'A.M.';

	hour = hour % 12;

	if (hour === 0) {
		hour = 12;
	}

	if (displayTime) {
		return `${month} ${day}, ${hour.toString().padStart(2, '0')} ${suffix}`;
	}

	return `${month} ${day}`;
}

function getDateByRangeSelector(rangeSelector: RangeSelectors) {
	return new Date().setDate(new Date().getDate() - Number(rangeSelector));
}

function formatDateRange(
	startRangeSelector: RangeSelectors,
	endRangeSelector: RangeSelectors = RangeSelectors.Yesterday
) {
	const formatDate = (date: Date, endOfDay = false) =>
		new Date(
			Date.UTC(
				date.getUTCFullYear(),
				date.getUTCMonth(),
				date.getUTCDate(),
				endOfDay ? 23 : 0,
				endOfDay ? 59 : 0,
				endOfDay ? 59 : 0
			)
		).toISOString();

	const startDate = formatDate(
		new Date(getDateByRangeSelector(startRangeSelector))
	);
	const endDate = formatDate(
		new Date(getDateByRangeSelector(endRangeSelector)),
		true
	);

	return `${formatUTCDate(startDate)} - ${formatUTCDate(endDate)}`;
}

export function getSafeRangeSelector(rangeSelector: RangeSelector) {
	return {
		rangeEnd: rangeSelector.rangeEnd,
		rangeKey:
			rangeSelector.rangeKey !== RangeSelectors.CustomRange
				? rangeSelector.rangeKey
				: '',
		rangeStart: rangeSelector.rangeStart,
	};
}

export {RangeSelectorsDropdown};
