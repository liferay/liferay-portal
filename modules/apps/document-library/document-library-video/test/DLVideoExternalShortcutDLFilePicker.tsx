/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	type RenderResult,
	fireEvent,
	render,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import {
	DLVideoExternalShortcutDLFilePicker,
	type Fields,
} from '../src/main/resources/META-INF/resources/js';

declare global {
	interface Window {
		onFilePickCallback: jest.Mock;
	}
}

window.onFilePickCallback = jest.fn();

const defaultProps = {
	getDLVideoExternalShortcutFieldsURL:
		'http://localhost/getDLVideoExternalShortcutFieldsURL',
	namespace: 'namespace',
	onFilePickCallback: 'onFilePickCallback',
};

const renderComponent = (
	props: React.ComponentProps<typeof DLVideoExternalShortcutDLFilePicker>
): RenderResult => render(<DLVideoExternalShortcutDLFilePicker {...props} />);

describe('DLVideoExternalShortcutDLFilePicker', () => {
	describe('when rendered with the default props', () => {
		let result: RenderResult;

		beforeEach(() => {
			result = renderComponent(defaultProps);
		});

		it('has a url input', () => {
			expect(result.getByLabelText('video-url')).toBeInTheDocument();
		});

		it('has a video preview placeholder with video icon', () => {
			expect(
				document.querySelector(
					'.video-preview .video-preview-placeholder'
				)
			).toBeInTheDocument();
			expect(
				document.querySelector('svg.lexicon-icon-video')
			).toBeInTheDocument();
		});
	});

	describe('when rendered with initial video', () => {
		let result: RenderResult;
		const props = {
			dlVideoExternalShortcutHTML: '<iframe data-video-liferay></iframe>',
			dlVideoExternalShortcutURL: 'VIDEO-URL',
		};

		beforeEach(() => {
			result = renderComponent({...defaultProps, ...props});
		});

		it('has a url input filled with the video url', () => {
			expect(
				(result.getByLabelText('video-url') as HTMLInputElement).value
			).toBe(props.dlVideoExternalShortcutURL);
		});

		it('has a video preview with embedded iframe', () => {
			expect(
				document.querySelector('iframe[data-video-liferay]')
			).toBeInTheDocument();
		});
	});

	describe('when there is a valid server response', () => {
		const responseFields: Fields = {
			DESCRIPTION: 'DESCRIPTION',
			HTML: '<iframe data-video-liferay></iframe>',
			THUMBNAIL_URL: 'https://thumbnail-url',
			TITLE: 'VIDEO TITLE',
			URL: 'https://video-url.com',
		};

		beforeEach(async () => {
			jest.useFakeTimers();

			fetch.mockResponseOnce(JSON.stringify(responseFields));

			const {getByLabelText} = renderComponent(defaultProps);

			fireEvent.change(getByLabelText('video-url'), {
				target: {value: 'https://video-url.com'},
			});

			jest.advanceTimersByTime(500);

			await waitForElementToBeRemoved(() =>
				document.querySelector('span.loading-animation')
			);
		});

		afterEach(() => {
			jest.clearAllTimers();
			jest.clearAllMocks();
		});

		afterAll(() => {
			jest.useRealTimers();
		});

		it('sends a GET request to the server with supported URL', () => {
			expect(fetch).toHaveBeenCalledWith(
				'http://localhost/getDLVideoExternalShortcutFieldsURL?namespacedlVideoExternalShortcutURL=https%3A%2F%2Fvideo-url.com',
				expect.anything()
			);
		});

		it('updates the video preview with the response markup from the server', () => {
			expect(
				document.querySelector('iframe[data-video-liferay]')
			).toBeInTheDocument();
		});

		it('calls the onFilePickCallback with the responseFields', () => {
			expect(window.onFilePickCallback).toHaveBeenCalledWith(
				responseFields
			);
		});
	});

	describe('when there is an invalid server response', () => {
		let result: RenderResult;

		beforeEach(async () => {
			jest.useFakeTimers();
			fetch.mockResponseOnce('');

			result = renderComponent(defaultProps);
			const {getByLabelText} = result;

			fireEvent.change(getByLabelText('video-url'), {
				target: {value: 'https://unsupported-video-url.com'},
			});

			jest.advanceTimersByTime(500);

			await waitForElementToBeRemoved(() =>
				document.querySelector('span.loading-animation')
			);
		});

		afterEach(() => {
			jest.clearAllTimers();
			jest.clearAllMocks();
		});

		afterAll(() => {
			jest.useRealTimers();
		});

		it('sends a GET request to the server with unsupported URL', () => {
			expect(fetch).toHaveBeenCalledWith(
				'http://localhost/getDLVideoExternalShortcutFieldsURL?namespacedlVideoExternalShortcutURL=https%3A%2F%2Funsupported-video-url.com',
				expect.anything()
			);
		});

		it('updates the video preview with error', () => {
			expect(
				result.getByText('sorry,-this-platform-is-not-supported')
			).toBeInTheDocument();
		});

		it('does not call the onFilePickCallback', () => {
			expect(window.onFilePickCallback).not.toBeCalled();
		});
	});
});
