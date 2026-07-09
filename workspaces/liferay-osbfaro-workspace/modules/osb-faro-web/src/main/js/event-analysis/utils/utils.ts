import moment from 'moment';
import {
	Attribute,
	AttributeOwnerTypes,
	AttributeTypes,
	Breakdown,
	BreakdownData,
	BreakdownDataItem,
	DataTypes,
	DateGroupings,
	Filter,
	Operators,
	ParsedBreakdownData,
	ParsedBreakdownItem,
} from './types';
import {formatTime} from 'shared/util/time';
import {formatUTCDate} from 'shared/util/date';
import {OrderByDirections} from 'shared/util/constants';

const DEFAULT_DATE_GROUPING = DateGroupings.Month;
const DEFAULT_DURATION_BIN = 60000;
const DEFAULT_NUMBER_BIN = 10;

const ATTRIBUTE_TYPE_LABEL_MAP = {
	[AttributeOwnerTypes.Account]: Liferay.Language.get('account'),
	[AttributeOwnerTypes.Event]: Liferay.Language.get('event'),
	[AttributeOwnerTypes.Individual]: Liferay.Language.get('individual'),
	[AttributeOwnerTypes.Session]: Liferay.Language.get('session'),
};

export const BOOLEAN_OPTIONS = ['true', 'false'];

export const DATE_GROUPING_OPTIONS = [
	DateGroupings.Day,
	DateGroupings.Month,
	DateGroupings.Year,
];

export const DATE_OPTIONS = [
	Operators.EQ,
	Operators.LT,
	Operators.GT,
	Operators.Between,
];

export const DURATION_OPTIONS = [Operators.GT, Operators.LT];

export const NUMBER_OPTIONS = [Operators.GT, Operators.LT, Operators.Between];

export const STRING_OPTIONS = [
	Operators.Contains,
	Operators.NotContains,
	Operators.EQ,
	Operators.NE,
];

export const BOOLEAN_LABELS_MAP: Record<string, string> = {
	false: Liferay.Language.get('false'),
	true: Liferay.Language.get('true'),
};

export const DATA_TYPE_ICONS_MAP = {
	[DataTypes.Boolean]: 'check',
	[DataTypes.Date]: 'date',
	[DataTypes.Duration]: 'time',
	[DataTypes.Number]: 'integer',
	[DataTypes.String]: 'text',
};

export const DATA_TYPE_LABELS_MAP = {
	[DataTypes.Boolean]: Liferay.Language.get('boolean'),
	[DataTypes.Date]: Liferay.Language.get('date'),
	[DataTypes.Duration]: Liferay.Language.get('duration'),
	[DataTypes.Number]: Liferay.Language.get('number'),
	[DataTypes.String]: Liferay.Language.get('string'),
};

export const DATE_GROUPING_LABELS_MAP = {
	[DateGroupings.Day]: Liferay.Language.get('date'),
	[DateGroupings.Month]: Liferay.Language.get('month'),
	[DateGroupings.Year]: Liferay.Language.get('year'),
};

type PartialOperatorLabelsMap = Partial<Record<Operators, string>>;

export const DATE_OPERATOR_LABELS_MAP: PartialOperatorLabelsMap = {
	[Operators.Between]: '-',
	[Operators.EQ]: Liferay.Language.get('is').toLowerCase(),
	[Operators.GT]: Liferay.Language.get('after').toLowerCase(),
	[Operators.LT]: Liferay.Language.get('before').toLowerCase(),
};

export const DATE_OPERATOR_LONGHAND_LABELS_MAP: PartialOperatorLabelsMap = {
	[Operators.Between]: Liferay.Language.get('is-between').toLowerCase(),
	[Operators.EQ]: Liferay.Language.get('is').toLowerCase(),
	[Operators.GT]: Liferay.Language.get('after').toLowerCase(),
	[Operators.LT]: Liferay.Language.get('before').toLowerCase(),
};

export const DURATION_OPERATOR_LABELS_MAP: PartialOperatorLabelsMap = {
	[Operators.GT]: Liferay.Language.get('is-greater-than').toLowerCase(),
	[Operators.LT]: Liferay.Language.get('is-less-than').toLowerCase(),
};

export const DURATION_OPERATOR_LONGHAND_LABELS_MAP: PartialOperatorLabelsMap = {
	[Operators.GT]: Liferay.Language.get('is-greater-than').toLowerCase(),
	[Operators.LT]: Liferay.Language.get('is-less-than').toLowerCase(),
};

