/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import CommonStylesManager from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/CommonStylesManager';
import {FRAGMENT_CLASS_PLACEHOLDER} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/fragmentClassPlaceholder';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {useGlobalContext} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/GlobalContext';
import {StoreAPIContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import getLayoutDataItemTopperUniqueClassName from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemTopperUniqueClassName';
import getLayoutDataItemUniqueClassName from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getLayoutDataItemUniqueClassName';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/GlobalContext'
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			commonStylesFields: {
				backgroundColor: {
					cssTemplate: 'background-color: {value} !important;',
					defaultValue: '',
				},

				marginBottom: {
					cssTemplate: 'margin-bottom: {value} !important;',
					defaultValue: '0',
				},

				marginTop: {
					cssTemplate: 'margin-top: {value} !important;',
					defaultValue: '0',
				},
			},

			frontendTokens: {
				dangerColor: {
					cssVariable: 'danger',
				},
				infoColor: {
					cssVariable: 'info',
				},
				primaryColor: {
					cssVariable: 'primary',
				},
			},
		},
	})
);

const COMMON_CSS_STYLE = `
	.lfr-layout-structure-item-container {
		padding: 0;
	}
	
	.lfr-layout-structure-item-row { 
		overflow: hidden;
	}
	
	.portlet-borderless .portlet-content{
		padding: 0;
	}
`;
const FRAGMENT_ID = 'FRAGMENT_ID';
const ITEM_ID = 'ITEM_ID';
const MASTER_ITEM_ID = 'ITEM_ID';

const renderCommonStylesManager = ({
	selectedViewportSize = VIEWPORT_SIZES.desktop,
	editableValues = {},
} = {}) => {
	return render(
		<StoreAPIContextProvider
			getState={() => ({
				fragmentEntryLinks: {
					fragmentEntryLinkId: {
						editableValues,
						fragmentEntryLinkId: 'fragmentEntryLink',
					},
				},
				layoutData: {
					items: {
						[FRAGMENT_ID]: {
							children: [],
							config: {
								fragmentEntryLinkId: 'fragmentEntryLinkId',

								styles: {
									backgroundColor: 'infoColor',
									marginBottom: '2',
									marginTop: '3',
								},
							},
							itemId: FRAGMENT_ID,
							type: LAYOUT_DATA_ITEM_TYPES.fragment,
						},
						[ITEM_ID]: {
							config: {
								[VIEWPORT_SIZES.tablet]: {
									customCSS: `.${FRAGMENT_CLASS_PLACEHOLDER} { color: lime; }`,

									styles: {
										backgroundColor: 'primaryColor',
										marginBottom: '2',
									},
								},

								customCSS: `.${FRAGMENT_CLASS_PLACEHOLDER} { color: aquamarine; }`,

								styles: {
									backgroundColor: 'dangerColor',
									marginBottom: '3',
									marginTop: '2',
								},
							},
							itemId: ITEM_ID,
							type: LAYOUT_DATA_ITEM_TYPES.row,
						},
					},
				},
				masterLayout: {
					masterLayoutData: {
						items: {
							[MASTER_ITEM_ID]: {
								config: {
									[VIEWPORT_SIZES.tablet]: {
										customCSS: `.${FRAGMENT_CLASS_PLACEHOLDER} { color: red; }`,
										styles: {
											backgroundColor: 'primaryColor',
											marginBottom: '2',
										},
									},

									customCSS: `.${FRAGMENT_CLASS_PLACEHOLDER} { color: blue; }`,

									styles: {
										backgroundColor: 'dangerColor',
										marginBottom: '3',
										marginTop: '2',
									},
								},
								itemId: MASTER_ITEM_ID,
								type: LAYOUT_DATA_ITEM_TYPES.container,
							},
						},
					},
				},
				selectedViewportSize,
			})}
		>
			<CommonStylesManager />
		</StoreAPIContextProvider>
	);
};

