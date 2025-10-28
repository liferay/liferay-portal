/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import React, {cloneElement, useState} from 'react';

import PaginationBar from '../../../../src/main/resources/META-INF/resources/js/shared/components/pagination-bar/PaginationBar.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const ContainerProps = ({children, initialPage = 1, initialPageSize = 20}) => {
	const [page, setPage] = useState(initialPage);
	const [pageSize, setPageSize] = useState(initialPageSize);
	const stateProps = {page, pageSize, setPage, setPageSize};

	return (
		<MockRouter withoutRouterProps>
			{cloneElement(children, stateProps)}
		</MockRouter>
	);
};

describe('The PaginationBar component should', () => {
	afterEach(cleanup);

	test('Render with initial params and change pageSize and page using state', () => {
		const {baseElement} = render(
			<PaginationBar totalCount={20} withoutRouting />,
			{wrapper: ContainerProps}
		);

		const pageSizeBtn = baseElement.querySelector('button.dropdown-toggle');

		expect(pageSizeBtn).toHaveTextContent('x-entries');

		fireEvent.click(pageSizeBtn);

		const pageSizeOptions = baseElement.querySelectorAll('.dropdown-item');

		expect(pageSizeOptions.length).toBe(6);

		let pageLinks = baseElement.querySelectorAll('.page-link');

		expect(pageLinks.length).toBe(3);

		expect(pageLinks[0].parentElement).toHaveClass('disabled');
		expect(pageLinks[1]).toHaveTextContent('1');
		expect(pageLinks[2].parentElement).toHaveClass('disabled');

		fireEvent.click(pageSizeOptions[0]);

		pageLinks = baseElement.querySelectorAll('.page-link');
		let pageItems = baseElement.querySelectorAll('.page-item');

		expect(pageLinks.length).toBe(6);

		expect(pageLinks[1]).toHaveTextContent('1');
		expect(pageLinks[2]).toHaveTextContent('2');
		expect(pageLinks[3]).toHaveTextContent('3');

		expect(pageItems[1].className.includes('active')).toBe(true);
		expect(pageItems[2].className.includes('active')).toBe(false);
		expect(pageItems[3].className.includes('active')).toBe(false);

		fireEvent.click(pageLinks[3]);

		pageItems = baseElement.querySelectorAll('.page-item');

		expect(pageItems[1].className.includes('active')).toBe(false);
		expect(pageItems[2].className.includes('active')).toBe(false);
		expect(pageItems[3].className.includes('active')).toBe(true);
	});

	test('Render with initial params and change pageSize and page using route params', () => {
		const {baseElement} = render(<PaginationBar totalCount={50} />, {
			wrapper: MockRouter,
		});

		const pageSizeBtn = baseElement.querySelector('button.dropdown-toggle');

		expect(pageSizeBtn).toHaveTextContent('x-entries');

		fireEvent.click(pageSizeBtn);

		const pageSizeOptions = baseElement.querySelectorAll('.dropdown-item');

		expect(pageSizeOptions.length).toBe(6);

		let pageLinks = baseElement.querySelectorAll('.page-link');
		let pageItems = baseElement.querySelectorAll('.page-item');

		expect(pageLinks.length).toBe(5);

		expect(pageLinks[0].parentElement).toHaveClass('disabled');
		expect(pageLinks[1]).toHaveTextContent('1');
		expect(pageLinks[2]).toHaveTextContent('2');
		expect(pageLinks[3]).toHaveTextContent('3');

		expect(pageItems[1].className.includes('active')).toBe(true);

		expect(pageItems[1].className.includes('active')).toBe(true);
		expect(pageItems[2].className.includes('active')).toBe(false);
		expect(pageItems[3].className.includes('active')).toBe(false);

		fireEvent.click(pageLinks[3]);

		pageItems = baseElement.querySelectorAll('.page-item');

		expect(pageItems[1].className.includes('active')).toBe(false);
		expect(pageItems[2].className.includes('active')).toBe(false);
		expect(pageItems[3].className.includes('active')).toBe(true);

		fireEvent.click(pageSizeOptions[4]);

		pageLinks = baseElement.querySelectorAll('.page-link');
		pageItems = baseElement.querySelectorAll('.page-item');

		expect(pageLinks.length).toBe(3);

		expect(pageItems[1].className.includes('active')).toBe(true);
		expect(pageLinks[1]).toHaveTextContent('1');
		expect(pageLinks[2].parentElement).toHaveClass('disabled');
	});

	test('Render with insufficient total count to pagination', () => {
		const {container} = render(<PaginationBar totalCount={4} />, {
			wrapper: MockRouter,
		});

		expect(container.innerHTML).toEqual('');
	});
});
