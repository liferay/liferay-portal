import {formatData} from '../util';

describe('formatData', () => {
	/**
	 * Apollo Client 3 deep-freezes query results in development to surface
	 * accidental mutations. formatData must therefore not sort the metrics
	 * array in place, otherwise Array.prototype.sort throws "Cannot assign to
	 * read only property '0'" as soon as the channel has two or more segments.
	 */
	it('does not mutate the frozen metrics array returned by the Apollo cache', () => {
		const metrics = Object.freeze([
			{value: 434, valueKey: '804330050123744518'},
			{value: 214, valueKey: '804357143036930828'},
			{value: 141, valueKey: 'Liferayers'}
		]);

		expect(() =>
			formatData({
				audienceReport: {
					anonymousUsersCount: 100,
					knownUsersCount: 100,
					nonsegmentedKnownUsersCount: 40,
					segmentedAnonymousUsersCount: 30,
					segmentedKnownUsersCount: 30
				},
				segment: {metrics, total: 789}
			})
		).not.toThrow();
	});
});
