import {mapPropsToOptions, mapResultToProps} from '../form-abandonment-query';

const router = {
	params: {
		assetId: 'formId',
		title: 'Liferay',
		touchpoint: 'Any',
	},
};

const data = {
	form: {
		formPageMetrics: [
			{
				formFieldMetrics: [
					{
						fieldAbandonmentsMetric: {
							value: 5,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 6230,
						},
						fieldInteractionsMetric: {
							value: 812,
						},
						fieldName: 'Name',
						fieldRefilledMetric: {
							value: 8,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 16,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 8410,
						},
						fieldInteractionsMetric: {
							value: 796,
						},
						fieldName: 'Address',
						fieldRefilledMetric: {
							value: 12,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 4,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 2100,
						},
						fieldInteractionsMetric: {
							value: 792,
						},
						fieldName: 'City',
						fieldRefilledMetric: {
							value: 2,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 12,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 1700,
						},
						fieldInteractionsMetric: {
							value: 780,
						},
						fieldName: 'State',
						fieldRefilledMetric: {
							value: 1.77,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 7,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 2890,
						},
						fieldInteractionsMetric: {
							value: 773,
						},
						fieldName: 'Zip Code',
						fieldRefilledMetric: {
							value: 32,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 221,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 2090,
						},
						fieldInteractionsMetric: {
							value: 34,
						},
						fieldName: 'Order Number',
						fieldRefilledMetric: {
							value: 23,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 3,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 3900,
						},
						fieldInteractionsMetric: {
							value: 766,
						},
						fieldName: 'Phone Number',
						fieldRefilledMetric: {
							value: 11,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 19,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 2870,
						},
						fieldInteractionsMetric: {
							value: 670,
						},
						fieldName: 'Requested Repair Date',
						fieldRefilledMetric: {
							value: 7.3,
						},
					},
					{
						fieldAbandonmentsMetric: {
							value: 17,
						},
						fieldEmptyMetric: {
							value: 0,
						},
						fieldInteractionDurationMetric: {
							value: 62230,
						},
						fieldInteractionsMetric: {
							value: 215,
						},
						fieldName: 'Description of Problem',
						fieldRefilledMetric: {
							value: 28,
						},
					},
				],
				pageAbandonmentsMetric: {
					value: 461,
				},
				pageIndex: '0',
				pageViewsMetric: {
					value: 1273,
				},
			},
		],
	},
};

describe('FormAbandonmentQuery mapper', () => {
	it('should extract items from result', () => {
		const props = mapResultToProps({data});

		expect(props).toHaveProperty('header');
		expect(props).toHaveProperty('items');
	});

	it('should return a object with empty true when formPageMetrics is not present in parameters', () => {
		const props = mapResultToProps({
			data: {
				form: {
					formPageMetrics: [],
				},
			},
		});

		expect(props).toMatchObject({
			error: null,
			header: [
				{
					icon: 'document',
					label: '0 Form Steps',
				},
				{
					icon: 'custom-field',
					label: '0 Fields',
				},
			],
			items: [],
			loading: false,
		});
	});

	it('should include options', () => {
		const options = mapPropsToOptions({
			filters: {},
			rangeSelectors: {rangeKey: '7'},
			router,
		});

		expect(options).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 7,
				rangeStart: null,
				title: 'Liferay',
				touchpoint: null,
			},
		});
	});

	it('should include segmentId in the options when provided', () => {
		const options = mapPropsToOptions({
			filters: {},
			rangeSelectors: {rangeKey: '7'},
			router,
			segmentId: 'segment-100',
		});

		expect(options).toEqual({
			variables: {
				assetId: 'formId',
				devices: 'Any',
				location: 'Any',
				rangeEnd: null,
				rangeKey: 7,
				rangeStart: null,
				segmentId: 'segment-100',
				title: 'Liferay',
				touchpoint: null,
			},
		});
	});
});
