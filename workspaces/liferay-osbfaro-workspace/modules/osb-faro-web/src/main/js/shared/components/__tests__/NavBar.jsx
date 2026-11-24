import NavBar from '../NavBar';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('NavBar', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<NavBar />);
		expect(container).toMatchSnapshot();
	});

	it('should render Brand as a child', () => {
		const {container} = render(
			<NavBar
				children={[<NavBar.Brand key='foo'>{'foo bar'}</NavBar.Brand>]}
			/>
		);
		expect(container.querySelector('.navbar-brand')).toBeTruthy();
	});

	it('should render with light display', () => {
		const {container} = render(<NavBar display='light' />);
		expect(container.querySelector('.navbar-light')).toBeTruthy();
	});

	it('should render with dark display', () => {
		const {container} = render(<NavBar display='dark' />);
		expect(container.querySelector('.navbar-dark')).toBeTruthy();
	});

	it('should render with underline', () => {
		const {container} = render(<NavBar underline />);
		expect(container.querySelector('.navbar-underline')).toBeTruthy();
	});

	it('should render with pageNav', () => {
		const {container} = render(<NavBar pageNav />);
		expect(container.querySelector('.page-nav')).toBeTruthy();
	});

	it('should render with justifyContent', () => {
		const {container} = render(<NavBar justifyContent='end' />);
		expect(container.querySelector('.justify-content-end')).toBeTruthy();
	});
});
