/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateItemConfig from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';
import {CommonStyles} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/CommonStyles';

const STATE = {
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
	permissions: {UPDATE: true},
	segmentsExperienceId: '0',
	selectedViewportSize: 'desktop',
};

const renderComponent = ({
	state = STATE,
	dispatch = () => {},
	itemConfig = {},
	itemType = '',
}) =>
	render(
		<StoreAPIContextProvider
			dispatch={dispatch}
			getState={() => ({...STATE, ...state})}
		>
			<CommonStyles
				commonStylesValues={{}}
				item={{
					children: [],
					config: {
						styles: {},
						tablet: {styles: {}},
						...itemConfig,
					},
					itemId: '0',
					parentId: '',
					type: itemType,
				}}
			/>
		</StoreAPIContextProvider>
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
				desktop: {label: 'Desktop'},
				tablet: {label: 'tablet'},
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
							label: 'margin-left',
							name: 'marginLeft',
							responsive: true,
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
						{
							dataType: 'string',
							defaultValue: '0',
							dependencies: [],
							displaySize: 'small',
							label: 'margin-right',
							name: 'marginRight',
							responsive: true,
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
		},
	})
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

describe('CommonStyles', () => {
	afterEach(() => {
		updateItemConfig.mockClear();
	});

	it('shows common styles panel', async () => {
		const {getByLabelText} = renderComponent({});

		expect(getByLabelText('margin-left')).toBeInTheDocument();
	});

	it('allows changing common styles for a given viewport', async () => {
		const {getByLabelText} = renderComponent({
			state: {selectedViewportSize: 'tablet'},
		});

		await userEvent.click(getByLabelText('margin-left'));
		await userEvent.click(getByLabelText('set-margin-left-to-1'));

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				tablet: {
					styles: {
						marginLeft: '1',
					},
				},
			},
			itemIds: ['0'],
		});
	});

	it('disables left and right margin selecting fixed width for containers', async () => {
		const {getByLabelText} = renderComponent({
			itemConfig: {widthType: 'fixed'},
			itemType: 'container',
		});

		const marginLeftInput = getByLabelText('margin-left');

		const marginRightInput = getByLabelText('margin-right');

		expect(marginLeftInput.disabled).toBe(true);

		expect(marginRightInput.disabled).toBe(true);
	});
});
