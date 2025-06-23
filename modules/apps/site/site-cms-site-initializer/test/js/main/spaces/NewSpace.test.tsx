/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import NewSpace, {
	NewSpaceProps,
} from '../../../../src/main/resources/META-INF/resources/js/main/spaces/NewSpace';
import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/services/ApiHelper';

describe('NewSpace', () => {
	const props: NewSpaceProps = {
		baseAddSpaceMembersURL: 'fake-add-member-url/',
	};

	let apiPostSpy: jest.SpyInstance;

	beforeEach(() => {
		apiPostSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({data: {id: 'fake-id'}, error: null});
	});

	afterEach(() => {
		apiPostSpy.mockRestore();
	});

	it('renders with correct title, description, buttons', () => {
		render(<NewSpace {...props} />);

		expect(
			screen.getByRole('heading', {name: 'create-a-space'})
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'spaces-are-essential-for-organizing-defining-and-managing-your-content-and-files'
			)
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'continue'})
		).toBeInTheDocument();
	});

	it('disables continue button until it has a value', async () => {
		render(<NewSpace {...props} />);

		expect(screen.getByRole('button', {name: 'continue'})).toBeDisabled();

		await userEvent.type(
			screen.getByRole('textbox', {
				name: /space-name/i,
			}),
			'test'
		);

		expect(
			screen.getByRole('button', {name: 'continue'})
		).not.toBeDisabled();
	});

	it('submits form with correct values', async () => {
		render(<NewSpace {...props} />);

		const spaceName = 'My Space';
		const spaceDescription = 'My space description';

		await userEvent.type(
			screen.getByRole('textbox', {
				name: /space-name/i,
			}),
			spaceName
		);

		await userEvent.type(
			screen.getByRole('textbox', {
				name: 'description',
			}),
			spaceDescription
		);

		expect(apiPostSpy).not.toHaveBeenCalled();

		await userEvent.click(
			screen.getByRole('button', {
				name: 'continue',
			})
		);

		expect(apiPostSpy).toHaveBeenCalledTimes(1);
		expect(apiPostSpy).toHaveBeenCalledWith(
			'/o/headless-asset-library/v1.0/asset-libraries',
			{
				description: spaceDescription,
				name: spaceName,
				settings: {
					logoColor: 'outline-0',
				},
			}
		);
	});

	it('submits form with custom color', async () => {
		render(<NewSpace {...props} />);

		await userEvent.type(
			screen.getByRole('textbox', {
				name: /space-name/i,
			}),
			'Space Name'
		);

		await userEvent.type(
			screen.getByRole('textbox', {
				name: 'description',
			}),
			'Space Description'
		);

		await userEvent.click(
			screen.getByRole('button', {
				name: 'space-color',
			})
		);

		const colorsMenu = screen.getByRole('menu');
		expect(colorsMenu).toBeInTheDocument();

		await userEvent.click(
			within(colorsMenu).getByRole('menuitem', {name: 'purple'})
		);

		await userEvent.click(
			screen.getByRole('button', {
				name: 'continue',
			})
		);

		expect(apiPostSpy).toHaveBeenCalledWith(
			'/o/headless-asset-library/v1.0/asset-libraries',
			expect.objectContaining({
				settings: {
					logoColor: 'outline-1',
				},
			})
		);
	});

	describe('hasErrors', () => {
		it('shows error message when space name is empty', async () => {
			render(<NewSpace {...props} />);

			await userEvent.click(
				screen.getByRole('button', {
					name: 'create-a-space-without-members',
				})
			);

			expect(apiPostSpy).not.toHaveBeenCalled();

			expect(
				screen.getByText('this-field-is-required')
			).toBeInTheDocument();
		});

		it('shows error message when space name is numeric', async () => {
			render(<NewSpace {...props} />);

			const spaceName = '123';

			await userEvent.type(
				screen.getByRole('textbox', {
					name: /space-name/i,
				}),
				spaceName
			);

			await userEvent.click(
				screen.getByRole('button', {
					name: 'create-a-space-without-members',
				})
			);

			expect(apiPostSpy).not.toHaveBeenCalled();

			expect(
				screen.getByText('please-enter-a-nonnumeric-name')
			).toBeInTheDocument();
		});

		it('shows error message when space name is equal to null', async () => {
			render(<NewSpace {...props} />);

			const spaceName = 'null';

			await userEvent.type(
				screen.getByRole('textbox', {
					name: /space-name/i,
				}),
				spaceName
			);

			await userEvent.click(
				screen.getByRole('button', {
					name: 'create-a-space-without-members',
				})
			);

			expect(apiPostSpy).not.toHaveBeenCalled();

			expect(screen.getByText('name-cannot-be-null')).toBeInTheDocument();
		});

		it('shows error message when space name has an invalid character', async () => {
			render(<NewSpace {...props} />);

			const spaceName = 'Space*Name';

			await userEvent.type(
				screen.getByRole('textbox', {
					name: /space-name/i,
				}),
				spaceName
			);

			await userEvent.click(
				screen.getByRole('button', {
					name: 'create-a-space-without-members',
				})
			);

			expect(apiPostSpy).not.toHaveBeenCalled();

			expect(
				screen.getByText(
					'name-cannot-contain-the-following-invalid-characters-x'
				)
			).toBeInTheDocument();
		});

		it('shows error message when space name is more than 150 characters long', async () => {
			render(<NewSpace {...props} />);

			const spaceName = 'a'.repeat(151);

			await userEvent.type(
				screen.getByRole('textbox', {
					name: /space-name/i,
				}),
				spaceName
			);

			await userEvent.click(
				screen.getByRole('button', {
					name: 'create-a-space-without-members',
				})
			);

			expect(apiPostSpy).not.toHaveBeenCalled();

			expect(
				screen.getByText('please-enter-no-more-than-x-characters')
			).toBeInTheDocument();
		});
	});
});
