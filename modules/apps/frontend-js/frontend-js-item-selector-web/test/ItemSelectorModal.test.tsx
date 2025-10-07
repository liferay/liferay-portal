/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {useModal} from '@clayui/modal';
import {IView} from '@liferay/frontend-data-set-web';
import {render, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch, loadClientExtensions, sub} from 'frontend-js-web';
import React from 'react';

import {ItemSelectorModal} from '../src/main/resources/META-INF/resources';

type TestItem = {
	itemId: number;
	name: string;
};

const mockFirstItem = {
	itemId: 1,
	name: 'First Item Name',
};

const mockSecondItem = {
	itemId: 2,
	name: 'Second Item Name',
};

jest.mock('frontend-js-web', () => {
	const actualPackage = jest.requireActual('frontend-js-web') as any;

	return {
		...actualPackage,
		fetch: jest.fn(() => {
			const headers = new Headers();
			headers.set('Content-Type', 'application/json');

			return Promise.resolve({
				headers,
				json: () =>
					Promise.resolve({
						items: [mockFirstItem, mockSecondItem],
						lastPage: 1,
						page: 1,
					}),
				ok: true,
				status: 200,
			});
		}),
		loadClientExtensions: jest.fn(() => {
			return Promise.resolve();
		}),
		sub: jest.fn((...args) => actualPackage.sub(...args)),
	};
});

const mockedFetch = fetch as jest.Mock;
const mockedLoadClientExtensions = loadClientExtensions as jest.Mock;
const mockedSub = sub as jest.Mock;

const ItemSelectorModalWrapper = ({
	createItemURL,
	defaultOpen,
	onItemsChange,
	selectedItems,
}: {
	createItemURL?: string;
	defaultOpen: boolean;
	onItemsChange: (items: TestItem[]) => void;
	selectedItems: TestItem[];
}) => {
	const {observer, onOpenChange, open} = useModal({defaultOpen});

	return (
		<>
			<button onClick={() => onOpenChange(true)}>open modal</button>

			<ItemSelectorModal<TestItem>
				apiURL={`${location.origin}/o/headless-delivery/v1.0/test-api-url`}
				createItemURL={createItemURL}
				fdsProps={{
					id: `itemSelectorModal-test-0001`,
					pagination: {
						deltas: [{label: 20}],
						initialDelta: 20,
					},
					views: [
						{
							contentRenderer: 'cards',
							label: 'Cards',
							name: 'cards',
							schema: {
								description: 'description',
								title: 'name',
							},
							thumbnail: 'cards2',
						} as IView,
					],
				}}
				itemTypeLabel="Space"
				items={selectedItems}
				locator={{
					id: 'itemId',
					label: 'name',
					value: 'itemId',
				}}
				observer={observer}
				onItemsChange={onItemsChange}
				onOpenChange={onOpenChange}
				open={open}
			/>
		</>
	);
};

