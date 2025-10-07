/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, cleanup, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import App from '../../../../src/main/resources/META-INF/resources/js/ddm_template_editor/components/App';

const renderApp = ({initialScript = ''} = {}) => {
	return render(
		<App
			editorAutocompleteData={{}}
			portletNamespace="portletNamespace"
			script={initialScript}
			showCacheableWarning
			templateVariableGroups={[
				{
					items: [
						{
							content: 'this is a variable 1',
							label: 'variableTemplate1',
							repetable: true,
							tooltip: 'this is a tooltip 1',
						},
						{
							content: 'this is a variable 2',
							label: 'variableTemplate2',
							repetable: true,
							tooltip: 'this is a tooltip 2',
						},
					],
					label: 'group',
				},
			]}
		/>
	);
};

describe('', () => {
	beforeAll(() => {
		Liferay.Util.unescapeHTML = (str) => str;
	});

	beforeEach(() => {
		cleanup();

		window.document.createRange = () => ({
			cloneRange: (range) => range,
			getBoundingClientRect: () => 1,
			getClientRects: () => 1,
			setEnd: () => {},
			setStart: () => {},
		});

		if (global.document) {
			const saveButton = global.document.createElement('button');
			saveButton.classList.add('save-button');

			const saveAndContinueButton =
				global.document.createElement('button');
			saveAndContinueButton.classList.add('save-and-continue-button');

			global.document.body.appendChild(saveButton);
			global.document.body.appendChild(saveAndContinueButton);
		}
		global.Liferay.SideNavigation = {instance: () => {}};
		global.Liferay.on = () => ({
			detach: () => {},
		});
	});

	it('renders', () => {
		renderApp({
			initialScript: 'thisistheinitialscript',
		});

		expect(screen.getByText('thisistheinitialscript')).toBeInTheDocument();
	});

	it('includes the variable in the script when clicked', async () => {
		renderApp();

		const variableButton = screen.getByText('variableTemplate1');

		await userEvent.click(variableButton);

		expect(screen.getByText('this is a variable 1')).toBeInTheDocument();
	});

	it('shows a popover with the tooltip when the preview icon is hovered', () => {
		renderApp();

		const variableButton = screen.getByText('variableTemplate1');

		fireEvent.mouseEnter(variableButton.querySelector('.preview-icon'));

		expect(screen.getByText('this is a tooltip 1')).toBeInTheDocument();
	});

	it('filters variable groups when search', async () => {
		renderApp();

		const searchInput = screen.getByLabelText('search');

		await userEvent.type(searchInput, 'variableTemplate2');

		expect(screen.queryByText('variableTemplate2')).toBeInTheDocument();
		expect(screen.queryByText('variableTemplate1')).not.toBeInTheDocument();
	});

	it('no result when searching', async () => {
		renderApp();

		const searchInput = screen.getByLabelText('search');

		await userEvent.type(searchInput, 'anotherVariable');

		expect(screen.queryByText('no-results-found')).toBeInTheDocument();
	});

	it('enables focus trap when clicking ctrl + m', () => {
		const {container} = renderApp();

		const editor = container.querySelector('.CodeMirror').CodeMirror;

		expect(editor.state.keyMaps).not.toContain(
			expect.objectContaining({name: 'tabKey'})
		);

		act(() => {
			editor.triggerOnKeyDown({
				altKey: false,
				ctrlKey: true,
				keyCode: 77,
				metaKey: false,
				shiftKey: false,
				type: 'keydown',
			});
		});

		expect(editor.state.keyMaps).toEqual(
			expect.arrayContaining([expect.objectContaining({name: 'tabKey'})])
		);
	});
});
