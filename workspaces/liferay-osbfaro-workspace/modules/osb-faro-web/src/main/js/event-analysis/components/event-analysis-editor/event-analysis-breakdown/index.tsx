import BarComparisonCell from './BarComparisonCell';
import EventAnalysisResultQuery, {
	EventAnalysisResultData,
	EventAnalysisResultVariables,
} from 'event-analysis/queries/EventAnalysisResultQuery';
import getCN from 'classnames';
import PercentOfCell from './PercentOfCell';
import React, {useEffect, useRef} from 'react';
import Table from 'shared/components/table';
import TextTruncate from 'shared/components/TextTruncate';
import {
	Attributes,
	Breakdown,
	BreakdownData,
	BreakdownDataItem,
	Breakdowns,
	CalculationTypes,
	Event,
	Filters,
	ParsedBreakdownData,
	ParsedBreakdownItem,
} from 'event-analysis/utils/types';
import {EditBreakdown, useAttributes} from '../context/attributes';
import {get, isNil, omit} from 'lodash';
import {getMaxEventValue, parseBreakdownData} from 'event-analysis/utils/utils';
import {
	getSafeDecodedURIComponent,
	getSafeRangeSelectors,
} from 'shared/util/util';
import {OrderedMap} from 'immutable';
import {SafeResults} from 'shared/hoc/util';
import {sub} from 'shared/util/lang';
import {useQuery} from '@apollo/client';
import {useStatefulPagination} from 'shared/hooks/useStatefulPagination';
import {withPaginationBar} from 'shared/hoc';
import {WithRangeKeyProps} from 'shared/hoc/WithRangeKey';

interface IBreakdownWithSafeResultsProps extends WithRangeKeyProps {
	channelId: string;
	compareToPrevious: boolean;
	event: Event;
	type: CalculationTypes;
}

interface IBreakdownTableProps
	extends WithRangeKeyProps,
		React.HTMLAttributes<HTMLElement> {
	attributes: Attributes;
	breakdownOrder: string[];
	breakdowns: Breakdowns;
	compareToPrevious: boolean;
	delta: number;
	editBreakdown: EditBreakdown;
	event: Event;
	eventAnalysisResult: EventAnalysisResultData;
	filters: Filters;
	onDeltaChange: (page: number) => void;
	onPageChange: (page: number) => void;
	page: number;
	type: CalculationTypes;
}

const getBreakdownByAccessor = (
	accessor: string,
	breakdownOrder: string[],
	breakdowns: Breakdowns
): Breakdown => {
	const orderIndex = Number(accessor.split('breakdown').pop());
	const breakdownId = breakdownOrder[orderIndex];

	return breakdowns[breakdownId];
};

const TableWithPagination = withPaginationBar()(Table);

