import * as utils from '../utils';

describe('utils', () => {
	describe('getPropertiesFromItems', () => {
		it('should convert Filter to JobProperty', () => {
			const filter = 'url ~ .*documents';

			expect(
				utils.getPropertiesFromItems([
					{name: 'includeFilter', value: filter}
				])
			).toEqual([{filter, negate: false}]);
		});
	});

	describe('getFilterValueBreakdown', () => {
		expect(utils.getFilterValueBreakdown('url ~ .*documentss')).toEqual({
			exactMatchSign: '~',
			metadataTag: 'url',
			rule: '.*documentss'
		});
	});
});
