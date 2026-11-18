import Conjunction from '../Conjunction';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('Conjunction', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Conjunction conjunctionName='and' />);

		expect(container).toMatchSnapshot();
	});

	it('should render the And label for an AND conjunction outside sequential mode', () => {
		const {getByText} = render(<Conjunction conjunctionName='and' />);

		expect(getByText(/^and$/i)).toBeInTheDocument();
	});

	it('should render the Or label for an OR conjunction', () => {
		const {getByText} = render(<Conjunction conjunctionName='or' />);

		expect(getByText(/^or$/i)).toBeInTheDocument();
	});

	it.each`
		sequential | label         | hidden
		${false}   | ${/^and$/i}  | ${true}
		${true}    | ${/^then$/i} | ${false}
	`(
		'should render a disabled separator that is hidden from assistive technology only when sequential is $sequential',
		({hidden, label, sequential}) => {
			const {getByText, queryByRole} = render(
				<Conjunction conjunctionName='and' sequential={sequential} />
			);

			expect(getByText(label)).toBeDisabled();
			expect(getByText(label)).toHaveAttribute(
				'aria-hidden',
				String(hidden)
			);
			expect(!!queryByRole('button')).toBe(!hidden);
		}
	);
});
