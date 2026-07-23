import * as API from 'shared/api';
import AccountDropdown from '../AccountDropdown';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {MemoryRouter, Route} from 'react-router-dom';

jest.unmock('react-dom');

const MOCK_ACCOUNT = (id: string, name: string) => ({
	id,
	name,
});

const Wrapper = ({children}: {children: React.ReactNode}) => (
	<MemoryRouter
		initialEntries={[
			'/workspace/123/456/sites/touchpoints/http%3A%2F%2Fliferay.com/Liferay%20DXP%20-%20Home',
		]}
	>
		<Route path="/workspace/:groupId/:channelId/sites/touchpoints/:touchpoint/:title">
			{children}
		</Route>
	</MemoryRouter>
);

const AssetWrapper = ({children}: {children: React.ReactNode}) => (
	<MemoryRouter
		initialEntries={[
			'/workspace/123/456/assets/blogs/999/page/http%3A%2F%2Fliferay.com/Liferay%20DXP%20-%20Home',
		]}
	>
		<Route path="/workspace/:groupId/:channelId/assets/blogs/:assetId/page/:touchpoint/:title">
			{children}
		</Route>
	</MemoryRouter>
);

const SiteWrapper = ({children}: {children: React.ReactNode}) => (
	<MemoryRouter initialEntries={['/workspace/123/456/sites']}>
		<Route path="/workspace/:groupId/:channelId/sites">{children}</Route>
	</MemoryRouter>
);

describe('AccountDropdown', () => {
	afterEach(cleanup);

	it('should render with "All Accounts" as the default value', async () => {
		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_ACCOUNT('100', 'Account 100')],
				total: 1,
			})
		);

		const {container} = render(
			<Wrapper>
				<AccountDropdown assetType="page" onFilterChange={jest.fn()} />
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'All Accounts'})
		).toHaveTextContent('All Accounts');

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalled()
		);

		expect(container).toMatchSnapshot();
	});

	it('should list the fetched accounts when opened', async () => {
		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [
					MOCK_ACCOUNT('100', 'Account 100'),
					MOCK_ACCOUNT('200', 'Account 200'),
				],
				total: 2,
			})
		);

		render(
			<Wrapper>
				<AccountDropdown assetType="page" onFilterChange={jest.fn()} />
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalled()
		);

		fireEvent.click(screen.getByRole('combobox', {name: 'All Accounts'}));

		expect(
			await screen.findByRole('option', {name: 'Account 100'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'Account 200'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('option', {name: 'All Accounts'})
		).toBeInTheDocument();
	});

	it('should call onFilterChange with the selected account', async () => {
		const onFilterChange = jest.fn();

		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_ACCOUNT('100', 'Account 100')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<AccountDropdown
					assetType="page"
					onFilterChange={onFilterChange}
				/>
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalled()
		);

		fireEvent.click(screen.getByRole('combobox', {name: 'All Accounts'}));

		fireEvent.click(
			await screen.findByRole('option', {name: 'Account 100'})
		);

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining({id: '100', name: 'Account 100'})
		);
	});

	it('should call onFilterChange with null when "All Accounts" is selected again', async () => {
		const onFilterChange = jest.fn();

		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_ACCOUNT('100', 'Account 100')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<AccountDropdown
					assetType="page"
					onFilterChange={onFilterChange}
				/>
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalled()
		);

		fireEvent.click(screen.getByRole('combobox', {name: 'All Accounts'}));

		fireEvent.click(
			await screen.findByRole('option', {name: 'Account 100'})
		);

		fireEvent.click(screen.getByRole('combobox', {name: 'All Accounts'}));

		fireEvent.click(
			await screen.findByRole('option', {name: 'All Accounts'})
		);

		expect(onFilterChange).toHaveBeenCalledWith(null);
	});

	it('should search accounts by the canonical URL for the page asset type', async () => {
		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		render(
			<Wrapper>
				<AccountDropdown assetType="page" onFilterChange={jest.fn()} />
			</Wrapper>
		);

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalledWith(
				expect.objectContaining({
					assetId: 'http://liferay.com',
					assetTitle: 'Liferay DXP - Home',
					assetType: 'page',
				})
			)
		);
	});

	it('should preselect the account passed via initialAccountId/initialAccountName', async () => {
		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_ACCOUNT('200', 'Account 200')],
				total: 1,
			})
		);

		render(
			<Wrapper>
				<AccountDropdown
					assetType="page"
					initialAccountId="100"
					initialAccountName="Account 100"
					onFilterChange={jest.fn()}
				/>
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'All Accounts'})
		).toHaveTextContent('Account 100');

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalled()
		);
	});

	it('should fall back to the raw id when initialAccountName is missing', () => {
		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		render(
			<Wrapper>
				<AccountDropdown
					assetType="page"
					initialAccountId="100"
					onFilterChange={jest.fn()}
				/>
			</Wrapper>
		);

		expect(
			screen.getByRole('combobox', {name: 'All Accounts'})
		).toHaveTextContent('100');
	});

	it('should search accounts by the assetId route param for non-page asset types', async () => {
		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({items: [], total: 0})
		);

		render(
			<AssetWrapper>
				<AccountDropdown assetType="blog" onFilterChange={jest.fn()} />
			</AssetWrapper>
		);

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalledWith(
				expect.objectContaining({
					assetId: '999',
					assetTitle: 'Liferay DXP - Home',
					assetType: 'blog',
				})
			)
		);
	});

	it('should search accounts channel-wide when no assetType is given', async () => {
		(API.accounts.searchAccounts as jest.Mock).mockReturnValue(
			Promise.resolve({
				items: [MOCK_ACCOUNT('100', 'Account 100')],
				total: 1,
			})
		);

		render(
			<SiteWrapper>
				<AccountDropdown onFilterChange={jest.fn()} />
			</SiteWrapper>
		);

		await waitFor(() =>
			expect(API.accounts.searchAccounts).toHaveBeenCalledWith(
				expect.objectContaining({
					assetId: undefined,
					assetType: undefined,
					channelId: '456',
					groupId: '123',
				})
			)
		);

		fireEvent.click(screen.getByRole('combobox', {name: 'All Accounts'}));

		expect(
			await screen.findByRole('option', {name: 'Account 100'})
		).toBeInTheDocument();
	});
});
