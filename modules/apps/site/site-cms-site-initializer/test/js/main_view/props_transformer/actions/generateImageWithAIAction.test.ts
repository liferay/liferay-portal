/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import generateImageWithAIAction from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/generateImageWithAIAction';

describe('generateImageWithAIAction', () => {
	let fireSpy: jest.SpyInstance;

	beforeEach(() => {
		fireSpy = jest.spyOn(Liferay, 'fire').mockImplementation(() => {});
	});

	afterEach(() => {
		fireSpy.mockRestore();
	});

	it('fires the open-chat event with the given message for a direct save to Files', () => {
		generateImageWithAIAction({
			action: 'generateImageWithAI',
			message: 'Generate Single Image',
		});

		expect(fireSpy).toHaveBeenCalledWith('openAIAssistantChat', {
			context: {
				groupId: undefined,
				objectEntryFolderExternalReferenceCode: undefined,
			},
			message: 'Generate Single Image',
		});
	});

	it('forwards the destination Space and folder in the chat context when provided', () => {
		generateImageWithAIAction({
			action: 'generateImageWithAI',
			groupId: '123',
			message: 'Generate Single Image',
			objectEntryFolderExternalReferenceCode: 'L_FILES',
		});

		expect(fireSpy).toHaveBeenCalledWith('openAIAssistantChat', {
			context: {
				groupId: '123',
				objectEntryFolderExternalReferenceCode: 'L_FILES',
			},
			message: 'Generate Single Image',
		});
	});
});
