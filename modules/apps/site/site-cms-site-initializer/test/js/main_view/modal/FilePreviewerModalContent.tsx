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
	name: 'Image 1',
	previewURL: '/preview/image1',
	thumbnailURL: '/thumbs/image1?version=1.0&imageThumbnail=1',
};

jest.mock('document-library-preview-image', () => ({
	ImagePreviewer: (props: any) => (
		<img
			alt={props.alt}
			data-testid="image-previewer"
			src={props.imageURL}
		/>
	),
}));

describe('FilePreviewerModalContent', () => {
	it('renders the file name and download link', () => {
		const {getByRole, getByText} = render(
			<FilePreviewerModalContent {...baseImageFile} />
		);

		expect(getByText('Image 1')).toBeInTheDocument();
		expect(getByRole('link')).toHaveAttribute('href', '/images/image1.jpg');
	});

	it('shows ImagePreviewer when imageThumbnail is present in thumbnailURL', () => {
		const {getByRole} = render(
			<FilePreviewerModalContent {...baseImageFile} />
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
			<FilePreviewerModalContent {...noImageThumbFile} />
		);

		expect(screen.queryByTestId('image-previewer')).not.toBeInTheDocument();

		expect(getByText('no-preview-available')).toBeInTheDocument();
	});

	it('checks the accessibility of the multiple file uploader', async () => {
		const {container} = render(
			<FilePreviewerModalContent {...baseImageFile} />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
