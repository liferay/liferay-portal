/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {CheckboxParameter} from '../../components/ObjectAction/tabs/ActionContainer/CheckboxParameter';

describe('The CheckboxParameter component should', () => {
	it('return the checked value', () => {
		render(
			<CheckboxParameter
				checked={false}
				disabled={false}
				label="also relate entries"
				onChange={(checked) => {
					expect(checked).toStrictEqual(true);
				}}
				title="Automatically relate object entries involved in the action"
			/>
		);

		userEvent.click(screen.getByRole('checkbox'));
	});

	it('return the unchecked value', () => {
		render(
			<CheckboxParameter
				checked={true}
				disabled={false}
				label="also relate entries"
				onChange={(checked) => {
					expect(checked).toStrictEqual(false);
				}}
				title="Automatically relate object entries involved in the action"
			/>
		);

		userEvent.click(screen.getByRole('checkbox'));
	});

	it('render the checkbox checked', () => {
		render(
			<CheckboxParameter
				checked={true}
				disabled={false}
				label="also relate entries"
				onChange={(checked) => {
					expect(checked).toStrictEqual(true);
				}}
				title="Automatically relate object entries involved in the action"
			/>
		);

		expect(screen.getByRole('checkbox')).toBeChecked();
	});

	it('render the checkbox unchecked', () => {
		render(
			<CheckboxParameter
				checked={false}
				disabled={false}
				label="also relate entries"
				onChange={(checked) => {
					expect(checked).toStrictEqual(true);
				}}
				title="Automatically relate object entries involved in the action"
			/>
		);

		expect(screen.getByRole('checkbox')).not.toBeChecked();
	});

	it('render the disabled checkbox', () => {
		render(
			<CheckboxParameter
				checked={true}
				disabled={true}
				label="also relate entries"
				onChange={(checked) => {
					expect(checked).toStrictEqual(true);
				}}
				title="Automatically relate object entries involved in the action"
			/>
		);

		expect(screen.getByRole('checkbox')).toBeDisabled();
	});

	it('render the label', () => {
		render(
			<CheckboxParameter
				checked={false}
				disabled={false}
				label="also relate entries"
				onChange={(checked) => {
					expect(checked).toStrictEqual(true);
				}}
				title="Automatically relate object entries involved in the action"
			/>
		);

		expect(screen.getByText('also relate entries')).toBeInTheDocument();
	});

	it('render the title', () => {
		render(
			<CheckboxParameter
				checked={false}
				disabled={false}
				label="also relate entries"
				onChange={(checked) => {
					expect(checked).toStrictEqual(true);
				}}
				title="Automatically relate object entries involved in the action"
			/>
		);

		expect(
			screen.getByTitle(
				'Automatically relate object entries involved in the action'
			)
		).toBeInTheDocument();
	});
});
