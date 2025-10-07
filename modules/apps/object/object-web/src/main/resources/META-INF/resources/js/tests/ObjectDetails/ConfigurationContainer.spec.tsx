/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import {renderHook} from '@testing-library/react-hooks';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ConfigurationContainer} from '../../components/ObjectDetails/ConfigurationContainer';
import {useObjectDetailsForm} from '../../components/ObjectDetails/useObjectDetailsForm';

jest.mock('frontend-js-web', () => ({
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

const ALL_TOGGLES = [
	'allow-users-to-save-entries-as-draft',
	'allow-users-to-schedule-a-display-expiration-and-review-date-for-entries',
	'enable-categorization-of-object-entries',
	'enable-comments-in-page-builder',
	'enable-entry-history-in-audit-framework',
	'enable-indexed-search',
	'enable-mapping-in-form-container',
	'show-widget-in-page-builder',
];

const TOGGLES_DISABLED_WHITH_NO_UPDATE_PERMISSION = ALL_TOGGLES.filter(
	(label) => label !== 'enable-entry-history-in-audit-framework'
);

const TOGGLES_DISABLED_FOR_SYSTEM_UNMODIFIABLE = ALL_TOGGLES.filter(
	(label) => label !== 'enable-categorization-of-object-entries'
);

describe('The ConfigurationContainer', () => {
	beforeEach(() => {
		global.Liferay = {
			FeatureFlags: {'LPD-17564': true},
			Language: {
				get: jest.fn((key) => key),
			},
		} as any;
	});

	const initialValues: Partial<ObjectDefinition> = {
		active: true,
		defaultLanguageId: 'en_US',
		externalReferenceCode: 'erc',
		id: 1,
		label: {en_US: 'label'},
		name: 'name',
		pluralLabel: {en_US: 'pluralLabel'},
	};

	const renderConfigurationContainer = (
		customValues: Partial<ObjectDefinition> = {},
		props: Partial<React.ComponentProps<typeof ConfigurationContainer>> = {}
	) => {
		const {result} = renderHook(useObjectDetailsForm, {
			initialProps: {
				initialValues: {...initialValues, ...customValues},
				onSubmit: () => {},
			},
		});

		render(
			<ConfigurationContainer
				hasUpdateObjectDefinitionPermission
				isEnableObjectEntrySchedule={false}
				setValues={result.current.setValues}
				values={result.current.values}
				{...props}
			/>
		);
	};

	it('allows enableIndexSearch toggle to be checked and unchecked when definition is not active', async () => {
		renderConfigurationContainer({active: false});

		const toggle = screen.getByRole('switch', {
			name: 'enable-indexed-search',
		});

		await userEvent.click(toggle);
		expect(toggle).toBeChecked();

		await userEvent.click(toggle);
		expect(toggle).not.toBeChecked();
	});

	it('disables enableIndexSearch toggle when definition is active', () => {
		renderConfigurationContainer();

		expect(
			screen.getByRole('switch', {name: 'enable-indexed-search'})
		).toBeDisabled();
	});

	it('disables enableObjectEntrySchedule toggle when definition is active and isEnableObjectEntrySchedule is true', () => {
		renderConfigurationContainer(initialValues, {
			isEnableObjectEntrySchedule: true,
		});

		const scheduleToggle = screen.getByRole('switch', {
			name: 'allow-users-to-schedule-a-display-expiration-and-review-date-for-entries',
		});

		expect(scheduleToggle).toBeDisabled();
	});

	it.each(ALL_TOGGLES.filter((label) => label !== 'enable-indexed-search'))(
		'"%s" toggle can be checked and unchecked correctly when the setting is active',
		async (label) => {
			renderConfigurationContainer();
			const toggle = screen.getByRole('switch', {name: label});

			await userEvent.click(toggle);
			expect(toggle).toBeChecked();

			await userEvent.click(toggle);
			expect(toggle).not.toBeChecked();
		}
	);

	it.each(TOGGLES_DISABLED_FOR_SYSTEM_UNMODIFIABLE)(
		'"%s" toggle is disabled when definition is system unmodifiable',
		async (label) => {
			renderConfigurationContainer({
				modifiable: false,
				system: true,
			});

			const toggle = screen.getByRole('switch', {name: label});

			expect(toggle).toBeDisabled();
			expect(toggle).not.toBeChecked();
		}
	);

	it.each(TOGGLES_DISABLED_WHITH_NO_UPDATE_PERMISSION)(
		'"%s" toggle is disabled when hasUpdateObjectDefinitionPermission is false',
		async (label) => {
			renderConfigurationContainer(initialValues, {
				hasUpdateObjectDefinitionPermission: false,
			});

			expect(screen.getByRole('switch', {name: label})).toBeDisabled();
		}
	);

	it.each(ALL_TOGGLES)(
		'"%s" toggle is disabled when isLinkedObjectDefinition is true',
		async (label) => {
			renderConfigurationContainer(initialValues, {
				isLinkedObjectDefinition: true,
			});

			expect(screen.getByRole('switch', {name: label})).toBeDisabled();
		}
	);
});
