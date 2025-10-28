/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import {StoreContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CommentForm from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/comments/components/CommentForm';

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/common/components/Editor',
	() =>
		({autoFocus, id, initialValue, label, onChange, placeholder}) => {
			return (
				<textarea
					aria-label={label}
					autoFocus={autoFocus}
					defaultValue={initialValue}
					id={id}
					onChange={onChange}
					placeholder={placeholder}
				></textarea>
			);
		}
);

const renderForm = (props) =>
	render(
		<StoreContextProvider
			initialState={[
				{},
				{
					defaultEditorConfigurations: {comment: {editorConfig: {}}},
					portletNamespace: '_testNamespace_',
				},
			]}
		>
			<CommentForm
				onCancelButtonClick={() => {}}
				onSubmitButtonClick={() => {}}
				onTextareaChange={() => {}}
				submitButtonLabel="submit"
				textareaContent=""
				{...props}
			/>
		</StoreContextProvider>
	);

describe('CommentForm', () => {
	afterEach(cleanup);

	afterEach(jest.clearAllMocks);

	it('automatically focuses textarea if autoFocus is true', () => {
		const {getByPlaceholderText} = renderForm({autoFocus: true});

		expect(getByPlaceholderText('type-your-comment-here')).toHaveFocus();
	});

	it('sets the textarea given id', () => {
		const {getByPlaceholderText} = renderForm({id: 'myId'});

		expect(getByPlaceholderText('type-your-comment-here').id).toBe('myId');
	});

	it('has an label for the textarea', () => {
		const {getByLabelText} = renderForm({id: 'id'});

		expect(getByLabelText('add-comment').id).toBe('id');
	});

	it('sets given submit button label', () => {
		const {getByText} = renderForm({
			showButtons: true,
			submitButtonLabel: 'customLabel',
		});

		expect(getByText('customLabel')).toBeInTheDocument();
	});

	it('shows buttons if showButtons is true', () => {
		const {getByText} = renderForm({
			showButtons: true,
			submitButtonLabel: 'mySubmit',
		});

		expect(getByText('cancel')).toBeInTheDocument();
		expect(getByText('mySubmit')).toBeInTheDocument();
	});

	it('disables everything if form is loading', () => {
		const {getAllByRole, getByPlaceholderText} = renderForm({
			loading: true,
			showButtons: true,
			submitButtonLabel: 'submit',
		});

		expect(getByPlaceholderText('type-your-comment-here')).toBeDisabled();
		expect(getAllByRole('button')[0]).toBeDisabled();
		expect(getAllByRole('button')[1]).toBeDisabled();
	});

	it('calls onCancelButtonClick when cancel button is clicked', () => {
		const onCancel = jest.fn();

		const {getByText} = renderForm({
			onCancelButtonClick: onCancel,
			showButtons: true,
		});

		fireEvent.click(getByText('cancel'));

		expect(onCancel).toHaveBeenCalled();
	});

	it('calls onFormFocus when form is focused', () => {
		const onFormFocus = jest.fn();

		renderForm({autoFocus: true, onFormFocus});

		expect(onFormFocus).toHaveBeenCalled();
	});

	it('sets given textareaContent as default textarea content', () => {
		const {getByPlaceholderText} = renderForm({
			textareaContent: 'This is Something',
		});

		expect(getByPlaceholderText('type-your-comment-here').value).toBe(
			'This is Something'
		);
	});

	it('disables submit button until there is some text', () => {
		const onSubmit = jest.fn();

		const {getByText} = renderForm({
			onSubmitButtonClick: onSubmit,
			showButtons: true,
			submitButtonLabel: 'tryToSubmit',
		});

		fireEvent.click(getByText('tryToSubmit'));

		expect(onSubmit).not.toHaveBeenCalled();
	});

	it('calls onSubmitButtonClick when submit button is clicked', () => {
		const onSubmit = jest.fn();

		const {getByText} = renderForm({
			onSubmitButtonClick: onSubmit,
			showButtons: true,
			submitButtonLabel: 'submitForm',
			textareaContent: 'tarta',
		});

		fireEvent.click(getByText('submitForm'));

		expect(onSubmit).toHaveBeenCalled();
	});

	it('calls onTextareaChange callback when textare is changed', async () => {
		const onChange = jest.fn();

		const {getByPlaceholderText} = renderForm({
			onTextareaChange: onChange,
		});

		await userEvent.type(
			getByPlaceholderText('type-your-comment-here'),
			'This is my comment'
		);

		expect(onChange).toHaveBeenCalled();
	});
});
