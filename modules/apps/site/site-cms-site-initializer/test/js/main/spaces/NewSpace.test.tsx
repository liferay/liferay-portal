/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import NewSpace, {
	NewSpaceProps,
} from '../../../../src/main/resources/META-INF/resources/js/main/spaces/NewSpace';
import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/structure_builder/services/ApiHelper';

describe('NewSpace', () => {
	const props: NewSpaceProps = {
		baseRedirectUrl: 'fake-redirect-url/',
	};

	let apiPostSpy: jest.SpyInstance;

	beforeAll(() => {
		apiPostSpy = jest
			.spyOn(ApiHelper, 'post')
			.mockResolvedValue({id: 'fake-id'});
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

		const learnMoreLink = screen.getByRole('link', {
			name: 'learn-more-about-spaces',
		});
		expect(learnMoreLink).toBeInTheDocument();
		expect(learnMoreLink).toHaveAttribute('href', '/');

		expect(
			screen.getByRole('button', {name: 'add-members'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {
				name: 'create-a-space-without-members',
			})
		).toBeInTheDocument();
	});

	it('submits form with correct values and redirect to baseRedirectUrl', async () => {
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
				name: 'create-a-space-without-members',
			})
		);

		expect(apiPostSpy).toHaveBeenCalledTimes(1);
		expect(apiPostSpy).toHaveBeenCalledWith(
			'/o/headless-asset-library/v1.0/asset-libraries',
			{
				description: spaceDescription,
				name: spaceName,
			}
		);
	});
});
