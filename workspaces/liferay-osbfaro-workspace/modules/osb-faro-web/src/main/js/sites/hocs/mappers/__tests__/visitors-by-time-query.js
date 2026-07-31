import {mapPropsToOptions, mapResultToProps} from '../visitors-by-time-query';

describe('VisitorsByTimeQuery Mappers', () => {
	it('should map results to props', () => {
		const mockResult = {
			data: {siteVisitorHeatMap: []},
		};

		expect(mapResultToProps(mockResult)).toEqual(
			expect.objectContaining({data: expect.any(Array)})
		);
	});

	it('should map empty results', () => {
		const mockResult = {
			data: {siteVisitorHeatMap: [{value: 0}]},
		};

		expect(mapResultToProps(mockResult)).toEqual(
			expect.objectContaining({data: expect.any(Array), total: 0})
		);
	});

	it('should map props to options', () => {
		const mockProps = {
			rangeSelectors: {rangeKey: '30'},
			router: {params: {channelId: 123}},
		};

		expect(mapPropsToOptions(mockProps)).toEqual(
			expect.objectContaining({
				variables: {
					channelId: 123,
					rangeEnd: null,
					rangeKey: parseInt('30'),
					rangeStart: null,
				},
			})
		);
	});

	it('should include segmentId in the options when provided', () => {
		const mockProps = {
			rangeSelectors: {rangeKey: '30'},
			router: {params: {channelId: 123}},
			segmentId: 'segment-100',
		};

		expect(mapPropsToOptions(mockProps)).toEqual(
			expect.objectContaining({
				variables: {
					channelId: 123,
					rangeEnd: null,
					rangeKey: parseInt('30'),
					rangeStart: null,
					segmentId: 'segment-100',
				},
			})
		);
	});
});
