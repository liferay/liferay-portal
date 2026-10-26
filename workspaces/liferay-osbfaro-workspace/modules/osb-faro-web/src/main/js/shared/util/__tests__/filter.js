import {getFilters} from '../filter';

describe('Filter Utils', () => {
	describe('getFilters', () => {
		it('should return all filters with default value of "Any" as a fallback when filters is empty', () => {
			expect(getFilters()).toEqual({
				devices: 'Any',
				location: 'Any',
			});
		});

		it('should map filters from key:array to key:string', () => {
			const filters = {
				devices: ['Desktop'],
				location: ['Brazil'],
			};

			expect(getFilters(filters)).toEqual({
				devices: 'Desktop',
				location: 'Brazil',
			});
		});

		it('should set a default value of "Any" for the location filter if no value was given', () => {
			const filters = {
				devices: ['Desktop'],
			};

			expect(getFilters(filters)).toEqual({
				devices: 'Desktop',
				location: 'Any',
			});
		});

		it('should set a default value of "Any" for the devices filter if no value was given', () => {
			const filters = {
				location: ['Brazil'],
			};

			expect(getFilters(filters)).toEqual({
				devices: 'Any',
				location: 'Brazil',
			});
		});

		it('should return all filters with default value of "Any" as a fallback when filter keys are empty', () => {
			const filters = {
				devices: [],
				location: [],
			};

			expect(getFilters(filters)).toEqual({
				devices: 'Any',
				location: 'Any',
			});
		});

		it('should set default value of "Any" for all filters if the filter object passed contains no matching keys', () => {
			const filters = {
				others: [],
			};

			expect(getFilters(filters)).toEqual({
				devices: 'Any',
				location: 'Any',
			});
		});
	});
});
