import React from 'react';
import Title from '../Title';
import {fireEvent, render, screen} from '@testing-library/react';

const renderTitle = (props: Partial<React.ComponentProps<typeof Title>> = {}) =>
	render(
		<Title
			id="name"
			label="Title"
			onChange={jest.fn()}
			placeholder="New Analysis"
			value=""
			{...props}
		/>
	);

describe('BaseEditPage.Title', () => {
	it('renders a labelled inline input with the placeholder', () => {
		renderTitle();

		const input = screen.getByLabelText(/title/i);

		expect(input).toHaveAttribute('placeholder', 'New Analysis');
	});

	it('shows the controlled value and reports changes', () => {
		const onChange = jest.fn();

		renderTitle({onChange, value: 'Visits'});

		const input = screen.getByLabelText(/title/i);

		expect(input).toHaveValue('Visits');

		fireEvent.change(input, {target: {value: 'Visits per page'}});

		expect(onChange).toHaveBeenCalledTimes(1);
	});

	it('marks the input as required', () => {
		renderTitle({required: true});

		expect(screen.getByLabelText(/title/i)).toBeRequired();
	});

	it('shows the error message', () => {
		renderTitle({errorMessage: 'Name cannot be blank.'});

		expect(screen.getByRole('alert')).toHaveTextContent(
			/name cannot be blank\./i
		);
		expect(screen.getByLabelText(/title/i)).toHaveAttribute(
			'aria-describedby',
			'nameError'
		);
	});

	it('shows no error message when there is none', () => {
		renderTitle();

		expect(screen.queryByRole('alert')).not.toBeInTheDocument();
	});

	it('confirms the title on Enter without submitting the form', () => {
		renderTitle();

		const input = screen.getByLabelText(/title/i);

		input.focus();

		expect(fireEvent.keyDown(input, {key: 'Enter'})).toBe(false);
		expect(input).not.toHaveFocus();
	});

	it('leaves the other keys alone', () => {
		renderTitle();

		const input = screen.getByLabelText(/title/i);

		input.focus();

		expect(fireEvent.keyDown(input, {key: 'a'})).toBe(true);
		expect(input).toHaveFocus();
	});
});
