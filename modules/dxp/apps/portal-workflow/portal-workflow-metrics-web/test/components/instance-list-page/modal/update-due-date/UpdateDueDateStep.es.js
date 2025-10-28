/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import {AppContext} from '../../../../../src/main/resources/META-INF/resources/js/components/AppContext.es';
import {ModalContext} from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
import UpdateDueDateStep from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/update-due-date/UpdateDueDateStep.es';

const MockModalContext = ({children}) => {
	const [updateDueDate, setUpdateDueDate] = useState('');

	return (
		<ModalContext.Provider value={{setUpdateDueDate, updateDueDate}}>
			{children}
		</ModalContext.Provider>
	);
};

describe('The UpdateDueDateStep component should be rendered with TimePickerInput with AM/PM format', () => {
	afterEach(cleanup);

	test('Render with error state and select any option', () => {
		const {getAllByRole, getByPlaceholderText} = render(
			<AppContext.Provider value={{isAmPm: true, timeFormat: 'H:mm a'}}>
				<MockModalContext>
					<UpdateDueDateStep />
				</MockModalContext>
			</AppContext.Provider>
		);

		const timeInput = getByPlaceholderText('HH:mm am/pm');

		fireEvent.focus(timeInput);

		const items = getAllByRole('listitem');

		items.forEach((item) => {
			expect(item.innerHTML).toMatch(/[0-9]{1,2}:[0-9]{2}\s(AM|PM)/);
		});

		fireEvent.mouseDown(items[0]);

		expect(timeInput.parentNode).not.toHaveClass('has-error');
		expect(timeInput.value).toBe('12:00 AM');

		fireEvent.focus(timeInput);
		fireEvent.mouseDown(items[1]);

		expect(timeInput.value).toBe('12:30 AM');

		fireEvent.change(timeInput, {target: {value: '14:00'}});

		expect(timeInput.parentNode).toHaveClass('has-error');

		fireEvent.change(timeInput, {target: {value: '2:00 PM'}});

		expect(timeInput.parentNode).not.toHaveClass('has-error');
	});
});

describe('The UpdateDueDateStep component should be rendered with TimePickerInput with another time format', () => {
	afterEach(cleanup);

	test('Render with error state and select any option', () => {
		const {getAllByRole, getByPlaceholderText} = render(
			<AppContext.Provider value={{isAmPm: false, timeFormat: 'HH:mm'}}>
				<MockModalContext>
					<UpdateDueDateStep />
				</MockModalContext>
			</AppContext.Provider>
		);

		const timeInput = getByPlaceholderText('HH:mm');

		fireEvent.focus(timeInput);

		const items = getAllByRole('listitem');

		items.forEach((item) => {
			expect(item.innerHTML).toMatch(/[0-9]{1,2}:[0-9]{2}/);
		});

		fireEvent.mouseDown(items[0]);

		expect(timeInput.parentNode).not.toHaveClass('has-error');
		expect(timeInput.value).toBe('00:00');

		fireEvent.focus(timeInput);
		fireEvent.mouseDown(items[1]);

		expect(timeInput.value).toBe('00:30');

		fireEvent.change(timeInput, {target: {value: '12:00 AM'}});

		expect(timeInput.parentNode).toHaveClass('has-error');

		fireEvent.change(timeInput, {target: {value: '14:00'}});

		expect(timeInput.parentNode).not.toHaveClass('has-error');

		fireEvent.focus(timeInput);

		let timePickerPopover = document.querySelector('.clay-popover-bottom');

		expect(timePickerPopover).toBeTruthy();

		fireEvent.blur(timeInput);

		timePickerPopover = document.querySelector('.clay-popover-bottom');

		expect(timePickerPopover).toBeFalsy();
	});
});
