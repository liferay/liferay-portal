import CompositionChart, {CompositionLegend} from '../CompositionChart';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('CompositionChart', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<CompositionChart />);

		expect(container).toBeTruthy();
	});
});

describe('CompositionLegend', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<CompositionLegend
				items={[
					{color: 'blue', label: 'foo', value: 25},
					{color: 'pink', label: 'bar', value: 75}
				]}
				total={100}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render "< 1%" if percentage is less than 1%', () => {
		const {container} = render(
			<CompositionLegend
				items={[
					{color: 'blue', label: 'foo', value: 1},
					{color: 'pink', label: 'bar', value: 1}
				]}
				total={1000}
			/>
		);

		container
			.querySelectorAll('b')
			.forEach(element => expect(element).toHaveTextContent('< 1%'));
	});

	it('should render "0%" if percentage is 0%', () => {
		const {container} = render(
			<CompositionLegend
				items={[
					{color: 'blue', label: 'foo', value: 0},
					{color: 'pink', label: 'bar', value: 0}
				]}
				total={1000}
			/>
		);

		container
			.querySelectorAll('b')
			.forEach(element => expect(element).toHaveTextContent('0%'));
	});

	it('should render a zero count as 0', () => {
		render(
			<CompositionLegend
				items={[{color: 'blue', label: 'foo', value: 0}]}
				total={1000}
			/>
		);

		expect(screen.getByTestId('active-count')).toHaveTextContent('0');
	});

	it('should render the percentage as no data when there is no total', () => {
		render(
			<CompositionLegend
				items={[{color: 'blue', label: 'foo', value: 0}]}
				total={0}
			/>
		);

		expect(
			screen.getByTestId('active-porcentage').querySelector('span')
		).toHaveClass('text-secondary');
		expect(screen.getByTestId('active-porcentage')).toHaveTextContent(
			'–'
		);
	});

	it('should render the count and percentage as processing', () => {
		render(
			<CompositionLegend
				items={[{color: 'blue', label: 'foo', value: 0}]}
				processing
				total={0}
			/>
		);

		expect(screen.getByTestId('active-count')).toHaveTextContent(
			'Processing'
		);
		expect(screen.getByTestId('active-porcentage')).toHaveTextContent(
			'Processing'
		);
	});
});
