import {getLocationsMapper, getLocationsMapperCountries} from '../locations';

const ASSET_ID = 'formId';
const TOUCHPOINT = null;

const data = {
	form: {
		submissionsMetric: {
			geolocation: [
				{
					metrics: [
						{
							value: 100,
							valueKey: 'Unknown',
						},
						{
							value: 100,
							valueKey: 'Pernambuco',
						},
						{
							value: 100,
							valueKey: 'Sao Paulo',
						},
						{
							value: 100,
							valueKey: 'Parana',
						},
						{
							value: 100,
							valueKey: 'Rio Grande do Sul',
						},
					],
					value: 100,
					valueKey: 'Brazil',
				},
				{
					metrics: [
						{
							value: 100,
							valueKey: 'Unknown',
						},
						{
							value: 100,
							valueKey: 'Catalonia',
						},
						{
							value: 100,
							valueKey: 'Madrid',
						},
						{
							value: 100,
							valueKey: 'Andalucia',
						},
						{
							value: 100,
							valueKey: 'Castilla y Leon',
						},
						{
							value: 100,
							valueKey: 'Others',
						},
					],
					value: 100,
					valueKey: 'Spain',
				},
				{
					metrics: [
						{
							value: 100,
							valueKey: 'California',
						},
						{
							value: 100,
							valueKey: 'Unknown',
						},
						{
							value: 100,
							valueKey: 'Georgia',
						},
						{
							value: 100,
							valueKey: 'New Jersey',
						},
						{
							value: 100,
							valueKey: 'Florida',
						},
						{
							value: 100,
							valueKey: 'Others',
						},
					],
					value: 100,
					valueKey: 'United States',
				},
			],
		},
	},
};

describe('Shared HOCs Mappers - Locations', () => {
	it('should map locations information', () => {
		const mapper = getLocationsMapper(
			(result) => result.form.submissionsMetric
		);

		const propsResult = mapper.props(
			{
				data,
				ownProps: {
					rangeSelectors: {rangeKey: '0'},
				},
			},
			{
				filters: {
					location: ['Any'],
				},
			}
		);

		expect(propsResult).toMatchObject({
			data: {
				geolocation: [
					{
						group: 'Brazil',
						id: 'Brazil',
						name: 'Brazil',
						total: 100,
						value: 33.3,
					},
					{
						group: 'Spain',
						id: 'Spain',
						name: 'Spain',
						total: 100,
						value: 33.3,
					},
					{
						group: 'United States',
						id: 'United States',
						name: 'United States',
						total: 100,
						value: 33.3,
					},
				],
				total: 3,
			},
			error: null,
			loading: false,
			rangeEnd: null,
			rangeKey: 0,
			rangeStart: null,
		});

		const optionsResult = mapper.options({
			filters: {
				location: ['Any'],
			},
			rangeSelectors: {rangeKey: '0'},
			router: {
				params: {
					assetId: ASSET_ID,
					touchpoint: TOUCHPOINT,
				},
			},
		});

		expect(optionsResult).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 0,
				rangeStart: null,
				title: '',
				touchpoint: null,
			},
		});
	});

	it('should include segmentId in the request variables when provided', () => {
		const mapper = getLocationsMapper(
			(result) => result.form.submissionsMetric
		);

		const optionsResult = mapper.options({
			filters: {
				location: ['Any'],
			},
			rangeSelectors: {rangeKey: '0'},
			router: {
				params: {
					assetId: ASSET_ID,
					touchpoint: TOUCHPOINT,
				},
			},
			segmentId: 'segment-100',
		});

		expect(optionsResult).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 0,
				rangeStart: null,
				segmentId: 'segment-100',
				title: '',
				touchpoint: null,
			},
		});
	});

	it('should map locations information with region Brazil', () => {
		const mapper = getLocationsMapper(
			(result) => result.form.submissionsMetric
		);

		const propsResult = mapper.props(
			{
				data,
				ownProps: {
					rangeSelectors: {rangeKey: '0'},
				},
			},
			{
				filters: {
					location: ['Brazil'],
				},
			}
		);

		expect(propsResult).toMatchObject({
			data: {
				geolocation: [
					{
						group: 'Unknown',
						id: 'Unknown',
						name: 'Unknown',
						total: 100,
						value: 20,
					},
					{
						group: 'Pernambuco',
						id: 'Pernambuco',
						name: 'Pernambuco',
						total: 100,
						value: 20,
					},
					{
						group: 'Sao Paulo',
						id: 'Sao Paulo',
						name: 'Sao Paulo',
						total: 100,
						value: 20,
					},
					{
						group: 'Parana',
						id: 'Parana',
						name: 'Parana',
						total: 100,
						value: 20,
					},
					{
						group: 'Rio Grande do Sul',
						id: 'Rio Grande do Sul',
						name: 'Rio Grande do Sul',
						total: 100,
						value: 20,
					},
				],
				total: 5,
			},
			error: null,
			loading: false,
			rangeEnd: null,
			rangeKey: 0,
			rangeStart: null,
		});

		const optionsResult = mapper.options({
			filters: {
				location: ['Brazil'],
			},
			rangeSelectors: {rangeKey: '0'},
			router: {
				params: {
					assetId: ASSET_ID,
					touchpoint: TOUCHPOINT,
				},
			},
		});

		expect(optionsResult).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Brazil',
				rangeEnd: null,
				rangeKey: 0,
				rangeStart: null,
				title: '',
				touchpoint: null,
			},
		});
	});

	it('should map locations information without geolocation', () => {
		const mapper = getLocationsMapper(
			(result) => result.form.submissionsMetric
		);

		const propsResult = mapper.props(
			{
				data: {
					form: {
						submissionsMetric: {
							geolocation: [],
						},
					},
				},
				ownProps: {
					rangeSelectors: {rangeKey: '0'},
				},
			},
			{
				filters: {
					location: ['Any'],
				},
			}
		);

		expect(propsResult).toMatchObject({
			data: {
				geolocation: [],
				total: 0,
			},
			error: null,
			loading: false,
			rangeEnd: null,
			rangeKey: 0,
			rangeStart: null,
		});
	});
});

