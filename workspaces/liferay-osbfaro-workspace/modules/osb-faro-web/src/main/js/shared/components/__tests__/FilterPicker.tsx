import FilterPicker, {IFilterPickerItem} from '../FilterPicker';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';

jest.unmock('react-dom');

const ITEMS: IFilterPickerItem[] = [
	{id: '1', name: 'Acme Corporation'},
	{id: '2', name: 'Globex'},
];

const VARIABLES = {channelId: '456', groupId: '789'};

const renderPicker = (props = {}) =>
	render(
		<FilterPicker
			entityLabel="Accounts"
			onFilterChange={jest.fn()}
			{...props}
		/>
	);

const getTrigger = () =>
	screen.getByRole('combobox', {name: 'Filter By Accounts'});

describe('FilterPicker', () => {
	afterEach(cleanup);

	describe('deferred request', () => {
		it('does not request the options before the picker is opened', () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			expect(dataSourceFn).not.toHaveBeenCalled();
		});

		it('leaves the trigger enabled while no request has been made', () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			expect(getTrigger()).toBeEnabled();
		});

		it('requests the options when the trigger is clicked', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await waitFor(() => expect(dataSourceFn).toHaveBeenCalledTimes(1));

			expect(dataSourceFn).toHaveBeenCalledWith(VARIABLES);
		});

		it('does not request the options again when the picker is reopened', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await waitFor(() => expect(dataSourceFn).toHaveBeenCalledTimes(1));

			fireEvent.click(getTrigger());
			fireEvent.click(getTrigger());

			await waitFor(() => expect(dataSourceFn).toHaveBeenCalledTimes(1));
		});

		it('renders the fetched options once the request resolves', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			expect(
				await screen.findByText('Acme Corporation')
			).toBeInTheDocument();
			expect(screen.getByText('Globex')).toBeInTheDocument();
		});

		it('reads a bare array response as the option list', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue(ITEMS);

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			expect(
				await screen.findByText('Acme Corporation')
			).toBeInTheDocument();
		});

		it('applies a caller supplied normalize to the response', async () => {
			const dataSourceFn = jest
				.fn()
				.mockResolvedValue({items: ['Tech', 'Finance']});

			renderPicker({
				dataSourceFn,
				normalize: (data: {items: string[]}) =>
					data.items.map((item) => ({id: item, name: item})),
				variables: VARIABLES,
			});

			fireEvent.click(getTrigger());

			expect(await screen.findByText('Tech')).toBeInTheDocument();
		});

		it('shows the loading state on the trigger while the request is pending', async () => {
			let resolveRequest: (value: unknown) => void = () => {};

			const dataSourceFn = jest.fn(
				() =>
					new Promise((resolve) => {
						resolveRequest = resolve;
					})
			);

			const {container} = renderPicker({
				dataSourceFn,
				variables: VARIABLES,
			});

			fireEvent.click(getTrigger());

			// `useRequest` debounces the call, so the request only exists (and
			// `resolveRequest` only points at it) once the spy has been hit.

			await waitFor(() => expect(dataSourceFn).toHaveBeenCalled());

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();

			resolveRequest({items: ITEMS});

			await waitFor(() =>
				expect(
					container.querySelector('.loading-root')
				).not.toBeInTheDocument()
			);
		});

		it('keeps the menu closed while the request is pending', async () => {
			let resolveRequest: (value: unknown) => void = () => {};

			const dataSourceFn = jest.fn(
				() =>
					new Promise((resolve) => {
						resolveRequest = resolve;
					})
			);

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await waitFor(() => expect(dataSourceFn).toHaveBeenCalled());

			expect(getTrigger()).toHaveAttribute('aria-expanded', 'false');
			expect(
				screen.queryByRole('option', {name: 'All Accounts'})
			).toBeNull();

			resolveRequest({items: ITEMS});

			expect(
				await screen.findByRole('option', {name: 'Acme Corporation'})
			).toBeInTheDocument();
		});

		it('does not flash the menu open between the click and the request starting', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			const trigger = getTrigger();

			// Every `aria-expanded` transition, oldest first. The request is
			// only marked pending by an effect, so a naive gate on `loading`
			// lets the menu commit open for one paint and then close again.

			const transitions: (string | null)[] = [];

			const observer = new MutationObserver((records) =>
				records.forEach((record) => transitions.push(record.oldValue))
			);

			observer.observe(trigger, {
				attributeFilter: ['aria-expanded'],
				attributeOldValue: true,
				attributes: true,
			});

			fireEvent.click(trigger);

			await screen.findByRole('option', {name: 'Acme Corporation'});

			observer.disconnect();

			expect(transitions).toEqual(['false']);
		});

		it('opens the menu once the request resolves', async () => {
			let resolveRequest: (value: unknown) => void = () => {};

			const dataSourceFn = jest.fn(
				() =>
					new Promise((resolve) => {
						resolveRequest = resolve;
					})
			);

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await waitFor(() => expect(dataSourceFn).toHaveBeenCalled());

			resolveRequest({items: ITEMS});

			await waitFor(() =>
				expect(getTrigger()).toHaveAttribute('aria-expanded', 'true')
			);
		});

		it('opens the menu when the request fails so the filter is not stuck closed', async () => {
			const dataSourceFn = jest
				.fn()
				.mockRejectedValue(new Error('request failed'));

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			expect(
				await screen.findByRole('option', {name: 'All Accounts'})
			).toBeInTheDocument();
		});

		it('closes the menu after an option is selected', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});
			const onFilterChange = jest.fn();

			renderPicker({dataSourceFn, onFilterChange, variables: VARIABLES});

			fireEvent.click(getTrigger());

			fireEvent.click(
				await screen.findByRole('option', {name: 'Acme Corporation'})
			);

			await waitFor(() =>
				expect(getTrigger()).toHaveAttribute('aria-expanded', 'false')
			);

			expect(onFilterChange).toHaveBeenCalledWith(ITEMS[0]);
		});
	});

	describe('server side search', () => {
		const getSearchInput = () =>
			screen.getByRole('textbox', {name: 'Search'});

		it('sends the typed text to the backend as the query', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await screen.findByRole('option', {name: 'Acme Corporation'});

			fireEvent.change(getSearchInput(), {target: {value: 'ini'}});

			await waitFor(() =>
				expect(dataSourceFn).toHaveBeenLastCalledWith({
					...VARIABLES,
					query: 'ini',
				})
			);
		});

		it('lowercases the typed text before sending it to the backend', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await screen.findByRole('option', {name: 'Acme Corporation'});

			fireEvent.change(getSearchInput(), {
				target: {value: 'ACME Corp'},
			});

			await waitFor(() =>
				expect(dataSourceFn).toHaveBeenLastCalledWith({
					...VARIABLES,
					query: 'acme corp',
				})
			);

			// The input still shows exactly what was typed.

			expect(getSearchInput()).toHaveValue('ACME Corp');
		});

		it('lists whatever the backend returns rather than filtering in the browser', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await screen.findByRole('option', {name: 'Acme Corporation'});

			// The response does not contain the typed text. A client-side
			// filter would hide it; a backend search shows it.

			dataSourceFn.mockResolvedValue({
				items: [{id: '3', name: 'Initech'}],
			});

			fireEvent.change(getSearchInput(), {target: {value: 'zzz'}});

			expect(
				await screen.findByRole('option', {name: 'Initech'})
			).toBeInTheDocument();
		});

		it('keeps the menu open while a search request is in flight', async () => {
			const dataSourceFn = jest.fn().mockResolvedValue({items: ITEMS});

			renderPicker({dataSourceFn, variables: VARIABLES});

			fireEvent.click(getTrigger());

			await screen.findByRole('option', {name: 'Acme Corporation'});

			fireEvent.change(getSearchInput(), {target: {value: 'glo'}});

			expect(getSearchInput()).toBeInTheDocument();
		});
	});

	describe('caller owned options', () => {
		it('renders the items passed by the caller without any request', () => {
			renderPicker({items: ITEMS});

			fireEvent.click(getTrigger());

			expect(screen.getByText('Acme Corporation')).toBeInTheDocument();
		});

		it('shows the loading state driven by the caller', () => {
			const {container} = renderPicker({items: [], loading: true});

			expect(
				container.querySelector('.loading-root')
			).toBeInTheDocument();
		});
	});

	describe('label and selection', () => {
		it('falls back to the "all-x" label when nothing is selected', () => {
			renderPicker({items: ITEMS});

			expect(screen.getByText('All Accounts')).toBeInTheDocument();
		});

		it('shows the selected item name on the trigger', () => {
			renderPicker({items: ITEMS, selected: ITEMS[0]});

			expect(screen.getByText('Acme Corporation')).toBeInTheDocument();
		});
	});
});
