/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetchMock from 'jest-fetch-mock';
import React from 'react';

import ScheduleLaunchModal from '../../src/main/resources/META-INF/resources/js/components/ScheduleLaunchModal';

const closeModal = jest.fn();
const onScheduled = jest.fn();

function renderScheduleLaunchModal() {
	render(
		<ScheduleLaunchModal
			closeModal={closeModal}
			launchName="Halloween Campaign"
			onScheduled={onScheduled}
			portletNamespace="_namespace_"
			scheduleLaunchURL="/schedule_launch"
			timeZoneId="Asia/Tokyo"
			timeZones={[
				{
					label: '(UTC -05:00) Eastern Standard Time',
					value: 'America/New_York',
				},
				{label: '(UTC) Coordinated Universal Time', value: 'UTC'},
				{label: '(UTC +05:45) Nepal Time', value: 'Asia/Katmandu'},
				{
					label: '(UTC +09:00) Japan Standard Time',
					value: 'Asia/Tokyo',
				},
			]}
		/>
	);
}

async function typeDateTime(dateTime: string) {
	await userEvent.type(screen.getByLabelText(/date-and-time/), dateTime);
}

describe('ScheduleLaunchModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('marks the date and time as required', () => {
		renderScheduleLaunchModal();

		expect(screen.getByLabelText(/date-and-time/)).toBeRequired();
		expect(screen.getByText('mandatory')).toBeInTheDocument();
	});

	it('enables Schedule only once a date and a time are set', async () => {
		renderScheduleLaunchModal();

		expect(screen.getByRole('button', {name: 'schedule'})).toBeDisabled();

		await typeDateTime('10/31/2030');

		expect(screen.getByRole('button', {name: 'schedule'})).toBeDisabled();

		await typeDateTime(' 09:00 AM');

		expect(screen.getByRole('button', {name: 'schedule'})).toBeEnabled();
	});

	it('shows the time in the user time zone only when another time zone is selected', async () => {
		renderScheduleLaunchModal();

		await typeDateTime('10/31/2030 09:00 AM');

		expect(screen.queryByText('your-local-time-x')).not.toBeInTheDocument();

		await userEvent.selectOptions(
			screen.getByLabelText('time-zone'),
			'America/New_York'
		);

		expect(
			screen.getByLabelText(/date-and-time/)
		).toHaveAccessibleDescription('your-local-time-x');
	});

	it('schedules the launch at the instant of the selected time zone', async () => {
		fetchMock.mockResponseOnce(JSON.stringify({redirect: '/launch'}));

		renderScheduleLaunchModal();

		await typeDateTime('10/31/2030 09:00 AM');

		await userEvent.selectOptions(
			screen.getByLabelText('time-zone'),
			'America/New_York'
		);

		await userEvent.click(screen.getByRole('button', {name: 'schedule'}));

		await waitFor(() =>
			expect(onScheduled).toHaveBeenCalledWith('/launch')
		);

		expect(closeModal).toHaveBeenCalled();

		const [url, {body}] = fetchMock.mock.calls[0] as [
			string,
			{body: FormData},
		];

		expect(url).toBe('/schedule_launch');
		expect(body.get('_namespace_publishTime')).toBe(
			String(Date.UTC(2030, 9, 31, 13))
		);
	});

	it('schedules the launch in the user time zone by default', async () => {
		fetchMock.mockResponseOnce(JSON.stringify({redirect: '/launch'}));

		renderScheduleLaunchModal();

		expect(screen.getByLabelText('time-zone')).toHaveValue('Asia/Tokyo');

		await typeDateTime('10/31/2030 09:00 AM');

		await userEvent.click(screen.getByRole('button', {name: 'schedule'}));

		await waitFor(() => expect(onScheduled).toHaveBeenCalled());

		const [, {body}] = fetchMock.mock.calls[0] as [
			string,
			{body: FormData},
		];

		expect(body.get('_namespace_publishTime')).toBe(
			String(Date.UTC(2030, 9, 31, 0))
		);
	});

	it('keeps the modal open and shows the error when scheduling fails', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				validationError: 'The publish time must be in the future.',
			})
		);

		renderScheduleLaunchModal();

		await typeDateTime('10/31/2030 09:00 AM');

		await userEvent.click(screen.getByRole('button', {name: 'schedule'}));

		expect(
			await screen.findByText('The publish time must be in the future.')
		).toBeInTheDocument();

		expect(screen.getByLabelText(/date-and-time/)).toHaveFocus();
		expect(closeModal).not.toHaveBeenCalled();
		expect(onScheduled).not.toHaveBeenCalled();
	});
});
