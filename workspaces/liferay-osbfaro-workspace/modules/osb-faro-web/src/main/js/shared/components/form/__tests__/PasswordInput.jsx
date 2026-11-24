import Form from 'shared/components/form';
import PasswordInput from '../PasswordInput';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {mockForm} from 'test/data';

const TestComponent = () => (
	<Form>
		<PasswordInput field={{name: 'foo'}} form={mockForm()} />
	</Form>
);

describe('PasswordInput', () => {
	it('should render', () => {
		const {container} = render(<TestComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render with an input type of "password" if showPassword is false', () => {
		const {container} = render(<TestComponent />);
		const input = container.querySelector('#foo');

		expect(input).toHaveAttribute('type', 'password');
	});

	it('should render with an input type of "text" if showPassword is true', () => {
		const {container, getByRole} = render(<TestComponent />);
		const input = container.querySelector('#foo');

		expect(input).toHaveAttribute('type', 'password');

		fireEvent.click(getByRole('button'));
		jest.runAllTimers();

		expect(input).toHaveAttribute('type', 'text');
		expect(container).toMatchSnapshot();
	});
});
