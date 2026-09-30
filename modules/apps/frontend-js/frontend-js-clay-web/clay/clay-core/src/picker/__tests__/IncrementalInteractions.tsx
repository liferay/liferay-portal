/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {Option, Picker} from '../../';

describe('Picker incremental interactions', () => {
	afterEach(cleanup);

	it('click trigger expand menu', () => {
		const {getByRole, queryByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		expect(combobox.getAttribute('aria-expanded')).toBe('false');
		expect(queryByRole('listbox')).toBeFalsy();

		userEvent.click(combobox);

		expect(combobox.getAttribute('aria-expanded')).toBe('true');
		expect(getByRole('listbox')).toBeTruthy();
	});

	it.concurrent.each([
		['down arrow', '[ArrowDown]'],
		['up arrow', '[ArrowUp]'],
		['enter', '[Enter]'],
		['spacebar', '[Space]'],
		['home', '[Home]'],
		['alt + down arrow', '{Alt>}{ArrowDown/}'],
	])('pressing the (%s) key expands the menu', (_, key) => {
		const {getByRole, queryByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		expect(combobox.getAttribute('aria-expanded')).toBe('false');
		expect(queryByRole('listbox')).toBeFalsy();

		userEvent.tab();
		userEvent.keyboard(key);

		expect(combobox.getAttribute('aria-expanded')).toBe('true');
		expect(getByRole('listbox')).toBeTruthy();
		expect(combobox.getAttribute('aria-activedescendant')).toBe('Apple');
	});

	it('pressing the end key expands the menu and accessibility focus on the last option', () => {
		const {getByRole, queryByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		expect(combobox.getAttribute('aria-expanded')).toBe('false');
		expect(queryByRole('listbox')).toBeFalsy();

		userEvent.tab();
		userEvent.keyboard('[End]');

		expect(combobox.getAttribute('aria-expanded')).toBe('true');
		expect(getByRole('listbox')).toBeTruthy();
		expect(combobox.getAttribute('aria-activedescendant')).toBe(
			'Blueberry'
		);
	});

	it('pressing typing keys expands the menu and accessibility focus with the first option that matches', () => {
		const {getByRole, queryByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		expect(combobox.getAttribute('aria-expanded')).toBe('false');
		expect(queryByRole('listbox')).toBeFalsy();

		userEvent.tab();
		userEvent.keyboard('{b}');

		expect(combobox.getAttribute('aria-expanded')).toBe('true');
		expect(getByRole('listbox')).toBeTruthy();
		expect(combobox.getAttribute('aria-activedescendant')).toBe('Banana');
	});

	it('typing the same character successively expands the menu and accessibility focus cycles through the corresponding options', () => {
		const {getByRole, queryByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		expect(combobox.getAttribute('aria-expanded')).toBe('false');
		expect(queryByRole('listbox')).toBeFalsy();

		userEvent.tab();
		userEvent.keyboard('{b}');

		expect(combobox.getAttribute('aria-expanded')).toBe('true');
		expect(getByRole('listbox')).toBeTruthy();
		expect(combobox.getAttribute('aria-activedescendant')).toBe('Banana');

		userEvent.keyboard('{b}');

		expect(combobox.getAttribute('aria-activedescendant')).toBe(
			'Blueberry'
		);
	});

	it('when expanding the menu the focus should persist on the trigger', () => {
		const {getByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		userEvent.click(combobox);

		expect(document.activeElement).toEqual(combobox);
	});

	it('when closing the menu the focus should persist on the trigger', () => {
		const {getByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		userEvent.click(combobox);

		expect(document.activeElement).toEqual(combobox);

		userEvent.keyboard('{Escape}');

		expect(document.activeElement === combobox).toBe(true);
		expect(combobox.getAttribute('aria-expanded')).toBe('false');
	});

	it('when expanding the menu the accessibility focus should be on the first option', () => {
		const {getByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		userEvent.click(combobox);

		expect(combobox.getAttribute('aria-activedescendant')).toBe('Apple');
	});

	it('close the expanded menu without selecting and expand again the first option must have accessibility focus', () => {
		const {getByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		userEvent.click(combobox);
		userEvent.keyboard('[ArrowDown]');

		expect(document.activeElement === combobox).toBe(true);
		expect(combobox.getAttribute('aria-activedescendant')).toBe('Banana');
	});

	it('scrolls the selected option into view when expanding the menu', () => {
		const items = Array.from({length: 50}, (_, i) => `Item ${i + 1}`);

		const ITEM_HEIGHT = 20;
		const LIST_CLIENT_HEIGHT = 200;

		const originalScrollTo = HTMLElement.prototype.scrollTo;

		HTMLElement.prototype.scrollTo = jest.fn() as any;

		const getBoundingClientRectSpy = jest
			.spyOn(Element.prototype, 'getBoundingClientRect')
			.mockImplementation(function (this: HTMLElement) {
				if (this.getAttribute('role') === 'option') {
					const top = items.indexOf(this.id) * ITEM_HEIGHT;

					return {
						bottom: top + ITEM_HEIGHT,
						height: ITEM_HEIGHT,
						left: 0,
						right: 0,
						toJSON: () => ({}),
						top,
						width: 0,
						x: 0,
						y: top,
					};
				}

				if (this.getAttribute('role') === 'listbox') {
					return {
						bottom: LIST_CLIENT_HEIGHT,
						height: LIST_CLIENT_HEIGHT,
						left: 0,
						right: 0,
						toJSON: () => ({}),
						top: 0,
						width: 0,
						x: 0,
						y: 0,
					};
				}

				return {
					bottom: 0,
					height: 0,
					left: 0,
					right: 0,
					toJSON: () => ({}),
					top: 0,
					width: 0,
					x: 0,
					y: 0,
				};
			});

		const clientHeightSpy = jest
			.spyOn(HTMLElement.prototype, 'clientHeight', 'get')
			.mockImplementation(function (this: HTMLElement) {
				return this.getAttribute('role') === 'listbox'
					? LIST_CLIENT_HEIGHT
					: 0;
			});

		const scrollHeightSpy = jest
			.spyOn(HTMLElement.prototype, 'scrollHeight', 'get')
			.mockImplementation(function (this: HTMLElement) {
				return this.getAttribute('role') === 'listbox'
					? items.length * ITEM_HEIGHT
					: 0;
			});

		try {
			const {getByRole} = render(
				<Picker items={items} selectedKey="Item 40">
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			userEvent.click(getByRole('combobox'));

			const selectedIndex = items.indexOf('Item 40');

			const expected = Math.max(
				0,
				Math.min(
					selectedIndex * ITEM_HEIGHT -
						(LIST_CLIENT_HEIGHT - ITEM_HEIGHT) / 2,
					items.length * ITEM_HEIGHT - LIST_CLIENT_HEIGHT
				)
			);

			expect(getByRole('listbox').scrollTop).toBe(expected);
		}
		finally {
			getBoundingClientRectSpy.mockRestore();

			clientHeightSpy.mockRestore();

			scrollHeightSpy.mockRestore();

			HTMLElement.prototype.scrollTo = originalScrollTo;
		}
	});

	describe('expanded menu', () => {
		it.concurrent.each([
			['enter', '[Enter]'],
			['spacebar', '[Space]'],
			['alt + up arrow', '{Alt>}{ArrowUp/}'],
		])('pressing (%s) selects option with visual focus', (_, key) => {
			const {getByRole} = render(
				<Picker items={['Apple', 'Banana', 'Blueberry']}>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);
			userEvent.keyboard(key);

			expect(combobox.getAttribute('aria-expanded')).toBe('false');
			expect(combobox.getAttribute('aria-activedescendant')).toBe('');
			expect(combobox.textContent).toBe('Apple');
		});

		it('pressing the down arrow key navigates down and update the visual focus', () => {
			const {getByRole} = render(
				<Picker items={['Apple', 'Banana', 'Blueberry']}>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);
			userEvent.keyboard('[ArrowDown]');

			expect(combobox.getAttribute('aria-expanded')).toBe('true');
			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Banana'
			);
			expect(combobox.textContent).not.toBe('Banana');

			userEvent.keyboard('[ArrowDown]');

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Blueberry'
			);
			expect(combobox.textContent).not.toBe('Blueberry');

			// Maintains visual focus on the last option when there are no more
			// options.

			userEvent.keyboard('[ArrowDown]');

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Blueberry'
			);
			expect(combobox.textContent).not.toBe('Blueberry');
		});

		it('pressing the up arrow key navigates up and update the visual focus', () => {
			const {getByRole} = render(
				<Picker items={['Apple', 'Banana', 'Blueberry']}>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);
			userEvent.keyboard('[ArrowDown]');
			userEvent.keyboard('[ArrowDown]');

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Blueberry'
			);
			expect(combobox.textContent).not.toBe('Blueberry');

			userEvent.keyboard('[ArrowUp]');

			expect(combobox.getAttribute('aria-expanded')).toBe('true');
			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Banana'
			);
			expect(combobox.textContent).not.toBe('Banana');

			userEvent.keyboard('[ArrowUp]');

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Apple'
			);
			expect(combobox.textContent).not.toBe('Apple');

			// Maintains visual focus on the first option when there are no more
			// options.

			userEvent.keyboard('[ArrowUp]');

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Apple'
			);
			expect(combobox.textContent).not.toBe('Apple');
		});

		it('pressing the tab key selects the option with visual focus and moves the focus to the next element on the screen', () => {
			const {getByRole} = render(
				<Picker items={['Apple', 'Banana', 'Blueberry']}>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);
			userEvent.keyboard('[ArrowDown]');

			expect(combobox.getAttribute('aria-expanded')).toBe('true');
			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Banana'
			);
			expect(combobox.textContent).not.toBe('Banana');

			userEvent.tab();

			expect(combobox.getAttribute('aria-expanded')).toBe('false');
			expect(combobox.getAttribute('aria-activedescendant')).toBe('');
			expect(combobox.textContent).toBe('Banana');
		});

		it('pressing the page up key jumps visual focus up 10 options', () => {
			const {getByRole} = render(
				<Picker
					items={[
						'Apple',
						'Banana',
						'Mangos',
						'Blueberry',
						'Boysenberry',
						'Cherry',
						'Cranberry',
						'Eggplant',
						'Fig',
						'Grape',
						'Guava',
						'Huckleberry',
					]}
				>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);
			userEvent.keyboard('{PageDown}');
			userEvent.keyboard('{PageDown}');
			userEvent.keyboard('{PageUp}');

			expect(combobox.getAttribute('aria-expanded')).toBe('true');
			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Banana'
			);
			expect(combobox.textContent).not.toBe('Banana');

			userEvent.keyboard('{PageUp}');

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Apple'
			);
			expect(combobox.textContent).not.toBe('Apple');
		});

		it('pressing the page down key jumps visual focus down 10 options', () => {
			const {getByRole} = render(
				<Picker
					items={[
						'Apple',
						'Banana',
						'Mangos',
						'Blueberry',
						'Boysenberry',
						'Cherry',
						'Cranberry',
						'Eggplant',
						'Fig',
						'Grape',
						'Guava',
						'Huckleberry',
					]}
				>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);
			userEvent.keyboard('{PageDown}');

			expect(combobox.getAttribute('aria-expanded')).toBe('true');
			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Guava'
			);
			expect(combobox.textContent).not.toBe('Guava');

			userEvent.keyboard('{PageDown}');

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Huckleberry'
			);
			expect(combobox.textContent).not.toBe('Huckleberry');
		});

		it('moving the mouse over the option updates the visual focus', () => {
			const {getAllByRole, getByRole} = render(
				<Picker items={['Apple', 'Banana', 'Blueberry']}>
					{(item) => <Option key={item}>{item}</Option>}
				</Picker>
			);

			const combobox = getByRole('combobox');

			userEvent.click(combobox);

			expect(combobox.getAttribute('aria-expanded')).toBe('true');
			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Apple'
			);

			const [, , blueberry] = getAllByRole('option');

			userEvent.hover(blueberry!);

			expect(combobox.getAttribute('aria-activedescendant')).toBe(
				'Blueberry'
			);
			expect(blueberry!.classList).toContain('hover');
		});

		it.concurrent.each([
			['up arrow', '[ArrowUp]'],
			['down arrow', '[ArrowDown]'],
		])(
			'pressing (%s) moves to next option from last mouse over over some option',
			(_, key) => {
				const {getAllByRole, getByRole} = render(
					<Picker items={['Apple', 'Banana', 'Blueberry']}>
						{(item) => <Option key={item}>{item}</Option>}
					</Picker>
				);

				const combobox = getByRole('combobox');

				userEvent.click(combobox);

				expect(combobox.getAttribute('aria-expanded')).toBe('true');
				expect(combobox.getAttribute('aria-activedescendant')).toBe(
					'Apple'
				);

				const [apple, banana, blueberry] = getAllByRole('option');

				userEvent.hover(banana!);

				expect(combobox.getAttribute('aria-activedescendant')).toBe(
					'Banana'
				);

				userEvent.keyboard(key);

				if (key === '[ArrowUp]') {
					expect(combobox.getAttribute('aria-activedescendant')).toBe(
						'Apple'
					);
					expect(apple!.classList).toContain('focus');
				}
				else {
					expect(combobox.getAttribute('aria-activedescendant')).toBe(
						'Blueberry'
					);
					expect(blueberry!.classList).toContain('focus');
				}
			}
		);
	});

	it('when navigating through the menu and closing and reopening the visual focus persists in the first option', () => {
		const {getByRole} = render(
			<Picker items={['Apple', 'Banana', 'Blueberry']}>
				{(item) => <Option key={item}>{item}</Option>}
			</Picker>
		);

		const combobox = getByRole('combobox');

		userEvent.click(combobox);
		userEvent.keyboard('[ArrowDown]');
		userEvent.keyboard('{Escape}');

		userEvent.click(combobox);

		expect(combobox.getAttribute('aria-activedescendant')).toBe('Apple');
	});
});
