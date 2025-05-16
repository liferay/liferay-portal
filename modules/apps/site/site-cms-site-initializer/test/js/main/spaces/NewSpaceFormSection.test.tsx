/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, {PropsWithChildren} from 'react';

import {
	NewSpaceFormSection,
	NewSpaceFormSectionProps,
} from '../../../../src/main/resources/META-INF/resources/js/main/spaces/NewSpaceFormSection';

describe('NewSpaceFormSection', () => {
	const props: PropsWithChildren<NewSpaceFormSectionProps> = {
		children: 'Test Children',
		description: 'Test Description',
		linkLabel: 'Learn more',
		linkUrl: 'https://www.liferay.com',
		onSubmit: jest.fn(),
		step: 1,
		title: 'Test Title',
	};

	it('renders with title, description, step, links, and children', () => {
		render(<NewSpaceFormSection {...props} />);

		expect(
			screen.getByRole('img', {name: 'cms-product'})
		).toBeInTheDocument();
		expect(
			screen.getByRole('heading', {name: props.title})
		).toBeInTheDocument();
		expect(screen.getByText(props.description)).toBeInTheDocument();
		expect(screen.getByText('step-x-of-x')).toBeInTheDocument();
		expect(
			screen.getByText(props.children!.toString())
		).toBeInTheDocument();
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
