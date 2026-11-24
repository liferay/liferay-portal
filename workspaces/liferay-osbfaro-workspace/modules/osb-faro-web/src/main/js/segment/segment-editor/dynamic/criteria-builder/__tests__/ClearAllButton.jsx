import ClearAllButton from '../ClearAllButton';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';

describe('ClearAllButton', () => {
	afterEach(cleanup);

	it('should render the clear-all button', () => {
		render(<ClearAllButton onClear={() => {}} />);

		expect(
			screen.getByRole('button', {name: /clear all/i})
		).toBeInTheDocument();
	});

	it('should not show the confirmation modal until the button is clicked', () => {
		render(<ClearAllButton onClear={() => {}} />);

		expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
	});

	it('should open the confirmation modal on click', () => {
		render(<ClearAllButton onClear={() => {}} />);

		fireEvent.click(screen.getByRole('button', {name: /clear all/i}));

		expect(screen.getByRole('dialog')).toBeInTheDocument();
	});

	it('should not invoke onClear when only the trigger is clicked', () => {
		const onClear = jest.fn();

		render(<ClearAllButton onClear={onClear} />);

		fireEvent.click(screen.getByRole('button', {name: /clear all/i}));

		expect(onClear).not.toHaveBeenCalled();
	});
});
