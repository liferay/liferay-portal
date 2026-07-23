import {getFilters} from 'shared/util/filter';
import {getLocationsData} from 'shared/util/charts';
import {getSafeRangeSelectors} from 'shared/util/util';
import {getVariables, safeResultToProps} from 'shared/util/mappers';

/**
 * MAPPER
 * @description Get Locations Mapper
 * @param {function} getMetric
 */
const getLocationsMapper = (getMetric) => {
	const mapResultToProps = safeResultToProps(
		(result, {filters}, {rangeSelectors}) => {
			let {geolocation} = getMetric(result);

			const {location} = getFilters(filters);

			if (location !== 'Any') {
				geolocation = geolocation[0].metrics;
			}

			const locationsData = getLocationsData(geolocation, location);

			return {
				...getSafeRangeSelectors(rangeSelectors),
				data: {
					geolocation: locationsData,
					total: locationsData.length,
				},
			};
		}
	);

	/**
	 * Map Props to Options
	 * @param {object} param0 props
	 * @param {object} param1 context
	 */
	const mapPropsToOptions = ({
		accountId,
		filters,
		interval,
		rangeSelectors,
		router: {params},
	}) => getVariables({accountId, filters, interval, params, rangeSelectors});

	return {
		options: mapPropsToOptions,
		props: mapResultToProps,
	};
};

/**
 * MAPPER
 * @description Get Countries Mapper
 * @param {function} getMetric
 */
const getLocationsMapperCountries = (getMetric) => {
	const mapResultToProps = safeResultToProps((result) => {
		const {geolocation} = getMetric(result);
		const countries = getLocationsData(geolocation, location);

		return {
			data: {
				countries,
				total: countries.length,
			},
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
	}) => {
		const {variables} = getVariables({
			accountId,
			experienceId,
			filters,
			interval,
			params,
			rangeSelectors,
		});

		return {
			variables: {
				...variables,
				location: 'Any',
			},
		};
	};

	return {
		options: mapPropsToOptions,
		props: mapResultToProps,
	};
};
export {getLocationsMapper, getLocationsMapperCountries};
export default getLocationsMapper;
