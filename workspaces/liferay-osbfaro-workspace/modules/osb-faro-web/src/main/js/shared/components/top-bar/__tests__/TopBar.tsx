import * as API from 'shared/api';
import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import TopBar, {getLanguageLabel} from '../index';
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

const renderTopBar = (props = {}, storeData = mockStoreDataLDP) =>
	render(
		<Provider store={mockStore(storeData)}>
			<MemoryRouter>
				<TopBar {...defaultProps} {...props} />
			</MemoryRouter>
		</Provider>
	);

describe('getLanguageLabel', () => {
	it('compacts a portal languageId', () => {
		expect(getLanguageLabel('en_US')).toBe('EN (US)');
		expect(getLanguageLabel('pt_BR')).toBe('PT (BR)');
	});

	it('falls back to the default language when there is none', () => {
		expect(getLanguageLabel(null)).toBe('EN (US)');
	});
});

describe('TopBar', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the product name', () => {
		renderTopBar();

		expect(screen.getByText(/liferay data platform/i)).toBeTruthy();
	});

	it('collapses the sidebar from the toggle', () => {
		renderTopBar();

		fireEvent.click(screen.getByTitle(/menu/i));

		expect(defaultProps.onToggle).toHaveBeenCalled();
	});

	it.each([
		[false, 'product-menu-open'],
		[true, 'product-menu-closed'],
	])('marks the toggle as collapsed=%p with %s', (collapsed, symbol) => {
		const {container} = renderTopBar({collapsed});

		expect(container.querySelector(`.lexicon-icon-${symbol}`)).toBeTruthy();
	});

	it('links the settings button to the workspace settings', () => {
		renderTopBar();

		expect(screen.getByTitle(/settings/i).closest('a')).toHaveAttribute(
			'href',
			toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId: '23'})
		);
	});

	it('labels the language selector with the active language', () => {
		renderTopBar();

		expect(screen.getByTitle(/language/i)).toHaveTextContent('EN (US)');
	});

	it('updates the language when another one is picked', async () => {
		renderTopBar();

		fireEvent.click(screen.getByTitle(/language/i));

		fireEvent.click(await screen.findByText(/japanese/i));

		await waitFor(() =>
			expect(API.user.updateLanguage).toHaveBeenCalledWith({
				languageId: 'ja_JP',
			})
		);
	});

	it('does not update the language when the active one is picked', async () => {
		renderTopBar();

		fireEvent.click(screen.getByTitle(/language/i));

		fireEvent.click(await screen.findByText(/english/i));

		expect(API.user.updateLanguage).not.toHaveBeenCalled();
	});

	it('opens the user menu from the sticker', async () => {
		renderTopBar();

		fireEvent.click(screen.getByLabelText('Test Test'));

		expect(await screen.findByText('test@test.com')).toBeTruthy();
		expect(screen.getByText(/sign.out/i)).toBeTruthy();
	});
});
