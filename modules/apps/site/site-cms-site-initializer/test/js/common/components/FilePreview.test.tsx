/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FilePreview from '../../../../src/main/resources/META-INF/resources/js/common/components/FilePreview';
import {IAssetFile} from '../../../../src/main/resources/META-INF/resources/js/common/types/AssetType';

jest.mock('document-library-preview-document', () => ({
	DocumentPreviewer: jest.fn(() => <div data-testid="document-previewer" />),
}));

jest.mock('document-library-preview-image', () => ({
	ImagePreviewer: jest.fn(() => <div data-testid="image-previewer" />),
}));

jest.mock('document-library-video', () => ({
	DLVideoIframe: jest.fn(() => <div data-testid="video-iframe" />),
}));

describe('FilePreview', () => {
	describe('When the item is a document', () => {
		const mockedFile = {
			link: {href: 'http://localhost/document'},
			metadata: {
				numberOfPages: 10,
			},
			previewURL: 'http://localhost/document-preview',
			thumbnailURL: 'http://localhost/document-thumbnail',
		} as IAssetFile;

		it('renders correctly', () => {
			render(<FilePreview file={mockedFile} />);

			expect(
				screen.getByTestId('document-previewer')
			).toBeInTheDocument();
		});

		it('checks the accessibility', async () => {
			const {container} = render(<FilePreview file={mockedFile} />);

			await checkAccessibility({bestPractices: true, context: container});
		});
	});

	describe('When the item is an image', () => {
		const mockedFile = {
			link: {href: 'http://localhost/image.jpg'},
			previewURL: 'http://localhost/image-preview',
			thumbnailURL:
				'http://localhost/image-thumbnail?imageThumbnail=true',
		} as IAssetFile;

		it('renders correctly', () => {
			render(<FilePreview file={mockedFile} />);

			expect(screen.getByTestId('image-previewer')).toBeInTheDocument();
		});

		it('checks the accessibility', async () => {
			const {container} = render(<FilePreview file={mockedFile} />);

			await checkAccessibility({bestPractices: true, context: container});
		});
	});

	describe('When the item is a video', () => {
		const mockedFile = {
			link: {href: 'http://localhost/video.mp4'},
			mimeType: 'video/mp4',
			previewURL: 'http://localhost/video-preview',
			thumbnailURL: 'http://localhost/video-thumbnail',
		} as IAssetFile;

		it('renders correctly', () => {
			render(<FilePreview file={mockedFile} />);

			expect(screen.getByTestId('video-iframe')).toBeInTheDocument();
		});

		it('checks the accessibility', async () => {
			const {container} = render(<FilePreview file={mockedFile} />);

			await checkAccessibility({bestPractices: true, context: container});
		});
	});

	it('renders the empty state when no preview is available', async () => {
		const {container} = render(
			<FilePreview
				file={
					{
						link: {href: 'http://localhost/file'},
						previewURL: 'http://localhost/file-preview',
						thumbnailURL: 'http://localhost/file-thumbnail',
					} as IAssetFile
				}
			/>
		);

		expect(container.querySelector('iframe')).not.toBeInTheDocument();

		expect(
			document.querySelector('img[src*="_reduced_motion"]')
		).not.toBeInTheDocument();
	});
});
