import React from 'react';
import SidebarPagination from '../SidebarPagination';
import {fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('SidebarPagination', () => {
	it('should render nothing when there is a single page', () => {
		const {container} = render(
			<SidebarPagination
				activePage={1}
				onPageChange={() => {}}
				totalPages={1}
			/>
		);

		expect(container).toBeEmptyDOMElement();
	});

	it('should report the page the user picked', () => {
		const onPageChange = jest.fn();

		render(
			<SidebarPagination
				activePage={1}
				onPageChange={onPageChange}
				totalPages={3}
			/>
		);

		fireEvent.click(screen.getByText('2'));

		expect(onPageChange).toHaveBeenCalledWith(2);
	});
});
