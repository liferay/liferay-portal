/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ScheduleContainer from '../../../object_entries/object_entry/ScheduleContainer';

import type {ScheduleProperties} from '../../../object_entries/object_entry/ScheduleContainer';

function renderScheduleContainer(
	scheduledProperties: ScheduleProperties = {
		displayDate: {
			value: '',
		},
		expirationDate: {
			checked: false,
			value: '',
		},
		reviewDate: {
			checked: false,
			value: '',
		},
	},
	portletNamespace = ''
) {
	return render(
		<>
			<ScheduleContainer
				portletNamespace={portletNamespace}
				scheduleProperties={scheduledProperties}
				submitRef=""
			/>
			<button data-testid="outside-click-target">
				Outside Click Target
			</button>
		</>
	);
}

describe('ScheduleContainer component', () => {
	beforeAll(() => {
		global.Liferay = {
			...global.Liferay,
			FeatureFlags: {
				...global.Liferay?.FeatureFlags,
				'LPD-17564': true,
			},
			ThemeDisplay: {
				...global.Liferay?.ThemeDisplay,
				getBCP47LanguageId: jest.fn(),
				getTimeZone: jest.fn(),
			},
		};
	});

	it('expiration and reviewDate are disabled when checked is true', async () => {
		const scheduledProperties: ScheduleProperties = {
			displayDate: {
				value: '',
			},
			expirationDate: {
				checked: true,
				value: '',
			},
			reviewDate: {
				checked: true,
				value: '',
			},
		};

		const {container} = renderScheduleContainer(scheduledProperties);

		for (const scheduledProperty in scheduledProperties) {
			if (
				scheduledProperty.includes('expiration') ||
				scheduledProperty.includes('review')
			) {
				const dateInput = container.querySelector(
					`input[id$="${scheduledProperty}"]`
				);

				expect(dateInput).toBeDisabled();
			}
		}

		const checkbox = screen.getAllByRole('checkbox');

		checkbox.forEach((checkbox) => {
			expect(checkbox).toBeChecked();
		});
	});

	it('displays a description message for the fields inside it', async () => {
		renderScheduleContainer();

		expect(
			screen.getByText(
				'set-expiration-and-review-dates-for-the-object-entry'
			)
		).toBeInTheDocument();
	});

	it('displays error for past expiration date', async () => {
		const {container} = renderScheduleContainer({
			displayDate: {
				value: '',
			},
			expirationDate: {
				checked: false,
				value: '05/13/2020 02:38 PM',
			},
			reviewDate: {
				checked: true,
				value: '',
			},
		});

		const expireDateInput = container.querySelector(
			'input[id$="expirationDate"]'
		) as HTMLElement;

		await userEvent.click(expireDateInput);

		await userEvent.click(screen.getByTestId('outside-click-target'));

		await waitFor(() =>
			expect(
				screen.getByText('the-date-entered-is-in-the-past')
			).toBeInTheDocument()
		);
	});

	it('displays panel with default displayType', async () => {
		const {container} = renderScheduleContainer();

		expect(
			container.querySelector('.panel.panel-default')
		).toBeInTheDocument();
	});

	it('shows required error on blur when no value is provided', async () => {
		const {container} = renderScheduleContainer();

		const scheduledProperties = ['expirationDate', 'reviewDate'];

		for (const scheduledProperty of scheduledProperties) {
			const dateInput = container.querySelector(
				`input[id$="${scheduledProperty}"]`
			) as HTMLElement;

			await userEvent.click(dateInput);
		}

		await userEvent.click(screen.getByTestId('outside-click-target'));

		await waitFor(() =>
			expect(screen.getAllByText('this-field-is-required').length).toBe(
				Object.keys(scheduledProperties).length
			)
		);
	});

	it('shows required error on change when no value is provided', async () => {
		const scheduledProperties: ScheduleProperties = {
			displayDate: {
				value: '',
			},
			expirationDate: {
				checked: false,
				value: '05/13/2025 02:38 PM',
			},
			reviewDate: {
				checked: false,
				value: '05/13/2025 02:38 PM',
			},
		};
		const {container} = renderScheduleContainer(scheduledProperties);

		for (const scheduledProperty in scheduledProperties) {
			if (
				scheduledProperty.includes('expiration') ||
				scheduledProperty.includes('review')
			) {
				const dateInput = container.querySelector(
					`input[id$="${scheduledProperty}"]`
				) as HTMLElement;

				await userEvent.click(dateInput);

				await userEvent.type(dateInput, '{selectall}{backspace}');

				await waitFor(() =>
					expect(
						screen.getByText('this-field-is-required')
					).toBeInTheDocument()
				);

				await userEvent.type(dateInput, '05/13/2025 02:38 PM');

				expect(
					screen.queryByText('this-field-is-required')
				).not.toBeInTheDocument();
			}
		}
	});
});
