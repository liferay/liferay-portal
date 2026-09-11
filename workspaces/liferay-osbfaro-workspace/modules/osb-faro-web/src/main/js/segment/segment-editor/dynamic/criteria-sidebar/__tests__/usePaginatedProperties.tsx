import {act, renderHook} from '@testing-library/react';
import {PaginatedSource} from '../../criterion-types/RemoteCriterionType';
import {Property} from 'shared/util/records';
import {PropertyTypes} from '../../utils/constants';
import {
	SEARCH_DEBOUNCE_DELAY,
	usePaginatedProperties,
} from '../usePaginatedProperties';

jest.unmock('react-dom');

interface ITestItem {
	id: string;
	name: string;
}

const createTestSource = (
	api: PaginatedSource<ITestItem>['api']
): PaginatedSource<ITestItem> => ({
	api,
	createProperty: ({name}) =>
		new Property({
			entityName: 'Individual',
			label: name,
			name,
			propertyKey: 'search-term',
			type: PropertyTypes.SearchTerm,
		}),
});

const flush = async () => {
	await act(async () => {
		await Promise.resolve();
	});
};

describe('usePaginatedProperties', () => {
	it('should map the fetched items through the source createProperty', async () => {
		const source = createTestSource(async () => ({
			items: [{id: 'shoes', name: 'shoes'}],
			totalCount: 1,
		}));

		const {result} = renderHook(() =>
			usePaginatedProperties({
				channelId: '123',
				groupId: '456',
				pageSize: 12,
				source,
			})
		);

		await flush();

		expect(
			result.current.items
				.toArray()
				.map((property: Property) => property.name)
		).toEqual(['shoes']);
	});

	it('should refetch when the keywords change', async () => {
		const api = jest.fn(async () => ({items: [], totalCount: 0}));
		const source = createTestSource(api);

		const {rerender} = renderHook(
			({keywords}) =>
				usePaginatedProperties({
					channelId: '123',
					groupId: '456',
					keywords,
					pageSize: 12,
					source,
				}),
			{initialProps: {keywords: ''}}
		);

		await flush();

		api.mockClear();

		rerender({keywords: 'sho'});

		act(() => {
			jest.advanceTimersByTime(SEARCH_DEBOUNCE_DELAY);
		});

		await flush();

		expect(api).toHaveBeenCalledWith(
			expect.objectContaining({keywords: 'sho'})
		);
	});

	it('should issue one request for a burst of keystrokes, not one per keystroke', async () => {
		const api = jest.fn(async () => ({items: [], totalCount: 0}));
		const source = createTestSource(api);

		const {rerender} = renderHook(
			({keywords}) =>
				usePaginatedProperties({
					channelId: '123',
					groupId: '456',
					keywords,
					pageSize: 12,
					source,
				}),
			{initialProps: {keywords: ''}}
		);

		await flush();

		api.mockClear();

		rerender({keywords: 's'});
		rerender({keywords: 'sh'});
		rerender({keywords: 'sho'});

		act(() => {
			jest.advanceTimersByTime(SEARCH_DEBOUNCE_DELAY);
		});

		await flush();

		expect(api).toHaveBeenCalledTimes(1);
		expect(api).toHaveBeenCalledWith(
			expect.objectContaining({keywords: 'sho'})
		);
	});

	it('should derive the total page count from the total item count', async () => {
		const source = createTestSource(async () => ({
			items: [],
			totalCount: 42,
		}));

		const {result} = renderHook(() =>
			usePaginatedProperties({
				channelId: '123',
				groupId: '456',
				pageSize: 12,
				source,
			})
		);

		await flush();

		expect(result.current.totalPages).toBe(4);
	});

	it('should fetch the requested page when setPage is called', async () => {
		const api = jest.fn(async () => ({items: [], totalCount: 42}));
		const source = createTestSource(api);

		const {result} = renderHook(() =>
			usePaginatedProperties({
				channelId: '123',
				groupId: '456',
				pageSize: 12,
				source,
			})
		);

		await flush();

		act(() => result.current.setPage(3));

		await flush();

		expect(api).toHaveBeenLastCalledWith(
			expect.objectContaining({page: 3})
		);
		expect(result.current.page).toBe(3);
	});

	it('should go back to the first page when the keywords change', async () => {
		const api = jest.fn(async () => ({items: [], totalCount: 42}));
		const source = createTestSource(api);

		const {rerender, result} = renderHook(
			({keywords}) =>
				usePaginatedProperties({
					channelId: '123',
					groupId: '456',
					keywords,
					pageSize: 12,
					source,
				}),
			{initialProps: {keywords: ''}}
		);

		await flush();

		act(() => result.current.setPage(3));

		await flush();

		rerender({keywords: 'sho'});

		act(() => {
			jest.advanceTimersByTime(SEARCH_DEBOUNCE_DELAY);
		});

		await flush();

		expect(result.current.page).toBe(1);
		expect(api).toHaveBeenLastCalledWith(
			expect.objectContaining({keywords: 'sho', page: 1})
		);
	});

	it('should not call the api while disabled', async () => {
		const api = jest.fn(async () => ({items: [], totalCount: 0}));
		const source = createTestSource(api);

		const {rerender} = renderHook(
			({enabled}) =>
				usePaginatedProperties({
					channelId: '123',
					enabled,
					groupId: '456',
					pageSize: 12,
					source,
				}),
			{initialProps: {enabled: false}}
		);

		await flush();

		expect(api).not.toHaveBeenCalled();

		rerender({enabled: true});

		await flush();

		expect(api).toHaveBeenCalledTimes(1);
	});

	it('should not request the stale page when the source changes', async () => {
		const nextApi = jest.fn(async () => ({items: [], totalCount: 42}));

		const source = createTestSource(async () => ({
			items: [],
			totalCount: 42,
		}));
		const nextSource = createTestSource(nextApi);

		const {rerender, result} = renderHook(
			({source}) =>
				usePaginatedProperties({
					channelId: '123',
					groupId: '456',
					pageSize: 12,
					source,
				}),
			{initialProps: {source}}
		);

		await flush();

		act(() => result.current.setPage(3));

		await flush();

		rerender({source: nextSource});

		await flush();

		expect(nextApi).toHaveBeenCalledTimes(1);
		expect(nextApi).toHaveBeenCalledWith(
			expect.objectContaining({page: 1})
		);
	});

	it('should ignore a response that resolves after the hook is disabled', async () => {
		let resolveApi: (result: {
			items: ITestItem[];
			totalCount: number;
		}) => void = () => {};

		const source = createTestSource(
			() =>
				new Promise((resolve) => {
					resolveApi = resolve;
				})
		);

		const {rerender, result} = renderHook(
			({enabled}) =>
				usePaginatedProperties({
					channelId: '123',
					enabled,
					groupId: '456',
					pageSize: 12,
					source,
				}),
			{initialProps: {enabled: true}}
		);

		rerender({enabled: false});

		await act(async () => {
			resolveApi({items: [{id: 'shoes', name: 'shoes'}], totalCount: 1});

			await Promise.resolve();
		});

		expect(result.current.items.toArray()).toHaveLength(0);
	});
});
