import Breadcrumbs from '../Breadcrumbs';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

const ITEMS = ['one', 'two', 'three', 'four', 'five', 'six', 'seven'];

jest.unmock('react-dom');

const defaultProps = {
	items: ITEMS.map((item, i) => ({
		active: i === 0,
		href: `#${item}`,
		label: item
	}))
};

const DefaultComponent = props => (
	<MemoryRouter>
		<Breadcrumbs {...defaultProps} {...props} />
	</MemoryRouter>
);

describe('Breadcrumbs', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {getByText} = render(<DefaultComponent />);

		expect(getByText('one')).toBeInTheDocument();
	});

	it('should render with two items visible', () => {
		const {container} = render(<DefaultComponent bufferSize={2} />);

		expect(
			container.querySelectorAll('.breadcrumb-item:not(.dropdown-root)')
				.length
		).toBe(2);
	});

	it('should render with no dropdown', () => {
		const {container} = render(<DefaultComponent bufferSize={0} />);

		expect(container.querySelector('.dropdown-root')).toBeNull();
	});

	it('should render using onClick rather than hrefs', () => {
		const {container} = render(
			<DefaultComponent
				items={ITEMS.map((item, i) => ({
					active: i === 0,
					label: item
				}))}
				onClick={jest.fn()}
			/>
		);

		expect(container.querySelector('a')).toBeNull();
	});

	it('should render the active breadcrumb as a span element', () => {
		const {container} = render(
			<DefaultComponent
				items={ITEMS.map((item, i) => ({
					active: i === ITEMS.length - 1,
					label: item
				}))}
				onClick={jest.fn()}
			/>
		);

		expect(
			container.querySelector('.breadcrumb-item.active span')
		).toHaveTextContent('seven');
	});
});
