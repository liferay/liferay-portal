/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../tests_utilities/polyfills';

import '@testing-library/jest-dom';
import {
	act,
	cleanup,
	createEvent,
	fireEvent,
	render,
	waitFor,
} from '@testing-library/react';
import faker from 'faker';
import fetchMock from 'fetch-mock';
import React from 'react';

import ServiceProvider from '../../../src/main/resources/META-INF/resources/ServiceProvider/index';
import AccountSelector from '../../../src/main/resources/META-INF/resources/components/account_selector/AccountSelector';
import {
	accountTemplate,
	getAccounts,
} from '../../tests_utilities/fake_data/accounts';
import {getOrders} from '../../tests_utilities/fake_data/orders';

const ACCOUNTS_HEADLESS_API_ENDPOINT =
	ServiceProvider.DeliveryCatalogAPI('v1').baseURL(24324);

const COMMERCE_DELIVERY_CATALOG_HEADLESS_API_ENDPOINT =
	'/headless-commerce-delivery-catalog/v1.0/channels/24324/accounts';

const ORGANIZATIONS_HEADLESS_API_ENDPOINT =
	'/o/headless-admin-user/v1.0/organizations';

describe('AccountSelector', () => {
	const {Liferay: originalLiferayObject} = global.window;

	beforeAll(() => {
		global.window.Liferay = {
			...originalLiferayObject,
			CommerceContext: {
				...global.window.Liferay.CommerceContext,
				accountEntryAllowedTypes: ['business', 'person'],
				commerceChannelId: 24324,
			},
		};
	});

	beforeEach(() => {
		fetchMock.mockGlobal();

		const accountsEndpointRegexp = new RegExp(
			ACCOUNTS_HEADLESS_API_ENDPOINT
		);

		const ordersEndpointRegexp = new RegExp(
			`${ServiceProvider.DeliveryCartAPI(
				'v1'
			).cartsByAccountIdAndChannelIdURL(42332, 24324)}`
		);

		const usersEndpointRegexp = new RegExp(
			COMMERCE_DELIVERY_CATALOG_HEADLESS_API_ENDPOINT
		);

		fetchMock.route(accountsEndpointRegexp, ({url}) => getAccounts(url));
		fetchMock.route(ordersEndpointRegexp, ({url}) => getOrders(url));
		fetchMock.route(usersEndpointRegexp, () => Promise.resolve());
	});

	afterAll(() => {
		global.window.Liferay = {...originalLiferayObject};
	});

	afterEach(() => {
		fetchMock.hardReset();

		cleanup();
	});

	describe('When no account is selected', () => {
		let renderedComponent;

		beforeEach(() => {
			renderedComponent = render(
				<AccountSelector
					createNewOrderURL="/order-link"
					selectOrderURL="/test-url/{id}"
					setCurrentAccountURL="/account-selector/setCurrentAccounts"
				/>
			);
		});

		afterEach(() => {
			cleanup();
		});

		it('must display the accounts search autocomplete component"', () => {
			expect(
				renderedComponent.getByPlaceholderText(/search/)
			).toBeInTheDocument();
		});

		it('must display a placeholder if no account is selected"', () => {
			expect(
				renderedComponent.getByText('select-account-and-order')
			).toBeInTheDocument();
		});

		it('displays an account list', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			await waitFor(() =>
				expect(
					renderedComponent.queryByText(/loading/i)
				).not.toBeInTheDocument()
			);

			const accountsList =
				renderedComponent.baseElement.querySelectorAll(
					'.accounts-list li'
				);

			const accountsListItem = accountsList[0];

			expect(accountsList.length).toBe(11);

			expect(accountsListItem.querySelector('img').src).toContain(
				'/test-logo-folder/test.jpg'
			);
		});

		it('must update the remote selected account when an account item is clicked', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			await waitFor(() =>
				expect(
					renderedComponent.queryByText(/loading/i)
				).not.toBeInTheDocument()
			);

			const accountsListItem =
				renderedComponent.baseElement.querySelectorAll(
					'.accounts-list li'
				)[0];

			fetchMock.post(
				new RegExp('account-selector/setCurrentAccounts'),
				({options: params, url}) => {
					expect(params.body.get('accountId')).toEqual(
						accountTemplate.id.toString()
					);

					expect(
						new URL(url).searchParams.get('groupId')
					).toBeTruthy();

					return 200;
				}
			);

			await act(async () => {
				fireEvent.click(accountsListItem.querySelector('button'));
			});
		});

		it('does not display the create new account button when the user is not allowed to create an account', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			await waitFor(() =>
				expect(
					renderedComponent.baseElement.querySelectorAll(
						'.accounts-list li'
					).length
				).toBe(11)
			);

			expect(
				renderedComponent.queryByText('create-new-account')
			).not.toBeInTheDocument();
		});

		it('closes the account list when the user clicks outside of it', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			const accountSelectorDropdownMenu =
				renderedComponent.baseElement.querySelector(
					'.account-selector-dropdown-menu'
				);

			expect(accountSelectorDropdownMenu).toHaveClass('show');

			await act(async () => {
				fireEvent.mouseDown(document.body);
				fireEvent.mouseUp(document.body);
			});

			expect(accountSelectorDropdownMenu).not.toHaveClass('show');
		});

		it('searches the accounts by the typed text', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			await waitFor(() =>
				expect(
					renderedComponent.queryByText(/loading/i)
				).not.toBeInTheDocument()
			);

			const search = faker.lorem.word();

			await act(async () => {
				fireEvent.change(
					renderedComponent.getByPlaceholderText('search'),
					{target: {value: search}}
				);
			});

			await waitFor(() => {
				const calls = fetchMock.callHistory.calls();

				const searches = calls.map(({url}) => {
					const {searchParams} = new URL(
						url,
						Liferay.ThemeDisplay.getPortalURL()
					);

					return searchParams.get('search');
				});

				expect(searches).toContain(search);
			});
		});
	});

	describe('When the user is allowed to create an account', () => {
		let renderedComponent;

		beforeEach(async () => {
			fetchMock.hardReset();
			fetchMock.mockGlobal();

			fetchMock.route(
				new RegExp(ORGANIZATIONS_HEADLESS_API_ENDPOINT),
				() => ({items: []})
			);

			fetchMock.route(
				new RegExp(
					`${ServiceProvider.DeliveryCartAPI(
						'v1'
					).cartsByAccountIdAndChannelIdURL(42332, 24324)}`
				),
				({url}) => getOrders(url)
			);

			fetchMock.route(
				new RegExp(ACCOUNTS_HEADLESS_API_ENDPOINT),
				({url}) => ({
					...getAccounts(url),
					actions: {create: {method: 'POST'}},
				})
			);

			renderedComponent = render(
				<AccountSelector
					createNewOrderURL="/order-link"
					selectOrderURL="/test-url/{id}"
					setCurrentAccountURL="/account-selector/setCurrentAccounts"
				/>
			);

			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			const createAccountButton =
				await renderedComponent.findByText('create-new-account');

			await act(async () => {
				fireEvent.click(createAccountButton);
			});

			await waitFor(() =>
				expect(
					renderedComponent.baseElement.querySelector(
						'.commerce-modal input[name="accountName"]'
					)
				).toBeInTheDocument()
			);
		});

		it('lets the browser move the focus between the new account form fields when tab is pressed', async () => {
			const accountNameInput =
				renderedComponent.baseElement.querySelector(
					'.commerce-modal input[name="accountName"]'
				);

			await act(async () => {
				accountNameInput.focus();
			});

			const tabEvent = createEvent.keyDown(accountNameInput, {
				key: 'Tab',
			});

			await act(async () => {
				fireEvent(accountNameInput, tabEvent);
			});

			expect(tabEvent.defaultPrevented).toBe(false);
			expect(accountNameInput).toHaveFocus();
		});

		it('marks the new account form as invalid until the account name is filled', async () => {
			const newAccountForm = renderedComponent.baseElement.querySelector(
				'.commerce-modal form'
			);

			expect(newAccountForm).toBeInvalid();

			const accountNameInput =
				renderedComponent.baseElement.querySelector(
					'.commerce-modal input[name="accountName"]'
				);

			await act(async () => {
				fireEvent.change(accountNameInput, {
					target: {value: faker.company.companyName()},
				});
			});

			expect(newAccountForm).toBeValid();
		});
	});

	describe('When the page is refreshed on account selection', () => {
		const {location} = window;

		let renderedComponent;

		beforeEach(async () => {
			Object.defineProperty(window, 'location', {
				configurable: true,
				value: {origin: location.origin, reload: jest.fn()},
			});

			fetchMock.route(
				new RegExp('account-selector/setCurrentAccounts'),
				200
			);

			renderedComponent = render(
				<AccountSelector
					createNewOrderURL="/order-link"
					refreshPageOnAccountSelected={true}
					selectOrderURL="/test-url/{id}"
					setCurrentAccountURL="/account-selector/setCurrentAccounts"
				/>
			);

			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			await waitFor(() =>
				expect(
					renderedComponent.getByText(accountTemplate.name)
				).toBeInTheDocument()
			);
		});

		afterEach(() => {
			Object.defineProperty(window, 'location', {
				configurable: true,
				value: location,
			});
		});

		it('reloads the page after setting the clicked account as the current account', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.getByText(accountTemplate.name)
				);
			});

			await waitFor(() =>
				expect(window.location.reload).toHaveBeenCalled()
			);
		});
	});

	describe('When account is selected', () => {
		let renderedComponent;

		beforeEach(() => {
			renderedComponent = render(
				<AccountSelector
					createNewOrderURL="/order-link"
					currentCommerceAccount={{
						id: 42332,
						name: 'My Account Name',
					}}
					selectOrderURL="/test-url/{id}"
					setCurrentAccountURL="/account-selector/setCurrentAccounts"
				/>
			);
		});

		afterEach(() => {
			cleanup();
		});

		it('must display the orders search autocomplete component"', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			expect(
				renderedComponent.getByPlaceholderText(/search-order/)
			).toBeInTheDocument();
		});

		it('must display the account name', () => {
			const currentAccountName =
				renderedComponent.container.querySelector(
					'.btn-account-selector .account-name .text-truncate'
				).innerHTML;
			expect(currentAccountName).toBe('My Account Name');
		});

		it('displays the current account name in the orders list header', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			expect(
				renderedComponent.baseElement.querySelector(
					'.orders-list-container .item-list-head .text-truncate'
				)
			).toHaveTextContent('My Account Name');
		});

		it('must display an order placeholder"', () => {
			const orderPlaceholder =
				renderedComponent.getByText(/no-order-selected/i);
			expect(orderPlaceholder).toBeInTheDocument();
		});

		it('displays an order list', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			await waitFor(() =>
				expect(
					renderedComponent.queryByText(/loading/i)
				).not.toBeInTheDocument()
			);

			const orders = renderedComponent.baseElement.querySelectorAll(
				'.orders-table tbody tr'
			);

			expect(orders.length).toBe(10);
		});

		it('displays the account list when the back to accounts button is clicked', async () => {
			await act(async () => {
				fireEvent.click(
					renderedComponent.baseElement.querySelector(
						'.btn-account-selector'
					)
				);
			});

			await act(async () => {
				fireEvent.click(
					renderedComponent.getByLabelText('back-to-accounts')
				);
			});

			await waitFor(() =>
				expect(
					renderedComponent.queryByText(/loading/i)
				).not.toBeInTheDocument()
			);

			expect(
				renderedComponent.baseElement.querySelector(
					'.orders-list-container'
				)
			).not.toBeInTheDocument();
			expect(
				renderedComponent.baseElement.querySelectorAll(
					'.accounts-list li'
				).length
			).toBe(11);
		});
	});

	describe('When account and order are selected', () => {
		let renderedComponent;

		beforeEach(() => {
			renderedComponent = render(
				<AccountSelector
					createNewOrderURL="/order-link"
					currentCommerceAccount={{
						id: 42332,
						name: 'My Account Name',
					}}
					currentCommerceOrder={{
						orderId: 34234,
						workflowStatusInfo: {
							label_i18n: 'Completed',
						},
					}}
					selectOrderURL="/test-url/{id}"
					setCurrentAccountURL="/account-selector/setCurrentAccounts"
				/>
			);
		});

		it('displays the current account name, order ID and order status localized label', () => {
			const button = renderedComponent.container.querySelector(
				'.btn-account-selector'
			);
			const currentAccountName = button.querySelector(
				'.account-name .text-truncate'
			).innerHTML;
			const currentOrderId = button.querySelector('.order-id').innerHTML;
			const currentOrderLabel = button.querySelector(
				'.order-label .label'
			).innerHTML;

			expect(currentAccountName).toBe('My Account Name');
			expect(currentOrderId).toBe('34234');
			expect(currentOrderLabel).toBe('Completed');
		});
	});

	describe('When account and order are selected and order view is hidden', () => {
		it('displays the current account name, order ID and order status localized label, but the orders list and button to change view are hidden', () => {
			const renderedComponent = render(
				<AccountSelector
					createNewOrderURL="/order-link"
					currentCommerceAccount={{
						id: 42332,
						name: 'My Account Name',
					}}
					currentCommerceOrder={{
						orderId: 34234,
						workflowStatusInfo: {
							label_i18n: 'Completed',
						},
					}}
					orderSelectionDisabled={true}
					selectOrderURL="/test-url/{id}"
					setCurrentAccountURL="/account-selector/setCurrentAccounts"
				/>
			);

			const button = renderedComponent.container.querySelector(
				'.btn-account-selector'
			);
			const changeViewButton =
				renderedComponent.container.querySelectorAll(
					'.lexicon-icon-angle-right-small'
				);
			const currentAccountName = button.querySelector(
				'.account-name .text-truncate'
			).innerHTML;
			const currentOrderId = button.querySelector('.order-id').innerHTML;
			const currentOrderLabel = button.querySelector(
				'.order-label .label'
			).innerHTML;

			expect(changeViewButton.length).toBe(0);
			expect(currentAccountName).toBe('My Account Name');
			expect(currentOrderId).toBe('34234');
			expect(currentOrderLabel).toBe('Completed');

			expect(
				renderedComponent.baseElement.querySelector(
					'.orders-list-container'
				)
			).not.toBeInTheDocument();
		});

		it('displays the current account name, order ID and order status localized label, the orders list and button to change view', () => {
			const renderedComponent = render(
				<AccountSelector
					createNewOrderURL="/order-link"
					currentCommerceAccount={{
						id: 42332,
						name: 'My Account Name',
					}}
					currentCommerceOrder={{
						orderId: 34234,
						workflowStatusInfo: {
							label_i18n: 'Completed',
						},
					}}
					orderSelectionDisabled={false}
					selectOrderURL="/test-url/{id}"
					setCurrentAccountURL="/account-selector/setCurrentAccounts"
				/>
			);

			const button = renderedComponent.container.querySelector(
				'.btn-account-selector'
			);
			const changeViewButton =
				renderedComponent.container.querySelectorAll(
					'.lexicon-icon-angle-left-small'
				);
			const currentAccountName = button.querySelector(
				'.account-name .text-truncate'
			).innerHTML;
			const currentOrderId = button.querySelector('.order-id').innerHTML;
			const currentOrderLabel = button.querySelector(
				'.order-label .label'
			).innerHTML;

			expect(changeViewButton.length).toBe(0);
			expect(currentAccountName).toBe('My Account Name');
			expect(currentOrderId).toBe('34234');
			expect(currentOrderLabel).toBe('Completed');

			expect(
				renderedComponent.baseElement.querySelector(
					'.orders-list-container'
				)
			).toBeInTheDocument();
		});
	});
});
