/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
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

const saveTemplateInNewSetWithError = async () => {
	(fetch as jest.Mock)
		.mockImplementationOnce(() =>
			Promise.resolve({
				json: () => ({layoutPageTemplateCollectionId: 5}),
			})
		)
		.mockImplementationOnce(() =>
			Promise.resolve({
				json: () => ({
					error: 'Name cannot contain the following invalid character: /.',
				}),
			})
		);

	const renderResult = renderComponent('page-template');

	await act(async () => {
		await userEvent.type(
			screen.getByLabelText('page-template-name'),
			'Page/Template'
		);
		await userEvent.click(screen.getByText('save-in-new-set'));
	});

	await act(async () => {
		await userEvent.click(screen.getByText('save'));
	});

	return renderResult;
};

describe('AddLayoutPageTemplateEntryDesignLibraryModalContent', () => {
	afterEach(() => {
		(fetch as jest.Mock).mockClear();
		(navigate as jest.Mock).mockClear();
		(openToast as jest.Mock).mockClear();
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

	it('adds a content page template to an existing set after a server error', async () => {
		(fetch as jest.Mock).mockImplementationOnce(() =>
			Promise.resolve({
				json: () => ({
					error: 'A page template entry with that name already exists.',
				}),
			})
		);

		const {unmount} = renderComponent('page-template');

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

		expect(
			screen.getByText(
				'A page template entry with that name already exists.'
			)
		).toBeInTheDocument();
		expect(openToast).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();

		await act(async () => {
			await userEvent.clear(screen.getByLabelText('page-template-name'));
			await userEvent.type(
				screen.getByLabelText('page-template-name'),
				'Page Template 2'
			);
			await userEvent.click(screen.getByText('save'));
		});

		const [url, {body}] = (fetch as jest.Mock).mock.calls[1];

		expect(url).toBe('addLayoutPageTemplateEntryURL');
		expect(Object.fromEntries(body.entries())).toEqual({
			_namespace_layoutPageTemplateCollectionId: '2',
			_namespace_name: 'Page Template 2',
		});
		expect(navigate).toHaveBeenCalledTimes(1);

		unmount();

		expect(navigate).toHaveBeenCalledTimes(1);
	});

	it('reloads the page when the modal closes after a new set', async () => {
		const {unmount} = await saveTemplateInNewSetWithError();

		expect(fetch).toHaveBeenCalledTimes(2);
		expect(navigate).not.toHaveBeenCalled();

		unmount();

		expect(navigate).toHaveBeenCalledWith(location.href);
	});

	it('keeps the new set when its template fails', async () => {
		await saveTemplateInNewSetWithError();

		expect(
			screen.getByText(
				'Name cannot contain the following invalid character: /.'
			)
		).toBeInTheDocument();
		expect(openToast).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();
		expect(screen.getByLabelText('page-template-set')).toHaveValue('5');

		await act(async () => {
			await userEvent.clear(screen.getByLabelText('page-template-name'));
			await userEvent.type(
				screen.getByLabelText('page-template-name'),
				'Page Template'
			);
			await userEvent.click(screen.getByText('save'));
		});

		const [url, {body}] = (fetch as jest.Mock).mock.calls[2];

		expect(url).toBe('addLayoutPageTemplateEntryURL');
		expect(Object.fromEntries(body.entries())).toEqual({
			_namespace_layoutPageTemplateCollectionId: '5',
			_namespace_name: 'Page Template',
		});

		await act(async () => {
			await userEvent.click(screen.getByText('save-in-new-set'));
		});

		expect(screen.getByLabelText('page-template-set-name')).toHaveValue(
			'untitled-set 2'
		);
	});
});
