import {CHART_COLORS} from 'shared/util/charts';
import {CONTROL_COLOR} from './constants';
import {
	FormatYAxisFn,
	GetFormattedMedianFn,
	GetMetricNameFn,
	GetMetricUnitFn,
	GetShortIntervals,
	GetStatusColorFn,
	GetStatusNameFn,
	GetTicksFn,
	GetVariantLabels,
	MergedVariant,
	MergedVariantsFn,
	MetricName,
	Variant,
	VariantMetric,
} from './types';
import {
	formatPercent,
	toRounded,
	toThousands,
	toThousandsBase,
} from 'shared/util/numbers';

const METRICS_NAMES = new Map([
	['BOUNCE_RATE', Liferay.Language.get('bounce-rate')],
	['CLICK_RATE', Liferay.Language.get('click-through-rate')],
	['MAX_SCROLL_DEPTH', Liferay.Language.get('max-scroll-depth')],
	['TIME_ON_PAGE', Liferay.Language.get('view-duration')],
]);

const METRICS_UNITS = new Map([
	['BOUNCE_RATE', '%'],
	['CLICK_RATE', '%'],
	['MAX_SCROLL_DEPTH', '%'],
	['TIME_ON_PAGE', 's'],
]);

const STATUS_COLORS = new Map([
	['COMPLETED', 'success'],
	['DRAFT', 'secondary'],
	['FINISHED_NO_WINNER', 'secondary'],
	['FINISHED_WINNER', 'success'],
	['PAUSED', 'secondary'],
	['RUNNING', 'info'],
	['SCHEDULED', 'warning'],
	['TERMINATED', 'danger'],
]);

const STATUS_NAMES = new Map([
	['COMPLETED', Liferay.Language.get('complete')],
	['DRAFT', Liferay.Language.get('draft')],
	['FINISHED_NO_WINNER', Liferay.Language.get('no-winner')],
	['FINISHED_WINNER', Liferay.Language.get('winner-declared')],
	['PAUSED', Liferay.Language.get('paused')],
	['RUNNING', Liferay.Language.get('running')],
	['SCHEDULED', Liferay.Language.get('scheduled')],
	['TERMINATED', Liferay.Language.get('terminated')],
]);

const getExperimentLink = ({
	action,
	id,
	pageURL,
}: {
	action?: string;
	id: string;
	pageURL: string;
}) => {
	const experimentLink = `${pageURL}?segmentsExperimentKey=${id}`;

	if (action) {
		return `${experimentLink}&segmentsExperimentAction=${action}`;
	}

	return experimentLink;
};

export const formatYAxis: FormatYAxisFn = (metricUnit) => (value) => {
	if (value % 1 === 0) {
		return `${value}${metricUnit}`;
	}

	return `${toRounded(value)}${metricUnit}`;
};

export const getFormattedMedian: GetFormattedMedianFn = (median, metric) => {
	const precision = metric === 'CLICK_RATE' ? 3 : 2;

	return toRounded(median, precision);
};

export const getFormattedMedianLabel = (metric: MetricName) =>
	metric === 'CLICK_RATE'
		? `${Liferay.Language.get('median')} ${getMetricName(metric)}`
		: `${getMetricName(metric)} ${Liferay.Language.get('median')}`;

export const getFormattedProbabilityToWin = (value: number): string => {
	if (value < 0.1) {
		return '< 0.1%';
	}
	else if (value > 99.9) {
		return '> 99.9%';
	}

	return formatPercent(value);
};

export const getMetricName: GetMetricNameFn = (metric) =>
	METRICS_NAMES.get(metric) ?? '';

export const getMetricUnit: GetMetricUnitFn = (metric) =>
	METRICS_UNITS.get(metric) ?? '';

export const getStatusColor: GetStatusColorFn = (status) =>
	STATUS_COLORS.get(status) ?? '';

export const getStatusName: GetStatusNameFn = (status) =>
	(STATUS_NAMES.get(status) ?? '').toUpperCase();

export const mergedVariants: MergedVariantsFn = (variants, variantMetrics) =>
	variants.map((variant) => {
		const metric = variantMetrics.find(
			({dxpVariantId}) => variant.dxpVariantId === dxpVariantId
		);

		return {
			...variant,
			confidenceInterval: metric?.confidenceInterval ?? [],
			improvement: metric?.improvement ?? 0,
			median: metric?.median ?? 0,
			probabilityToWin: metric?.probabilityToWin ?? 0,
		};
	});

interface IGetActionsOptions {
	id: string;
	onDelete?: () => void;
	pageURL: string;
	publishable?: boolean;
}

