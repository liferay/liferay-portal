/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import SpaceService from '../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {openCMSModal} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import generateContentWithAIAction from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/actions/generateContentWithAIAction';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService',
	() => ({__esModule: true, default: {getSpaces: jest.fn()}})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);

const mockGetSpaces = SpaceService.getSpaces as jest.Mock;
const mockOpenCMSModal = openCMSModal as jest.Mock;

const contentTypes = [{externalReferenceCode: 'a', label: 'A', name: 'a'}];

const data = {
	action: 'generateContentWithAI' as const,
	contentTypes: JSON.stringify(contentTypes),
};

const flushPromises = () => new Promise((resolve) => setTimeout(resolve, 0));

describe('generateContentWithAIAction', () => {
	let fireSpy: jest.SpyInstance;

	beforeEach(() => {
		fireSpy = jest.spyOn(Liferay, 'fire').mockImplementation(() => {});
	});

	afterEach(() => {
		jest.clearAllMocks();
		jest.restoreAllMocks();
	});

	it('logs an error when retrieving the spaces fails', async () => {
		const consoleErrorSpy = jest
			.spyOn(console, 'error')
			.mockImplementation(() => {});
		const error = new Error('Request failed');

		mockGetSpaces.mockRejectedValue(error);

		generateContentWithAIAction(data);

		await flushPromises();

		expect(consoleErrorSpy).toHaveBeenCalledWith(
			'Failed to retrieve spaces:',
			error
		);
		expect(fireSpy).not.toHaveBeenCalled();
		expect(mockOpenCMSModal).not.toHaveBeenCalled();
	});

	it('opens a space selection modal when there is more than one space', async () => {
		mockGetSpaces.mockResolvedValue([
			{name: 'A', siteId: 1},
			{name: 'B', siteId: 2},
		]);

		generateContentWithAIAction(data);

		await flushPromises();

		expect(mockOpenCMSModal).toHaveBeenCalledTimes(1);
		expect(mockOpenCMSModal).toHaveBeenCalledWith(
			expect.objectContaining({
				center: true,
				contentComponent: expect.any(Function),
				size: 'sm',
			})
		);
		expect(fireSpy).not.toHaveBeenCalled();
	});

	it('opens the chat scoped to the single space when there is only one', async () => {
		mockGetSpaces.mockResolvedValue([{name: 'A', siteId: 123}]);

		generateContentWithAIAction(data);

		await flushPromises();

		expect(fireSpy).toHaveBeenCalledTimes(1);
		expect(fireSpy).toHaveBeenCalledWith('openAIAssistantChat', {
			contentTypes,
			context: {spaceId: '123'},
		});
		expect(mockOpenCMSModal).not.toHaveBeenCalled();
	});

	it('opens the chat without a space when there are no spaces', async () => {
		mockGetSpaces.mockResolvedValue([]);

		generateContentWithAIAction(data);

		await flushPromises();

		expect(fireSpy).toHaveBeenCalledTimes(1);
		expect(fireSpy).toHaveBeenCalledWith('openAIAssistantChat', {
			contentTypes,
			context: {spaceId: undefined},
		});
		expect(mockOpenCMSModal).not.toHaveBeenCalled();
	});
});
