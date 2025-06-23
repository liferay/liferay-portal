/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';
import {VIEWPORT_SIZES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateItemConfig from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';
import {FragmentStylesPanel} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/FragmentStylesPanel';
import {StyleBookContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_design_options/hooks/useStyleBook';
const FRAGMENT_ENTRY_LINK_ID = '1';

const fragmentEntryLinkWithStyles = {
	comments: [],
	configuration: {
		fieldSets: [
			{
				configurationRole: 'style',
				fields: [
					{
						dataType: 'string',
						defaultValue: '1',
						description: '',
						label: 'Custom Style',
						name: 'customStyle',
						type: 'select',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '1', value: '1'},
							],
						},
					},
				],
				label: '',
			},
		],
	},
	defaultConfigurationValues: {
		customStyle: '1',
	},
	editableValues: {
		[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {},
		[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {},
	},
	fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
	name: 'Fragment',
};

const renderComponent = ({
	dispatch = () => {},
	fragmentEntryLink = {},
	selectedViewportSize = VIEWPORT_SIZES.desktop,
} = {}) =>
	render(
		<StoreAPIContextProvider
			dispatch={dispatch}
			getState={() => ({
				availableSegmentsExperiences: {
					0: {
						hasLockedSegmentsExperiment: false,
						name: 'Default Experience',
						priority: -1,
						segmentsEntryId: 'test-segment-id-00',
						segmentsExperienceId: '0',
						segmentsExperimentStatus: undefined,
						segmentsExperimentURL: 'https//:default-experience.com',
					},
				},
				fragmentEntryLinks: {
					[FRAGMENT_ENTRY_LINK_ID]: fragmentEntryLink,
				},
				permissions: {UPDATE: true},
				segmentsExperienceId: '0',
				selectedViewportSize,
			})}
		>
			<StyleBookContextProvider>
				<FragmentStylesPanel
					item={{
						children: [],
						config: {
							fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
							tablet: {styles: {}},
						},
						itemId: '0',
						parentId: '',
						type: '',
					}}
				/>
			</StyleBookContextProvider>
		</StoreAPIContextProvider>
	);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableLanguages: {
				ar_SA: {
					default: false,
					displayName: 'Arabic (Saudi Arabia)',
					languageIcon: 'ar-sa',
					languageId: 'ar_SA',
					w3cLanguageId: 'ar-SA',
				},
				en_US: {
					default: false,
					displayName: 'English (United States)',
					languageIcon: 'en-us',
					languageId: 'en_US',
					w3cLanguageId: 'en-US',
				},
				es_ES: {
					default: true,
					displayName: 'Spanish (Spain)',
					languageIcon: 'es-es',
					languageId: 'es_ES',
					w3cLanguageId: 'es-ES',
				},
			},
			availableViewportSizes: {
				desktop: {label: 'Desktop', sizeId: 'desktop'},
				mobile: {label: 'Mobile', sizeId: 'mobile'},
				tablet: {label: 'Tablet', sizeId: 'tablet'},
			},
			commonStyles: [
				{
					label: 'margin',
					styles: [
						{
							dataType: 'string',
							defaultValue: '0',
							dependencies: [],
							displaySize: 'small',
							label: 'margin-top',
							name: 'marginTop',
							responsive: true,
							responsiveTemplate: 'mt{viewport}{value}',
							type: 'select',
							typeOptions: {
								validValues: [
									{
										label: '0',
										value: '0',
									},
									{
										label: '1',
										value: '1',
									},
								],
							},
						},
					],
				},
			],
			defaultLanguageId: 'es_ES',
			defaultSegmentsExperienceId: '0',
			frontendTokens: {
				spacer0: {
					defaultValue: '3rem',
					label: 'Spacer 0',
					value: '3rem',
				},
				spacer1: {
					defaultValue: '5rem',
					label: 'Spacer 0',
					value: '5rem',
				},
			},
		},
	})
);

describe('FragmentStylesPanel', () => {
	afterEach(() => {
		updateItemConfig.mockClear();
	});

	it('does not show custom styles panel when there are no custom styles', async () => {
		const {queryByText} = renderComponent({});

		expect(queryByText('custom-styles')).not.toBeInTheDocument();
	});

	it('shows custom styles panel when there is at least one custom style', async () => {
		const {getByText} = renderComponent({
			fragmentEntryLink: fragmentEntryLinkWithStyles,
		});

		expect(getByText('custom-styles')).toBeInTheDocument();
	});

	it('allows changing custom styles for a given viewport', async () => {
		renderComponent({
			selectedViewportSize: VIEWPORT_SIZES.tablet,
		});

		await userEvent.click(screen.getByLabelText('margin-top'));
		await userEvent.click(screen.getByLabelText('set-margin-top-to-1'));

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				tablet: {
					styles: {
						marginTop: '1',
					},
				},
			},
			itemIds: ['0'],
		});
	});
});
