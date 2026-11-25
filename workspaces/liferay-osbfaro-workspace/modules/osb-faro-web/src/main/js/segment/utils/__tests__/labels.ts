import {getSegmentCategoryLabel, getSegmentTypeLabel} from '../labels';
import {SegmentCategories, SegmentTypes} from 'shared/util/constants';

describe('labels', () => {
	it.each([
		[SegmentCategories.Account, /^account$/i],
		[SegmentCategories.Individual, /^individual$/i],
		[undefined, /^individual$/i],
	])(
		'should label the %s segment category',
		(segmentCategory, expected) => {
			expect(getSegmentCategoryLabel(segmentCategory)).toMatch(expected);
		}
	);

	it.each([
		[SegmentTypes.Batch, /^batch$/i],
		[SegmentTypes.RealTime, /^real-time$/i],
	])('should label the %s segment type', (segmentType, expected) => {
		expect(getSegmentTypeLabel(segmentType)).toMatch(expected);
	});
});
