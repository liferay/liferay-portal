import React from 'react';
import {
	cleanup,
	render,
	renderHook,
	screen,
	waitFor,
} from '@testing-library/react';
import {columns, FrontendDataSet, useSnapshots} from '../FrontendDataSet';
import {EConfigInURLBehavior} from '@liferay/frontend-data-set-web';
import {Routes} from 'shared/util/router';
import {warmFrontendDataSet} from 'test/warm-frontend-data-set';

jest.unmock('react-dom');

let lastProps: {[key: string]: unknown} | undefined;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: {[key: string]: unknown; id: string}) => {
		lastProps = props;

		return <div data-testid="base-fds" id={props.id} />;
	},
}));

const mockFetch = (items: unknown[]) => {
	const fetch = jest.fn(() =>
		Promise.resolve({json: () => Promise.resolve({items})})
	);

	Liferay.Util = {fetch} as unknown as typeof Liferay.Util;

	return fetch;
};

afterEach(() => {
	cleanup();

	lastProps = undefined;
});

beforeAll(warmFrontendDataSet);

describe('columns.countRenderer', () => {
	it('should render a real zero as 0', () => {
		render(columns.countRenderer({itemData: {}, value: 0}));

		expect(screen.getByText('0')).toBeInTheDocument();
	});

	it('should render a missing count as the no data value', () => {
		render(columns.countRenderer({itemData: {}, value: undefined}));

		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});

	it('should render a numeric string count as a number', () => {
		const {container} = render(
			columns.countRenderer({
				itemData: {},
				value: '5' as unknown as number,
			})
		);

		expect(container.textContent).toBe('5');
	});

	it('should render an empty string count as the no data value', () => {
		render(
			columns.countRenderer({
				itemData: {},
				value: '' as unknown as number,
			})
		);

		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});
});

describe('columns.attributeNameAndValue', () => {
	it('should render a real zero value as 0', () => {
		render(
			columns.attributeNameAndValue({attributeName: 'Count', value: 0})
		);

		expect(screen.getByText('0')).toBeInTheDocument();
	});

	it('should render a missing value as the no data value', () => {
		render(
			columns.attributeNameAndValue({attributeName: 'Count', value: ''})
		);

		expect(screen.getByText('Count')).toBeInTheDocument();
		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});
});

describe('columns.textRenderer', () => {
	it('should render the value', () => {
		render(columns.textRenderer({itemData: {}, value: 'Software'}));

		expect(screen.getByText('Software')).toBeInTheDocument();
	});

	it('should render a real zero as 0', () => {
		render(columns.textRenderer({itemData: {}, value: 0}));

		expect(screen.getByText('0')).toBeInTheDocument();
	});

	it('should render an empty value as the no data value', () => {
		render(columns.textRenderer({itemData: {}, value: ''}));

		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});

	it('should render a missing value as the no data value', () => {
		render(
			columns.textRenderer({
				itemData: {},
				value: undefined as unknown as string,
			})
		);

		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});
});

describe('columns.dateRenderer', () => {
	it('should render a missing date as the no data value', () => {
		render(columns.dateRenderer({itemData: {}, value: ''}));

		expect(screen.getByText('–')).toHaveClass('text-secondary');
	});

	it('should render an ISO date in the custom date format, in UTC', () => {
		render(
			columns.dateRenderer({
				itemData: {},
				value: '2026-06-10T01:30:00Z',
			})
		);

		expect(screen.getByText('Jun 10, 2026')).toBeInTheDocument();
	});

	it('should render a timestamp in the custom date format, in UTC', () => {
		render(
			columns.dateRenderer({
				itemData: {},
				value: Date.parse('2026-06-10T23:30:00Z'),
			})
		);

		expect(screen.getByText('Jun 10, 2026')).toBeInTheDocument();
	});
});

