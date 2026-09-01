/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '..';
import {NetworkStatus} from '@clayui/data-provider';
import {cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

const messages = {
	listCount: '{0} option available.',
	listCountPlural: '{0} options available.',
	loading: 'Loading...',
	notFound: 'No results found',
};

global.ResizeObserver = require('resize-observer-polyfill');

/**
 * Incremental iteration tests for the new autocomplete behavior.
 */
describe('Autocomplete incremental interactions', () => {
	afterEach(cleanup);

	it('render default component', () => {
		const {getByRole, queryByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);

		expect(input).toBeDefined();

		const listbox = queryByRole('listbox');

		expect(listbox).toBeFalsy();
	});

	it('typing a value in the input shows the menu', () => {
		const {getAllByRole, getByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox');

		userEvent.type(input, 't');

		const listbox = getByRole('listbox');

		expect(listbox.parentElement!.classList).toContain('show');

		const [two, three] = getAllByRole('option');

		expect(two).toBeDefined();
		expect(three).toBeDefined();
	});

	it('typing a value that does not exist shows the menu with results not found', () => {
		const {getByRole} = render(
			<ClayAutocomplete
				defaultItems={['one', 'two', 'three']}
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{(item: any) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				)}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox');

		userEvent.type(input, 'fou');

		const listbox = getByRole('listbox');

		expect(listbox.parentElement!.classList).toContain('show');

		const notFound = getByRole('option');

		expect(notFound).toBeDefined();
		expect(notFound.innerHTML).toBe(messages.notFound);
	});

	it('typing the value should filter the options in the menu', () => {
		const {getAllByRole, getByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox');

		userEvent.type(input, 't');

		const listbox = getByRole('listbox');

		expect(listbox.parentElement!.classList).toContain('show');
		expect(listbox.children.length).toBe(2);

		const [two, three] = getAllByRole('option');

		expect(two).toBeDefined();
		expect(three).toBeDefined();

		userEvent.clear(input);
		userEvent.type(input, 'two');

		const [two2] = getAllByRole('option');

		expect(listbox.children.length).toBe(1);
		expect(two2).toBeDefined();
	});

	it('entering the value and clearing the value shows the menu with all options', () => {
		const {getAllByRole, getByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox');

		userEvent.type(input, 't');

		const listbox = getByRole('listbox');

		expect(listbox.parentElement!.classList).toContain('show');
		expect(listbox.children.length).toBe(2);

		const [two, three] = getAllByRole('option');

		expect(two).toBeDefined();
		expect(three).toBeDefined();

		userEvent.clear(input);

		const [one, two2, three2] = getAllByRole('option');

		expect(listbox.children.length).toBe(3);
		expect(one).toBeDefined();
		expect(two2).toBeDefined();
		expect(three2).toBeDefined();
	});

	it('focus on input shows display if configured', () => {
		const {getByRole} = render(
			<ClayAutocomplete
				menuTrigger="focus"
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox');

		userEvent.click(input);

		const listbox = getByRole('listbox');

		expect(listbox.parentElement!.classList).toContain('show');
		expect(listbox.children.length).toBe(3);
	});

	it('clicking option closes the menu and adds the value of the selected item', () => {
		const {getAllByRole, getByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);
		userEvent.type(input, 't');

		const [two] = getAllByRole('option');

		fireEvent.click(two!);

		expect(document.querySelector('.dropdown-menu')).toBeFalsy();
		expect(input.value).toBe('two');
	});

	it('press enter close the menu', () => {
		const {getByRole, queryByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);
		userEvent.type(input, 't');

		const listbox = getByRole('listbox');

		expect(listbox.parentElement!.classList).toContain('show');

		userEvent.keyboard('[Enter]');

		expect(queryByRole('listbox')).toBeFalsy();
	});

	it('pressing alt + key down shows the menu if not open', () => {
		const {getByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);

		userEvent.keyboard('{Alt>}{ArrowDown/}');

		const listbox = getByRole('listbox');

		expect(listbox.parentElement!.classList).toContain('show');
	});

	it('pressing left or right arrow key moves focus to input', () => {
		const {getByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);
		userEvent.type(input, 't');

		expect(input).toEqual(document.activeElement);

		userEvent.keyboard('[ArrowDown]');

		expect(input.getAttribute('aria-activedescendant')).toBe('two');

		userEvent.keyboard('[ArrowLeft]');

		expect(input.getAttribute('aria-activedescendant')).toBe('');

		userEvent.keyboard('[ArrowDown]');

		expect(input.getAttribute('aria-activedescendant')).toBe('two');

		userEvent.keyboard('[ArrowRight]');

		expect(input.getAttribute('aria-activedescendant')).toBe('');
	});

	it('pressing the up arrow key opens the menu and moves the visual focus to the last element in the list', () => {
		const {getByRole, queryByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);
		userEvent.keyboard('[ArrowUp]');

		expect(queryByRole('listbox')).toBeDefined();
		expect(input.getAttribute('aria-activedescendant')).toBe('three');
	});

	it('pressing the down arrow key opens the menu and moves the visual focus to the first element in the list', () => {
		const {getByRole, queryByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);
		userEvent.keyboard('[ArrowDown]');

		expect(queryByRole('listbox')).toBeDefined();
		expect(input.getAttribute('aria-activedescendant')).toBe('one');
	});

	it('cycle through options in loop when run out of options', () => {
		const {getByRole, queryByRole} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		const input = getByRole('combobox') as HTMLInputElement;

		userEvent.click(input);
		userEvent.keyboard('[ArrowDown]');

		expect(queryByRole('listbox')).toBeDefined();
		expect(input.getAttribute('aria-activedescendant')).toBe('one');

		userEvent.keyboard('[ArrowDown]');

		expect(input.getAttribute('aria-activedescendant')).toBe('two');

		userEvent.keyboard('[ArrowDown]');

		expect(input.getAttribute('aria-activedescendant')).toBe('three');

		userEvent.keyboard('[ArrowDown]');

		expect(input.getAttribute('aria-activedescendant')).toBe('one');

		userEvent.keyboard('[ArrowUp]');

		expect(input.getAttribute('aria-activedescendant')).toBe('three');
	});

	describe('Selected option', () => {
		it('pressing esc closes the menu', () => {
			const {getAllByRole, getByRole, queryByRole} = render(
				<ClayAutocomplete
					messages={messages}
					placeholder="Enter a number from One to Five"
				>
					{['one', 'two', 'three'].map((item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					))}
				</ClayAutocomplete>
			);

			const input = getByRole('combobox') as HTMLInputElement;

			userEvent.click(input);
			userEvent.type(input, 't');

			expect(getByRole('listbox')).toBeDefined();

			const [two] = getAllByRole('option');

			fireEvent.click(two!);

			expect(queryByRole('listbox')).toBeFalsy();
			expect(input.value).toBe('two');

			userEvent.type(input, 'oo');

			expect(input.value).toBe('twooo');
			expect(getByRole('option').textContent).toBe(messages.notFound);

			userEvent.keyboard('{Escape}');

			expect(input.value).toBe('two');
			expect(queryByRole('listbox')).toBeFalsy();
		});

		it('clicking outside closes the menu', () => {
			const {getAllByRole, getByRole, queryByRole} = render(
				<>
					<ClayAutocomplete
						messages={messages}
						placeholder="Enter a number from One to Five"
					>
						{['one', 'two', 'three'].map((item) => (
							<ClayAutocomplete.Item key={item}>
								{item}
							</ClayAutocomplete.Item>
						))}
					</ClayAutocomplete>
					<button type="button">Click outside</button>
				</>
			);

			const input = getByRole('combobox') as HTMLInputElement;

			userEvent.click(input);
			userEvent.type(input, 't');

			expect(getByRole('listbox')).toBeDefined();

			const [two] = getAllByRole('option');

			fireEvent.click(two!);

			expect(queryByRole('listbox')).toBeFalsy();
			expect(input.value).toBe('two');

			userEvent.type(input, 'oo');

			expect(input.value).toBe('twooo');
			expect(getByRole('option').textContent).toBe(messages.notFound);

			const buttonOutside = getByRole('button', {hidden: true});

			userEvent.click(buttonOutside);

			expect(input.value).toBe('two');
			expect(queryByRole('listbox')).toBeFalsy();
		});

		it('moving the focus outside of autocomplete closes the menu', () => {
			const {getAllByRole, getByRole, queryByRole} = render(
				<>
					<ClayAutocomplete
						messages={messages}
						placeholder="Enter a number from One to Five"
					>
						{['one', 'two', 'three'].map((item) => (
							<ClayAutocomplete.Item key={item}>
								{item}
							</ClayAutocomplete.Item>
						))}
					</ClayAutocomplete>
					<button type="button">Click outside</button>
				</>
			);

			const input = getByRole('combobox') as HTMLInputElement;

			userEvent.click(input);
			userEvent.type(input, 't');

			expect(getByRole('listbox')).toBeDefined();

			const [two] = getAllByRole('option');

			fireEvent.click(two!);

			expect(queryByRole('listbox')).toBeFalsy();
			expect(input.value).toBe('two');

			userEvent.type(input, 'oo');

			expect(input.value).toBe('twooo');
			expect(getByRole('option').textContent).toBe(messages.notFound);

			userEvent.tab();

			expect(input.value).toBe('two');
			expect(queryByRole('listbox')).toBeFalsy();
		});

		it('shows marker and visual feedback when an item is selected', () => {
			const {getAllByRole, getByRole} = render(
				<ClayAutocomplete
					messages={messages}
					placeholder="Enter a number from One to Five"
					selectedKeys={['one', 'two']}
				>
					{['one', 'two', 'three', 'four', 'five'].map((item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					))}
				</ClayAutocomplete>
			);

			const input = getByRole('combobox');

			userEvent.type(input, 'o');

			expect(getByRole('listbox')).toBeDefined();

			const [one, two, four] = getAllByRole('option');

			expect(one?.getAttribute('aria-selected')).toBe('true');
			expect(one?.classList.contains('active')).toBe(true);
			expect(
				one?.querySelector('.lexicon-icon-check-small')
			).toBeDefined();

			expect(two?.getAttribute('aria-selected')).toBe('true');
			expect(two?.classList.contains('active')).toBe(true);
			expect(
				two?.querySelector('.lexicon-icon-check-small')
			).toBeDefined();

			expect(four?.getAttribute('aria-selected')).toBeNull();
			expect(four?.classList.contains('active')).toBe(false);
			expect(four?.querySelector('.lexicon-icon-check-small')).toBeNull();
		});
	});

	describe('Option not selected', () => {
		it('pressing esc closes the menu', () => {
			const {getByRole, queryByRole} = render(
				<ClayAutocomplete
					messages={messages}
					placeholder="Enter a number from One to Five"
				>
					{['one', 'two', 'three'].map((item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					))}
				</ClayAutocomplete>
			);

			const input = getByRole('combobox') as HTMLInputElement;

			userEvent.click(input);
			userEvent.type(input, 't');

			expect(getByRole('listbox')).toBeDefined();

			userEvent.keyboard('{Escape}');

			expect(input.value).toBe('');
			expect(queryByRole('listbox')).toBeFalsy();
		});

		it('clicking outside closes the menu', () => {
			const {getByRole, queryByRole} = render(
				<>
					<ClayAutocomplete
						messages={messages}
						placeholder="Enter a number from One to Five"
					>
						{['one', 'two', 'three'].map((item) => (
							<ClayAutocomplete.Item key={item}>
								{item}
							</ClayAutocomplete.Item>
						))}
					</ClayAutocomplete>
					<button type="button">Click outside</button>
				</>
			);

			const input = getByRole('combobox') as HTMLInputElement;

			userEvent.click(input);
			userEvent.type(input, 't');

			expect(getByRole('listbox')).toBeDefined();

			const buttonOutside = getByRole('button', {hidden: true});

			userEvent.click(buttonOutside);

			expect(input.value).toBe('');
			expect(queryByRole('listbox')).toBeFalsy();
		});

		it('moving the focus outside of autocomplete closes the menu', () => {
			const {getByRole, queryByRole} = render(
				<>
					<ClayAutocomplete
						messages={messages}
						placeholder="Enter a number from One to Five"
					>
						{['one', 'two', 'three'].map((item) => (
							<ClayAutocomplete.Item key={item}>
								{item}
							</ClayAutocomplete.Item>
						))}
					</ClayAutocomplete>
					<button type="button">Click outside</button>
				</>
			);

			const input = getByRole('combobox') as HTMLInputElement;

			userEvent.click(input);
			userEvent.type(input, 't');

			expect(getByRole('listbox')).toBeDefined();

			userEvent.tab();

			expect(input.value).toBe('');
			expect(queryByRole('listbox')).toBeFalsy();
		});
	});

	describe('Primary Action interactions', () => {
		it('renders', async () => {
			const {getByRole, getByText} = render(
				<ClayAutocomplete
					messages={messages}
					placeholder="Enter a number from One to Five"
					primaryAction={{
						label: 'Create',
						onClick: () => {},
					}}
				>
					{['one', 'two', 'three'].map((item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					))}
				</ClayAutocomplete>
			);

			const input = getByRole('combobox');

			userEvent.click(input);
			userEvent.type(input, '{arrowdown}');

			expect(getByText('Create')).toBeTruthy();
		});

		it('onClick fires when item is selected', async () => {
			const onClickMock = jest.fn();

			const {getByRole} = render(
				<ClayAutocomplete
					primaryAction={{
						label: 'Create',
						onClick: onClickMock,
					}}
				>
					{['one', 'two', 'three'].map((item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					))}
				</ClayAutocomplete>
			);

			const input = getByRole('combobox');

			userEvent.click(input);
			userEvent.type(input, '{arrowdown}');
			userEvent.type(input, '{enter}');

			expect(onClickMock).toHaveBeenCalled();
		});

		it('renders when autocomplete uses dynamic rendering', async () => {
			const onClickMock = jest.fn();

			const {getByRole, getByText} = render(
				<ClayAutocomplete
					defaultItems={['one', 'two', 'three']}
					primaryAction={{
						label: 'Create',
						onClick: onClickMock,
					}}
				>
					{(item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					)}
				</ClayAutocomplete>
			);

			expect(onClickMock).not.toHaveBeenCalled();

			const input = getByRole('combobox');

			userEvent.click(input);

			userEvent.type(input, '{arrowdown}');

			expect(getByText('Create')).toBeTruthy();

			userEvent.type(input, '{enter}');
			expect(onClickMock).toHaveBeenCalled();
		});
	});

	describe('Infinite scroll interactions', () => {
		it('calls onLoadMore when last item is active using keyboard', async () => {
			const onLoadMore = jest.fn();

			const {getByRole} = render(
				<ClayAutocomplete onLoadMore={onLoadMore}>
					{Array(20)
						.fill(0)
						.map((_, index) => (
							<ClayAutocomplete.Item
								key={index}
								textValue={`Item ${index + 1}`}
							>
								Item {index + 1}
							</ClayAutocomplete.Item>
						))}
				</ClayAutocomplete>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);

			expect(onLoadMore).not.toHaveBeenCalled();

			// Navigates to the last item

			userEvent.type(combobox, '{arrowup}');

			await waitFor(() => {
				expect(onLoadMore).toHaveBeenCalled();
			});
		});

		it('announces count of initially loaded items and when more items are loaded', async () => {
			const initialCount = 10;
			const step = 5;

			const TestComponent = () => {
				const [count, setCount] = React.useState(initialCount);
				const [networkStatus, setNetworkStatus] =
					React.useState<NetworkStatus>(NetworkStatus.Unused);

				const onLoadMore = jest.fn().mockImplementation(() => {
					return new Promise<void>((resolve) => {
						setNetworkStatus(NetworkStatus.Loading);

						setTimeout(() => {
							setCount((count) => count + step);
							setNetworkStatus(NetworkStatus.Unused);
							resolve();
						}, 100);
					});
				});

				return (
					<ClayAutocomplete
						loadingState={networkStatus}
						onLoadMore={onLoadMore}
					>
						{Array(count)
							.fill(0)
							.map((_, index) => (
								<ClayAutocomplete.Item
									key={index}
									textValue={`Item ${index + 1}`}
								>
									Item {index + 1}
								</ClayAutocomplete.Item>
							))}
					</ClayAutocomplete>
				);
			};

			const {getAllByRole, getByRole} = render(<TestComponent />);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);

			// Display the listbox

			userEvent.type(combobox, '{arrowdown}');

			const [announcer] = getAllByRole('log');

			await waitFor(() => {
				expect(announcer?.innerHTML).toContain(
					`${initialCount} items loaded. Reach the last item to load more.`
				);
			});

			// Navigates to the last item

			userEvent.type(combobox, '{arrowup}');

			await waitFor(() => {
				expect(announcer?.innerHTML).toContain(`Loading more items.`);

				expect(announcer?.innerHTML).toContain(
					`${initialCount + step} items loaded.`
				);
			});
		});
	});

	describe('keyboard arrows indicator', () => {
		it('does not render the indicator by default', async () => {
			const {getByRole} = render(
				<ClayAutocomplete messages={messages}>
					{['one', 'two', 'three'].map((item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					))}
				</ClayAutocomplete>
			);

			await userEvent.type(getByRole('combobox'), 'o');

			expect(
				document.body.querySelector('.clay-keyboard-arrows-indicator')
			).not.toBeInTheDocument();
		});

		it('renders the floating indicator alongside the input when enabled', async () => {
			const {getByRole} = render(
				<ClayAutocomplete
					displayKeyboardArrowsIndicator
					messages={messages}
				>
					{['one', 'two', 'three'].map((item) => (
						<ClayAutocomplete.Item key={item}>
							{item}
						</ClayAutocomplete.Item>
					))}
				</ClayAutocomplete>
			);

			await userEvent.type(getByRole('combobox'), 'o');

			const indicator = document.body.querySelector(
				'.clay-keyboard-arrows-indicator'
			);

			expect(indicator).toBeInTheDocument();
			expect(indicator).toHaveClass('clay-keyboard-arrows-vertical');
			expect(indicator).toHaveClass(
				'clay-keyboard-arrows-indicator-floating'
			);
		});
	});

	it('renders the caption pinned at the bottom of the menu', () => {
		const {getByRole, getByText} = render(
			<ClayAutocomplete
				caption={<span aria-hidden="true">Showing 3 of 30 Items</span>}
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		userEvent.type(getByRole('combobox'), 't');

		const caption = getByText('Showing 3 of 30 Items');

		expect(caption).toHaveAttribute('aria-hidden', 'true');

		expect(caption.parentElement!.classList).toContain('dropdown-caption');

		expect(getByRole('listbox').parentElement!.classList).toContain(
			'dropdown-menu-has-caption'
		);
	});

	it('does not render a caption or its menu class when the prop is omitted', () => {
		const {getByRole, queryByText} = render(
			<ClayAutocomplete
				messages={messages}
				placeholder="Enter a number from One to Five"
			>
				{['one', 'two', 'three'].map((item) => (
					<ClayAutocomplete.Item key={item}>
						{item}
					</ClayAutocomplete.Item>
				))}
			</ClayAutocomplete>
		);

		userEvent.type(getByRole('combobox'), 't');

		expect(queryByText(/Showing/)).toBeNull();

		expect(getByRole('listbox').parentElement!.classList).not.toContain(
			'dropdown-menu-has-caption'
		);
	});
});
