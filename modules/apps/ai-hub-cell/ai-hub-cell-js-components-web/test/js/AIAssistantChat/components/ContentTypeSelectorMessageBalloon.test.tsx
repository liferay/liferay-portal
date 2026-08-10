/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import ContentTypeSelectorMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/ContentTypeSelectorMessageBalloon';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const CONTENT_TYPES = [
	{externalReferenceCode: 'L_CMS_BLOG', label: 'Blog', name: 'C_Blog'},
	{
		externalReferenceCode: 'L_CMS_BASIC_WEB_CONTENT',
		label: 'Basic Web Content',
		name: 'C_BasicWebContent',
	},
];

describe('ContentTypeSelectorMessageBalloon', () => {
	beforeEach(() => {
		mockFetch.mockReset();

		(Liferay.Util.openToast as jest.Mock).mockClear();
	});

	it('stores the selected type and its object fields in the context ref and sends the message', async () => {
		const objectFields = {
			items: [{businessType: 'Text', name: 'headline'}],
		};

		mockFetch.mockResolvedValue({
			json: () => Promise.resolve(objectFields),
			ok: true,
		} as never);

		const contextRef = {current: {}};
		const sendMessage = jest.fn();
		const setIsGenerating = jest.fn();

		render(
			<ContentTypeSelectorMessageBalloon
				contentTypes={CONTENT_TYPES}
				contextRef={contextRef}
				message="What type of content do you want to generate?"
				sendMessage={sendMessage}
				setIsGenerating={setIsGenerating}
			/>
		);

		expect(screen.queryByRole('button', {name: 'send'})).toBeNull();

		await userEvent.selectOptions(
			screen.getByLabelText('content-type'),
			'L_CMS_BASIC_WEB_CONTENT'
		);

		expect(mockFetch).toHaveBeenCalledWith(
			expect.stringContaining(
				'by-external-reference-code/L_CMS_BASIC_WEB_CONTENT/object-fields'
			)
		);
		expect(setIsGenerating).toHaveBeenCalledWith(true);

		await waitFor(() =>
			expect(sendMessage).toHaveBeenCalledWith(
				'generate Basic Web Content'
			)
		);

		expect(contextRef.current).toEqual({
			objectDefinitionName: 'C_BasicWebContent',
			objectFields: JSON.stringify(objectFields),
		});
	});

	it('stops generating and reports the failure when the object fields cannot be fetched', async () => {
		mockFetch.mockResolvedValue({ok: false} as never);

		const sendMessage = jest.fn();
		const setIsGenerating = jest.fn();

		render(
			<ContentTypeSelectorMessageBalloon
				contentTypes={CONTENT_TYPES}
				contextRef={{current: {}}}
				message="What type of content do you want to generate?"
				sendMessage={sendMessage}
				setIsGenerating={setIsGenerating}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('content-type'),
			'L_CMS_BASIC_WEB_CONTENT'
		);

		await waitFor(() =>
			expect(Liferay.Util.openToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			)
		);

		expect(setIsGenerating).toHaveBeenLastCalledWith(false);
		expect(sendMessage).not.toHaveBeenCalled();

		const select = screen.getByLabelText('content-type');

		expect(select).toBeEnabled();
		expect(select).toHaveValue('');
	});
});
