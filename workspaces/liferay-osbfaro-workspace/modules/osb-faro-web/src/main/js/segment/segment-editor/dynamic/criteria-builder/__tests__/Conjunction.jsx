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

	it('should render the button as disabled when the disabled prop is set', () => {
		const {getByRole} = render(
			<Conjunction conjunctionName='and' disabled />
		);

		expect(getByRole('button')).toBeDisabled();
	});

	it('should render the Then label for an AND conjunction in sequential mode', () => {
		const {getByRole} = render(
			<Conjunction conjunctionName='and' sequential />
		);

		expect(getByRole('button')).toHaveTextContent(/then/i);
	});

	it('should render the And label for an AND conjunction outside sequential mode', () => {
		const {getByRole} = render(<Conjunction conjunctionName='and' />);

		expect(getByRole('button')).toHaveTextContent(/and/i);
	});
});
