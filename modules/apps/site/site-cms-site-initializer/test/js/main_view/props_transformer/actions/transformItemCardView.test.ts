/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

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
		L_BASIC_WEB_CONTENT: 'content-icon-basic-content',
		default: 'content-icon-custom-structure',
	};
	const mockObjectDefinitionIcons = {
		L_BASIC_WEB_CONTENT: 'forms',
		default: 'web-content',
	};

	it('See stickerProps has empty className and empty icon because the item is a folder', () => {
		assertStickerProps(
			'folder',
			'folder',
			transformItemCardView(
				{
					entryClassName:
						'com.liferay.object.model.ObjectEntryFolder',
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
								externalReferenceCode: 'L_BASIC_WEB_CONTENT',
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
								externalReferenceCode: 'L_BASIC_DOCUMENT',
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
								externalReferenceCode: 'L_BASIC_DOCUMENT',
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
								externalReferenceCode: 'L_BASIC_DOCUMENT',
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
								externalReferenceCode: 'L_BASIC_DOCUMENT',
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
});
