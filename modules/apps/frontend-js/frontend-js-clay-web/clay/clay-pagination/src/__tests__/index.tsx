/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {ClayPaginationWithBasicItems} from '..';
import {cleanup, fireEvent, getByText, render} from '@testing-library/react';
import React from 'react';

const spritemap = 'path/to/spritemap';

global.ResizeObserver = require('resize-observer-polyfill');

describe('ClayPagination', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(
			<ClayPaginationWithBasicItems
				defaultActive={12}
				size="lg"
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with only one page', () => {
		const {container} = render(
			<ClayPaginationWithBasicItems
				defaultActive={1}
				ellipsisBuffer={1}
				spritemap={spritemap}
				totalPages={1}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('totalPages with 0 will render the pagination with only one page', () => {
		const {container} = render(
			<ClayPaginationWithBasicItems
				defaultActive={1}
				ellipsisBuffer={1}
				spritemap={spritemap}
				totalPages={0}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('calls onPageChange when arrow is clicked', () => {
		const changeMock = jest.fn();

		const {getByTestId} = render(
			<ClayPaginationWithBasicItems
				activePage={12}
				onPageChange={changeMock}
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		fireEvent.click(getByTestId('prevArrow'), {});

		expect(changeMock).toHaveBeenLastCalledWith(11);

		fireEvent.click(getByTestId('nextArrow'), {});

		expect(changeMock).toHaveBeenLastCalledWith(13);
	});

	it('calls onPageChange when individual page is clicked', () => {
		const changeMock = jest.fn();

		const {getByText} = render(
			<ClayPaginationWithBasicItems
				activePage={12}
				onPageChange={changeMock}
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		fireEvent.click(getByText('25'), {});

		expect(changeMock).toHaveBeenLastCalledWith(25);
	});

	it('calls onPageChange when individual page is (using new properties)', () => {
		const changeMock = jest.fn();

		const {getByText} = render(
			<ClayPaginationWithBasicItems
				active={12}
				onActiveChange={changeMock}
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		fireEvent.click(getByText('25'), {});

		expect(changeMock).toHaveBeenLastCalledWith(25);
	});

	it('disable ellipsis when disableEllipsis prop is passed', () => {
		const {getAllByText} = render(
			<ClayPaginationWithBasicItems
				defaultActive={12}
				disableEllipsis
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		getAllByText('...').forEach((ellipsisButton) =>
			expect(ellipsisButton).toBeDisabled()
		);
	});

	it('render pagination with links and active item without link', () => {
		const {getByText} = render(
			<ClayPaginationWithBasicItems
				defaultActive={12}
				ellipsisBuffer={2}
				hrefConstructor={(page) => `/#${page}`}
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		const currentActivePage = getByText('12');

		expect(
			(currentActivePage.parentElement as HTMLElement).classList
		).toContain('active');
		expect(currentActivePage.getAttribute('href')).toBe(null);
		expect(currentActivePage.getAttribute('aria-current')).toBe('page');
	});

	it('shows dropdown when ellipsis is clicked', () => {
		const {getAllByText} = render(
			<ClayPaginationWithBasicItems
				defaultActive={12}
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		fireEvent.click(getAllByText('...')[0] as HTMLElement, {});

		expect(
			document.body.querySelector('.dropdown-menu')!.classList
		).toContain('show');
	});

	it('calls onPageChange when an item is clicked in dropdown-menu', () => {
		const changeMock = jest.fn();

		const {getAllByText} = render(
			<ClayPaginationWithBasicItems
				activePage={12}
				onPageChange={changeMock}
				spritemap={spritemap}
				totalPages={25}
			/>
		);

		fireEvent.click(getAllByText('...')[0] as HTMLElement, {});

		fireEvent.click(getByText(document.body, '4') as HTMLAnchorElement, {});

		expect(changeMock).toHaveBeenLastCalledWith(4);
	});

	it("does not call a wrapping form's onSubmit", () => {
		const changeMock = jest.fn();
		const onSubmitFn = jest.fn();

		const {getByText} = render(
			<form onSubmit={onSubmitFn}>
				<ClayPaginationWithBasicItems
					activePage={1}
					onPageChange={changeMock}
					spritemap={spritemap}
					totalPages={5}
				/>
			</form>
		);

		fireEvent.click(getByText('2'), {});

		expect(changeMock).toHaveBeenLastCalledWith(2);
		expect(onSubmitFn).not.toHaveBeenCalled();
	});

	it('renders the aria labels that are provided', () => {
		const {getByLabelText} = render(
			<ClayPaginationWithBasicItems
				ariaLabels={{
					link: 'Vai alla pagina, {0}',
					next: 'Vai alla pagina successiva, {0}',
					previous: 'Vai alla pagina precedente, {0}',
				}}
				defaultActive={2}
				spritemap={spritemap}
				totalPages={5}
			/>
		);

		expect(getByLabelText('Vai alla pagina, 3')).toBeInTheDocument();
		expect(
			getByLabelText('Vai alla pagina successiva, 3')
		).toBeInTheDocument();
		expect(
			getByLabelText('Vai alla pagina precedente, 1')
		).toBeInTheDocument();
	});

	it('falls back to the default aria labels that are not provided', () => {
		const {getByLabelText} = render(
			<ClayPaginationWithBasicItems
				ariaLabels={{link: 'Vai alla pagina, {0}'}}
				defaultActive={2}
				spritemap={spritemap}
				totalPages={5}
			/>
		);

		expect(getByLabelText('Vai alla pagina, 3')).toBeInTheDocument();
		expect(getByLabelText('Go to the Next Page, 3')).toBeInTheDocument();
		expect(
			getByLabelText('Go to the Previous Page, 1')
		).toBeInTheDocument();
	});

	it('renders the ellipsis aria label that is provided', () => {
		const {getByLabelText} = render(
			<ClayPaginationWithBasicItems
				ariaLabels={{ellipsis: 'Mostra le pagine da {0} a {1}'}}
				defaultActive={1}
				spritemap={spritemap}
				totalPages={10}
			/>
		);

		expect(getByLabelText('Mostra le pagine da 4 a 9')).toBeInTheDocument();
	});
});
