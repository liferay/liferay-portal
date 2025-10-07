/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import FilePreviewerModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/FilePreviewerModalContent';

const baseImageFile = {
	externalReferenceCode: 'img-001',
	id: 101,
	link: {
		href: '/images/image1.jpg',
		label: 'Download Image',
	},
	mimeType: 'image/jpeg',
	name: 'Image 1',
	previewURL: '/preview/image1',
	thumbnailURL: '/thumbs/image1?version=1.0&imageThumbnail=1',
};

const baseVideoFile = {
	externalReferenceCode: 'vid-001',
	id: 201,
	link: {
		href: '/videos/video1.mp4',
		label: 'Download Video',
	},
	mimeType: 'video/mp4',
	name: 'Video 1',
	previewURL: '/preview/video1.mp4',
	thumbnailURL: '/thumbs/video1?version=1.0',
};

const baseDocumentFile = {
	alternativeText: 'Preview of Document 1',
	externalReferenceCode: 'pdf-001',
	id: 301,
	link: {
		href: '/pdfs/doc1.pdf',
		label: 'Download PDF',
	},
	metadata: {
		numberOfPages: 5,
	},
	mimeType: 'application/pdf',
	name: 'Document 1',
	previewURL: '/preview/doc1.pdf',
	thumbnailURL: '/thumbs/doc1.pdf?version=1.0',
};

describe('FilePreviewerModalContent', () => {
	it('renders the file name and download link', () => {
		const {getByRole, getByText} = render(
			<FilePreviewerModalContent file={baseImageFile} />
		);

		expect(getByText('Image 1')).toBeInTheDocument();
		expect(getByRole('link')).toHaveAttribute('href', '/images/image1.jpg');
	});

	it('shows ImagePreviewer when imageThumbnail is present in thumbnailURL', () => {
		const {getByRole} = render(
			<FilePreviewerModalContent file={baseImageFile} />
		);

		const imagePreview = getByRole('img');

		expect(imagePreview).toBeInTheDocument();
		expect(imagePreview).toHaveAttribute('src', baseImageFile.link.href);
		expect(imagePreview).toHaveAttribute('alt', baseImageFile.name);
	});

	it('shows empty state message if imageThumbnail is missing in thumbnailURL', () => {
		const noImageThumbFile = {
			...baseImageFile,
			thumbnailURL: '/thumbs/image1?otherParam=true',
		};

		const {getByText} = render(
			<FilePreviewerModalContent file={noImageThumbFile} />
		);

		expect(screen.queryByTestId('image-previewer')).not.toBeInTheDocument();

		expect(getByText('no-preview-available')).toBeInTheDocument();
	});

	it('checks the accessibility of the multiple file uploader', async () => {
		const {container} = render(
			<FilePreviewerModalContent file={baseImageFile} />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('renders a video iframe when file is a video with previewURL', () => {
		const {container} = render(
			<FilePreviewerModalContent file={baseVideoFile} />
		);

		const videoIframe = container.querySelector('[data-video-liferay]');

		expect(videoIframe).toBeInTheDocument();
		expect(videoIframe).toHaveAttribute('src', baseVideoFile.previewURL);
	});

	it('shows PDF preview when numberOfPages and previewURL is present', () => {
		const {getByRole} = render(
			<FilePreviewerModalContent file={baseDocumentFile} />
		);

		const documentImagePreview = getByRole('img');

		expect(documentImagePreview).toBeInTheDocument();
		expect(documentImagePreview).toHaveAttribute(
			'src',
			'http://localhost/preview/doc1.pdf?previewFileIndex=1'
		);
	});
});
