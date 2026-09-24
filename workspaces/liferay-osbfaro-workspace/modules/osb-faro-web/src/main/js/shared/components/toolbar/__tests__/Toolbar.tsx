import * as API from 'shared/api';
import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import Toolbar from '../index';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {User} from 'shared/util/records';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	user: {
		updateLanguage: jest.fn(() => Promise.resolve()),
	},
}));

const defaultProps = {
	collapsed: false,
	currentUser: new User({
		emailAddress: 'test@test.com',
		languageId: 'en_US',
		name: 'Test Test',
	}),
	groupId: '23',
	onToggle: jest.fn(),
};

const renderToolbar = (props = {}, storeData = mockStoreDataLDP) =>
	render(
		<Provider store={mockStore(storeData)}>
			<MemoryRouter>
				<Toolbar {...defaultProps} {...props} />
			</MemoryRouter>
		</Provider>
	);

describe('Toolbar', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the product name', () => {
		renderToolbar();

		expect(screen.getByText(/liferay data platform/i)).toBeTruthy();
	});

	it('collapses the sidebar from the toggle', () => {
		renderToolbar();

		fireEvent.click(screen.getByTitle(/menu/i));

		expect(defaultProps.onToggle).toHaveBeenCalled();
	});

	it.each([
		[false, 'product-menu-open'],
		[true, 'product-menu-closed'],
	])('marks the toggle as collapsed=%p with %s', (collapsed, symbol) => {
		const {container} = renderToolbar({collapsed});

		expect(container.querySelector(`.lexicon-icon-${symbol}`)).toBeTruthy();
	});

	it('links the settings button to the workspace settings', () => {
		renderToolbar();

		expect(screen.getByTitle(/settings/i).closest('a')).toHaveAttribute(
			'href',
			toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId: '23'})
		);
	});

	it('labels the language selector with the active language', () => {
		renderToolbar();

		expect(screen.getByTitle(/language/i)).toHaveTextContent('EN (US)');
	});

	it('updates the language when another one is picked', async () => {
		renderToolbar();

		fireEvent.click(screen.getByTitle(/language/i));

		fireEvent.click(await screen.findByText(/日本語/));

		await waitFor(() =>
			expect(API.user.updateLanguage).toHaveBeenCalledWith({
				languageId: 'ja_JP',
			})
		);
	});

	it('does not update the language when the active one is picked', async () => {
		renderToolbar();

		fireEvent.click(screen.getByTitle(/language/i));

		fireEvent.click(await screen.findByText(/english/i));

		expect(API.user.updateLanguage).not.toHaveBeenCalled();
	});

	it('closes the language menu once a language is picked', async () => {
		renderToolbar();

		const trigger = screen.getByTitle(/language/i);

		fireEvent.click(trigger);

		expect(trigger).toHaveAttribute('aria-expanded', 'true');

		fireEvent.click(await screen.findByText(/english/i));

		await waitFor(() =>
			expect(trigger).toHaveAttribute('aria-expanded', 'false')
		);
	});

	it('opens the user menu from the sticker', async () => {
		renderToolbar();

		fireEvent.click(screen.getByLabelText('Test Test'));

		expect(await screen.findByText('test@test.com')).toBeTruthy();
		expect(screen.getByText(/sign.out/i)).toBeTruthy();
	});
});
