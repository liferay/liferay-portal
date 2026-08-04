import AccountProfile from '../AccountProfile';
import DataSourcesProvider from 'shared/context/dataSources';
import mockStore from 'test/mock-store';
import React from 'react';
import {ChannelContext} from 'shared/context/channel';
import {cleanup, render, screen} from '@testing-library/react';
import {mockChannelContext} from 'test/mock-channel-context';
import {mockSegment} from 'test/data';
import {Provider} from 'react-redux';
import {Segment} from 'shared/util/records';
import {SegmentCategories} from 'shared/util/constants';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

type FakeFilter = {
	id: string;
	preloadedData?: {
		exclude: boolean;
		selectedItems: Array<{label?: string; value: string}>;
	};
};

let lastApiURL: string | undefined;
let lastDataSetId: string | undefined;
let lastFilters: FakeFilter[] | undefined;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: ({
		apiURL,
		filters,
		id,
	}: {
		apiURL: string;
		filters: FakeFilter[];
		id: string;
	}) => {
		lastApiURL = apiURL;
		lastDataSetId = id;
		lastFilters = filters;

		return <div data-testid="fds-component" id={id} />;
	},
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC',
	}),
}));

const renderAccountProfile = (data = {}) =>
	render(
		<Provider store={mockStore()}>
			<StaticRouter>
				<ChannelContext.Provider value={mockChannelContext() as any}>
					<DataSourcesProvider groupId="23">
						<AccountProfile
							channelId="123"
							groupId="23"
							segment={
								new Segment(
									mockSegment(0, {
										externalReferenceCode: 'my-erc',
										segmentCategory:
											SegmentCategories.Account,
										...data,
									})
								)
							}
						/>
					</DataSourcesProvider>
				</ChannelContext.Provider>
			</StaticRouter>
		</Provider>
	);

describe('AccountProfile', () => {
	beforeEach(() => {
		lastApiURL = undefined;
		lastDataSetId = undefined;
		lastFilters = undefined;
	});

	afterEach(cleanup);

	it('should render the segment name, its type and its external reference code', () => {
		renderAccountProfile();

		expect(screen.getByRole('heading', {name: 'Seattle0'})).toBeTruthy();
		expect(screen.getByText('Account Batch Segment')).toBeTruthy();
		expect(screen.getByText('ERC: my-erc')).toBeTruthy();
	});

	it('should link to the segment editor', () => {
		const {container} = renderAccountProfile();

		expect(
			container.querySelector('a[href$="/segments/0/edit"]')
		).toBeTruthy();
	});

	it('should list the accounts matching the segment', () => {
		renderAccountProfile();

		expect(screen.getByText('SEGMENT MEMBERSHIP')).toBeTruthy();
		expect(lastApiURL).toBe(
			'/o/faro/contacts/23/account/search?channelId=123&segmentId=0'
		);
		expect(lastDataSetId).toBe('segment-accounts-dataset');
	});

	it('should list the accounts matching the segment without any filter applied', () => {
		renderAccountProfile();

		const preloadedFilters = lastFilters?.filter(
			({preloadedData}) => preloadedData
		);

		expect(preloadedFilters).toHaveLength(0);
	});

	it('should offer the segment membership as a CSV download', () => {
		renderAccountProfile();

		expect(
			screen.getByRole('button', {name: 'Download Reports'})
		).toBeTruthy();
	});

	it('should display the segment criteria', () => {
		renderAccountProfile({
			criteriaString:
				"accounts.filter(filter='industry eq ''Technology''')",
		});

		expect(screen.getByText('Industry')).toBeTruthy();
		expect(screen.getByText('Segment Criteria')).toBeTruthy();
		expect(screen.getByText('"Technology"')).toBeTruthy();
	});
});