export const NUMBER_OPERATOR_LABELS_MAP: PartialOperatorLabelsMap = {
	[Operators.Between]: '-',
	[Operators.GT]: Liferay.Language.get('is-greater-than').toLowerCase(),
	[Operators.LT]: Liferay.Language.get('is-less-than').toLowerCase(),
};

export const NUMBER_OPERATOR_LONGHAND_LABELS_MAP: PartialOperatorLabelsMap = {
	[Operators.Between]: Liferay.Language.get('between').toLowerCase(),
	[Operators.GT]: Liferay.Language.get('is-greater-than').toLowerCase(),
	[Operators.LT]: Liferay.Language.get('is-less-than').toLowerCase(),
};

export const STRING_OPERATOR_LABELS_MAP: PartialOperatorLabelsMap = {
	[Operators.Contains]: Liferay.Language.get('contains').toLowerCase(),
	[Operators.NotContains]:
		Liferay.Language.get('does-not-contain').toLowerCase(),
	[Operators.EQ]: Liferay.Language.get('is').toLowerCase(),
	[Operators.NE]: Liferay.Language.get('is-not').toLowerCase(),
};

const getBooleanDisplay = (
	attribute: Attribute,
	{attributeType, values: [value]}: Filter
): [string, string] => [
	getBreakdownDisplay(attribute, attributeType).join(' | '),
	BOOLEAN_LABELS_MAP[String(value)],
];

const getDateDisplay = (
	attribute: Attribute,
	{attributeType, operator, values: [startDate, endDate]}: Filter
): [string, string] => {
	const operatorLabel = DATE_OPERATOR_LABELS_MAP[operator];

	const formattedStartDate = formatUTCDate(startDate as string, 'll');

	const breakdownValue =
		operator === Operators.Between
			? `${Liferay.Language.get(
					'between'
				)} ${formattedStartDate} ${operatorLabel} ${formatUTCDate(
					endDate as string,
					'll'
				)}`
			: `${operatorLabel} ${formattedStartDate}`;

	return [
		getBreakdownDisplay(attribute, attributeType).join(' | '),
		`${breakdownValue}`,
	];
};

export const getBreakdownDisplay = (
	{displayName, name}: Attribute,
	attributeType: AttributeOwnerTypes
): [string, string] => [
	ATTRIBUTE_TYPE_LABEL_MAP[attributeType],
	displayName || name,
];

const getDurationDisplay = (
	attribute: Attribute,
	{attributeType, operator, values: [value]}: Filter
): [string, string] => {
	const duration = formatTime(Number(value));

	return [
		getBreakdownDisplay(attribute, attributeType).join(' | '),
		`${DURATION_OPERATOR_LABELS_MAP[operator]} ${duration}`,
	];
};

const getNumberDisplay = (
	attribute: Attribute,
	{attributeType, operator, values: [start, end]}: Filter
): [string, string] => {
	const operatorLabel = NUMBER_OPERATOR_LABELS_MAP[operator];

	const breakdownValue =
		operator === Operators.Between
			? `${start} ${operatorLabel} ${end}`
			: `${operatorLabel} ${start}`;

	return [
		getBreakdownDisplay(attribute, attributeType).join(' | '),
		breakdownValue,
	];
};

const getStringDisplay = (
	attribute: Attribute,
	{attributeType, operator, values}: Filter
): [string, string] => [
	getBreakdownDisplay(attribute, attributeType).join(' | '),
	`${STRING_OPERATOR_LABELS_MAP[operator]} "${values}"`,
];

const FILTER_DISPLAY_MAP = {
	[DataTypes.Boolean]: getBooleanDisplay,
	[DataTypes.Date]: getDateDisplay,
	[DataTypes.Duration]: getDurationDisplay,
	[DataTypes.Number]: getNumberDisplay,
	[DataTypes.String]: getStringDisplay,
};

export const getFilterDisplay = (
	attribute: Attribute,
	filter: Filter
): [string, string] => {
	const displayFn = FILTER_DISPLAY_MAP[filter.dataType];

	return displayFn(attribute, filter);
};

export const isAttribute = (item: Attribute | Event): boolean =>
	(item as Attribute).dataType !== undefined;

export const cloneAttributes = (attributes: Attribute[]): Attribute[] =>
	attributes.map((attribute) => ({...attribute}));

interface IBreakdownFnArgs {
	attributeId: string;
	attributeType: AttributeOwnerTypes;
	binSize?: number;
	dateGrouping?: DateGroupings;
	description?: string;
	displayName: string;
}

