jest.unmock('react-dom');

import {CSVType, useDownloadCSV} from '../utils';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {renderHook} from '@testing-library/react';

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: jest.fn(() => ({
		channelId: '123',
		groupId: '456',
		title: 'my asset title',
	})),
}));

describe('useDownloadCSV', () => {
	beforeEach(() => {
		delete (window as {location?: Location}).location;

		(window as {location: unknown}).location = new URL(
			'https://ldp.liferay.com/workspace/liferay.com/420253908131944590'
		);
	});

	it('should return the correct URL for default parameters', () => {
		const {result} = renderHook(() =>
			useDownloadCSV({type: CSVType.Individual})
		);

		const url = result.current({
			rangeEnd: '',
			rangeKey: RangeKeyTimeRanges.Last30Days,
			rangeStart: '',
		});

		expect(url).toBe(
			'/o/faro/main/456/reports/export/csv/individual?channelId=123&rangeKey=30&assetTitle=my asset title'
		);
	});

	it('should include custom date range in the URL', () => {
		const {result} = renderHook(() =>
			useDownloadCSV({type: CSVType.Individual})
		);

		const url = result.current({
			rangeEnd: '2024-01-31',
			rangeKey: RangeKeyTimeRanges.CustomRange,
			rangeStart: '2024-01-01',
		});
		expect(url).toContain('&rangeKey=CUSTOM');
		expect(url).toContain('&fromDate=2024-01-01');
		expect(url).toContain('&toDate=2024-01-31');
	});

	it('should include optional parameters when provided', () => {
		const {result} = renderHook(() =>
			useDownloadCSV({
				assetId: '12345',
				assetType: 'blog',
				individualId: '67890',
				segmentId: 'segment123',
				type: CSVType.Individual,
			})
		);

		const url = result.current({
			rangeEnd: '',
			rangeKey: RangeKeyTimeRanges.Last30Days,
			rangeStart: '',
		});

		expect(url).toBe(
			'/o/faro/main/456/reports/export/csv/individual?channelId=123&rangeKey=30&assetId=12345&assetTitle=my asset title&assetType=blog&individualId=67890&segmentId=segment123'
		);
	});

	it('should include the fixed filter and objectType when provided', () => {
		const {result} = renderHook(() =>
			useDownloadCSV({
				filter: "(assetType eq 'blog')",
				objectType: 'ObjectType1',
				type: CSVType.Asset,
			})
		);

		const url = result.current({
			rangeEnd: '',
			rangeKey: RangeKeyTimeRanges.Last30Days,
			rangeStart: '',
		});

		expect(url).toContain(
			`filter=${encodeURIComponent("(assetType eq 'blog')")}`
		);
		expect(url).toContain('objectType=ObjectType1');
	});

	it('should override the fixed filter and query when overrides are provided', () => {
		const {result} = renderHook(() =>
			useDownloadCSV({
				filter: "(assetType eq 'blog')",
				type: CSVType.Asset,
			})
		);

		const url = result.current(
			{
				rangeEnd: '',
				rangeKey: RangeKeyTimeRanges.Last30Days,
				rangeStart: '',
			},
			{filter: "(assetType eq 'webContent')", query: 'liferay'}
		);

		expect(url).toContain(
			`filter=${encodeURIComponent("(assetType eq 'webContent')")}`
		);
		expect(url).toContain('query=liferay');
	});

	it('should fall back to the fixed filter when no override filter is provided', () => {
		const {result} = renderHook(() =>
			useDownloadCSV({
				filter: "(assetType eq 'blog')",
				type: CSVType.Asset,
			})
		);

		const url = result.current({
			rangeEnd: '',
			rangeKey: RangeKeyTimeRanges.Last30Days,
			rangeStart: '',
		});

		expect(url).toContain(
			`filter=${encodeURIComponent("(assetType eq 'blog')")}`
		);
	});

	it('should include order by fields if field and sortOrder are present', () => {
		(window as {location: unknown}).location = new URL(
			'https://ldp.liferay.com/workspace/liferay.com/420253908131944590/?field=name&page=1&sortOrder=DESC'
		);

		const {result} = renderHook(() =>
			useDownloadCSV({type: CSVType.Individual})
		);

		const url = result.current({
			rangeEnd: '',
			rangeKey: RangeKeyTimeRanges.Last30Days,
			rangeStart: '',
		});

		expect(url).toBe(
			'/o/faro/main/456/reports/export/csv/individual?channelId=123&rangeKey=30&assetTitle=my asset title&orderByFields=%5B%7B%22fieldName%22%3A%22givenName%22%2C%22orderBy%22%3A%22desc%22%2C%22system%22%3Afalse%7D%2C%7B%22fieldName%22%3A%22familyName%22%2C%22orderBy%22%3A%22desc%22%2C%22system%22%3Afalse%7D%5D'
		);
	});
});
