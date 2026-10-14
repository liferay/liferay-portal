import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import Sidebar from '../index';
import {fireEvent, render, screen} from '@testing-library/react';
import {Map} from 'immutable';
import {MemoryRouter} from 'react-router';
import {Provider} from 'react-redux';
import {resetBodyWidth, setBodyWidth} from 'test/body-width';

const defaultProps = {
	activePathname: '',
	channelId: '123',
	containerRef: React.createRef(),
	groupId: '23',
	onCollapsedChange: jest.fn()
};

const renderSidebar = (props = {}) =>
	render(
		<Provider store={mockStore(mockStoreDataLDP)}>
			<MemoryRouter>
				<Sidebar {...defaultProps} {...props} />
			</MemoryRouter>
		</Provider>
	);

jest.unmock('react-dom');

describe('Sidebar', () => {
	afterEach(() => {
		resetBodyWidth();
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

		expect(container.querySelector('.sidebar-root')).toHaveAttribute(
			'inert'
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

		expect(container.querySelector('.nav-link.active')).toHaveAttribute(
			'href',
			activePathName
		);
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
					<Sidebar {...defaultProps} collapsedSections={new Map()} />
				</MemoryRouter>
			</Provider>
		);

		expect(
			screen.getByRole('menuitem', {name: 'Touchpoints'})
		).toHaveAttribute('aria-expanded', 'true');
	});

	it('should collapse a section whose collapsedSections entry is true', () => {
		render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar
						{...defaultProps}
						collapsedSections={new Map({touchpoints: true})}
					/>
				</MemoryRouter>
			</Provider>
		);

		expect(
			screen.getByRole('menuitem', {name: 'Touchpoints'})
		).toHaveAttribute('aria-expanded', 'false');
	});

	it('should call onSectionToggle with the section key when its header is clicked', () => {
		const onSectionToggle = jest.fn();

		render(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar
						{...defaultProps}
						onSectionToggle={onSectionToggle}
					/>
				</MemoryRouter>
			</Provider>
		);

		fireEvent.click(screen.getByRole('menuitem', {name: 'Touchpoints'}));

		expect(onSectionToggle).toHaveBeenCalledWith('touchpoints', true);
	});

	it('should close on mobile when it mounts', () => {
		const onCollapsedChange = jest.fn();

		renderSidebar({onCollapsedChange});

		expect(onCollapsedChange).toHaveBeenCalledWith(true);
	});

	it('should close on mobile after navigating', () => {
		const onCollapsedChange = jest.fn();

		const {rerender} = renderSidebar({onCollapsedChange});

		onCollapsedChange.mockClear();

		rerender(
			<Provider store={mockStore(mockStoreDataLDP)}>
				<MemoryRouter>
					<Sidebar
						{...defaultProps}
						activePathname="/workspace/23/123/sites"
						onCollapsedChange={onCollapsedChange}
					/>
				</MemoryRouter>
			</Provider>
		);

		expect(onCollapsedChange).toHaveBeenCalledWith(true);
	});

	it('should close from its close button on mobile', () => {
		const onCollapsedChange = jest.fn();

		renderSidebar({onCollapsedChange});

		onCollapsedChange.mockClear();

		fireEvent.click(screen.getByRole('button', {name: 'Close'}));

		expect(onCollapsedChange).toHaveBeenCalledWith(true);
	});

	it('should keep its state and have no close button on desktop', () => {
		setBodyWidth(1024);

		const onCollapsedChange = jest.fn();

		renderSidebar({onCollapsedChange});

		expect(onCollapsedChange).not.toHaveBeenCalled();
		expect(screen.queryByRole('button', {name: 'Close'})).toBeNull();
	});

	it('should close from its backdrop on mobile', () => {
		const onCollapsedChange = jest.fn();

		renderSidebar({onCollapsedChange});

		onCollapsedChange.mockClear();

		fireEvent.click(screen.getByTestId('side-panel-backdrop'));

		expect(onCollapsedChange).toHaveBeenCalledWith(true);
	});

	it('should have no backdrop on mobile once collapsed', () => {
		renderSidebar({collapsed: true});

		expect(screen.queryByTestId('side-panel-backdrop')).toBeNull();
	});

	it('should close from its backdrop where Clay overlays it above mobile', () => {
		Object.defineProperty(document.body, 'clientWidth', {
			configurable: true,
			value: 800
		});

		const onCollapsedChange = jest.fn();

		renderSidebar({onCollapsedChange});

		fireEvent.click(screen.getByTestId('side-panel-backdrop'));

		expect(onCollapsedChange).toHaveBeenCalledWith(true);
	});
});
