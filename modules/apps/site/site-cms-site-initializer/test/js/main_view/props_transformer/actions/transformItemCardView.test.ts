/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {transformItemCardView} from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/transformViewsItemProps';

jest.mock('@clayui/icon', () => (props: any) => ({
	props,
}));

function assertStickerProps(
	expectedClassName: string,
	expectedSymbol: string,
	result: any
): void {
	expect(result).toHaveProperty('stickerProps');

	expect(result.stickerProps?.className).toBe(expectedClassName);
	expect(result.stickerProps?.content?.props?.symbol).toBe(expectedSymbol);
}

describe('transformItemCardView', () => {
	const baseMockProps = {actions: []};
	const mockFileMimeTypeCssClasses = {
		'default': 'file-icon-color-0',
		'image': 'file-icon-color-3',
		'text/plain': 'file-icon-color-6',
		'video': 'file-icon-color-3',
	};
	const mockFileMimeTypeIcons = {
		'default': 'document-default',
		'image': 'document-image',
		'text/plain': 'document-text',
		'video': 'document-multimedia',
	};
	const mockObjectDefinitionCssClasses = {
		L_CMS_BASIC_WEB_CONTENT: 'content-icon-basic-content',
		default: 'content-icon-custom-structure',
	};
	const mockObjectDefinitionIcons = {
		L_CMS_BASIC_WEB_CONTENT: 'forms',
		default: 'web-content',
	};

	it('See stickerProps has empty className and empty icon because the item is a folder', () => {
		assertStickerProps(
			'folder',
			'folder',
			transformItemCardView(
				{
					entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			)
		);
	});

	it('See stickerProps has values because the item is a basic content', () => {
		assertStickerProps(
			'content-icon-basic-content',
			'forms',
			transformItemCardView(
				{
					embedded: {
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode:
									'L_CMS_BASIC_WEB_CONTENT',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			)
		);
	});

	it('See stickerProps has values because the item is a custom structure', () => {
		assertStickerProps(
			'content-icon-custom-structure',
			'web-content',
			transformItemCardView(
				{
					embedded: {
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'CUSTOM_STRUCTURE',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			)
		);
	});

	it('See stickerProps has values because the item is a basic document with text/plain mimetype', () => {
		assertStickerProps(
			'file-icon-color-6',
			'document-text',
			transformItemCardView(
				{
					embedded: {
						file: {
							mimeType: 'text/plain',
						},
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_BASIC_DOCUMENT',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			)
		);
	});

	it('See stickerProps has values because the item is a basic document with not configured mimetype', () => {
		assertStickerProps(
			'file-icon-color-0',
			'document-default',
			transformItemCardView(
				{
					embedded: {
						file: {
							mimeType: 'test',
						},
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_BASIC_DOCUMENT',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			)
		);
	});

	it('See stickerProps has values because the item is a basic document with an image mimetype', () => {
		assertStickerProps(
			'file-icon-color-3',
			'document-image',
			transformItemCardView(
				{
					embedded: {
						file: {
							mimeType: 'image/jpeg',
						},
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_BASIC_DOCUMENT',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			)
		);
	});

	it('See stickerProps has values because the item is a basic document with a video mimetype', () => {
		assertStickerProps(
			'file-icon-color-3',
			'document-multimedia',
			transformItemCardView(
				{
					embedded: {
						file: {
							mimeType: 'video/mp4',
						},
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_BASIC_DOCUMENT',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			)
		);
	});

	it('Shows Untitled Asset if title is not present', () => {
		const cardView = transformItemCardView(
			{
				entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
			},
			mockFileMimeTypeCssClasses,
			mockFileMimeTypeIcons,
			mockObjectDefinitionCssClasses,
			mockObjectDefinitionIcons,
			baseMockProps
		);

		expect(cardView.title).toBe('untitled-asset');
	});

	it('Shows the search title if the title field has another name', () => {
		const cardView = transformItemCardView(
			{
				embedded: {id: 1, specName: 'Real Spec Title Value'},
				title: 'Real Spec Title Value',
			},
			mockFileMimeTypeCssClasses,
			mockFileMimeTypeIcons,
			mockObjectDefinitionCssClasses,
			mockObjectDefinitionIcons,
			baseMockProps
		);

		expect(cardView.title).toBe('Real Spec Title Value');
	});

	it('Shows Untitled Asset if the search title falls back to the ID', () => {
		const cardView = transformItemCardView(
			{embedded: {id: 1, specName: ''}, title: '1'},
			mockFileMimeTypeCssClasses,
			mockFileMimeTypeIcons,
			mockObjectDefinitionCssClasses,
			mockObjectDefinitionIcons,
			baseMockProps
		);

		expect(cardView.title).toBe('untitled-asset');
	});

	describe('External Video Thumbnail', () => {
		it('External Video should show a thumbnail if it is a YouTube video (Standard URL)', () => {
			const result = transformItemCardView(
				{
					embedded: {
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_EXTERNAL_VIDEO',
							},
						},
						title: 'My Video',
						videoURL: 'https://www.youtube.com/watch?v=IqCSx3omX4o',
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			);

			expect(result.imgProps).toEqual({
				alt: 'My Video',
				src: 'https://img.youtube.com/vi/IqCSx3omX4o/0.jpg',
			});
		});

		it('External Video should show a thumbnail if it is a YouTube video (Short URL)', () => {
			const result = transformItemCardView(
				{
					embedded: {
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_EXTERNAL_VIDEO',
							},
						},
						title: 'My Video',
						videoURL: 'https://youtu.be/IqCSx3omX4o',
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			);

			expect(result.imgProps).toEqual({
				alt: 'My Video',
				src: 'https://img.youtube.com/vi/IqCSx3omX4o/0.jpg',
			});
		});

		it('External Video should show a video icon if it is not a YouTube video', () => {
			const result = transformItemCardView(
				{
					embedded: {
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_EXTERNAL_VIDEO',
							},
						},
						videoURL: 'https://vimeo.com/483035084',
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			);

			expect(result.symbol).toBe('video');
		});
	});

	describe('File Thumbnail', () => {
		it('shows a thumbnail with alternative text', () => {
			const result = transformItemCardView(
				{
					embedded: {
						file: {
							alternativeText: 'My Alternative Text',
							name: 'file.png',
							thumbnailURL: '/path/to/thumbnail',
						},
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_BASIC_DOCUMENT',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			);

			expect(result.imgProps).toEqual({
				alt: 'My Alternative Text',
				src: '/path/to/thumbnail',
			});
		});

		it('shows a thumbnail with name as alternative text if alternativeText is not present', () => {
			const result = transformItemCardView(
				{
					embedded: {
						file: {
							name: 'file.png',
							thumbnailURL: '/path/to/thumbnail',
						},
						systemProperties: {
							objectDefinitionBrief: {
								externalReferenceCode: 'L_CMS_BASIC_DOCUMENT',
							},
						},
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			);

			expect(result.imgProps).toEqual({
				alt: 'file.png',
				src: '/path/to/thumbnail',
			});
		});
	});

	describe('Image Thumbnail', () => {
		it('shows the first image that has a file', () => {
			const result = transformItemCardView(
				{
					embedded: {
						images: [
							{image: null},
							{image: {link: {href: '/first.png'}}},
							{image: {link: {href: '/second.png'}}},
						],
						title: 'Camera',
					},
				},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			);

			expect(result.imgProps).toEqual({
				alt: 'Camera',
				src: '/first.png',
			});
		});

		it('keeps the symbol when no image has a file', () => {
			const result = transformItemCardView(
				{embedded: {images: [{image: null}], title: 'Tripod'}},
				mockFileMimeTypeCssClasses,
				mockFileMimeTypeIcons,
				mockObjectDefinitionCssClasses,
				mockObjectDefinitionIcons,
				baseMockProps
			);

			expect(result.imgProps).toBeUndefined();
			expect(result.symbol).toBe('web-content');
		});
	});
});
