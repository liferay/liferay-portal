import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import Sidebar from '../index';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {User} from 'shared/util/records';

const defaultProps = {
	activePathname: '',
	channelId: '123',
	currentUser: new User({emailAddress: 'test@test.com', name: 'Test Test'}),
	groupId: '23'
};

jest.unmock('react-dom');

jest.mock('shared/util/feature-flags', () => ({
	...jest.requireActual('shared/util/feature-flags'),
	ENABLE_CAMPAIGNS: false
}));

const featureFlags = jest.requireMock('shared/util/feature-flags');

describe('Sidebar', () => {
	beforeEach(() => {
		featureFlags.ENABLE_CAMPAIGNS = false;
	});

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render as collapsed', () => {
		const {container} = render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar {...defaultProps} collapsed />
				</MemoryRouter>
			</Provider>
		);

		expect(container.querySelector('.sidebar-root')).toHaveClass(
			'collapsed'
		);
	});

	it('should render with a specific sidebar id active', () => {
		const activePathName = '/workspace/23/123/contacts/individuals';

		const {container} = render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar
						{...defaultProps}
						activePathname={activePathName}
					/>
				</MemoryRouter>
			</Provider>
		);

		expect(
			container.querySelector('.sidebar-item-root.active').firstChild
		).toHaveAttribute('href', activePathName);
	});

	it('should render lifecycle and accounts items when LDP is enabled', () => {
		const {queryByText} = render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		expect(queryByText('Lifecycles')).toBeTruthy();
		expect(queryByText('Accounts')).toBeTruthy();
	});

	it('should not render the campaigns item while the feature flag is off', () => {
		const {queryByText} = render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		expect(queryByText('Campaigns')).toBeNull();
	});

	it('should render the campaigns item when the feature flag is on', () => {
		featureFlags.ENABLE_CAMPAIGNS = true;

		const {queryByText} = render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		expect(queryByText('Campaigns').closest('a')).toHaveAttribute(
			'href',
			'/workspace/23/123/campaigns'
		);
	});

	it('should not render the campaigns item when the feature flag is on but LDP is not enabled', () => {
		featureFlags.ENABLE_CAMPAIGNS = true;

		const {queryByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Sidebar {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		expect(queryByText('Campaigns')).toBeNull();
	});

	it('should not render lifecycle and accounts items when LDP is not enabled', () => {
		const {queryByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<Sidebar {...defaultProps} />
				</MemoryRouter>
			</Provider>
		);

		expect(queryByText('Lifecycles')).toBeNull();
		expect(queryByText('Accounts')).toBeNull();
	});
});
