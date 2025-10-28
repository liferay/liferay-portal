/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import HTMLEditorModal from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/HTMLEditorModal';

const renderModal = async ({initialContent = '', onClose, onSave} = {}) => {
	document.body.createTextRange = () => {
		const textRange = {
			getBoundingClientRect: () => 1,
			getClientRects: () => 1,
		};

		return textRange;
	};

	window.document.createRange = () => ({
		cloneRange: (range) => range,
		getBoundingClientRect: () => 1,
		getClientRects: () => 1,
		setEnd: () => {},
		setStart: () => {},
	});

	render(
		<HTMLEditorModal
			initialContent={initialContent}
			onClose={onClose}
			onSave={onSave}
		/>
	);

	await act(async () => {
		jest.advanceTimersByTime(2000);
	});
};

describe('HTMLEditorModal', () => {
	afterAll(() => {
		jest.useRealTimers();
	});

	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('modal is rendered', async () => {
		await renderModal();

		expect(screen.getByText('save')).toBeInTheDocument();
	});

	it('sets initialContent to the editor', async () => {
		await renderModal({initialContent: 'Hello Jordi Kappler'});

		expect(
			screen.queryAllByText('Hello Jordi Kappler')[0]
		).toBeInTheDocument();
	});

	it('defaults to column view type', async () => {
		await renderModal();

		const editor = document.querySelector(
			'.page-editor__html-editor-modal__editor-container > div'
		);

		expect(editor).toHaveClass('w-50');
	});

	it('changes to row view type when clicking the display horizontally button', async () => {
		await renderModal();

		fireEvent.click(screen.getByTitle('display-horizontally'));

		const editor = document.querySelector(
			'.page-editor__html-editor-modal__editor-container > div'
		);

		expect(editor).toHaveClass('w-100');
	});

	it('changes to full-screen view type when clicking the full-screen button', async () => {
		await renderModal();

		fireEvent.click(screen.getByTitle('full-screen'));

		expect(
			document.querySelector(
				'.page-editor__html-editor-modal__preview-rows'
			)
		).not.toBeInTheDocument();
	});

	it('calls close callback when cliking close button', async () => {
		const onClose = jest.fn();

		await renderModal({onClose});

		fireEvent.click(screen.getByText('cancel'));

		jest.advanceTimersByTime(1000);

		expect(onClose).toHaveBeenCalled();
	});
});
