import {mapPropsToOptions, mapResultToProps} from '../cohort-query';

const mockData = {
	cohort: {
		anonymousCohortHeatMapMetrics: ['test'],
		knownCohortHeatMapMetrics: ['foo'],
		visitorsCohortHeatMapMetrics: ['bar'],
	},
};

describe('Cohort Query Mapper', () => {
	describe('mapPropsToOptions', () => {
		it('should map cohort query props to options', () => {
			const interval = 'D';
			const channelId = '123';

			expect(mapPropsToOptions({channelId, interval})).toEqual(
				expect.objectContaining({
					variables: {
						channelId,
						interval,
					},
				})
			);
		});

		it('should include segmentId in the mapped options when passed', () => {
			const interval = 'D';
			const channelId = '123';
			const segmentId = 'segment-1';

			expect(mapPropsToOptions({channelId, interval, segmentId})).toEqual(
				expect.objectContaining({
					variables: {
						channelId,
						interval,
						segmentId,
					},
				})
			);
		});
	});

	describe('mapResultToProps', () => {
		it('should map cohort query result to props', () => {
			expect(mapResultToProps({data: mockData})).toEqual(
				expect.objectContaining({
					data: {
						anonymousVisitors: {
							items: expect.any(Array),
						},
						knownVisitors: {
							items: expect.any(Array),
						},
						visitors: {
							items: expect.any(Array),
						},
					},
					empty: false,
				})
			);
		});

		it('should receive empty as true', () => {
			expect(
				mapResultToProps({
					data: {
						cohort: {
							anonymousCohortHeatMapMetrics: [],
							knownCohortHeatMapMetrics: [],
							visitorsCohortHeatMapMetrics: [],
						},
					},
				})
			).toEqual(
				expect.objectContaining({
					data: {
						anonymousVisitors: {
							items: expect.any(Array),
						},
						knownVisitors: {
							items: expect.any(Array),
						},
						visitors: {
							items: expect.any(Array),
						},
					},
					empty: true,
				})
			);
		});
	});
});
