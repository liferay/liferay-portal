/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {saveGeneratedImages} from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/services/saveGeneratedImages';

jest.mock('frontend-js-web', () => ({fetch: jest.fn()}));

const mockFetch = fetch as jest.MockedFunction<typeof fetch>;

const IMAGE_ONE = 'data:image/png;base64,one';
const IMAGE_TWO = 'data:image/png;base64,two';

function response(ok = true) {
	return {
		json: () => Promise.resolve({id: 1, title: 'AI-image.png'}),
		ok,
		statusText: 'Bad Request',
	};
}

function bodyOf(callIndex: number) {
	return JSON.parse(
		(mockFetch.mock.calls[callIndex][1] as RequestInit).body as string
	);
}

describe('saveGeneratedImages', () => {
	beforeEach(() => {
		mockFetch.mockReset();
		mockFetch.mockResolvedValue(response() as never);
	});

	it('uploads a single image to the basic-documents endpoint of the given group, tagged as AI-generated', async () => {
		await saveGeneratedImages([IMAGE_ONE], {groupId: 123});

		expect(mockFetch).toHaveBeenCalledTimes(1);

		const [url, init] = mockFetch.mock.calls[0];

		expect(url).toBe('/o/cms/basic-documents/scopes/123');
		expect((init as RequestInit).method).toBe('POST');

		const body = bodyOf(0);

		expect(body.file.fileBase64).toBe('one');
		expect(body.keywords).toEqual(['AI-generated']);
		expect(body.title).toBe(body.file.name);
	});

	it('defaults to the Files folder when no folder external reference code is given', async () => {
		await saveGeneratedImages([IMAGE_ONE], {groupId: 123});

		expect(bodyOf(0).objectEntryFolderExternalReferenceCode).toBe(
			'L_FILES'
		);
	});

	it('uploads into the given folder when a folder external reference code is provided', async () => {
		await saveGeneratedImages([IMAGE_ONE], {
			groupId: 123,
			objectEntryFolderExternalReferenceCode: 'ABC',
		});

		expect(bodyOf(0).objectEntryFolderExternalReferenceCode).toBe('ABC');
	});

	it('uploads every image and gives each a unique file name', async () => {
		await saveGeneratedImages([IMAGE_ONE, IMAGE_TWO], {groupId: 123});

		expect(mockFetch).toHaveBeenCalledTimes(2);

		const names = mockFetch.mock.calls.map(
			(_, index) => bodyOf(index).file.name
		);

		expect(new Set(names).size).toBe(2);
	});

	it('throws when the upload fails', async () => {
		mockFetch.mockResolvedValueOnce(response(false) as never);

		await expect(
			saveGeneratedImages([IMAGE_ONE], {groupId: 123})
		).rejects.toThrow('Unable to save generated image');
	});
});
