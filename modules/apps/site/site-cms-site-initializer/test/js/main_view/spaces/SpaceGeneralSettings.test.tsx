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
import {ERC_MAX_LENGTH} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
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

const SPACE_NAME_WITH_MARKUP = '<img src=x onerror="alert(1)">';

const SPACE: Partial<Space> = {
	description: 'This is the description for Cool Space',
	externalReferenceCode: 'space-external-reference-code',
	friendlyURL: '/cool-space',
	name: 'Cool Space',
	settings: {
		logoColor: 'outline-2',
		sharingEnabled: true,
		trashEnabled: true,
		trashEntriesMaxAge: 2880,
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

	it('shows a name containing markup as text in the success toast', async () => {
		renderComponent();

		const nameField = screen.getByRole('textbox', {name: /space-name/});

		await userEvent.clear(nameField);
		await userEvent.type(nameField, SPACE_NAME_WITH_MARKUP);

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(
				screen.getByText(
					`${SPACE_NAME_WITH_MARKUP}-was-saved-successfully`
				)
			).toBeInTheDocument();
		});

		expect(document.querySelector('img[src="x"]')).toBeNull();

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

	it.each([
		'Please enter a unique name',
		'This external reference code is already in use.',
	])('shows API error message: %s', async (errorMessage) => {
		jest.spyOn(SpaceService, 'updateSpace').mockResolvedValue({
			data: null,
			error: errorMessage,
		});

		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(SpaceService.updateSpace).toBeCalled();
			expect(
				screen.queryByText('My Space-was-saved-successfully')
			).not.toBeInTheDocument();
			expect(screen.getByText(errorMessage)).toBeInTheDocument();
		});

		await closeToast();
	});

	describe('Trash entries max age', () => {
		it('displays the value in days converted from the stored minutes', () => {
			renderComponent({
				space: {
					...SPACE,
					settings: {
						...SPACE.settings!,
						trashEntriesMaxAge: 7200,
					},
				} as Partial<Space>,
			});

			expect(screen.getByLabelText('trash-entries-max-age')).toHaveValue(
				5
			);
		});

		it('converts the value to minutes on save', async () => {
			renderComponent();

			const trashEntriesMaxAgeField = screen.getByLabelText(
				'trash-entries-max-age'
			);

			await userEvent.clear(trashEntriesMaxAgeField);
			await userEvent.type(trashEntriesMaxAgeField, '5');

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			await waitFor(() => {
				expect(SpaceService.updateSpace).toBeCalledWith(
					expect.any(String),
					expect.objectContaining({
						settings: expect.objectContaining({
							trashEntriesMaxAge: 7200,
						}),
					})
				);
			});

			await closeToast();
		});

		it('does not save when the value is less than 1', async () => {
			renderComponent();

			const trashEntriesMaxAgeField = screen.getByLabelText(
				'trash-entries-max-age'
			);

			await userEvent.clear(trashEntriesMaxAgeField);
			await userEvent.type(trashEntriesMaxAgeField, '0');

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(SpaceService.updateSpace).not.toHaveBeenCalled();
		});
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

		it('rejects an ERC longer than the column length', async () => {
			renderComponent();

			const ercInput = screen.getByRole('textbox', {name: /erc/});

			await userEvent.clear(ercInput);
			await userEvent.type(ercInput, 'a'.repeat(ERC_MAX_LENGTH + 1));

			await userEvent.click(screen.getByRole('button', {name: 'save'}));

			expect(
				screen.getByText(/please-enter-no-more-than/)
			).toBeInTheDocument();
			expect(SpaceService.updateSpace).not.toBeCalled();
		});
	});
});