export const createBooleanBreakdown = ({
	attributeId,
	attributeType,
	description,
	displayName,
}: IBreakdownFnArgs): Breakdown => ({
	attributeId,
	attributeType,
	binSize: null,
	dataType: DataTypes.Boolean,
	dateGrouping: null,
	description,
	displayName,
	sortType: OrderByDirections.Descending,
});

export const createDateBreakdown = ({
	attributeId,
	attributeType,
	dateGrouping = DEFAULT_DATE_GROUPING,
	description,
	displayName,
}: IBreakdownFnArgs): Breakdown => ({
	attributeId,
	attributeType,
	binSize: null,
	dataType: DataTypes.Date,
	dateGrouping,
	description,
	displayName,
	sortType: OrderByDirections.Descending,
});

export const createDurationBreakdown = ({
	attributeId,
	attributeType,
	binSize = DEFAULT_DURATION_BIN,
	description,
	displayName,
}: IBreakdownFnArgs): Breakdown => ({
	attributeId,
	attributeType,
	binSize,
	dataType: DataTypes.Duration,
	dateGrouping: null,
	description,
	displayName,
	sortType: OrderByDirections.Descending,
});

export const createNumberBreakdown = ({
	attributeId,
	attributeType,
	binSize = DEFAULT_NUMBER_BIN,
	description,
	displayName,
}: IBreakdownFnArgs): Breakdown => ({
	attributeId,
	attributeType,
	binSize,
	dataType: DataTypes.Number,
	dateGrouping: null,
	description,
	displayName,
	sortType: OrderByDirections.Descending,
});

export const createStringBreakdown = ({
	attributeId,
	attributeType,
	description,
	displayName,
}: IBreakdownFnArgs): Breakdown => ({
	attributeId,
	attributeType,
	binSize: null,
	dataType: DataTypes.String,
	dateGrouping: null,
	description,
	displayName,
	sortType: OrderByDirections.Descending,
});

export const BREAKDOWN_FNS_MAP = {
	[DataTypes.Boolean]: createBooleanBreakdown,
	[DataTypes.Date]: createDateBreakdown,
	[DataTypes.Duration]: createDurationBreakdown,
	[DataTypes.Number]: createNumberBreakdown,
	[DataTypes.String]: createStringBreakdown,
};

export const getRowSpan = (breakdownItems: BreakdownDataItem[]): number => {
	let rowSpan = breakdownItems.length || 1;

	breakdownItems.forEach(({breakdownItems, leafNode}) => {
		if (!leafNode) {
			rowSpan = rowSpan + (getRowSpan(breakdownItems ?? []) - 1);
		}
	});

	return rowSpan;
};

export const formatDateName = (
	name: string,
	dateGrouping: DateGroupings
): string => {
	switch (dateGrouping) {
		case DateGroupings.Day:
			return moment(name, 'YYYY-MM-DD').format('ll');
		case DateGroupings.Month:
			return moment(name, 'YYYY-MM').format('MMM YYYY');
		case DateGroupings.Year:
		default:
			return name;
	}
};

export const formatDurationName = (name: string): string => {
	const [durationStart, durationEnd] = name.split('-');

	return `${Liferay.Language.get('between')} ${formatTime(
		Number(durationStart)
	)} - ${formatTime(Number(durationEnd))}`;
};

export const formatBreakdownNameByDataType = (
	name: string,
	breakdown: Breakdown
): string | number => {
	if (name === 'undefined') {
		return name;
	}

	switch (breakdown?.dataType) {
		case DataTypes.Date:
			return formatDateName(
				name,
				breakdown.dateGrouping ?? DateGroupings.Month
			);
		case DataTypes.Duration:
			return formatDurationName(name);
		case DataTypes.Boolean:
		case DataTypes.String:
		case DataTypes.Number:
		default:
			return name;
	}
};

function formatName(name = 'undefined') {
	if (name === '') {
		return 'undefined';
	}

	return name;
}

