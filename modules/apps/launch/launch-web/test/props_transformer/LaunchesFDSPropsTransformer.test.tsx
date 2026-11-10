/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {screen} from '@testing-library/react';

import {Launch} from '../../src/main/resources/META-INF/resources/js/api/launches';
import LaunchesFDSPropsTransformer from '../../src/main/resources/META-INF/resources/js/props_transformer/LaunchesFDSPropsTransformer';

const IN_PROGRESS_LAUNCH: Launch = {
	id: 42,
	name: 'Halloween Campaign',
	status: {code: 2},
};

const SCHEDULED_LAUNCH: Launch = {
	...IN_PROGRESS_LAUNCH,
	scheduledDate: '2030-10-31T13:00:00Z',
	status: {code: 7},
};

const TIME_ZONES = [
	{label: '(UTC +09:00) Japan Standard Time', value: 'Asia/Tokyo'},
];

function transformProps() {
	return LaunchesFDSPropsTransformer({
		additionalProps: {
			portletNamespace: '_namespace_',
			scheduleLaunchURL: 'http://localhost/schedule?launchSetId={id}',
			timeZoneId: 'Asia/Tokyo',
			timeZones: TIME_ZONES,
			unscheduleLaunchURL: 'http://localhost/unschedule?launchSetId={id}',
			viewLaunchURL: 'http://localhost/view?launchSetId={id}',
		},
		itemsActions: [{data: {id: 'schedule'}}, {data: {id: 'unschedule'}}],
	});
}

function getScheduleAction() {
	return transformProps().itemsActions?.find(
		(action) => action.data?.id === 'schedule'
	) as {isVisible: (launch: Launch) => boolean};
}

describe('LaunchesFDSPropsTransformer', () => {
	beforeEach(() => {
		(Liferay.on as jest.Mock).mockReturnValue({detach: jest.fn()});
	});

	afterEach(() => {
		(Liferay.on as jest.Mock).mockReturnValue(undefined);
	});

	describe('schedule action', () => {
		it('is visible only for in progress launches', () => {
			const scheduleAction = getScheduleAction();

			expect(scheduleAction.isVisible(IN_PROGRESS_LAUNCH)).toBe(true);
			expect(scheduleAction.isVisible(SCHEDULED_LAUNCH)).toBe(false);
			expect(
				scheduleAction.isVisible({
					...IN_PROGRESS_LAUNCH,
					scheduledDate: '2030-10-31T13:00:00Z',
				})
			).toBe(false);
			expect(
				scheduleAction.isVisible({
					...IN_PROGRESS_LAUNCH,
					status: {code: 0},
				})
			).toBe(false);
		});

		it('opens the schedule modal for the launch', async () => {
			transformProps().onActionDropdownItemClick({
				action: {data: {id: 'schedule'}},
				itemData: IN_PROGRESS_LAUNCH,
				loadData: jest.fn(),
			});

			expect(await screen.findByText('schedule-x')).toBeInTheDocument();
			expect(screen.getByLabelText('time-zone')).toHaveValue(
				'Asia/Tokyo'
			);
		});
	});
});
