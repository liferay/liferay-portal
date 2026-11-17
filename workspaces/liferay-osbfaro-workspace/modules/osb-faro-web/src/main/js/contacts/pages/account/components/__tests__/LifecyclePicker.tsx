import LifecyclePicker from '../LifecyclePicker';
import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {ILifecycleDetail} from 'shared/api/lifecycle';

jest.unmock('react-dom');

const lifecycles = [
	{id: 'al-1', name: 'Customer'},
	{id: 'al-2', name: 'Partner'},
] as ILifecycleDetail[];

describe('LifecyclePicker', () => {
	afterEach(cleanup);

	it('should render the selected lifecycle', () => {
		render(
			<LifecyclePicker
				lifecycles={lifecycles}
				onSelectionChange={jest.fn()}
				selectedLifecycleId="al-2"
			/>
		);

		expect(
			screen.getByRole('combobox', {name: 'Lifecycle'})
		).toHaveTextContent('Partner');
	});

	it('should list every lifecycle and report the one picked', () => {
		const onSelectionChange = jest.fn();

		render(
			<LifecyclePicker
				lifecycles={lifecycles}
				onSelectionChange={onSelectionChange}
				selectedLifecycleId="al-1"
			/>
		);

		fireEvent.click(screen.getByRole('combobox', {name: 'Lifecycle'}));

		expect(
			screen.getAllByRole('option').map(({textContent}) => textContent)
		).toEqual(['Customer', 'Partner']);

		fireEvent.click(screen.getByRole('option', {name: 'Partner'}));

		expect(onSelectionChange).toHaveBeenCalledWith('al-2');
	});

	it('should be disabled when there are no lifecycles', () => {
		render(
			<LifecyclePicker lifecycles={[]} onSelectionChange={jest.fn()} />
		);

		expect(
			screen.getByRole('combobox', {name: 'Lifecycle'})
		).toBeDisabled();
	});
});
