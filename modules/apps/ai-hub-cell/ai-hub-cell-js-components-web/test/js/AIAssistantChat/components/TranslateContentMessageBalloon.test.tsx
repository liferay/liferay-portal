/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import TranslateContentMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/TranslateContentMessageBalloon';
import {putAgentInstanceResume} from '../../../../src/main/resources/META-INF/resources/js/TranslateContent/api';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/TranslateContent/api'
);

const mockPutAgentInstanceResume =
	putAgentInstanceResume as jest.MockedFunction<
		typeof putAgentInstanceResume
	>;

function renderComponent(props = {}) {
	const setIsGenerating = jest.fn();

	const view = render(
		<TranslateContentMessageBalloon
			agentInstanceId={12345}
			availableLanguageIds={['es_ES', 'pt_BR', 'fr_FR']}
			setIsGenerating={setIsGenerating}
			sourceLanguageIdRef={{current: 'en_US'}}
			{...props}
		/>
	);

	return {setIsGenerating, ...view};
}

describe('TranslateContentMessageBalloon', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockPutAgentInstanceResume.mockResolvedValue(undefined);
	});

	describe('when results are available', () => {
		let originalUnescapeHTML: typeof Liferay.Util.unescapeHTML;

		beforeEach(() => {
			originalUnescapeHTML = Liferay.Util.unescapeHTML;
			Liferay.Util.unescapeHTML = (value: string) => value;
		});

		afterEach(() => {
			Liferay.Util.unescapeHTML = originalUnescapeHTML;
		});

		it('shows a confirmation when the content has been translated', () => {
			renderComponent({
				results: [
					{targetLanguageId: 'es_ES'},
					{targetLanguageId: 'pt_BR'},
				],
			});

			expect(
				screen.getByText('the-content-has-been-translated')
			).toBeInTheDocument();
		});

		it('fires the auto translate event for each result with fields', () => {
			renderComponent({
				results: [
					{
						fields: {title: 'Hola'},
						targetLanguageId: 'es_ES',
					},
				],
			});

			expect(Liferay.fire).toHaveBeenCalledWith(
				'localizationSelect:autoTranslate',
				{
					fields: {title: 'Hola'},
					languageId: 'es_ES',
				}
			);
		});

		it('does not fire the auto translate event when a result has no fields', () => {
			renderComponent({
				results: [{targetLanguageId: 'es_ES'}],
			});

			expect(Liferay.fire).not.toHaveBeenCalledWith(
				'localizationSelect:autoTranslate',
				expect.anything()
			);
		});
	});

	describe('when selecting languages', () => {
		it('hides the translate button until a language is selected', () => {
			renderComponent();

			expect(
				screen.queryByRole('button', {name: 'translate'})
			).not.toBeInTheDocument();
		});

		it('preselects requested languages that are available', () => {
			renderComponent({requestedLanguageIds: ['es_ES', 'de_DE']});

			expect(
				screen.getByRole('button', {name: 'translate'})
			).toBeEnabled();
		});

		it('submits the resume request for languages without an existing translation', async () => {
			const {setIsGenerating} = renderComponent({
				requestedLanguageIds: ['es_ES'],
			});

			await userEvent.click(
				screen.getByRole('button', {name: 'translate'})
			);

			expect(setIsGenerating).toHaveBeenCalledWith(true);
			expect(mockPutAgentInstanceResume).toHaveBeenCalledWith(
				expect.objectContaining({
					agentInstanceId: 12345,
					context: expect.objectContaining({
						sourceLanguageId: 'en_US',
						targetLanguageIds: JSON.stringify(['es_ES']),
					}),
				})
			);
		});
	});

	describe('when a selected language is already translated', () => {
		let fixture: HTMLDivElement;

		beforeEach(() => {
			fixture = document.createElement('div');
			fixture.innerHTML = `
				<div data-field-type="text" data-localizable="true">
					<input name="field_es_ES" type="hidden" value="Hola" />
				</div>
			`;
			document.body.appendChild(fixture);
		});

		afterEach(() => {
			fixture.remove();
		});

		it('asks for confirmation before overwriting', async () => {
			renderComponent({requestedLanguageIds: ['es_ES']});

			await userEvent.click(
				screen.getByRole('button', {name: 'translate'})
			);

			expect(
				screen.getByText(
					'some-of-the-selected-languages-already-have-a-translation.-what-do-you-want-to-do'
				)
			).toBeInTheDocument();
			expect(mockPutAgentInstanceResume).not.toHaveBeenCalled();
		});

		it('overwrites every translation when confirming', async () => {
			const {setIsGenerating} = renderComponent({
				requestedLanguageIds: ['es_ES'],
			});

			await userEvent.click(
				screen.getByRole('button', {name: 'translate'})
			);
			await userEvent.click(
				screen.getByRole('button', {name: 'overwrite-all'})
			);

			expect(setIsGenerating).toHaveBeenCalledWith(true);
			expect(mockPutAgentInstanceResume).toHaveBeenCalledWith(
				expect.objectContaining({
					context: expect.objectContaining({
						targetLanguageIds: JSON.stringify(['es_ES']),
					}),
				})
			);
		});

		it('lets the user review and overwrite the selected translations', async () => {
			const {setIsGenerating} = renderComponent({
				requestedLanguageIds: ['es_ES'],
			});

			await userEvent.click(
				screen.getByRole('button', {name: 'translate'})
			);
			await userEvent.click(screen.getByRole('button', {name: 'review'}));

			expect(screen.getByRole('checkbox', {name: 'es_ES'})).toBeChecked();

			await userEvent.click(
				screen.getByRole('button', {name: 'overwrite'})
			);

			expect(setIsGenerating).toHaveBeenCalledWith(true);
			expect(mockPutAgentInstanceResume).toHaveBeenCalled();
		});
	});

	it('stops generating when the resume request fails', async () => {
		mockPutAgentInstanceResume.mockRejectedValue(new Error('boom'));

		const {setIsGenerating} = renderComponent({
			requestedLanguageIds: ['es_ES'],
		});

		await act(async () => {
			await userEvent.click(
				screen.getByRole('button', {name: 'translate'})
			);
		});

		expect(setIsGenerating).toHaveBeenCalledWith(true);
		expect(setIsGenerating).toHaveBeenCalledWith(false);
	});
});