export const parseBreakdownData = (
	{breakdownItems}: BreakdownData | BreakdownDataItem,
	orderedBreakdowns: Breakdown[],
	rows: ParsedBreakdownData = [{index: '0'} as ParsedBreakdownItem],
	level: number = 0
): ParsedBreakdownData => {
	const items = breakdownItems ?? [];

	items.forEach((data) => {
		const {
			breakdownItems: nextBreakdownItems,
			leafNode: isLeafCurrentNode,
			name,
			...node
		} = data;

		const nextItems = nextBreakdownItems ?? [];

		const currentRowIndex = rows.length - 1;

		if (isLeafCurrentNode && level === 0) {
			Object.assign(rows[currentRowIndex], {
				events: [data],
			});

			return;
		}

		const isLeafNextNode = nextItems.length > 0 && nextItems[0].leafNode;

		Object.assign(rows[currentRowIndex], {
			[`breakdown${level}`]: {
				...node,
				name: isLeafCurrentNode
					? formatName(name)
					: formatBreakdownNameByDataType(
							formatName(name),
							orderedBreakdowns[level]
						),
				rowSpan:
					!isLeafCurrentNode && !isLeafNextNode
						? getRowSpan(nextItems)
						: 1,
			},
			index: currentRowIndex,
		});

		if (!nextItems.length) {
			rows.push({} as ParsedBreakdownItem);
		}

		if (!isLeafNextNode) {
			parseBreakdownData(data, orderedBreakdowns, rows, level + 1);
		}
		else {
			Object.assign(rows[currentRowIndex], {
				events: nextItems,
			});

			rows.push({} as ParsedBreakdownItem);
		}
	});

	return level === 0 && !items.length
		? rows
		: rows.filter((obj) => Object.keys(obj).length !== 0);
};

export const getMaxEventValue = (
	parsedData: ParsedBreakdownData,
	compareToPrevious: boolean
) =>
	parsedData.reduce<number>(
		(prev, {events = []}) =>
			events.reduce<number>(
				(
					prev2,
					{
						breakdownItems: segments = [],
						previousValue = 0,
						value = 0,
					}
				) =>
					segments.length <= 1
						? Math.max(
								value,
								prev2,
								compareToPrevious ? previousValue : 0
							)
						: segments.reduce<number>(
								(prev3, {previousValue = 0, value = 0}) =>
									Math.max(
										value,
										prev3,
										compareToPrevious ? previousValue : 0
									),
								prev2
							),
				prev
			),
		0
	);

export function getModifiedEventAttributeDefinitions({
	attribute,
	attributeOwnerType,
	eventAttributeDefinitions,
}: {
	attribute: Attribute;
	attributeOwnerType: AttributeOwnerTypes;
	eventAttributeDefinitions: Attribute[];
}): Attribute[] {
	let modifiedEventAttributeDefinitions: Attribute[] = [];

	if (attributeOwnerType === AttributeOwnerTypes.Event) {
		modifiedEventAttributeDefinitions = attribute
			? eventAttributeDefinitions.map((eventAttributeDefinition) => {
					if (attribute.id === eventAttributeDefinition.id) {
						return attribute;
					}

					return eventAttributeDefinition;
				})
			: eventAttributeDefinitions;
	}
	else if (attributeOwnerType === AttributeOwnerTypes.Individual) {
		modifiedEventAttributeDefinitions = [
			{
				dataType: DataTypes.String,
				displayName: 'jobTitle',
				id: 'jobTitle',
				name: 'jobTitle',
				type: AttributeTypes.Global,
			},
			{
				dataType: DataTypes.String,
				displayName: 'languageId',
				id: 'languageId',
				name: 'languageId',
				type: AttributeTypes.Global,
			},
			{
				dataType: DataTypes.String,
				displayName: Liferay.Language.get('role'),
				id: 'role',
				name: 'role',
				type: AttributeTypes.Local,
			},
			{
				dataType: DataTypes.String,
				displayName: Liferay.Language.get('site-membership'),
				id: 'group',
				name: 'group',
				type: AttributeTypes.Local,
			},
			{
				dataType: DataTypes.String,
				displayName: Liferay.Language.get('team'),
				id: 'team',
				name: 'team',
				type: AttributeTypes.Local,
			},
			{
				dataType: DataTypes.String,
				displayName: Liferay.Language.get('user-group'),
				id: 'userGroup',
				name: 'userGroup',
				type: AttributeTypes.Local,
			},
		];
	}

	return modifiedEventAttributeDefinitions;
}

export const getTabs = (
	setAttributeOwnerType: (type: AttributeOwnerTypes) => void
) => [
	{
		onClick: () => setAttributeOwnerType(AttributeOwnerTypes.Event),
		tabId: AttributeOwnerTypes.Event,
		title: Liferay.Language.get('event'),
	},
	{
		onClick: () => setAttributeOwnerType(AttributeOwnerTypes.Individual),
		tabId: AttributeOwnerTypes.Individual,
		title: Liferay.Language.get('individual'),
	},
];
