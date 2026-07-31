import {getDeviceLabel} from 'shared/util/lang';
import {getPercentage} from 'shared/util/util';
import {getVariables, safeResultToProps} from 'shared/util/mappers';
import {sortBy} from 'lodash';

const MAX_BROWSER = 8;
const MAX_SYSTEMS = 3;

const groupBrowserData = (data, max) => {
	const orderedData = sortBy(data, 'value').reverse();

	if (orderedData.length <= max) {
		return orderedData;
	}

	const totalOtherValue = orderedData
		.slice(max)
		.reduce((actual, next) => actual + next.value, 0);

	return [
		...orderedData.slice(0, max),
		{
			value: totalOtherValue,
			valueKey: Liferay.Language.get('others'),
		},
	];
};

const groupDeviceData = (data, max) => {
	if (data.length <= max) {
		return data.map((group) => ({
			...group,
			label: getDeviceLabel(group.type),
		}));
	}

	const otherData = data.slice(max).reduce(
		(actual, next) => ({
			...actual,
			data: [
				{
					type: Liferay.Language.get('other'),
					views:
						actual.data[0].views +
						next.data.reduce((acc, next) => acc + next.views, 0),
				},
			],
			percentageOfTotal:
				actual.percentageOfTotal + next.percentageOfTotal,
		}),
		{
			data: [{views: 0}],
			percentageOfTotal: 0,
			type: Liferay.Language.get('other'),
		}
	);

	return [
		...data.slice(0, max).map((group) => ({
			...group,
			label: getDeviceLabel(group.type),
		})),
		{
			...otherData,
			label: Liferay.Language.get('others'),
		},
	];
};

/**
 * MAPPER
 * @description Get Devices Mapper
 * @param {function} getMetric
 */
const getDevicesMapper = (getMetric) => {
	const mapResultToProps = safeResultToProps((result) => {
		const metric = getMetric(result);
		const devices = metric.device.map((device) => {
			const data = device.metrics.map(({value, valueKey}) => ({
				percentage: getPercentage(value, metric.value),
				type: valueKey,
				views: value,
			}));

			return {
				data,
				percentageOfTotal: getPercentage(device.value, metric.value),
				totalViews: device.value,
				type: device.valueKey,
			};
		});

		return {
			browsers: groupBrowserData(metric.browser, MAX_BROWSER),
			devices: groupDeviceData(devices, MAX_SYSTEMS),
			total: metric.value,
		};
	});

	/**
	 * Map Props to Options
	 * @param {object} param0 props
	 * @param {object} param1 context
	 */
	const mapPropsToOptions = ({
		accountId,
		experienceId,
		filters,
		interval,
		rangeSelectors,
		router: {params},
		segmentId,
	}) =>
		getVariables({
			accountId,
			experienceId,
			filters,
			interval,
			params,
			rangeSelectors,
			segmentId,
		});

	return {
		options: mapPropsToOptions,
		props: mapResultToProps,
	};
};

export {getDevicesMapper};
export default getDevicesMapper;
