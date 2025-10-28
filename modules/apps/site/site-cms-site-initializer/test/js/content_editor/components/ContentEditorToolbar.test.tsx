/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ContentEditorToolbar from '../../../../src/main/resources/META-INF/resources/js/content_editor/components/ContentEditorToolbar';

const renderComponent = () => {
	return render(
		<>
			<ContentEditorToolbar
				backURL=""
				displayDate="2025-10-31T13:00"
				hasWorkflow={false}
				headerTitle="New Content"
				type="Basic Content"
			/>

			<form
				className="lfr-main-form-container"
				data-testid="form"
				id="formId"
			></form>
		</>
	);
};

describe('ContentEditorToolbar', () => {
	it('publishes the content pressing ctrl + alt + Enter', () => {
		renderComponent();

		const form = screen.getByTestId('form');
		const submitSpy = jest.fn((event: Event) => event.preventDefault());

		form.addEventListener('submit', submitSpy);

		const event = new KeyboardEvent('keydown', {
			altKey: true,
			bubbles: true,
			ctrlKey: true,
			key: 'Enter',
		});

		document.body.dispatchEvent(event);

		expect(submitSpy).toHaveBeenCalledTimes(1);
	});
});