describe('ItemSelectorModal component', () => {
	beforeAll(() => {
		Object.assign(Liferay.ThemeDisplay, {
			isImpersonated: () => false,
		});
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	afterAll(() => {
		Object.assign(Liferay.ThemeDisplay, {
			isImpersonated: undefined,
		});

		jest.restoreAllMocks();
		mockedFetch.mockReset();
		mockedLoadClientExtensions.mockReset();
		mockedSub.mockReset();
	});

	it('renders an item selector modal with header and footer modal structure', async () => {
		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={jest.fn()}
				selectedItems={[]}
			/>
		);

		const modal = await findByRole('dialog');

		const title = await within(modal).findByRole('heading');

		expect(title).toBeInTheDocument();

		expect(sub).toHaveBeenNthCalledWith(1, 'select-x', 'Space');

		const footerActions = await within(modal).findByRole('group');

		const [cancel, select] =
			await within(footerActions).findAllByRole('button');

		expect(cancel).toBeInTheDocument();

		expect(cancel).toBeEnabled();

		expect(select).toBeInTheDocument();

		expect(select).toBeDisabled();
	});

	it('renders an item selector modal with a create new item link that opens in a new tab', async () => {
		const createItemURL = 'www.example.com';

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				createItemURL={createItemURL}
				defaultOpen={true}
				onItemsChange={jest.fn()}
				selectedItems={[]}
			/>
		);

		const modal = await findByRole('dialog');

		const newItemLink = await within(modal).findByText('new');

		expect(newItemLink).toBeInTheDocument();

		expect(newItemLink.getAttribute('href')).toEqual(createItemURL);

		expect(newItemLink.getAttribute('target')).toEqual('_blank');
	});

	it('renders items with radio for single selection type', async () => {
		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={jest.fn()}
				selectedItems={[]}
			/>
		);

		const modal = await findByRole('dialog');

		const items = await within(modal).findAllByLabelText(/item name$/gi);

		expect(items).toHaveLength(2);

		const [firstItem, secondItem] = items;

		expect(firstItem).toHaveTextContent(mockFirstItem.name);

		expect(secondItem).toHaveTextContent(mockSecondItem.name);

		const radios = await within(modal).findAllByRole('radio');

		expect(radios).toHaveLength(2);

		const [firstItemRadio, secondItemRadio] = radios;

		expect(firstItemRadio).not.toBeChecked();

		expect(secondItemRadio).not.toBeChecked();
	});

	it('enables the "Select" button after checking an item', async () => {
		const user = userEvent.setup();

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={jest.fn()}
				selectedItems={[]}
			/>
		);

		const modal = await findByRole('dialog');

		const [firstItemRadio, secondItemRadio] =
			await within(modal).findAllByRole('radio');

		expect(firstItemRadio).not.toBeChecked();

		expect(secondItemRadio).not.toBeChecked();

		const select = await within(modal).findByRole('button', {
			name: 'select',
		});

		expect(select).toBeDisabled();

		await user.click(firstItemRadio);

		expect(firstItemRadio).toBeChecked();

		expect(secondItemRadio).not.toBeChecked();

		expect(select).toBeEnabled();
	});

	it('shows selected message with item name for single selection type', async () => {
		const user = userEvent.setup();

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={jest.fn()}
				selectedItems={[]}
			/>
		);

		const modal = await findByRole('dialog');

		const [firstItemRadio, secondItemRadio] =
			await within(modal).findAllByRole('radio');

		await user.click(firstItemRadio);

		expect(firstItemRadio).toBeChecked();

		expect(secondItemRadio).not.toBeChecked();

		const selectedMessage = await within(modal).findByText('x-selected');

		expect(selectedMessage).toBeInTheDocument();

		expect(sub).toHaveBeenLastCalledWith('x-selected', mockFirstItem.name);
	});

	it('shows selected items when they are provided', async () => {
		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={jest.fn()}
				selectedItems={[mockSecondItem]}
			/>
		);

		const modal = await findByRole('dialog');

		const [firstItemRadio, secondItemRadio] =
			await within(modal).findAllByRole('radio');

		expect(firstItemRadio).not.toBeChecked();

		expect(secondItemRadio).toBeChecked();

		const selectedMessage = await within(modal).findByText('x-selected');

		expect(selectedMessage).toBeInTheDocument();

		expect(sub).toHaveBeenLastCalledWith('x-selected', mockSecondItem.name);

		const select = await within(modal).findByRole('button', {
			name: 'select',
		});

		expect(select).toBeEnabled();
	});

	it('must not fire change items callback until click on "Select" button', async () => {
		const user = userEvent.setup();
		const mockedOnSelectedItemsChange = jest.fn();

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={mockedOnSelectedItemsChange}
				selectedItems={[]}
			/>
		);

		const modal = await findByRole('dialog');

		const [firstItemRadio] = await within(modal).findAllByRole('radio');

		const footerActions = await within(modal).findByRole('group');

		const [, select] = await within(footerActions).findAllByRole('button');

		expect(select).toBeDisabled();

		await user.click(firstItemRadio);

		await waitFor(() => {
			expect(select).toBeEnabled();
		});

		expect(mockedOnSelectedItemsChange).not.toHaveBeenCalled();

		await user.click(select);

		expect(mockedOnSelectedItemsChange).toHaveBeenCalledTimes(1);

		expect(mockedOnSelectedItemsChange).toHaveBeenLastCalledWith([
			mockFirstItem,
		]);
	});

	it('must not fire change items callback when clicking on "Cancel" button', async () => {
		const user = userEvent.setup();
		const mockedOnSelectedItemsChange = jest.fn();

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={mockedOnSelectedItemsChange}
				selectedItems={[]}
			/>
		);

		const modal = await findByRole('dialog');

		const [firstItemRadio] = await within(modal).findAllByRole('radio');

		const footerActions = await within(modal).findByRole('group');

		const [cancel, select] =
			await within(footerActions).findAllByRole('button');

		expect(cancel).toBeEnabled();

		await user.click(firstItemRadio);

		await waitFor(() => {
			expect(select).toBeEnabled();
		});

		expect(mockedOnSelectedItemsChange).not.toHaveBeenCalled();

		await user.click(cancel);

		expect(mockedOnSelectedItemsChange).not.toHaveBeenCalled();
	});

	it('must fire change items callback with updated item', async () => {
		const user = userEvent.setup();
		const mockedOnSelectedItemsChange = jest.fn();

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={mockedOnSelectedItemsChange}
				selectedItems={[mockFirstItem]}
			/>
		);

		const modal = await findByRole('dialog');

		const [firstItemRadio, secondItemRadio] =
			await within(modal).findAllByRole('radio');

		const select = await within(modal).findByRole('button', {
			name: 'select',
		});

		await user.click(secondItemRadio);

		expect(firstItemRadio).not.toBeChecked();

		expect(secondItemRadio).toBeChecked();

		await user.click(select);

		expect(mockedOnSelectedItemsChange).toHaveBeenCalledTimes(1);

		expect(mockedOnSelectedItemsChange).toHaveBeenLastCalledWith([
			mockSecondItem,
		]);
	});

	it('must not fire change items callback after choosing another item but canceling', async () => {
		const user = userEvent.setup();
		const mockedOnSelectedItemsChange = jest.fn();

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={mockedOnSelectedItemsChange}
				selectedItems={[mockFirstItem]}
			/>
		);

		const modal = await findByRole('dialog');

		const [firstItemRadio, secondItemRadio] =
			await within(modal).findAllByRole('radio');

		const cancel = await within(modal).findByRole('button', {
			name: 'cancel',
		});

		await user.click(secondItemRadio);

		expect(firstItemRadio).not.toBeChecked();

		expect(secondItemRadio).toBeChecked();

		await user.click(cancel);

		expect(mockedOnSelectedItemsChange).not.toHaveBeenCalled();
	});

	it('must reset selected items to provided ones after canceling', async () => {
		const user = userEvent.setup();

		const {findByRole} = render(
			<ItemSelectorModalWrapper
				defaultOpen={true}
				onItemsChange={jest.fn}
				selectedItems={[mockFirstItem]}
			/>
		);

		let modal = await findByRole('dialog');

		let [firstItemRadio, secondItemRadio] =
			await within(modal).findAllByRole('radio');

		expect(firstItemRadio).toBeChecked();

		expect(secondItemRadio).not.toBeChecked();

		const cancel = await within(modal).findByRole('button', {
			name: 'cancel',
		});

		await user.click(secondItemRadio);

		expect(firstItemRadio).not.toBeChecked();

		expect(secondItemRadio).toBeChecked();

		await user.click(cancel);

		await waitFor(() => {
			expect(modal).not.toBeInTheDocument();
		});

		const openModal = await findByRole('button', {name: 'open modal'});

		await user.click(openModal);

		modal = await findByRole('dialog');

		[firstItemRadio, secondItemRadio] =
			await within(modal).findAllByRole('radio');

		expect(firstItemRadio).toBeChecked();

		expect(secondItemRadio).not.toBeChecked();

		const selectedMessage = await within(modal).findByText('x-selected');

		expect(selectedMessage).toBeInTheDocument();

		expect(sub).toHaveBeenLastCalledWith('x-selected', mockFirstItem.name);
	});
});