const BreakdownTable: React.FC<IBreakdownTableProps> = ({
	attributes,
	breakdownOrder,
	breakdowns,
	compareToPrevious,
	delta,
	editBreakdown,
	event,
	eventAnalysisResult,
	onDeltaChange,
	onPageChange,
	page,
}) => {
	const parseData = (
		data: BreakdownData
	): {
		columns: Array<{
			accessor?: string;
			cellRenderer: (props: {
				className?: string;
				data: ParsedBreakdownItem;
			}) => React.ReactNode;
			headProps?: {
				order: string;
			};
			label: React.ReactNode;
			sortable?: boolean;
		}>;
		count: number;
		highestValue: number;
		items: ParsedBreakdownData;
	} => {
		const orderedBreakdowns = breakdownOrder.map(
			(breakdownId) => breakdowns[breakdownId]
		);

		const items = parseBreakdownData(data, orderedBreakdowns);

		const highestValue = getMaxEventValue(items, compareToPrevious);

		const columns = getColumns({
			attributes,
			breakdowns,
			compareToPrevious,
			event,
			highestValue,
			order: breakdownOrder,
			value: data.value,
		});

		return {
			columns,
			count: data.count,
			highestValue,
			items,
		};
	};

	const {columns, count, highestValue, items} =
		parseData(eventAnalysisResult);

	const orderIOMap = OrderedMap(
		breakdownOrder.map((breakdownId, i) => {
			const {sortType} = breakdowns[breakdownId];

			return [
				`breakdown${i}`,
				{field: `breakdown${i}`, sortOrder: sortType},
			];
		})
	);

	const tableRef = useRef<HTMLDivElement>(null);

	const handleSort = (
		orderIOMap: OrderedMap<string, {field: string; sortOrder: string}>
	) => {
		const first = orderIOMap.first();

		if (!first) {
			return;
		}

		const {field, sortOrder} = first;

		const breakdown = getBreakdownByAccessor(
			field,
			breakdownOrder,
			breakdowns
		);

		const attribute = attributes[breakdown.attributeId];

		editBreakdown({
			attribute,
			breakdown: {
				...breakdown,
				sortType: sortOrder as Breakdown['sortType'],
			},
			id: breakdown.id ?? '',
		});
	};

	return (
		<div
			className={getCN('breakdown-table-root', {
				'breakdown-single-event': !breakdownOrder.length,
			})}
			ref={tableRef}
		>
			{!breakdownOrder.length ? (
				<div className="table-hover">
					<BarComparisonCell
						compareToPrevious={compareToPrevious}
						event={event}
						events={items[0].events}
						topValue={highestValue}
					/>
				</div>
			) : (
				<TableWithPagination
					bordered
					columns={columns}
					delta={delta}
					items={items}
					onDeltaChange={onDeltaChange}
					onOrderIOMapChange={handleSort}
					onPageChange={onPageChange}
					onSortChange={handleSort}
					orderIOMap={orderIOMap}
					page={page}
					rowIdentifier="index"
					striped={false}
					total={count}
				/>
			)}
		</div>
	);
};

const BreakdownWithSafeResults: React.FC<IBreakdownWithSafeResultsProps> = ({
	channelId,
	compareToPrevious,
	event,
	rangeSelectors,
	type,
}) => {
	const {
		attributes,
		breakdownOrder,
		breakdowns,
		editBreakdown,
		filterOrder,
		filters,
	} = useAttributes();

	const {delta, onDeltaChange, onPageChange, page} = useStatefulPagination();

	const result = useQuery<
		EventAnalysisResultData,
		EventAnalysisResultVariables
	>(EventAnalysisResultQuery, {
		fetchPolicy: 'network-only',
		variables: {
			analysisType: type,
			channelId,
			compareToPrevious,
			eventAnalysisBreakdowns: breakdownOrder.map((breakdownId) =>
				omit(breakdowns[breakdownId], 'id')
			),
			eventAnalysisFilters: filterOrder.map((filterId) =>
				omit(filters[filterId], 'id')
			),
			eventDefinitionId: event.id,
			page: page - 1,
			size: delta,
			...getSafeRangeSelectors(rangeSelectors!),
		},
	});

	useEffect(() => {
		onPageChange(1);
	}, [breakdownOrder, breakdowns, event, filters, rangeSelectors]);

	return (
		<SafeResults {...result} page={false} pageDisplay={false}>
			{({
				eventAnalysisResult,
			}: {
				eventAnalysisResult: EventAnalysisResultData;
			}) => (
				<BreakdownTable
					attributes={attributes}
					breakdownOrder={breakdownOrder}
					breakdowns={breakdowns}
					compareToPrevious={compareToPrevious}
					delta={delta}
					editBreakdown={editBreakdown}
					event={event}
					eventAnalysisResult={eventAnalysisResult}
					filters={filters}
					onDeltaChange={onDeltaChange}
					onPageChange={onPageChange}
					page={page}
					rangeSelectors={rangeSelectors}
					type={type}
				/>
			)}
		</SafeResults>
	);
};

