/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import PreviewButton from '../../../src/main/resources/META-INF/resources/js/article/PreviewButton';

const NAMESPACE = 'namespace';

const renderComponent = (articleId) => {
	const articleIdInput = document.createElement('input');

	articleIdInput.id = `${NAMESPACE}articleId`;
	articleIdInput.value = articleId;

	document.body.appendChild(articleIdInput);

	return render(
		<PreviewButton
			disabled={false}
			getPreviewURL={() => 'http://localhost/preview'}
			namespace={NAMESPACE}
			newArticle={!articleId}
			saveAsDraftURL="http://localhost/save-as-draft"
		/>
	);
};

describe('PreviewButton', () => {
	beforeEach(() => {
		Liferay.componentReady = jest.fn(() => Promise.resolve({}));
	});

	afterEach(() => {
		document.getElementById(`${NAMESPACE}articleId`)?.remove();

		delete Liferay.componentReady;
	});

	it('asks to save as draft or publish a new web content before previewing it', () => {
		renderComponent('');

		const button = screen.getByRole('button', {
			name: 'save-as-draft-or-publish-the-web-content-to-preview-it',
		});

		expect(button).toBeDisabled();
		expect(button).toHaveAttribute(
			'title',
			'save-as-draft-or-publish-the-web-content-to-preview-it'
		);
	});

	it('says a draft will be saved for a drafted or published web content', () => {
		renderComponent('12345');

		const button = screen.getByRole('button', {
			name: 'a-draft-will-be-saved-before-displaying-the-preview',
		});

		expect(button).toBeEnabled();
		expect(button).toHaveAttribute(
			'title',
			'a-draft-will-be-saved-before-displaying-the-preview'
		);
	});
});
