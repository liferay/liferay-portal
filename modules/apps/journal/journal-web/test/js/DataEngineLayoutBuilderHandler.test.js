/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, waitFor} from '@testing-library/react';
import {openModal} from 'frontend-js-components-web';
import {postForm} from 'frontend-js-web';

import DataEngineLayoutBuilderHandler from '../../src/main/resources/META-INF/resources/js/DataEngineLayoutBuilderHandler.es';

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
	openToast: jest.fn(),
}));
jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	postForm: jest.fn(),
}));

const namespace = 'test_';

const submit = async ({
	showStructureKeyChangesWarning = true,
	structureKey,
}) => {
	const handler = DataEngineLayoutBuilderHandler({
		namespace,
		originalStructureKey: 'STRUCTURE_KEY',
		showStructureKeyChangesWarning,
	});

	document.getElementById(`${namespace}structureKey`).value = structureKey;

	fireEvent.submit(document.getElementById(`${namespace}fm`));

	await waitFor(() => {
		expect(
			openModal.mock.calls.length + postForm.mock.calls.length
		).toBeGreaterThan(0);
	});

	handler.dispose();
};

describe('DataEngineLayoutBuilderHandler > structure key changes', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		document.body.innerHTML = `
			<form id="${namespace}fm">
				<input id="${namespace}name" value="WC Structure Name" />
				<input id="${namespace}structureKey" />
			</form>
		`;

		global.Liferay.component = jest.fn();
		global.Liferay.componentReady = jest.fn().mockResolvedValue({
			current: {
				state: {
					dataDefinition: {
						defaultLanguageId: 'en_US',
						serialize: () => ({}),
					},
					dataLayout: {serialize: () => ({})},
				},
			},
		});
	});

	afterEach(() => {
		document.body.innerHTML = '';
	});

	it('asks for confirmation before saving a changed structure key', async () => {
		await submit({structureKey: 'STRUCTURE_KEY_EDITED'});

		expect(postForm).not.toHaveBeenCalled();
		expect(openModal).toHaveBeenCalledWith(
			expect.objectContaining({
				bodyHTML: expect.stringContaining(
					'are-you-sure-you-want-to-continue-and-save-the-key'
				),
				title: 'structure-key-changes',
			})
		);

		const [{buttons}] = openModal.mock.calls[0];

		buttons
			.find(({label}) => label === 'save')
			.onClick({processClose: jest.fn()});

		expect(postForm).toHaveBeenCalledWith(
			document.getElementById(`${namespace}fm`),
			expect.anything()
		);
	});

	it('saves without confirmation when the structure key does not change', async () => {
		await submit({structureKey: 'STRUCTURE_KEY'});

		expect(openModal).not.toHaveBeenCalled();
		expect(postForm).toHaveBeenCalled();
	});

	it('saves without confirmation when the warning is off', async () => {
		await submit({
			showStructureKeyChangesWarning: false,
			structureKey: 'STRUCTURE_KEY_EDITED',
		});

		expect(openModal).not.toHaveBeenCalled();
		expect(postForm).toHaveBeenCalled();
	});
});
