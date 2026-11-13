/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import EditAdaptiveMedia from '../src/main/resources/META-INF/resources/js/EditAdaptiveMedia';

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	fetch: jest.fn(),
	navigate: jest.fn(),
}));

function renderEditAdaptiveMedia(props) {
	render(
		<EditAdaptiveMedia
			actionUrl="http://localhost/edit"
			automaticUuid={true}
			configurationEntryEditable={true}
			configurationEntryUuid=""
			namespace="_portlet_"
			redirect="http://localhost/view"
			{...props}
		/>
	);
}

describe('EditAdaptiveMedia', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('fills in the ID from the name', async () => {
		renderEditAdaptiveMedia();

		await userEvent.type(
			screen.getByLabelText(/^name/),
			'Small Image Variant'
		);

		expect(screen.getByLabelText('id')).toHaveValue('small-image-variant');
	});

	it('ignores non-numeric sizes', async () => {
		renderEditAdaptiveMedia();

		await userEvent.type(
			screen.getByLabelText(/^name/),
			'Small Image Variant'
		);
		await userEvent.type(
			screen.getByLabelText('max-height-px'),
			'notallowed'
		);
		await userEvent.type(
			screen.getByLabelText('max-width-px'),
			'notallowed'
		);

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(fetch).not.toHaveBeenCalled();
		expect(screen.getByLabelText('max-height-px')).toHaveValue(null);
		expect(screen.getByLabelText('max-width-px')).toHaveValue(null);
		expect(
			screen.getByText('at-least-one-value-is-required')
		).toBeInTheDocument();
	});

	it('rejects a zero max height and max width', async () => {
		renderEditAdaptiveMedia({
			amImageConfigurationEntry: {
				description: '',
				name: 'testResolution',
				properties: {
					'max-height': '1000',
					'max-width': '1000',
				},
			},
			automaticUuid: false,
			configurationEntryUuid: 'testresolution',
		});

		await userEvent.clear(screen.getByLabelText(/^name/));
		await userEvent.type(
			screen.getByLabelText(/^name/),
			'testResolutionEdit'
		);
		await userEvent.clear(screen.getByLabelText('max-height-px'));
		await userEvent.type(screen.getByLabelText('max-height-px'), '0');
		await userEvent.clear(screen.getByLabelText('max-width-px'));
		await userEvent.type(screen.getByLabelText('max-width-px'), '0');

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(fetch).not.toHaveBeenCalled();
		expect(
			screen.getByText(
				'please-enter-a-max-width-or-max-height-value-larger-than-0'
			)
		).toBeInTheDocument();
	});

	it('requires a max height or max width', async () => {
		renderEditAdaptiveMedia();

		await userEvent.type(
			screen.getByLabelText(/^name/),
			'testResolutionNoDimensions'
		);

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(fetch).not.toHaveBeenCalled();
		expect(
			screen.getByText('at-least-one-value-is-required')
		).toBeInTheDocument();
	});

	it('shows the error message of a rejected save', async () => {
		fetch.mockResolvedValue({
			json: () =>
				Promise.resolve({
					message:
						'A configuration with this ID already exists. Enter a unique ID.',
					success: false,
				}),
		});

		renderEditAdaptiveMedia();

		await userEvent.type(
			screen.getByLabelText(/^name/),
			'Image Variant With Duplicate Custom ID'
		);
		await userEvent.type(screen.getByLabelText('max-height-px'), '350');
		await userEvent.type(screen.getByLabelText('max-width-px'), '450');

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(
			await screen.findByText(
				'A configuration with this ID already exists. Enter a unique ID.'
			)
		).toBeInTheDocument();
		expect(fetch).toHaveBeenCalledTimes(1);
	});
});
