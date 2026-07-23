/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
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
			FeatureFlags: {},
			Language: {
				get: jest.fn((key) => key),
			},
			ThemeDisplay: {
				getDefaultLanguageId: () => 'en_US',
				getLanguageId: () => 'en_US',
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
				isApproved={true}
				isEnableObjectEntrySchedule={false}
				setValues={result.current.setValues}
				values={result.current.values}
				{...props}
			/>
		);
	};

	it('allows enableIndexSearch toggle to be checked and unchecked when definition is not published', async () => {
		renderConfigurationContainer(initialValues, {
			isApproved: false,
		});

		const toggle = screen.getByRole('switch', {
			name: 'enable-indexed-search',
		});

		await userEvent.click(toggle);
		expect(toggle).toBeChecked();

		await userEvent.click(toggle);
		expect(toggle).not.toBeChecked();
	});

	it('disables enableIndexSearch toggle when definition is published', () => {
		renderConfigurationContainer();

		expect(
			screen.getByRole('switch', {name: 'enable-indexed-search'})
		).toBeDisabled();
	});

	it('disables enableIndexSearch toggle when definition is published and inactive', () => {
		renderConfigurationContainer({active: false});

		expect(
			screen.getByRole('switch', {name: 'enable-indexed-search'})
		).toBeDisabled();
	});

	it('disables enableObjectEntrySchedule toggle when definition is published and isEnableObjectEntrySchedule is true', () => {
		renderConfigurationContainer(initialValues, {
			isEnableObjectEntrySchedule: true,
		});

		const scheduleToggle = screen.getByRole('switch', {
			name: 'allow-users-to-schedule-a-display-expiration-and-review-date-for-entries',
		});

		expect(scheduleToggle).toBeDisabled();
	});

	it.each(ALL_TOGGLES.filter((label) => label !== 'enable-indexed-search'))(
		'"%s" toggle can be checked and unchecked correctly when definition is published',
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

	describe('allow-standalone-entries toggle', () => {
		it('appends a new setting entry when none exists', async () => {
			const setValues = jest.fn();

			render(
				<ConfigurationContainer
					hasUpdateObjectDefinitionPermission
					isApproved={true}
					isEnableObjectEntrySchedule={false}
					isRootDescendantNode={true}
					setValues={setValues}
					values={initialValues}
				/>
			);

			await userEvent.click(
				screen.getByRole('switch', {name: 'allow-standalone-entries'})
			);

			expect(setValues).toHaveBeenCalledWith({
				objectDefinitionSettings: [
					{name: 'allowStandaloneObjectEntry', value: 'false'},
				],
			});
		});

		it('is disabled when hasStandaloneEntries is true and the setting is "true"', () => {
			renderConfigurationContainer(
				{
					objectDefinitionSettings: [
						{
							name: 'allowStandaloneObjectEntry',
							value: 'true',
						},
					],
				},
				{hasStandaloneEntries: true, isRootDescendantNode: true}
			);

			expect(
				screen.getByRole('switch', {name: 'allow-standalone-entries'})
			).toBeDisabled();
		});

		it('is enabled when hasStandaloneEntries is false', () => {
			renderConfigurationContainer(initialValues, {
				hasStandaloneEntries: false,
				isRootDescendantNode: true,
			});

			expect(
				screen.getByRole('switch', {name: 'allow-standalone-entries'})
			).toBeEnabled();
		});

		it('is hidden when isRootDescendantNode is not set', () => {
			renderConfigurationContainer();

			expect(
				screen.queryByRole('switch', {name: 'allow-standalone-entries'})
			).toBeNull();
		});

		it('is visible and defaults to ON when isRootDescendantNode is true and the setting is absent', () => {
			renderConfigurationContainer(initialValues, {
				isRootDescendantNode: true,
			});

			const toggle = screen.getByRole('switch', {
				name: 'allow-standalone-entries',
			});

			expect(toggle).toBeVisible();
			expect(toggle).toBeChecked();
		});

		it.each([
			['false', false],
			['true', true],
		])(
			'reflects the existing "%s" setting value',
			(value, expectedChecked) => {
				renderConfigurationContainer(
					{
						objectDefinitionSettings: [
							{name: 'allowStandaloneObjectEntry', value},
						],
					},
					{isRootDescendantNode: true}
				);

				const toggle = screen.getByRole('switch', {
					name: 'allow-standalone-entries',
				});

				if (expectedChecked) {
					expect(toggle).toBeChecked();
				}
				else {
					expect(toggle).not.toBeChecked();
				}
			}
		);

		it('shows the default popover content when no standalone entries exist', async () => {
			renderConfigurationContainer(initialValues, {
				isRootDescendantNode: true,
			});

			const icon = document.querySelector(
				'.lfr-objects__allow-standalone-entries-tooltip-icon'
			);

			fireEvent.mouseOver(icon!);

			expect(
				await screen.findByText('standalone-entries')
			).toBeInTheDocument();
			expect(
				screen.getByText(
					/when-enabled-you-can-create-entries-without-a-parent-object/
				)
			).toBeInTheDocument();
		});

		it('shows the disabling-not-allowed popover content when standalone entries exist', async () => {
			renderConfigurationContainer(
				{
					objectDefinitionSettings: [
						{
							name: 'allowStandaloneObjectEntry',
							value: 'true',
						},
					],
				},
				{hasStandaloneEntries: true, isRootDescendantNode: true}
			);

			const icon = document.querySelector(
				'.lfr-objects__allow-standalone-entries-tooltip-icon'
			);

			fireEvent.mouseOver(icon!);

			expect(
				await screen.findByText(
					'disabling-standalone-entries-not-allowed'
				)
			).toBeInTheDocument();
			expect(
				screen.getByText(/this-object-has-existing-standalone-entries/)
			).toBeInTheDocument();
		});

		it('writes the setting value to objectDefinitionSettings on toggle', async () => {
			const setValues = jest.fn();

			render(
				<ConfigurationContainer
					hasUpdateObjectDefinitionPermission
					isApproved={true}
					isEnableObjectEntrySchedule={false}
					isRootDescendantNode={true}
					setValues={setValues}
					values={{
						...initialValues,
						objectDefinitionSettings: [
							{
								name: 'allowStandaloneObjectEntry',
								value: 'true',
							},
						],
					}}
				/>
			);

			await userEvent.click(
				screen.getByRole('switch', {name: 'allow-standalone-entries'})
			);

			expect(setValues).toHaveBeenCalledWith({
				objectDefinitionSettings: [
					{name: 'allowStandaloneObjectEntry', value: 'false'},
				],
			});
		});
	});
});
