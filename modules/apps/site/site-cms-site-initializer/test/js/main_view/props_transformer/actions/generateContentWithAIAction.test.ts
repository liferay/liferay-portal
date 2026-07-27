/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import generateContentWithAIAction from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/generateContentWithAIAction';

const contentTypes = [{externalReferenceCode: 'a', label: 'A', name: 'a'}];

const data = {
	action: 'generateContentWithAI' as const,
	contentTypes: JSON.stringify(contentTypes),
};

describe('generateContentWithAIAction', () => {
	let fireSpy: jest.SpyInstance;

	beforeEach(() => {
		fireSpy = jest.spyOn(Liferay, 'fire').mockImplementation(() => {});
	});

	afterEach(() => {
		jest.clearAllMocks();
		jest.restoreAllMocks();
	});

	it('opens the chat with the parsed content types', () => {
		generateContentWithAIAction(data);

		expect(fireSpy).toHaveBeenCalledTimes(1);
		expect(fireSpy).toHaveBeenCalledWith('openAIAssistantChat', {
			contentTypes,
		});
	});
});
