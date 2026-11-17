/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {EDITABLE_TYPES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableTypes';
import {VIEWPORT_SIZES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import ImageSourcePanel from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/ImageSourcePanel';

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			defaultLanguageId: 'en_US',
			getAvailableImageConfigurationsURL:
				'getAvailableImageConfigurationsURL',
			getInfoItemFieldValueURL: 'getInfoItemFieldValueURL',
		},
	})
);

const frontendJsWebMock = jest.requireMock('frontend-js-web');

function renderImageSourcePanel(editableValue) {
	const state = {
		fragmentEntryLinks: {
			0: {
				editableValues: {
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
						'editable-id-0': editableValue,
					},
				},
			},
		},
		languageId: 'en_US',
		selectedViewportSize: VIEWPORT_SIZES.tablet,
	};

	return render(
		<StoreAPIContextProvider dispatch={() => {}} getState={() => state}>
			<ImageSourcePanel
				item={{
					editableId: 'editable-id-0',
					editableValueNamespace: EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					fragmentEntryLinkId: '0',
					itemId: '',
					type: EDITABLE_TYPES.image,
				}}
			/>
		</StoreAPIContextProvider>
	);
}

describe('ImageSourcePanel', () => {
	beforeEach(() => {
		frontendJsWebMock.fetch.mockImplementation((url) =>
			Promise.resolve({
				clone() {
					return this;
				},
				json: () =>
					Promise.resolve(
						url === 'getInfoItemFieldValueURL'
							? {
									fieldValue: {
										fileEntryId: '1234',
										url: 'http://localhost/image.png',
									},
								}
							: [
									{
										label: 'Auto',
										size: 300,
										value: 'auto',
										width: 2560,
									},
									{
										label: 'Thumbnail-300x300',
										size: 20,
										value: 'Thumbnail-300x300',
										width: 300,
									},
								]
					),
				status: 200,
			})
		);
	});

	afterEach(() => {
		frontendJsWebMock.fetch.mockReset();
	});

	it('shows the image sizes of a mapped image with a default value', async () => {
		renderImageSourcePanel({
			classNameId: '1',
			classPK: '2',
			config: {},
			defaultValue: 'data:image/png;base64,AAAA',
			fieldId: 'fileURL',
		});

		await screen.findByRole('option', {name: 'Thumbnail-300x300'});

		const select = screen.getByLabelText('resolution');

		expect(
			Array.from(select.options).map((option) => option.value)
		).toEqual(['Thumbnail-300x300', 'auto']);
	});
});
