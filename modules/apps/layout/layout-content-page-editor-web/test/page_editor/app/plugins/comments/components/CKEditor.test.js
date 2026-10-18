/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import {config} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index';
import {StoreContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CommentForm from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/comments/components/CommentForm';

jest.mock('frontend-editor-ckeditor-web', () => ({
	CKEditor5BalloonEditor: ({config, onChange, onReady}) => {
		const {useEffect, useRef} = jest.requireActual('react');

		const textareaRef = useRef();

		useEffect(() => {
			const editor = {
				getData: () => textareaRef.current.value,
				setData: (data) => {
					textareaRef.current.value = data;
				},
			};

			onReady?.(editor);
		}, [onReady]);

		return (
			<textarea
				aria-label={config.label}
				defaultValue={config.initialData}
				onChange={(event) =>
					onChange(event, {getData: () => event.target.value})
				}
				ref={textareaRef}
			/>
		);
	},
}));

const renderForm = (textareaContent) => (
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
			submitButtonLabel="comment"
			textareaContent={textareaContent}
		/>
	</StoreContextProvider>
);

describe('CKEditor', () => {
	beforeEach(() => {
		Liferay.FeatureFlags['LPD-11235'] = false;

		config.defaultEditorConfigurations = {comment: {editorConfig: {}}};
	});

	afterEach(() => {
		delete config.defaultEditorConfigurations;
		delete Liferay.FeatureFlags['LPD-11235'];
	});

	it('clears the comment when the form resets its content', async () => {
		const {rerender} = render(renderForm(''));

		await userEvent.type(
			screen.getByLabelText('add-comment'),
			'This is a comment'
		);

		rerender(renderForm('This is a comment'));

		expect(screen.getByLabelText('add-comment')).toHaveValue(
			'This is a comment'
		);

		rerender(renderForm(''));

		expect(screen.getByLabelText('add-comment')).toHaveValue('');
	});
});