describe('columns.nameAndLinkRenderer', () => {
	it('should generate an href that includes the channelId path segment', () => {
		render(
			columns.nameAndLinkRenderer({
				channelId: '123',
				groupId: '23',
				itemData: {id: 'abc'},
				route: Routes.CONTACTS_ACCOUNT,
				value: 'Acme Corp',
			})
		);

		expect(screen.getByRole('link')).toHaveAttribute(
			'href',
			'/workspace/23/123/contacts/accounts/abc'
		);
	});

	it('should use value as the link text when provided', () => {
		render(
			columns.nameAndLinkRenderer({
				channelId: '123',
				groupId: '23',
				itemData: {id: 'abc'},
				route: Routes.CONTACTS_ACCOUNT,
				value: 'Acme Corp',
			})
		);

		expect(screen.getByRole('link')).toHaveTextContent('Acme Corp');
	});

	it('should fall back to itemData.id as the link text when value is empty', () => {
		render(
			columns.nameAndLinkRenderer({
				channelId: '123',
				groupId: '23',
				itemData: {id: 'abc'},
				route: Routes.CONTACTS_ACCOUNT,
				value: '',
			})
		);

		expect(screen.getByRole('link')).toHaveTextContent('abc');
	});
});

describe('useSnapshots', () => {
	it('should return null while the snapshots are still loading', () => {
		mockFetch([]);

		const {result} = renderHook(() =>
			useSnapshots('accounts-list-dataset')
		);

		expect(result.current).toBeNull();
	});

	it('should return saved views as a single group of snapshots', async () => {
		mockFetch([
			{
				externalReferenceCode: 'erc-1',
				label: 'My View',
				viewConfig: '{"filters":[]}',
			},
		]);

		const {result} = renderHook(() =>
			useSnapshots('accounts-list-dataset')
		);

		await waitFor(() =>
			expect(result.current).toEqual([
				{
					headerVisible: false,
					items: [
						{
							configuration: '{"filters":[]}',
							erc: 'erc-1',
							label: 'My View',
						},
					],
				},
			])
		);
	});

	it('should return an empty array when there are no saved views', async () => {
		mockFetch([]);

		const {result} = renderHook(() =>
			useSnapshots('accounts-list-dataset')
		);

		await waitFor(() => expect(result.current).toEqual([]));
	});

	it('should return an empty array and not fetch when disabled through the second argument', () => {
		const fetch = mockFetch([]);

		const {result} = renderHook(() =>
			useSnapshots('accounts-list-dataset', false)
		);

		expect(fetch).not.toHaveBeenCalled();
		expect(result.current).toEqual([]);
	});
});

