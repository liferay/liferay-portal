/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {ConfigurationContainer} from '../../components/ObjectDetails/ConfigurationContainer';

describe('The ConfigurationContainer component should', () => {
	beforeEach(() => {
		global.Liferay = {
			FeatureFlags: {'LPD-17564': true},
			Language: {
				get: jest.fn((key) => key),
			},
		} as any;
	});

	describe('render object entry schedule toggle', () => {
		const scheduleToggleLabel =
			'allow-users-to-schedule-a-display-expiration-and-review-date-for-entries';

		const configurationContainer = (enableObjectEntrySchedule: boolean) =>
			render(
				<ConfigurationContainer
					hasUpdateObjectDefinitionPermission
					isRootDescendantNode={false}
					setValues={jest.fn()}
					values={{enableObjectEntrySchedule}}
				/>
			);

		it('unchecked when enableObjectEntrySchedule is false', () => {
			configurationContainer(false);
			expect(
				screen.getByRole('switch', {name: scheduleToggleLabel})
			).not.toBeChecked();
		});

		it('checked when enableObjectEntrySchedule is true', () => {
			configurationContainer(true);
			expect(
				screen.getByRole('switch', {name: scheduleToggleLabel})
			).toBeChecked();
		});
	});
});
