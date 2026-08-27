/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';

import useAIChat from '../../../src/main/resources/META-INF/resources/js/AIAssistantChat/useAIChat';

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/AIAssistantChat/api',
	() => ({
		createEventSource: jest.fn(() => Promise.resolve(null)),
		executeHttpRequestAction: jest.fn(() => Promise.resolve()),
		postChatByExternalReferenceCodeMessage: jest.fn(() =>
			Promise.resolve()
		),
	})
);

describe('useAIChat', () => {
	it('keeps the generating indicator until every balloon has finished', async () => {
		const {result} = renderHook(() =>
			useAIChat({instructionDefinitionScope: ''})
		);

		await act(async () => {});

		act(() => {
			result.current.setBalloonGenerating('balloon-1', true);
			result.current.setBalloonGenerating('balloon-2', true);
		});

		expect(result.current.isGenerating).toBe(true);

		act(() => {
			result.current.setBalloonGenerating('balloon-1', false);
		});

		expect(result.current.isGenerating).toBe(true);

		act(() => {
			result.current.setBalloonGenerating('balloon-2', false);
		});

		expect(result.current.isGenerating).toBe(false);
	});
});