type BreakdownColumn = {
	accessor?: string;
	cellRenderer: (props: {
		className?: string;
		data: ParsedBreakdownItem;
	}) => React.ReactNode;
	headProps?: {order: string};
	label: React.ReactNode;
	sortable?: boolean;
};

const getColumns = ({
	attributes,
	breakdowns,
	compareToPrevious,
	event,
	highestValue,
	order,
	value,
}: {
	attributes: Attributes;
	breakdowns: Breakdowns;
	compareToPrevious: boolean;
	event: Event;
	highestValue: number;
	order: string[];
	value: number;
}): BreakdownColumn[] => {
	const columns: BreakdownColumn[] = order.map(
		(breakdownId: string, i: number) => {
			const {attributeId, attributeType, sortType} =
				breakdowns[breakdownId];

			const accessor = `breakdown${i}`;

			return {
				accessor,
				cellRenderer: ({
					className,
					data,
				}: {
					className?: string;
					data: ParsedBreakdownItem;
				}) => {
					const dataEvents = get(data, 'events');
					const dataValue = get(data, accessor);
					const nextDataValue = get(data, `breakdown${i + 1}`);

					if (
						isNil(dataValue) &&
						isNil(dataEvents) &&
						isNil(nextDataValue)
					) {
						return (
							<td
								className={getCN(
									'align-top',
									'empty-breakdown-column',
									className
								)}
							>
								{Liferay.Language.get('no-results')}
							</td>
						);
					}
					else if (isNil(dataValue)) {
						return null;
					}

					return (
						<td
							className={getCN(
								'font-weight-semibold',
								'align-top',
								className
							)}
							data-testid={getSafeDecodedURIComponent(
								(dataValue as BreakdownDataItem).name
							)}
							rowSpan={
								(
									dataValue as BreakdownDataItem & {
										rowSpan: number;
									}
								).rowSpan
							}
						>
							<div style={{width: 128}}>
								<TextTruncate
									className="white-space-normal"
									maxCharLength={200}
									title={getSafeDecodedURIComponent(
										(dataValue as BreakdownDataItem).name
									)}
								/>
							</div>
						</td>
					);
				},
				headProps: {
					order: sortType,
				},
				label: (
					<div>
						<span className="breakdown-category d-block text-uppercase">
							{attributeType}
						</span>

						{attributes[attributeId].displayName}
					</div>
				),
			};
		}
	);

	columns.push({
		cellRenderer: ({
			className,
			data: {events},
		}: {
			className?: string;
			data: ParsedBreakdownItem;
		}) => {
			if (isNil(events)) {
				return (
					<td
						className={getCN(
							'align-top',
							'empty-breakdown-column',
							className
						)}
					>
						{Liferay.Language.get('no-results')}
					</td>
				);
			}

			return (
				<td className={getCN('align-top', className)}>
					<BarComparisonCell
						compareToPrevious={compareToPrevious}
						event={event}
						events={events}
						topValue={highestValue}
					/>
				</td>
			);
		},
		label: Liferay.Language.get('events'),
		sortable: false,
	});

	const fullTitleText = sub(Liferay.Language.get('percent-of-x'), [
		event.displayName || event.name,
	]);

	columns.push({
		cellRenderer: ({
			className,
			data: {events},
		}: {
			className?: string;
			data: ParsedBreakdownItem;
		}) => (
			<td className={getCN('align-top ', className)}>
				<PercentOfCell
					compareToPrevious={compareToPrevious}
					events={events}
					totalValue={value}
				/>
			</td>
		),
		label: (
			<div style={{width: 128}}>
				<TextTruncate
					className="white-space-normal table-column-text-end"
					maxCharLength={40}
					title={fullTitleText}
				/>
			</div>
		),
		sortable: false,
	});

	return columns;
};

export default BreakdownWithSafeResults;
