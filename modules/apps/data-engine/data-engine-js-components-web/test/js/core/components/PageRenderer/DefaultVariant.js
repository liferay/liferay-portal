/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {
	Page,
	PageHeader,
} from '../../../../../src/main/resources/META-INF/resources/js/core/components/PageRenderer/DefaultVariant.es';

const mockUseFormState = jest.fn(() => ({portletId: 'test.portlet'}));

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/core/hooks/useForm.es',
	() => ({
		useFormState: () => mockUseFormState(),
	})
);

describe('DefaultVariant Page', () => {
	afterEach(() => {
		mockUseFormState.mockReturnValue({portletId: 'test.portlet'});
	});

	it('renders page container with role="group" and aria attributes', () => {
		render(
			<Page
				header={
					<PageHeader
						description="Page Description"
						title="Page Title"
					/>
				}
				pageIndex={0}
			></Page>
		);

		const group = screen.getByRole('group');

		expect(group).toHaveAttribute('aria-labelledby', 'pageTitle0');
		expect(group).toHaveAttribute('aria-describedby', 'pageDescription0');
	});

	it('renders page title and description with correct ids', () => {
		render(
			<Page
				header={
					<PageHeader
						description="Page Description"
						title="Page Title"
					/>
				}
				pageIndex={1}
			/>
		);

		expect(screen.getByText('Page Title')).toHaveAttribute(
			'id',
			'pageTitle1'
		);

		expect(screen.getByText('Page Description')).toHaveAttribute(
			'id',
			'pageDescription1'
		);
	});

	it('omits aria-describedby when the Journal web content portlet suppresses the page description', () => {
		mockUseFormState.mockReturnValue({
			portletId: '_com_liferay_journal_web_portlet_JournalPortlet_',
		});

		render(
			<Page
				header={
					<PageHeader
						description="Page Description"
						title="Page Title"
					/>
				}
				pageIndex={0}
			/>
		);

		const group = screen.getByRole('group');

		expect(group).toHaveAttribute('aria-labelledby', 'pageTitle0');
		expect(group).not.toHaveAttribute('aria-describedby');
		expect(screen.queryByText('Page Description')).not.toBeInTheDocument();
	});

	it('renders no aria attributes without a header', () => {
		render(<Page header={null} pageIndex={0} />);

		const group = screen.getByRole('group');

		expect(group).not.toHaveAttribute('aria-labelledby');
		expect(group).not.toHaveAttribute('aria-describedby');
	});

	it('renders no aria attributes with a custom header', () => {
		render(<Page header={<div>Custom Header</div>} pageIndex={0} />);

		const group = screen.getByRole('group');

		expect(group).not.toHaveAttribute('aria-labelledby');
		expect(group).not.toHaveAttribute('aria-describedby');
	});

	it('renders no aria-describedby without a description', () => {
		render(
			<Page header={<PageHeader title="Page Title" />} pageIndex={0} />
		);

		const group = screen.getByRole('group');

		expect(group).toHaveAttribute('aria-labelledby', 'pageTitle0');
		expect(group).not.toHaveAttribute('aria-describedby');
	});

	it('renders no aria-labelledby without a title', () => {
		render(
			<Page
				header={<PageHeader description="Page Description" />}
				pageIndex={0}
			/>
		);

		const group = screen.getByRole('group');

		expect(group).not.toHaveAttribute('aria-labelledby');
		expect(group).toHaveAttribute('aria-describedby', 'pageDescription0');
	});
});
