/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTimePicker from '..';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

const spritemap = 'icons.svg';

function TimePickerWithState(props: any) {
	const [state, setState] = React.useState({
		ampm: '--',
		hours: '--',
		minutes: '--',
	});

	return (
		<ClayTimePicker
			{...props}
			onInputChange={setState}
			spritemap={spritemap}
			values={state}
		/>
	);
}

describe('BasicRendering', () => {
	afterEach(cleanup);

	it('renders by default', () => {
		const {container} = render(<TimePickerWithState />);

		expect(container.innerHTML).toMatchSnapshot();
	});

	it('renders time picker with disabled', () => {
		const {container} = render(<TimePickerWithState disabled />);

		expect(container.innerHTML).toMatchSnapshot();
	});

	it('renders time picker with icon', () => {
		const {container} = render(<TimePickerWithState icon />);

		expect(container.innerHTML).toMatchSnapshot();
	});

	it('renders time picker with timezone', () => {
		const {container} = render(<TimePickerWithState timezon="GMT+01:00" />);

		expect(container.innerHTML).toMatchSnapshot();
	});

	it('renders time picker with 12 hours', () => {
		const {container} = render(<TimePickerWithState use12Hours />);

		expect(container.innerHTML).toMatchSnapshot();
	});

	it('renders time picker with a hidden input when the name attribute is provided', () => {
		const {container} = render(<TimePickerWithState name="time-picker" />);

		expect(container.innerHTML).toMatchSnapshot();
	});
});

