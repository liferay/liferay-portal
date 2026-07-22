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

describe('Sidebar', () => {
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
