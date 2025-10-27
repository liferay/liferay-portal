/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {navigate} from 'frontend-js-web';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import SpaceGeneralSettings from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/SpaceGeneralSettings';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService',
	() => ({
		updateSpace: jest
			.fn()
			.mockImplementation(() =>
				Promise.reject(new Error('Network error'))
			),
	})
);

const SPACE: Partial<Space> = {
	description: 'This is the description for Cool Space',
	externalReferenceCode: 'space-external-reference-code',
	name: 'Cool Space',
	settings: {
		logoColor: 'outline-2',
		mimeTypeLimits: [{maximumSize: 1024, mimeType: 'application/json'}],
		sharingEnabled: true,
		trashEnabled: true,
		trashEntriesMaxAge: 0,
	},
};

const closeToast = async () => {
	await userEvent.click(screen.getByRole('button', {name: 'close'}));
};

const renderComponent = ({
	backURL = '/all-spaces',
	groupId = '1234',
	space = SPACE,
}: {
	backURL?: string;
	groupId?: string;
	space?: Partial<Space>;
} = {}) => {
	return render(
		<SpaceGeneralSettings
			backURL={backURL}
			groupId={groupId}
			space={space as Space}
		/>
	);
};

describe('SpaceGeneralSettings', () => {
	beforeEach(() => {
		SpaceService.updateSpace = jest.fn().mockResolvedValue({data: {}});
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders the fields with the correct values', () => {
		renderComponent();

		expect(
			screen.getByRole('combobox', {name: 'space-color'})
		).toHaveTextContent('yellow');

		const nameField = screen.getByRole('textbox', {name: /space-name/});

		expect(nameField).toHaveValue('Cool Space');
		expect(nameField).toBeRequired();

		expect(screen.getByLabelText('description')).toHaveValue(
			'This is the description for Cool Space'
		);

		const groupIdField = screen.getByLabelText('group-id');

		expect(groupIdField).toHaveValue('1234');
		expect(groupIdField).toHaveAttribute('readonly');

		const ercField = screen.getByRole('textbox', {name: /erc/});

		expect(ercField).toHaveValue('space-external-reference-code');
		expect(ercField).toBeRequired();

		expect(
			screen.getByRole('checkbox', {
				name: /enable-sharing/,
			})
		).toBeChecked();

		expect(screen.getByRole('textbox', {name: /mime-type/})).toHaveValue(
			'application/json'
		);

		expect(
			screen.getByRole('spinbutton', {name: /maximum-file-size/})
		).toHaveValue(1024);
	});

	it('checks the accessibility of the general settings', async () => {
		renderComponent();

		const {container} = renderComponent();

		await checkAccessibility({
			context: {exclude: ['.form-control-select'], include: container},
		});
	});

	it('submits form with correct values', async () => {
		renderComponent();

		const nameField = screen.getByRole('textbox', {name: /space-name/});

		await userEvent.clear(nameField);
		await userEvent.type(nameField, 'My Space');

		const descriptionField = screen.getByLabelText('description');

		await userEvent.clear(descriptionField);
		await userEvent.type(descriptionField, 'My space description');

		const ercField = screen.getByRole('textbox', {name: /erc/});

		await userEvent.clear(ercField);
		await userEvent.type(ercField, 'My New ERC');

		await userEvent.click(
			screen.getByRole('button', {
				name: 'save',
			})
		);

		await waitFor(() => {
			const {externalReferenceCode, ...space} = SPACE;

			expect(SpaceService.updateSpace).toBeCalledWith(
				externalReferenceCode,
				{
					...space,
					description: 'My space description',
					externalReferenceCode: 'My New ERC',
					name: 'My Space',
				}
			);

			expect(
				screen.getByText('My Space-was-saved-successfully')
			).toBeInTheDocument();
		});

		await closeToast();
	});

	it('redirects to the previous url when the cancel button is pressed', async () => {
		renderComponent();

		await userEvent.click(
			screen.getByRole('button', {
				name: 'cancel',
			})
		);

		await waitFor(() => {
			expect(navigate).toHaveBeenCalledWith('/all-spaces');
		});
	});

	it('shows an error toast when the request fails', async () => {
		SpaceService.updateSpace = jest
			.fn()
			.mockResolvedValue({error: 'Error'});

		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(SpaceService.updateSpace).toBeCalled();

			expect(
				screen.queryByText('My Space-was-saved-successfully')
			).not.toBeInTheDocument();

			expect(
				screen.getByText(
					'an-unexpected-error-occurred-while-saving-the-space'
				)
			).toBeInTheDocument();
		});

		await closeToast();
	});

	it('adds and remove fields for the mime type limit', async () => {
		renderComponent();

		expect(screen.getAllByLabelText('maximum-file-size').length).toBe(1);

		await userEvent.click(screen.getByLabelText('add-x'));

		expect(screen.getAllByLabelText('maximum-file-size').length).toBe(2);

		await userEvent.click(screen.getAllByLabelText('remove-x')[1]);

		expect(screen.getAllByLabelText('maximum-file-size').length).toBe(1);
	});

	describe('Errors', () => {
		it('does not save the name field when there is an error and the field is focused', async () => {
			renderComponent();

			const nameInput = screen.getByRole('textbox', {name: /space-name/});

			await userEvent.clear(nameInput);
			await userEvent.type(nameInput, '123');

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(
				screen.getByText('please-enter-a-nonnumeric-name')
			).toBeInTheDocument();

			expect(nameInput).toHaveFocus();
		});

		it('does not save the form when the ERC field has an error and the field is focused', async () => {
			renderComponent();

			const nameInput = screen.getByRole('textbox', {name: /space-name/});

			await userEvent.clear(nameInput);

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(
				screen.getByText('this-field-is-required')
			).toBeInTheDocument();

			expect(nameInput).toHaveFocus();
		});

		it('does not save the form when the Maximum File Size field has an error and the field is focused', async () => {
			renderComponent();

			const maximumSizeInput = screen.getByLabelText('maximum-file-size');

			await userEvent.type(maximumSizeInput, '123.123');

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(
				screen.getByText('please-enter-a-valid-number')
			).toBeInTheDocument();

			expect(maximumSizeInput).toHaveFocus();
		});

		it('saves the form when a maximum file size field has an error and this field is removed', async () => {
			renderComponent();

			await userEvent.click(screen.getByLabelText('add-x'));

			const inputs = screen.getAllByLabelText('maximum-file-size');

			const [firstInput, secondInput] = inputs;

			await userEvent.type(firstInput, '123.123');

			firstInput.blur();

			await userEvent.type(secondInput, '123');

			secondInput.blur();

			await waitFor(() => {
				expect(
					screen.getByText('please-enter-a-valid-number')
				).toBeInTheDocument();
			});

			await userEvent.click(screen.getAllByLabelText('remove-x')[0]);

			await waitFor(() => {
				expect(
					screen.queryByText('please-enter-a-valid-number')
				).not.toBeInTheDocument();
			});

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			await waitFor(() => {
				const {externalReferenceCode} = SPACE;

				expect(SpaceService.updateSpace).toBeCalledWith(
					externalReferenceCode,
					expect.objectContaining({
						settings: expect.objectContaining({
							mimeTypeLimits: [
								{maximumSize: '123', mimeType: ''},
							],
						}),
					})
				);

				expect(
					screen.getByText(/was-saved-successfully/)
				).toBeInTheDocument();
			});
		});
	});
});
