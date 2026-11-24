import MaskedInput from '../MaskedInput';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('MaskedInput', () => {
	afterEach(cleanup);

	it('should render input', () => {
		const {container} = render(<MaskedInput mask={[]} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with mask as a function', () => {
		const maskFn = rawValue => {
			rawValue = rawValue.replace('NUMBER', '');
			rawValue = rawValue.replace('STRING', '');

			const isNumber = !isNaN(Number(rawValue));

			return isNumber ? 'NUMBER'.split('') : 'STRING'.split('');
		};
		const defaultProps = {mask: maskFn, showMask: true, value: 'test'};

		const {container, rerender} = render(<MaskedInput {...defaultProps} />);

		expect(container.querySelector('input')).toHaveAttribute(
			'value',
			'test'
		);

		rerender(<MaskedInput {...defaultProps} value={123} />);
		jest.runAllTimers();
		expect(container.querySelector('input')).toHaveAttribute(
			'value',
			'123'
		);
	});

	it('should render with mask showing', () => {
		const {container} = render(
			<MaskedInput mask={[/\d/, /\d/, /\d/]} showMask />
		);
		expect(container).toMatchSnapshot();
	});

	it('should succeed with a specific mask', () => {
		const {container} = render(
			<MaskedInput mask={[/\d/, /\d/, /\d/]} value={123} />
		);
		expect(container).toMatchSnapshot();
	});
});
