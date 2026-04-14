/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {PropsWithChildren} from 'react';

import {
	NewSpaceFormSection,
	NewSpaceFormSectionProps,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/spaces/NewSpaceFormSection';

const mockLearnResources = {
	'site-cms-site-initializer': {
		'test-resource-key': {
			en_US: {
				message: 'Test Message',
				url: 'https://learn.liferay.com/test-url',
			},
		},
	},
};

describe('NewSpaceFormSection', () => {
	const props: PropsWithChildren<NewSpaceFormSectionProps> = {
		children: 'Test Children',
		description: 'Test Description',
		learnResourceKey: 'test-resource-key',
		learnResources: mockLearnResources,
		onSubmit: jest.fn(),
		step: 1,
		title: 'Test Title',
	};

	it('renders with title, description, step, links, and children', () => {
		render(<NewSpaceFormSection {...props} />);

		expect(
			screen.getByRole('heading', {name: props.title})
		).toBeInTheDocument();
		expect(screen.getByText(props.description)).toBeInTheDocument();
		expect(screen.getByText('step-x-of-x')).toBeInTheDocument();
		expect(
			screen.getByText(props.children!.toString())
		).toBeInTheDocument();

		const learnMoreLink = screen.getByRole('link', {
			name: 'x-opens-new-window',
		});

		expect(learnMoreLink).toHaveAttribute(
			'href',
			'https://learn.liferay.com/test-url'
		);
		expect(learnMoreLink).toHaveClass('font-weight-regular');
		expect(learnMoreLink).not.toHaveClass('font-weight-semi-bold');
	});

	it('calls onSubmit callback when clicking on a button of type submit', async () => {
		render(
			<NewSpaceFormSection {...props}>
				<button type="submit">Submit</button>
			</NewSpaceFormSection>
		);

		const submitBtn = screen.getByRole('button', {name: 'Submit'});
		expect(submitBtn).toBeInTheDocument();

		expect(props.onSubmit).not.toHaveBeenCalled();

		await userEvent.click(submitBtn);

		expect(props.onSubmit).toHaveBeenCalledTimes(1);
	});
});
