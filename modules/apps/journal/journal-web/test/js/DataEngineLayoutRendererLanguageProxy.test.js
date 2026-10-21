/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';

import DataEngineLayoutRendererLanguageProxy from '../../src/main/resources/META-INF/resources/js/DataEngineLayoutRendererLanguageProxy.es';

const NAMESPACE = '_com_liferay_journal_web_portlet_JournalPortlet_';

describe('DataEngineLayoutRendererLanguageProxy', () => {
	let defaultLanguageIdInput;
	let proxy;
	let updateEditingLanguageId;

	const renderForm = ({
		currentLanguageId = 'en_US',
		defaultLanguageId = 'en_US',
		renderedDefaultLanguageId = 'en_US',
		renderedEditingLanguageId = 'en_US',
	} = {}) => {
		defaultLanguageIdInput.value = defaultLanguageId;

		const languages = {
			defaultLanguageId: renderedDefaultLanguageId,
			editingLanguageId: renderedEditingLanguageId,
		};

		global.Liferay.componentReady = jest.fn().mockResolvedValue({
			reactComponentRef: {
				current: {
					get: (key) => languages[key],
					updateEditingLanguageId,
				},
			},
		});

		proxy = DataEngineLayoutRendererLanguageProxy({
			currentLanguageId,
			namespace: NAMESPACE,
		});
	};

	beforeEach(() => {
		jest.clearAllMocks();

		defaultLanguageIdInput = document.createElement('input');

		defaultLanguageIdInput.id = `${NAMESPACE}defaultLanguageId`;
		defaultLanguageIdInput.type = 'hidden';

		document.body.appendChild(defaultLanguageIdInput);

		updateEditingLanguageId = jest.fn();
	});

	afterEach(() => {
		proxy.dispose();

		defaultLanguageIdInput.remove();
	});

	it('does not reload the form when it is already rendered in the current language', async () => {
		renderForm();

		await waitFor(() =>
			expect(Liferay.after).toHaveBeenCalledWith(
				'inputLocalized:localeChanged',
				expect.any(Function)
			)
		);

		expect(updateEditingLanguageId).not.toHaveBeenCalled();
	});

	it('reloads the form when it is rendered in another language', async () => {
		renderForm({renderedEditingLanguageId: 'es_ES'});

		await waitFor(() =>
			expect(updateEditingLanguageId).toHaveBeenCalledWith({
				defaultLanguageId: 'en_US',
				editingLanguageId: 'en_US',
				preserveValue: true,
			})
		);
	});

	it('reloads the form when the default language changed', async () => {
		renderForm({defaultLanguageId: 'es_ES'});

		await waitFor(() =>
			expect(updateEditingLanguageId).toHaveBeenCalledWith({
				defaultLanguageId: 'es_ES',
				editingLanguageId: 'en_US',
				preserveValue: true,
			})
		);
	});

	it('reloads the form when the current language is not the default language', async () => {
		renderForm({
			currentLanguageId: 'es_ES',
			renderedEditingLanguageId: 'es_ES',
		});

		await waitFor(() =>
			expect(updateEditingLanguageId).toHaveBeenCalledWith({
				defaultLanguageId: 'en_US',
				editingLanguageId: 'es_ES',
				preserveValue: true,
			})
		);
	});

	it('reloads the form when the current language is not the site default language', async () => {
		renderForm({
			currentLanguageId: 'es_ES',
			defaultLanguageId: 'es_ES',
			renderedDefaultLanguageId: 'es_ES',
			renderedEditingLanguageId: 'es_ES',
		});

		await waitFor(() =>
			expect(updateEditingLanguageId).toHaveBeenCalledWith({
				defaultLanguageId: 'es_ES',
				editingLanguageId: 'es_ES',
				preserveValue: true,
			})
		);
	});
});
