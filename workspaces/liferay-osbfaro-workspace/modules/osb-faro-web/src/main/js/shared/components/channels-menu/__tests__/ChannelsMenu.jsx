import ChannelsMenu from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const mockMenuItems = () => [
	{
		id: '1',
		name: 'Link 1',
		url: '/1'
	},
	{
		id: '2',
		name: 'Link 2',
		url: '/2'
	}
];

describe('ChannelsMenu', () => {
	afterEach(cleanup);

	it('should render the first item as label', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelsMenu channels={mockMenuItems()} groupId='123456' />
				</BrowserRouter>
			</Provider>
		);

		expect(container).toHaveTextContent('Link 1');
		expect(container).toMatchSnapshot();
	});

	it('should render the empty state label when channels is not provided', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelsMenu groupId='123456' />
				</BrowserRouter>
			</Provider>
		);

		expect(container).toHaveTextContent('No Properties');
	});

	it('should render dropdown menu when clicked', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelsMenu
						channels={mockMenuItems()}
						defaultChannelId='1'
						groupId='123456'
					/>
				</BrowserRouter>
			</Provider>
		);

		const toggleButton = screen.getByRole('combobox', {name: 'Property'});

		fireEvent.click(toggleButton);

		expect(
			document.body.querySelector('.channels-menu-dropdown')
		).toBeTruthy();
	});

	it('should mark the selected channel and let the other one be picked', () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<ChannelsMenu
						channels={mockMenuItems()}
						defaultChannelId='1'
						groupId='123456'
					/>
				</BrowserRouter>
			</Provider>
		);

		fireEvent.click(screen.getByRole('combobox', {name: 'Property'}));

		expect(screen.getByText('Link 2')).toBeTruthy();
	});
});
