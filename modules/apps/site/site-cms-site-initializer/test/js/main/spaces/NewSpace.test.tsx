/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/structure_builder/services/ApiHelper';

import NewSpace, {
	NewSpaceProps,
} from '../../../../src/main/resources/META-INF/resources/js/main/spaces/NewSpace';

describe('NewSpace', () => {
	const props: NewSpaceProps = {
		baseRedirectUrl: 'fake-redirect-url/',
	};

	let apiPostSpy: jest.SpyInstance;

	beforeAll(() => {
		apiPostSpy = jest.spyOn(ApiHelper, 'post').mockResolvedValue({id: 'fake-id'});
	});

	afterEach(() => {
		apiPostSpy.mockRestore();
	});

	it('renders with correct title, description, buttons', () => {
		render(<NewSpace {...props} />);

		const title = screen.getByRole('heading', {name: 'create-a-space'});
		expect(title).toBeInTheDocument();

		const description = screen.getByText(
			'spaces-are-essential-for-organizing-defining-and-managing-your-content-and-files'
		);
		expect(description).toBeInTheDocument();

		const learnMoreLink = screen.getByRole('link', {
			name: 'learn-more-about-spaces',
		});
		expect(learnMoreLink).toBeInTheDocument();
		expect(learnMoreLink).toHaveAttribute('href', '/');

		const addMembersBtn = screen.getByRole('button', {name: 'add-members'});
		expect(addMembersBtn).toBeInTheDocument();

		const createSpaceBtn = screen.getByRole('button', {
			name: 'create-a-space-without-members',
		});
		expect(createSpaceBtn).toBeInTheDocument();
	});

	it('submits form with correct values and redirect to baseRedirectUrl', async () => {
		render(<NewSpace {...props} />);

		const spaceName = 'My Space';
		const spaceDescription = 'My space description';

		const spaceNameInput = screen.getByRole('textbox', {
			name: /space-name/i,
		});
		await userEvent.type(spaceNameInput, spaceName);
		expect(spaceNameInput).toHaveValue(spaceName);

		const descriptionInput = screen.getByRole('textbox', {
			name: 'description',
		});
		await userEvent.type(descriptionInput, spaceDescription);
		expect(descriptionInput).toHaveValue(spaceDescription);

		expect(apiPostSpy).not.toHaveBeenCalled();

		const submitButton = screen.getByRole('button', {
			name: 'create-a-space-without-members',
		});
		await userEvent.click(submitButton);

		expect(apiPostSpy).toHaveBeenCalledTimes(1);
		expect(apiPostSpy).toHaveBeenCalledWith('/o/headless-asset-library/v1.0/asset-libraries', {
			description: spaceDescription,
			name: spaceName,
		});
	});
});
