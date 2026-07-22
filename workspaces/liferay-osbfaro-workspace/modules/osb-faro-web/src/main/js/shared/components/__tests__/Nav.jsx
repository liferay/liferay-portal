import ClayButton from '@clayui/button';
import Nav from '../Nav';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

const items = [
	<Nav.Item active href='#' key={1}>
		{'foo'}
	</Nav.Item>,
	<Nav.Item key={2}>{'bar'}</Nav.Item>,
	<Nav.Item key={3}>
		<ClayButton className='button-root' displayType='secondary'>
			{'baz'}
		</ClayButton>
	</Nav.Item>
];

const DefaultComponent = props => (
	<MemoryRouter>
		<Nav children={items} {...props} />
	</MemoryRouter>
);

describe('Nav', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefaultComponent />);
		expect(container).toMatchSnapshot();
	});

	it('should render stacked', () => {
		const {container} = render(<DefaultComponent display='stacked' />);
		expect(container.querySelector('.nav-stacked')).toBeTruthy();
	});

	it('should render with items as pills', () => {
		const {container} = render(<DefaultComponent display='pills' />);
		expect(container.querySelector('.nav-pills')).toBeTruthy();
	});

	it('should render with items as tabs', () => {
		const {container} = render(<DefaultComponent display='tabs' />);
		expect(container.querySelector('.nav-tabs')).toBeTruthy();
	});

	it('should render with underline class', () => {
		const {container} = render(<DefaultComponent display='underline' />);
		expect(container.querySelector('.nav-underline')).toBeTruthy();
	});
});
