/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch, navigate} from 'frontend-js-web';
import React from 'react';

import AddLayoutPageTemplateEntryDesignLibraryModalContent from '../../src/main/resources/META-INF/resources/js/AddLayoutPageTemplateEntryDesignLibraryModalContent';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => {
	const actual = jest.requireActual('frontend-js-web');

	return {
		...actual,
		fetch: jest.fn(() =>
			Promise.resolve({
				json: () => ({layoutPageTemplateCollectionId: 1}),
			})
		),
		navigate: jest.fn(),
	};
});

const renderComponent = (mode: 'page-template' | 'set') =>
	render(
		<AddLayoutPageTemplateEntryDesignLibraryModalContent
			addLayoutPageTemplateEntryURL="addLayoutPageTemplateEntryURL"
			addPageTemplateSetURL="addPageTemplateSetURL"
			closeModal={jest.fn()}
			mode={mode}
			namespace="_namespace_"
			pageTemplateSets={[{id: 2, name: 'set-1'}]}
		/>
	);

describe('AddLayoutPageTemplateEntryDesignLibraryModalContent', () => {
	afterEach(() => {
		(fetch as jest.Mock).mockClear();
		(navigate as jest.Mock).mockClear();
	});

	it('adds a page template set', async () => {
		renderComponent('set');

		expect(screen.getByText('add-page-template-set')).toBeInTheDocument();

		await act(async () => {
			await userEvent.click(screen.getByText('save'));
		});

		const [url, {body}] = (fetch as jest.Mock).mock.calls[0];

		expect(url).toBe('addPageTemplateSetURL');
		expect(Object.fromEntries(body.entries())).toEqual({
			_namespace_description: '',
			_namespace_name: 'untitled-set',
		});

		expect(navigate).toHaveBeenCalled();
	});

	it('adds a content page template to an existing set', async () => {
		renderComponent('page-template');

		expect(screen.getByText('add-page-template')).toBeInTheDocument();

		await act(async () => {
			await userEvent.type(
				screen.getByLabelText('page-template-name'),
				'Page Template 1'
			);
			await userEvent.selectOptions(
				screen.getByLabelText('page-template-set'),
				'2'
			);
			await userEvent.click(screen.getByText('save'));
		});

		const [url, {body}] = (fetch as jest.Mock).mock.calls[0];

		expect(url).toBe('addLayoutPageTemplateEntryURL');
		expect(Object.fromEntries(body.entries())).toEqual({
			_namespace_layoutPageTemplateCollectionId: '2',
			_namespace_name: 'Page Template 1',
		});
	});
});
