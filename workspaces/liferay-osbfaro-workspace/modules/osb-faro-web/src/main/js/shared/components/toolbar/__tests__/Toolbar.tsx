import * as API from 'shared/api';
import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import Toolbar from '../index';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	user: {
		updateLanguage: jest.fn(() => Promise.resolve()),
	},
}));

const renderToolbar = (storeData = mockStoreDataLDP) =>
	render(
		<Provider store={mockStore(storeData)}>
			<MemoryRouter>
				<Toolbar groupId="23" />
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
		const {container} = renderToolbar();

		expect(
			container.querySelector('.lexicon-icon-product-menu-open')
		).toBeTruthy();

		fireEvent.click(screen.getByTitle(/menu/i));

		expect(
			container.querySelector('.lexicon-icon-product-menu-closed')
		).toBeTruthy();
	});

	it('reads the collapsed sidebar of the current user', () => {
		const {container} = renderToolbar(
			mockStoreDataLDP.setIn(['sidebar', '23', 'collapsed'], true)
		);

		expect(
			container.querySelector('.lexicon-icon-product-menu-closed')
		).toBeTruthy();
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

	it('closes the language menu when clicking outside of it', async () => {
		renderToolbar();

		const trigger = screen.getByTitle(/language/i);

		fireEvent.click(trigger);

		expect(trigger).toHaveAttribute('aria-expanded', 'true');

		fireEvent.pointerDown(document.body);
		fireEvent.pointerUp(document.body);
		fireEvent.mouseDown(document.body);
		fireEvent.mouseUp(document.body);

		await waitFor(() =>
			expect(trigger).toHaveAttribute('aria-expanded', 'false')
		);
	});

	it('closes the language menu when its trigger is clicked again', async () => {
		renderToolbar();

		const trigger = screen.getByTitle(/language/i);

		fireEvent.click(trigger);

		fireEvent.pointerDown(trigger);
		fireEvent.pointerUp(trigger);
		fireEvent.mouseDown(trigger);
		fireEvent.mouseUp(trigger);
		fireEvent.click(trigger);

		await waitFor(() =>
			expect(trigger).toHaveAttribute('aria-expanded', 'false')
		);
	});

	it('opens the user menu from the sticker', async () => {
		renderToolbar();

		fireEvent.click(screen.getByLabelText('Test Test'));

		expect(await screen.findByText('test@liferay.com')).toBeTruthy();
		expect(screen.getByText(/sign.out/i)).toBeTruthy();
	});
});