describe('Shared HOCs Mappers - Locations Countries', () => {
	it('should return countries information', () => {
		const mapper = getLocationsMapperCountries(
			(result) => result.form.submissionsMetric
		);

		const propsResult = mapper.props(
			{
				data,
				ownProps: {
					rangeSelectors: {rangeKey: '0'},
				},
			},
			{
				filters: {
					location: ['Any'],
				},
			}
		);

		expect(propsResult).toMatchObject({
			data: {
				countries: [
					{
						group: 'Brazil',
						id: 'Brazil',
						name: 'Brazil',
						total: 100,
						value: 33.3,
					},
					{
						group: 'Spain',
						id: 'Spain',
						name: 'Spain',
						total: 100,
						value: 33.3,
					},
					{
						group: 'United States',
						id: 'United States',
						name: 'United States',
						total: 100,
						value: 33.3,
					},
				],
				total: 3,
			},
			error: null,
			loading: false,
		});

		const optionsResult = mapper.options({
			filters: {
				location: ['Any'],
			},
			rangeSelectors: {rangeKey: '0'},
			router: {
				params: {
					assetId: ASSET_ID,
					touchpoint: TOUCHPOINT,
				},
			},
		});

		expect(optionsResult).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 0,
				rangeStart: null,
				title: '',
				touchpoint: null,
			},
		});
	});

	it('should include segmentId in the countries request variables when provided', () => {
		const mapper = getLocationsMapperCountries(
			(result) => result.form.submissionsMetric
		);

		const optionsResult = mapper.options({
			filters: {
				location: ['Any'],
			},
			rangeSelectors: {rangeKey: '0'},
			router: {
				params: {
					assetId: ASSET_ID,
					touchpoint: TOUCHPOINT,
				},
			},
			segmentId: 'segment-100',
		});

		expect(optionsResult).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 0,
				rangeStart: null,
				segmentId: 'segment-100',
				title: '',
				touchpoint: null,
			},
		});
	});

	it('should return countries information indepent if Brazil is selected', () => {
		const mapper = getLocationsMapperCountries(
			(result) => result.form.submissionsMetric
		);

		const propsResult = mapper.props(
			{
				data,
				ownProps: {
					rangeSelectors: {rangeKey: '0'},
				},
			},
			{
				filters: {
					location: ['Brazil'],
				},
			}
		);

		expect(propsResult).toMatchObject({
			data: {
				countries: [
					{
						group: 'Brazil',
						id: 'Brazil',
						name: 'Brazil',
						total: 100,
						value: 33.3,
					},
					{
						group: 'Spain',
						id: 'Spain',
						name: 'Spain',
						total: 100,
						value: 33.3,
					},
					{
						group: 'United States',
						id: 'United States',
						name: 'United States',
						total: 100,
						value: 33.3,
					},
				],
				total: 3,
			},
			error: null,
			loading: false,
		});

		const optionsResult = mapper.options({
			filters: {
				location: ['Brazil'],
			},
			rangeSelectors: {rangeKey: '0'},
			router: {
				params: {
					assetId: ASSET_ID,
					touchpoint: TOUCHPOINT,
				},
			},
		});

		expect(optionsResult).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 0,
				rangeStart: null,
				title: '',
				touchpoint: null,
			},
		});
	});
});
