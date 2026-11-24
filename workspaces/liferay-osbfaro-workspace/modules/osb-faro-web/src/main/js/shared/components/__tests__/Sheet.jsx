import React from 'react';
import Sheet from '../Sheet';
import {cleanup, render} from '@testing-library/react';

describe('Sheet', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Sheet />);
		expect(container).toMatchSnapshot();
	});

	it('should render as a large sheet', () => {
		const {container} = render(<Sheet large />);
		expect(container.querySelector('.sheet-lg')).toBeTruthy();
	});

	it('Footer should render', () => {
		const {container} = render(<Sheet.Footer />);
		expect(container).toMatchSnapshot();
	});

	it('Header should render', () => {
		const {container} = render(<Sheet.Header />);
		expect(container).toMatchSnapshot();
	});

	it('Section should render', () => {
		const {container} = render(<Sheet.Section />);
		expect(container).toMatchSnapshot();
	});

	it('Subtitle should render', () => {
		const {container} = render(<Sheet.Subtitle />);
		expect(container).toMatchSnapshot();
	});

	it('TertiaryTitle should render', () => {
		const {container} = render(<Sheet.TertiaryTitle />);
		expect(container).toMatchSnapshot();
	});

	it('Text should render', () => {
		const {container} = render(<Sheet.Text />);
		expect(container).toMatchSnapshot();
	});

	it('Title should render', () => {
		const {container} = render(<Sheet.Title />);
		expect(container).toMatchSnapshot();
	});
});
