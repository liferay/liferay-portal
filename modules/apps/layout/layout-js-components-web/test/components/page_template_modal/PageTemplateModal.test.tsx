/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import PageTemplateModal from '../../../src/main/resources/META-INF/resources/js/components/page_template_modal/PageTemplateModal';

import '@testing-library/jest-dom';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => {
	const actual = jest.requireActual('frontend-js-web');

	return {
		...actual,
		fetch: jest.fn(() => Promise.resolve({json: () => {}})),
		sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
	};
});

function renderConvertToPageTemplateModal() {
	return render(
		<PageTemplateModal
			createTemplateURL="createTemplateURL"
			getCollectionsURL="getCollectionsURL"
			hasMultipleSegmentsExperienceIds={false}
			layoutId="0"
			namespace=""
			onClose={jest.fn()}
			segmentsExperienceId="0"
		/>
	);
}

describe('ConvertToPageTemplateModal', () => {
	afterEach(() => {
		(fetch as any).mockClear();
	});

	beforeAll(() => {
		jest.useFakeTimers();
	});

	describe('Select Page Template Set modal', () => {
		it('renders the modal', async () => {
			await act(async () => {
				renderConvertToPageTemplateModal();
			});

			act(() => {
				jest.runAllTimers();
			});

			expect(
				screen.getByText('select-page-template-set')
			).toBeInTheDocument();
		});

		it('does not call URL to create a template when clicking Save without any set selected', async () => {
			await act(async () => {
				renderConvertToPageTemplateModal();
			});

			act(() => {
				jest.runAllTimers();
			});

			const saveButton = screen.getByText('save');

			fireEvent.click(saveButton);

			const [calledURL] = (fetch as any).mock.calls.pop();

			expect(
				screen.getByText('page-template-set-field-is-required')
			).toBeInTheDocument();

			expect(calledURL).not.toBe('createTemplateURL');
		});

		it('calls URL to create a template when clicking Save with a set selected', async () => {
			(fetch as any).mockImplementation(() =>
				Promise.resolve({json: () => [{name: 'set-1'}]})
			);

			await act(async () => {
				renderConvertToPageTemplateModal();
			});

			act(() => {
				jest.runAllTimers();
			});

			const saveButton = screen.getByText('save');
			const select = screen.getByLabelText('page-template-set');

			await userEvent.selectOptions(select, 'set-1', {
				advanceTimers: jest.advanceTimersByTime,
			});
			fireEvent.change(select);

			fireEvent.click(saveButton);

			const [calledURL] = (fetch as any).mock.calls.pop();

			expect(calledURL).toBe('createTemplateURL');
		});

		it('changes the modal when the Save In New Set Button is pressed', async () => {
			(fetch as any).mockImplementation(() =>
				Promise.resolve({json: () => [{name: 'set-1'}]})
			);

			await act(async () => {
				renderConvertToPageTemplateModal();
			});

			act(() => {
				jest.runAllTimers();
			});

			const saveInNewSetButton = screen.getByText('save-in-new-set');

			fireEvent.click(saveInNewSetButton);

			expect(
				screen.getByText('add-page-template-set')
			).toBeInTheDocument();
		});
	});

	describe('Add Page Template Set modal', () => {
		it('renders the set creation modal when there are no sets', async () => {
			(fetch as any).mockImplementation(() =>
				Promise.resolve({json: () => []})
			);

			await act(async () => {
				renderConvertToPageTemplateModal();
			});

			act(() => {
				jest.runAllTimers();
			});

			expect(
				screen.getByText('add-page-template-set')
			).toBeInTheDocument();
		});

		it('calls URL to create a template with typed description and default name', async () => {
			(fetch as any).mockImplementation(() =>
				Promise.resolve({json: () => []})
			);

			await act(async () => {
				renderConvertToPageTemplateModal();
			});

			act(() => {
				jest.runAllTimers();
			});

			const descriptionInput = screen.getByLabelText('description');
			const saveButton = screen.getByText('save');

			await userEvent.type(descriptionInput, 'This is a description', {
				advanceTimers: jest.advanceTimersByTime,
			});

			fireEvent.click(saveButton);

			const [calledURL, {body}] = (fetch as any).mock.calls.pop();

			const bodyJSON = Object.fromEntries(body.entries());

			expect(calledURL).toBe('createTemplateURL');

			expect(bodyJSON).toHaveProperty(
				'layoutPageTemplateCollectionDescription',
				'This is a description'
			);
			expect(bodyJSON).toHaveProperty(
				'layoutPageTemplateCollectionName',
				'untitled-set'
			);
		});

		it('does not call URL to create a template when the input name is empty', async () => {
			(fetch as any).mockImplementation(() =>
				Promise.resolve({json: () => []})
			);

			await act(async () => {
				renderConvertToPageTemplateModal();
			});

			act(() => {
				jest.runAllTimers();
			});

			const nameInput = screen.getByLabelText('name');
			const saveButton = screen.getByText('save');

			fireEvent.change(nameInput, {
				target: {value: ''},
			});

			fireEvent.click(saveButton);

			const [calledURL] = (fetch as any).mock.calls.pop();

			expect(
				screen.getByText('name-field-is-required')
			).toBeInTheDocument();

			expect(calledURL).not.toBe('createTemplateURL');
		});
	});
});
