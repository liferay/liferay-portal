import EmptyState, {CardEmpty} from '../EmptyState';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

describe('Workspace Empty State', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<EmptyState />
				</BrowserRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should open the contact sales modal', async () => {
		render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<EmptyState />
				</BrowserRouter>
			</Provider>
		);

		expect(screen.queryByRole('dialog')).toBeNull();

		fireEvent.click(screen.getByText('Contact Sales'));

		expect(await screen.findByRole('dialog')).toBeInTheDocument();
		expect(
			await screen.findByRole('heading', {name: 'Contact Sales'})
		).toBeInTheDocument();
	});
});

describe('Workspace Empty State - CardEmpty', () => {
	it('should render CardEmpty', () => {
		const {container} = render(
			<CardEmpty
				buttonProps={{
					label: 'CardEmpty Button Label'
				}}
				description='CardEmpty Description'
				icon='home'
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render a button with the secondary display', () => {
		const {getByText} = render(
			<CardEmpty
				buttonProps={{
					displayType: 'secondary',
					label: 'CardEmpty Button Label'
				}}
				description='CardEmpty Description'
				icon='home'
			/>
		);

		expect(
			getByText('CardEmpty Button Label').classList.contains(
				'btn-secondary'
			)
		).toBeTruthy();
	});

	it('should contain a button with onClick', () => {
		let foo = '';

		const {getByText} = render(
			<CardEmpty
				buttonProps={{
					displayType: 'secondary',
					label: 'CardEmpty Button Label',
					onClick: () => {
						foo = 'it was clicked';
					}
				}}
				description='CardEmpty Description'
				icon='home'
			/>
		);

		fireEvent.click(getByText('CardEmpty Button Label'));

		expect(foo).toEqual('it was clicked');
	});

	it('should contain a button with href', () => {
		const {getByText} = render(
			<BrowserRouter>
				<CardEmpty
					buttonProps={{
						displayType: 'secondary',
						href: '/workspaces',
						label: 'CardEmpty Button Label'
					}}
					description='CardEmpty Description'
					icon='home'
				/>
			</BrowserRouter>
		);

		expect(getByText('CardEmpty Button Label')).toHaveAttribute(
			'href',
			'/workspaces'
		);
	});
});
