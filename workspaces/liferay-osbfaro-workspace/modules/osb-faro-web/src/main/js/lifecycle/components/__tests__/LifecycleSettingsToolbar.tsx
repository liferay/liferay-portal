import LifecycleSettingsToolbar from '../LifecycleSettingsToolbar';
import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

jest.unmock('react-dom');

const renderToolbar = (
	props: Partial<React.ComponentProps<typeof LifecycleSettingsToolbar>> = {}
) =>
	render(
		<MemoryRouter>
			<LifecycleSettingsToolbar
				backURL="/back"
				onCancel={jest.fn()}
				onCreate={jest.fn()}
				{...props}
			/>
		</MemoryRouter>
	);

describe('LifecycleSettingsToolbar', () => {
	it('enables the Create button by default', () => {
		renderToolbar();

		expect(screen.getByRole('button', {name: 'Create'})).toBeEnabled();
	});

	it('disables the Create button when createDisabled is set', () => {
		renderToolbar({createDisabled: true});

		expect(screen.getByRole('button', {name: 'Create'})).toBeDisabled();
	});

	it('calls onCreate when the enabled Create button is clicked', () => {
		const onCreate = jest.fn();

		renderToolbar({onCreate});

		fireEvent.click(screen.getByRole('button', {name: 'Create'}));

		expect(onCreate).toHaveBeenCalledTimes(1);
	});

	it('calls onCancel when the Cancel button is clicked', () => {
		const onCancel = jest.fn();

		renderToolbar({onCancel});

		fireEvent.click(screen.getByRole('button', {name: 'Cancel'}));

		expect(onCancel).toHaveBeenCalledTimes(1);
	});
});
