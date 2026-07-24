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
				onSubmit={jest.fn()}
				submitLabel="Save"
				{...props}
			/>
		</MemoryRouter>
	);

describe('LifecycleSettingsToolbar', () => {
	it('renders the submit button with the given label', () => {
		renderToolbar({submitLabel: 'Create'});

		expect(screen.getByRole('button', {name: 'Create'})).toBeEnabled();
	});

	it('disables the submit button when submitDisabled is set', () => {
		renderToolbar({submitDisabled: true});

		expect(screen.getByRole('button', {name: 'Save'})).toBeDisabled();
	});

	it('calls onSubmit when the enabled submit button is clicked', () => {
		const onSubmit = jest.fn();

		renderToolbar({onSubmit});

		fireEvent.click(screen.getByRole('button', {name: 'Save'}));

		expect(onSubmit).toHaveBeenCalledTimes(1);
	});

	it('calls onCancel when the Cancel button is clicked', () => {
		const onCancel = jest.fn();

		renderToolbar({onCancel});

		fireEvent.click(screen.getByRole('button', {name: 'Cancel'}));

		expect(onCancel).toHaveBeenCalledTimes(1);
	});
});
