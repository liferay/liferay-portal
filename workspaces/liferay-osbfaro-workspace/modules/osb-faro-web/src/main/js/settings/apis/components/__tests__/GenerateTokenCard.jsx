import GenerateTokenCard from '../GenerateTokenCard';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

const defaultProps = {
	expiresIn: '2592000',
	groupId: '23'
};

const DefaultComponent = props => (
	<GenerateTokenCard {...defaultProps} {...props} />
);

describe('GenerateTokenCard', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		jest.runAllTimers();

		expect(container).toBeInTheDocument();
	});
	it('should render a select for the user to chose the expiration date', () => {
		const {container} = render(<DefaultComponent />);

		const selectElement = container.querySelector('.select-root');

		const option30days = selectElement.querySelector(
			'option[value="2592000"]'
		);
		const option60Months = selectElement.querySelector(
			'option[value="15778800"]'
		);
		const option1Year = selectElement.querySelector(
			'option[value="31557600"]'
		);
		const optionIndefinite = selectElement.querySelector(
			'option[value="3155760000"]'
		);
		expect(option30days).toBeInTheDocument();
		expect(option60Months).toBeInTheDocument();
		expect(option1Year).toBeInTheDocument();
		expect(optionIndefinite).toBeInTheDocument();

		fireEvent.change(selectElement, {target: {value: '2592000'}});

		expect(selectElement.value).toBe('2592000');
	});
	it('should render a button to generate the token if there are no tokens', () => {
		const {getByTestId} = render(<DefaultComponent />);

		expect(getByTestId('generate-token-button')).toBeInTheDocument();
	});
});