describe('IncrementalInteractions', () => {
	afterEach(cleanup);

	it('pressing arrow right should move focus to minutes input', async () => {
		const {getByTestId} = render(<TimePickerWithState />);

		const hoursEl = getByTestId('hours');
		const minutesEl = getByTestId('minutes');

		hoursEl.focus();
		fireEvent.keyDown(hoursEl, {key: 'ArrowRight'});

		expect(document.activeElement).toBe(minutesEl);
	});

	it('pressing arrow left should move focus to hour input', () => {
		const {getByTestId} = render(<TimePickerWithState />);

		const hoursEl = getByTestId('hours');
		const minutesEl = getByTestId('minutes');

		minutesEl.focus();
		fireEvent.keyDown(minutesEl, {key: 'ArrowLeft'});

		expect(document.activeElement).toBe(hoursEl);
	});

	describe('with actions', () => {
		it('shows the spin action when the mouse is inside the form', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const containerSpinEl = getByTestId('containerSpin');

			expect(containerSpinEl.style.opacity).toBe('0');

			fireEvent.mouseEnter(formControlEl, {});

			expect(containerSpinEl.style.opacity).toBe('1');
		});

		it('hides spin action when mouse is out of form', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const containerSpinEl = getByTestId('containerSpin');

			fireEvent.mouseEnter(formControlEl, {});

			expect(containerSpinEl.style.opacity).toBe('1');

			fireEvent.mouseLeave(formControlEl, {});

			expect(containerSpinEl.style.opacity).toBe('0');
		});

		it('keep the spin action visible when focus on the inputs', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const containerSpinEl = getByTestId('containerSpin');
			const hoursEl = getByTestId('hours');

			fireEvent.mouseEnter(formControlEl, {});

			expect(containerSpinEl.style.opacity).toBe('1');

			fireEvent.focus(hoursEl, {});
			fireEvent.mouseLeave(formControlEl, {});

			expect(containerSpinEl.style.opacity).toBe('1');
		});

		it('keep the reset action visible when focus on the inputs and have value', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const hoursEl = getByTestId('hours');
			const containerResetEl = getByTestId('containerReset');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(hoursEl, {});

			expect(containerResetEl.style.opacity).toBe('0');
			expect(containerResetEl.style.pointerEvents).toBe('none');

			fireEvent.keyDown(hoursEl, {key: '1'});

			expect(containerResetEl.style.opacity).toBe('1');
			expect(containerResetEl.style.pointerEvents).toBe('auto');
		});

		it('sets the values to the default when clicking on the reset action', () => {
			const {getByTestId} = render(<TimePickerWithState use12Hours />);

			const formControlEl = getByTestId('formControl');
			const hoursEl = getByTestId('hours') as HTMLInputElement;
			const minutesEl = getByTestId('minutes') as HTMLInputElement;
			const ampmEl = getByTestId('ampm') as HTMLInputElement;
			const defaultValues = '--';
			const containerResetEl = getByTestId('containerReset');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(hoursEl, {});
			fireEvent.keyDown(hoursEl, {key: '1'});
			fireEvent.keyDown(minutesEl, {key: '1'});
			fireEvent.keyDown(ampmEl, {key: 'ArrowUp'});

			expect(hoursEl.value).toBe('01');
			expect(minutesEl.value).toBe('01');
			expect(ampmEl.value).toBe('PM');

			fireEvent.click(
				containerResetEl.querySelector('button') as HTMLButtonElement,
				{}
			);

			expect(hoursEl.value).toBe(defaultValues);
			expect(minutesEl.value).toBe(defaultValues);
			expect(ampmEl.value).toBe(defaultValues);
		});

		it('increment the value of the hour input with focus by clicking on the increment spin action', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const hoursEl = getByTestId('hours') as HTMLInputElement;
			const spinIncEl = getByTestId('spinInc');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(hoursEl, {});
			fireEvent.click(spinIncEl, {});

			expect(hoursEl.value).toBe('00');
		});

		it('increment the value of the minutes input with focus by clicking on the increment spin action', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const minutesEl = getByTestId('minutes') as HTMLInputElement;
			const spinIncEl = getByTestId('spinInc');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(minutesEl, {});
			fireEvent.click(spinIncEl, {});

			expect(minutesEl.value).toBe('00');
		});

		it('increment the value of the am/pm input with focus by clicking on the increment spin action', () => {
			const {getByTestId} = render(<TimePickerWithState use12Hours />);

			const formControlEl = getByTestId('formControl');
			const ampmEl = getByTestId('ampm') as HTMLInputElement;
			const spinIncEl = getByTestId('spinInc');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(ampmEl, {});
			fireEvent.click(spinIncEl, {});

			expect(ampmEl.value).toBe('PM');
		});

		it('decrement the value of the hour input with focus by clicking on the decrement spin action', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const hoursEl = getByTestId('hours') as HTMLInputElement;
			const spinDecEl = getByTestId('spinDec');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(hoursEl, {});
			fireEvent.click(spinDecEl, {});

			expect(hoursEl.value).toBe('23');
		});

		it('decrement the value of the minutes input with focus by clicking on the decrement spin action', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const minutesEl = getByTestId('minutes') as HTMLInputElement;
			const spinDecEl = getByTestId('spinDec');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(minutesEl, {});
			fireEvent.click(spinDecEl, {});

			expect(minutesEl.value).toBe('59');
		});

		it('decrement the value of the am/pm input with focus by clicking on the decrement spin action', () => {
			const {getByTestId} = render(<TimePickerWithState use12Hours />);

			const formControlEl = getByTestId('formControl');
			const ampmEl = getByTestId('ampm') as HTMLInputElement;
			const spinDecEl = getByTestId('spinDec');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.focus(ampmEl, {});
			fireEvent.click(spinDecEl, {});

			expect(ampmEl.value).toBe('AM');
		});

		it('increments or decrements the value of the input hour when there is no focused input when clicking on the spin action', () => {
			const {getByTestId} = render(<TimePickerWithState />);

			const formControlEl = getByTestId('formControl');
			const hoursEl = getByTestId('hours') as HTMLInputElement;
			const spinIncEl = getByTestId('spinInc');
			const spinDecEl = getByTestId('spinDec');

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.click(spinIncEl, {});
			fireEvent.click(spinDecEl, {});

			expect(hoursEl.value).toBe('23');
		});
	});

	describe('with values increment', () => {
		it('pressing backspace on the hour input should set the default value', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const hoursEl = getByTestId('hours') as HTMLInputElement;

			fireEvent.keyDown(hoursEl, {key: '1'});

			expect(hoursEl.value).toBe('01');

			fireEvent.keyDown(hoursEl, {key: 'Backspace'});

			expect(hoursEl.value).toBe('--');
		});

		it('pressing backspace on the minutes input should set the default value', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const minutesEl = getByTestId('minutes') as HTMLInputElement;

			fireEvent.keyDown(minutesEl, {key: '1'});

			expect(minutesEl.value).toBe('01');

			fireEvent.keyDown(minutesEl, {key: 'Backspace'});

			expect(minutesEl.value).toBe('--');
		});

		it('pressing backspace on the am/pm input should set the default value', () => {
			const {getByTestId} = render(<TimePickerWithState use12Hours />);
			const ampmEl = getByTestId('ampm') as HTMLInputElement;
			const spinDecEl = getByTestId('spinDec');

			fireEvent.focus(ampmEl, {});
			fireEvent.click(spinDecEl, {});

			expect(ampmEl.value).toBe('AM');

			fireEvent.keyDown(ampmEl, {key: 'Backspace'});

			expect(ampmEl.value).toBe('--');
		});

		it('pressing arrow up on the hour input should increase value', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const hoursEl = getByTestId('hours') as HTMLInputElement;

			fireEvent.keyDown(hoursEl, {key: 'ArrowUp'});

			expect(hoursEl.value).toBe('00');
		});

		it('pressing arrow up on the minutes input should increase value', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const minutesEl = getByTestId('minutes') as HTMLInputElement;

			fireEvent.keyDown(minutesEl, {key: 'ArrowUp'});

			expect(minutesEl.value).toBe('00');
		});

		it('pressing arrow down on the hour input should decrement value', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const hoursEl = getByTestId('hours') as HTMLInputElement;

			fireEvent.keyDown(hoursEl, {key: 'ArrowDown'});

			expect(hoursEl.value).toBe('23');
		});

		it('pressing arrow down on the minutes input should decrement value', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const minutesEl = getByTestId('minutes') as HTMLInputElement;

			fireEvent.keyDown(minutesEl, {key: 'ArrowDown'});

			expect(minutesEl.value).toBe('59');
		});

		it('pressing arrow up on the am/pm input should set the value for PM', () => {
			const {getByTestId} = render(<TimePickerWithState use12Hours />);
			const ampmEl = getByTestId('ampm') as HTMLInputElement;

			fireEvent.keyDown(ampmEl, {key: 'ArrowUp'});

			expect(ampmEl.value).toBe('PM');
		});

		it('pressing arrow down on the am/pm input should set the value for AM', () => {
			const {getByTestId} = render(<TimePickerWithState use12Hours />);
			const formControlEl = getByTestId('formControl');
			const ampmEl = getByTestId('ampm') as HTMLInputElement;

			fireEvent.mouseEnter(formControlEl, {});
			fireEvent.keyDown(ampmEl, {key: 'ArrowDown'});

			expect(ampmEl.value).toBe('AM');
		});

		it('only allows numbers to be entered in the input hour', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const hoursEl = getByTestId('hours') as HTMLInputElement;

			['A', '!', '@', '(', '^', '9', 'p'].forEach((value) => {
				fireEvent.keyDown(hoursEl, {key: value});
			});

			expect(hoursEl.value).toBe('09');
		});

		it('only allows numbers to be entered in the input minutes', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const minutesEl = getByTestId('minutes') as HTMLInputElement;

			['A', '!', '@', '(', '^', '9', 'p'].forEach((value) => {
				fireEvent.keyDown(minutesEl, {key: value});
			});

			expect(minutesEl.value).toBe('09');
		});

		it('returns to the maximum value when it reaches the minimum value when pressing in the arrow down', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const hoursEl = getByTestId('hours') as HTMLInputElement;
			const minutesEl = getByTestId('minutes') as HTMLInputElement;

			fireEvent.keyDown(hoursEl, {key: '0'});
			fireEvent.keyDown(minutesEl, {key: '0'});

			expect(hoursEl.value).toBe('00');
			expect(minutesEl.value).toBe('00');

			fireEvent.keyDown(hoursEl, {key: 'ArrowDown'});
			fireEvent.keyDown(minutesEl, {key: 'ArrowDown'});

			expect(hoursEl.value).toBe('23');
			expect(minutesEl.value).toBe('59');
		});

		it('returns to the minimum value when it reaches the maximum value when pressing in the arrow up', () => {
			const {getByTestId} = render(<TimePickerWithState />);
			const hoursEl = getByTestId('hours') as HTMLInputElement;
			const minutesEl = getByTestId('minutes') as HTMLInputElement;

			fireEvent.keyDown(hoursEl, {key: '23'});
			fireEvent.keyDown(minutesEl, {key: '59'});

			expect(hoursEl.value).toBe('23');
			expect(minutesEl.value).toBe('59');

			fireEvent.keyDown(hoursEl, {key: 'ArrowUp'});
			fireEvent.keyDown(minutesEl, {key: 'ArrowUp'});

			expect(hoursEl.value).toBe('00');
			expect(minutesEl.value).toBe('00');
		});
	});
});
