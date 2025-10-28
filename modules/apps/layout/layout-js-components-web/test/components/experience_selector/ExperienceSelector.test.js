/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {navigate} from 'frontend-js-web';
import React from 'react';

import {ExperienceSelector} from '../../../src/main/resources/META-INF/resources/js/index';

const mockSegments = {
	segmentsExperiences: [
		{
			active: true,
			segmentsEntryId: '0',
			segmentsEntryName: 'Anyone',
			segmentsExperienceId: '33590',
			segmentsExperienceName: 'Experience Default',
			statusLabel: 'Active',
			url: 'url',
		},
		{
			active: false,
			segmentsEntryId: '0',
			segmentsEntryName: 'Segment 1',
			segmentsExperienceId: '33591',
			segmentsExperienceName: 'Experience 1',
			statusLabel: 'Inactive',
			url: 'url',
		},
	],
	selectedSegmentsExperience: {
		active: true,
		segmentsEntryId: '0',
		segmentsEntryName: 'Anyone',
		segmentsExperienceId: '33590',
		segmentsExperienceName: 'Experience Default',
		statusLabel: 'Active',
		url: 'url',
	},
};

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	navigate: jest.fn(),
}));

const renderComponent = ({
	displayType = 'light',
	disabled,
	segmentsExperiences = mockSegments.segmentsExperiences,
	selectedSegmentsExperience = mockSegments.selectedSegmentsExperience,
} = {}) =>
	render(
		<ExperienceSelector
			disabled={disabled}
			displayType={displayType}
			segmentsExperiences={segmentsExperiences}
			selectedSegmentsExperience={selectedSegmentsExperience}
		/>
	);

describe('ExperienceSelector', () => {
	it('renders', async () => {
		const {findByText} = renderComponent();

		expect(await findByText('Experience Default')).toBeInTheDocument();
		expect(await findByText('Active')).toBeInTheDocument();
	});

	it('disabled the selector', async () => {
		const {findByRole} = renderComponent({disabled: true});

		expect(await findByRole('combobox')).toBeDisabled();
	});

	it('displays selector as secondary button when the display type is "light"', async () => {
		const {findByRole} = renderComponent();

		expect(await findByRole('combobox')).toHaveClass('btn-secondary');
	});

	it('displays selector as button without styles when the display type is "dark"', async () => {
		const {findByRole} = renderComponent({displayType: 'dark'});

		expect(await findByRole('combobox')).not.toHaveClass('btn-secondary');
	});

	it('renders name, status and segment in each option', async () => {
		const {findByRole, findByText} = renderComponent();

		await userEvent.click(await findByRole('combobox'));

		expect(await findByText('Experience 1')).toBeInTheDocument();
		expect(await findByText('Inactive')).toBeInTheDocument();
		expect(await findByText('segment: Segment 1')).toBeInTheDocument();
	});

	it('calls navigate when an option is selected', async () => {
		const {findByRole} = renderComponent();

		await userEvent.click(await findByRole('combobox'));

		const button = await findByRole('option', {
			name: 'Experience 1 segment: Segment 1 Inactive',
		});

		fireEvent.click(button);

		expect(navigate).toHaveBeenCalled();
	});
});
