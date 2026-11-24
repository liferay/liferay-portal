import Pagination from '../Pagination';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {withStaticRouter} from 'test/mock-router';

const DefaultComponent = withStaticRouter(Pagination);

describe('Pagination', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<DefaultComponent href='' page={1} total={1} />
		);

		expect(container.querySelector('.pagination-root')).toBeInTheDocument();
	});

	it('should render with buttons if passed an onChange handler', () => {
		const {container} = render(
			<DefaultComponent onChange={jest.fn()} page={1} total={1} />
		);

		expect(container.querySelector('.pagination-root')).toBeTruthy();
		expect(container.querySelectorAll('.page-item').length).toBeGreaterThan(
			0
		);
	});

	it('should render with a lot of pages', () => {
		const {container} = render(
			<DefaultComponent href='' page={1} total={100} />
		);

		// Should have prev + pages (with ellipsis) + next
		expect(container.querySelectorAll('.page-item').length).toBeGreaterThan(
			5
		);
	});

	it('should render a lot of pages with a middle page active', () => {
		const {container} = render(
			<DefaultComponent href='' page={50} total={100} />
		);

		const activeItem = container.querySelector('.page-item.active');

		expect(activeItem).toBeTruthy();
	});

	it('should render a lot of pages with the end page active', () => {
		const {container} = render(
			<DefaultComponent href='' page={100} total={100} />
		);

		const activeItem = container.querySelector('.page-item.active');

		expect(activeItem).toBeTruthy();

		// Last page should be active, next button disabled
		expect(container.querySelector('.page-item.disabled')).toBeTruthy();
	});
});