export const getActions = (
	status: string,
	{
		id,
		onDelete,
		pageURL,
		publishable,
	}: IGetActionsOptions = {} as IGetActionsOptions
) => {
	const deleteButton = {
		displayType: 'secondary',
		label: Liferay.Language.get('delete'),
		onClick: onDelete,
	};

	switch (status) {
		case 'COMPLETED': {
			return [deleteButton];
		}
		case 'DRAFT': {
			return [
				{
					displayType: 'primary',
					label: Liferay.Language.get('review'),
					redirectURL: getExperimentLink({
						action: 'reviewAndRun',
						id,
						pageURL,
					}),
				},
				{
					displayType: 'secondary',
					label: Liferay.Language.get('delete'),
					redirectURL: getExperimentLink({
						action: 'delete',
						id,
						pageURL,
					}),
				},
			];
		}
		case 'FINISHED_NO_WINNER':
		case 'FINISHED_WINNER': {
			return [
				{
					displayType: 'primary',
					label: Liferay.Language.get('publish'),
					redirectURL: getExperimentLink({
						action: 'publish',
						id,
						pageURL,
					}),
				},
				{
					displayType: 'secondary',
					label: Liferay.Language.get('delete'),
					redirectURL: getExperimentLink({
						action: 'delete',
						id,
						pageURL,
					}),
				},
			];
		}
		case 'TERMINATED': {
			if (publishable) {
				return [
					{
						displayType: 'primary',
						label: Liferay.Language.get('publish'),
						redirectURL: getExperimentLink({
							action: 'publish',
							id,
							pageURL,
						}),
					},
					{
						displayType: 'secondary',
						label: Liferay.Language.get('delete'),
						redirectURL: getExperimentLink({
							action: 'delete',
							id,
							pageURL,
						}),
					},
				];
			}

			return [deleteButton];
		}
		case 'RUNNING': {
			return [
				{
					displayType: 'secondary',
					label: Liferay.Language.get('terminate'),
					redirectURL: getExperimentLink({
						action: 'terminate',
						id,
						pageURL,
					}),
				},
			];
		}
		default: {
			return [];
		}
	}
};

export const getBestVariant = ({
	dxpVariants,
	goal,
	metrics: {variantMetrics},
}: {
	dxpVariants: Variant[];
	goal?: {metric: MetricName};
	metrics: {variantMetrics: VariantMetric[]};
}): MergedVariant | null => {
	if (
		!dxpVariants ||
		variantMetrics.every(({median}) => median === variantMetrics[0].median)
	) {
		return null;
	}

	if (goal?.metric === 'BOUNCE_RATE') {
		return mergedVariants(dxpVariants, variantMetrics).reduce(
			(prev, current) => (prev.median < current.median ? prev : current)
		);
	}

	return mergedVariants(dxpVariants, variantMetrics).reduce(
		(prev, current) => (prev.median > current.median ? prev : current)
	);
};

export const getVariantLabels: GetVariantLabels = ({
	bestVariant,
	dxpVariantId,
	publishedDXPVariantId,
	status,
	winnerDXPVariantId,
}) => {
	const labels = [];

	if (status === 'RUNNING' && bestVariant?.dxpVariantId === dxpVariantId) {
		labels.push({
			status: 'success',
			value: Liferay.Language.get('current-best'),
		});
	}

	if (
		winnerDXPVariantId === dxpVariantId &&
		(status === 'COMPLETED' || status === 'FINISHED_WINNER')
	) {
		labels.push({
			status: 'success',
			value: Liferay.Language.get('winner'),
		});
	}

	if (publishedDXPVariantId === dxpVariantId) {
		labels.push({
			status: 'info',
			value: Liferay.Language.get('published'),
		});
	}

	return labels;
};

export const getTicks: GetTicksFn = (maxValue) => {
	const arr = [];
	let interval = 1;
	const step = Math.round(maxValue / 8);

	while (interval <= maxValue) {
		arr.push(interval);

		interval = interval + step;
	}

	return [...arr];
};

export const getShortIntervals: GetShortIntervals = (intervals) =>
	getTicks(intervals.length).map((tick) => intervals[tick - 1]);

export const toThousandsABTesting = (number: number) => {
	if (number > 1e4) {
		return toThousandsBase(number, (factor: number) => number * factor);
	}

	return toThousands(number);
};

export const getLegendData = (dxpVariants: Variant[]) => {
	const COLORS = [...CHART_COLORS];

	return dxpVariants.map(({control, dxpVariantId, dxpVariantName}) => ({
		color: control ? CONTROL_COLOR : COLORS.shift(),
		id: dxpVariantId,
		name: dxpVariantName,
	}));
};

export const getMedianGraphData = ({
	dxpVariants,
	metricUnit,
}: {
	dxpVariants: MergedVariant[];
	metricUnit: string;
}) => {
	const COLORS = [...CHART_COLORS];

	const type = metricUnit === '%' ? 'percentage' : 'number';

	const formatter =
		metricUnit === '%'
			? (value: number) => value
			: (value: number) => `${value}s`;

	const items = dxpVariants.map(({confidenceInterval, control, median}) => ({
		intervals: [
			{
				end: confidenceInterval[1],
				start: confidenceInterval[0],
			},
		],
		progress: [
			{
				color: control ? CONTROL_COLOR : COLORS.shift(),
				value: median,
			},
		],
	}));

	const maxValue = Math.max(
		...dxpVariants.map(({confidenceInterval}) => confidenceInterval[1])
	);

	return {
		disableScroll: true,
		empty: maxValue === 0,
		formatSpacement: false,
		grid: {
			formatter,
			maxValue,
			minValue: 0,
			precision: 2,
			show: true,
			type,
		},
		items,
	};
};
