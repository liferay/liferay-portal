import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import Sidebar from '../index';
import {fireEvent, render, screen} from '@testing-library/react';
import {Map} from 'immutable';
import {MemoryRouter} from 'react-router';
import {Provider} from 'react-redux';

const defaultProps = {
	activePathname: '',
	channelId: '123',
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

	it('should render the campaigns item when LDP is enabled', () => {
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

	it('should not render the campaigns item when LDP is not enabled', () => {
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

	it('should default a section to expanded when nothing is stored for it', () => {
		render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar {...defaultProps} expandedSections={new Map()} />
				</MemoryRouter>
			</Provider>
		);

		expect(
			screen.getByRole('button', {name: 'Touchpoints'})
		).toHaveAttribute('aria-expanded', 'true');
	});

	it('should collapse a section whose expandedSections entry is false', () => {
		render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar
						{...defaultProps}
						expandedSections={
							new Map({touchpoints: false})
						}
					/>
				</MemoryRouter>
			</Provider>
		);

		expect(
			screen.getByRole('button', {name: 'Touchpoints'})
		).toHaveAttribute('aria-expanded', 'false');
	});

	it('should call onSectionExpandedChange with the section key when its header is clicked', () => {
		const onSectionExpandedChange = jest.fn();

		render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar
						{...defaultProps}
						onSectionExpandedChange={onSectionExpandedChange}
					/>
				</MemoryRouter>
			</Provider>
		);

		fireEvent.click(screen.getByRole('button', {name: 'Touchpoints'}));

		expect(onSectionExpandedChange).toHaveBeenCalledWith(
			'touchpoints',
			false
		);
	});
});