describe('CommonStylesManager', () => {
	beforeAll(() => {
		useGlobalContext.mockReturnValue({
			document,
			window,
		});
	});

	it('creates a style tag', () => {
		renderCommonStylesManager();

		const style = document.getElementById('layout-common-styles');

		expect(style).toBeInTheDocument();
	});

	it('creates a style tag for master layout', () => {
		renderCommonStylesManager();

		const style = document.getElementById('layout-master-common-styles');

		expect(style).toBeInTheDocument();
	});

	it('add custom styles to the style tag', () => {
		renderCommonStylesManager();

		const expected = `
			${COMMON_CSS_STYLE}
			
			.${getLayoutDataItemUniqueClassName(FRAGMENT_ID)} {
				background-color: var(--info) !important;
			}
			
			.${getLayoutDataItemTopperUniqueClassName(FRAGMENT_ID)} {
				margin-bottom: var(--spacer-2, 0.5rem) !important;
				margin-top: var(--spacer-3, 1rem) !important;
			}

			.${getLayoutDataItemUniqueClassName(ITEM_ID)} {
				background-color: var(--danger) !important;
			}
			
			.${getLayoutDataItemTopperUniqueClassName(ITEM_ID)} {
				margin-bottom: var(--spacer-3, 1rem) !important;
				margin-top: var(--spacer-2, 0.5rem) !important;
			}

			.${getLayoutDataItemUniqueClassName(ITEM_ID)} { 
				color: aquamarine; 
			}`;

		const style = document.getElementById('layout-common-styles');

		expect(normalize(style.innerHTML)).toBe(normalize(expected));
	});

	it('add custom styles to the master layout style tag', () => {
		renderCommonStylesManager();

		const expected = `
			${COMMON_CSS_STYLE}
			
			.${getLayoutDataItemUniqueClassName(MASTER_ITEM_ID)} {
				background-color: var(--danger) !important;
				margin-bottom: var(--spacer-3, 1rem) !important;
				margin-top: var(--spacer-2, 0.5rem) !important;
			}

			.${getLayoutDataItemUniqueClassName(ITEM_ID)} { 
				color: blue; 
			}
			`;

		const style = document.getElementById('layout-master-common-styles');

		expect(normalize(style.innerHTML)).toBe(normalize(expected));
	});

	it('add custom styles taking into account the viewportsize to the style tag', () => {
		renderCommonStylesManager({
			selectedViewportSize: VIEWPORT_SIZES.tablet,
		});

		const expected = `
			${COMMON_CSS_STYLE}
			
			.${getLayoutDataItemUniqueClassName(FRAGMENT_ID)} {
				background-color: var(--info) !important;
			}
			
			.${getLayoutDataItemTopperUniqueClassName(FRAGMENT_ID)} {
				margin-bottom: var(--spacer-2, 0.5rem) !important;
				margin-top: var(--spacer-3, 1rem) !important;
			}

			.${getLayoutDataItemUniqueClassName(ITEM_ID)} {
				background-color: var(--primary) !important;
			}
			
			.${getLayoutDataItemTopperUniqueClassName(ITEM_ID)} {
				margin-bottom: var(--spacer-2, 0.5rem) !important;
				margin-top: var(--spacer-2, 0.5rem) !important;
			}

			.${getLayoutDataItemUniqueClassName(ITEM_ID)} { 
				color: lime; 
			}
			`;

		const style = document.getElementById('layout-common-styles');

		expect(normalize(style.innerHTML)).toBe(normalize(expected));
	});

	it('does not add styles to the topper if the fragment has inner common styles', () => {
		renderCommonStylesManager({
			editableValues: {
				['com.liferay.fragment.entry.processor.styles.StylesFragmentEntryProcessor']:
					{
						hasCommonStyles: true,
					},
			},
			selectedViewportSize: VIEWPORT_SIZES.tablet,
		});

		const expected = `
			${COMMON_CSS_STYLE}
			
			.${getLayoutDataItemUniqueClassName(FRAGMENT_ID)} {
				background-color: var(--info) !important;
				margin-bottom: var(--spacer-2, 0.5rem) !important;
				margin-top: var(--spacer-3, 1rem) !important;
			}
			
			.${getLayoutDataItemUniqueClassName(ITEM_ID)} {
				background-color: var(--primary) !important;
			}
			
			.${getLayoutDataItemTopperUniqueClassName(ITEM_ID)} {
				margin-bottom: var(--spacer-2, 0.5rem) !important;
				margin-top: var(--spacer-2, 0.5rem) !important;
			}

			.${getLayoutDataItemUniqueClassName(ITEM_ID)} { 
				color: lime; 
			}
			`;

		const style = document.getElementById('layout-common-styles');

		expect(normalize(style.innerHTML)).toBe(normalize(expected));
	});
});

function normalize(value) {
	return value.replaceAll(/[\s\n\t]/g, '');
}