describe('FrontendDataSet (snapshots wrapper)', () => {
	describe('when snapshotsEnabled is not set', () => {
		it('should render the base FrontendDataSet directly with the given props', () => {
			render(<FrontendDataSet id="assetTable" views={[]} />);

			expect(screen.getByTestId('base-fds')).toHaveAttribute(
				'id',
				'assetTable'
			);
		});

		it('should not fetch snapshots and disable them on the base FrontendDataSet', () => {
			const fetch = mockFetch([]);

			render(<FrontendDataSet id="assetTable" views={[]} />);

			expect(fetch).not.toHaveBeenCalled();
			expect(lastProps?.snapshotsEnabled).toBe(false);
		});
	});

	describe('when snapshotsEnabled is true', () => {
		it('should render the loading indicator while the snapshots are still loading', () => {
			Liferay.Util = {
				fetch: jest.fn(() => new Promise(() => {})),
			} as unknown as typeof Liferay.Util;

			const {container} = render(
				<FrontendDataSet id="assetTable" snapshotsEnabled views={[]} />
			);

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
			expect(screen.queryByTestId('base-fds')).not.toBeInTheDocument();
		});

		it('should render the base FrontendDataSet with snapshots enabled once they are ready', async () => {
			mockFetch([]);

			render(
				<FrontendDataSet id="assetTable" snapshotsEnabled views={[]} />
			);

			await waitFor(() =>
				expect(screen.getByTestId('base-fds')).toBeInTheDocument()
			);

			expect(lastProps?.snapshotsEnabled).toBe(true);
		});
	});

	describe('configInURLBehavior', () => {
		it('should default configInURLBehavior to OFF when it is not provided', () => {
			render(<FrontendDataSet id="assetTable" views={[]} />);

			expect(lastProps?.configInURLBehavior).toBe(
				EConfigInURLBehavior.OFF
			);
		});

		it('should let configInURLBehavior be overridden through props', () => {
			render(
				<FrontendDataSet
					configInURLBehavior={EConfigInURLBehavior.PUSH}
					id="assetTable"
					views={[]}
				/>
			);

			expect(lastProps?.configInURLBehavior).toBe(
				EConfigInURLBehavior.PUSH
			);
		});
	});

	describe('sorts', () => {
		const views = [
			{
				name: 'table',
				schema: {
					fields: [
						{fieldName: 'name', label: 'Name', sortable: true},
						{fieldName: 'views', label: 'Views', sortable: true},
						{fieldName: 'notes', label: 'Notes', sortable: false},
					],
				},
			},
			{
				name: 'allAttributes',
				schema: {
					fields: [
						{fieldName: 'name', label: 'Name', sortable: true},
						{
							fieldName: 'downloads',
							label: 'Downloads',
							sortable: true,
						},
					],
				},
			},
		];

		const getSorts = () =>
			lastProps?.sorts as {
				active: boolean;
				default: boolean;
				direction: string;
				key: string;
			}[];

		it('should offer one sort for each sortable field across the views, with no duplicates', () => {
			render(
				<FrontendDataSet
					defaultSort={{key: 'views'}}
					id="assetTable"
					views={views}
				/>
			);

			expect(lastProps?.sorts).toEqual([
				{
					active: false,
					default: false,
					direction: 'asc',
					key: 'name',
					label: 'Name',
				},
				{
					active: true,
					default: true,
					direction: 'desc',
					key: 'views',
					label: 'Views',
				},
				{
					active: false,
					default: false,
					direction: 'asc',
					key: 'downloads',
					label: 'Downloads',
				},
			]);
		});

		it('should give the default and the active sort the default direction, and start the others ascending', () => {
			render(
				<FrontendDataSet
					activeSortKey="downloads"
					defaultSort={{key: 'views'}}
					id="assetTable"
					views={views}
				/>
			);

			expect(getSorts().map(({direction}) => direction)).toEqual([
				'asc',
				'desc',
				'desc',
			]);
		});

		it('should use the direction of the default sort', () => {
			render(
				<FrontendDataSet
					defaultSort={{direction: 'asc', key: 'name'}}
					id="assetTable"
					views={views}
				/>
			);

			expect(getSorts().map(({direction}) => direction)).toEqual([
				'asc',
				'asc',
				'asc',
			]);
		});

		it('should activate the active sort key and keep the default sort as the default', () => {
			render(
				<FrontendDataSet
					activeSortKey="downloads"
					defaultSort={{key: 'views'}}
					id="assetTable"
					views={views}
				/>
			);

			expect(
				getSorts()
					.filter((sort) => sort.active)
					.map(({key}) => key)
			).toEqual(['downloads']);
			expect(
				getSorts()
					.filter((sort) => sort.default)
					.map(({key}) => key)
			).toEqual(['views']);
		});

		it('should fall back to the default sort when the active sort key names no sortable field', () => {
			render(
				<FrontendDataSet
					activeSortKey="notes"
					defaultSort={{key: 'views'}}
					id="assetTable"
					views={views}
				/>
			);

			expect(
				getSorts()
					.filter((sort) => sort.active)
					.map(({key}) => key)
			).toEqual(['views']);
		});

		it('should keep the default sort when its field is not sortable', () => {
			render(
				<FrontendDataSet
					defaultSort={{key: 'notes'}}
					id="assetTable"
					views={views}
				/>
			);

			expect(getSorts().map(({key}) => key)).toEqual([
				'name',
				'views',
				'notes',
				'downloads',
			]);
			expect(
				getSorts()
					.filter((sort) => sort.active)
					.map(({key}) => key)
			).toEqual(['notes']);
		});

		it('should pass an explicit sorts unchanged', () => {
			const sorts = [
				{active: true, direction: 'asc' as const, key: 'name'},
			];

			render(
				<FrontendDataSet
					defaultSort={{key: 'views'}}
					id="assetTable"
					sorts={sorts}
					views={views}
				/>
			);

			expect(lastProps?.sorts).toBe(sorts);
		});

		it('should pass no sorts without a default sort', () => {
			render(<FrontendDataSet id="assetTable" views={views} />);

			expect(lastProps?.sorts).toBeUndefined();
		});
	});
});
