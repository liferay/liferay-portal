import React from 'react';
import {
	cleanup,
	render,
	renderHook,
	screen,
	waitFor,
} from '@testing-library/react';
import {
	columns,
	EConfigInURLBehavior,
	FrontendDataSet,
	useSnapshots,
} from '../FrontendDataSet';
import {Routes} from 'shared/util/router';

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

const enableSnapshotsFeatureFlags = () => {
	Liferay.FeatureFlags['LPS-164563'] = true;
};

afterEach(() => {
	cleanup();

	delete Liferay.FeatureFlags['LPS-164563'];

	lastProps = undefined;
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
	beforeEach(enableSnapshotsFeatureFlags);

	it('should return null while the snapshots are still loading', () => {
		mockFetch([]);

		const {result} = renderHook(() =>
			useSnapshots('accounts-list-dataset')
		);

		expect(result.current).toBeNull();
	});

	it('should return saved views as a flat list of snapshots', async () => {
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
					configuration: '{"filters":[]}',
					erc: 'erc-1',
					label: 'My View',
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

	it('should not fetch when the feature flags are disabled', () => {
		Liferay.FeatureFlags['LPS-164563'] = false;

		const fetch = mockFetch([]);

		const {result} = renderHook(() =>
			useSnapshots('accounts-list-dataset')
		);

		expect(fetch).not.toHaveBeenCalled();
		expect(result.current).toEqual([]);
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
			enableSnapshotsFeatureFlags();

			const fetch = mockFetch([]);

			render(<FrontendDataSet id="assetTable" views={[]} />);

			expect(fetch).not.toHaveBeenCalled();
			expect(lastProps?.snapshotsEnabled).toBe(false);
		});
	});

	describe('when snapshotsEnabled is true', () => {
		beforeEach(enableSnapshotsFeatureFlags);

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
});
