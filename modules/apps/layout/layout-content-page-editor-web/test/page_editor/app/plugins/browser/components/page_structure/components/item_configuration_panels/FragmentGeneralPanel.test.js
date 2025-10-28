/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';
import {FREEMARKER_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/freemarkerFragmentEntryProcessor';

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';

import {VIEWPORT_SIZES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import FragmentService from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/FragmentService';
import {FragmentGeneralPanel} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/FragmentGeneralPanel';

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve({}))
);

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/FragmentService',
	() => ({
		updateConfigurationValues: jest.fn(() => Promise.resolve({})),
	})
);

const FRAGMENT_ENTRY_LINK_ID = '1';

const defaultFragmentEntryLink = (localizable = false) => ({
	comments: [],
	configuration: {
		fieldSets: [
			{
				fields: [
					{
						dataType: 'string',
						defaultValue: 'h1',
						description: '',
						label: 'Heading Level',
						localizable,
						name: 'headingLevel',
						type: 'select',
						typeOptions: {
							validValues: [
								{label: 'H1', value: 'h1'},
								{label: 'H2', value: 'h2'},
								{label: 'H3', value: 'h3'},
								{label: 'H4', value: 'h4'},
							],
						},
					},
				],
				label: '',
			},
		],
	},
	defaultConfigurationValues: {
		headingLevel: 'h1',
	},
	editableValues: {
		[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {},
		[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {},
	},
	fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
	name: 'Heading',
});

const item = {
	children: [],
	config: {
		fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
	},
	itemId: '1',
	parentId: '',
	type: '',
};

const renderGeneralPanel = ({
	segmentsExperienceId,
	fragmentEntryLink = defaultFragmentEntryLink(),
	restrictedItemIds = new Set(),
}) => {
	const state = {
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
			1: {
				hasLockedSegmentsExperiment: false,
				languageIds: ['es_ES', 'en_US'],
				name: 'Experience #1',
				priority: 3,
				segmentsEntryId: 'test-segment-id-00',
				segmentsExperienceId: 'test-experience-id-01',
				segmentsExperimentStatus: undefined,
				segmentsExperimentURL: 'https//:experience-1.com',
			},
			2: {
				hasLockedSegmentsExperiment: false,
				languageIds: ['es_ES', 'en_US', 'ar_SA'],
				name: 'Experience #2',
				priority: 1,
				segmentsEntryId: 'test-segment-id-01',
				segmentsExperienceId: 'test-experience-id-02',
				segmentsExperimentStatus: undefined,
				segmentsExperimentURL: 'https//:experience-2.com',
			},
		},
		defaultSegmentsExperienceId: '0',
		fragmentEntryLinks: {[FRAGMENT_ENTRY_LINK_ID]: fragmentEntryLink},
		languageId: 'en_US',
		permissions: {UPDATE: true},
		restrictedItemIds,
		segmentsExperienceId,
		selectedViewportSize: VIEWPORT_SIZES.desktop,
	};

	const mockDispatch = jest.fn((a) => {
		if (typeof a === 'function') {
			return a(mockDispatch, () => state);
		}
	});

	return render(
		<StoreAPIContextProvider dispatch={mockDispatch} getState={() => state}>
			<FragmentGeneralPanel item={item} />
		</StoreAPIContextProvider>
	);
};

describe('FragmentGeneralPanel', () => {
	afterEach(() => {
		FragmentService.updateConfigurationValues.mockClear();
	});

	it('does not prefix values with segments if we do not have experiences', async () => {
		const {getByLabelText} = renderGeneralPanel({
			segmentsExperienceId: null,
		});

		const input = getByLabelText('Heading Level');

		await fireEvent.change(input, {
			target: {value: 'h2'},
		});

		expect(FragmentService.updateConfigurationValues).toBeCalledWith(
			expect.objectContaining({
				editableValues: {
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {},
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
						headingLevel: 'h2',
					},
				},
				languageId: 'en_US',
			})
		);
	});

	it('does not show flag icon when localizable property is false', async () => {
		const {getByLabelText} = renderGeneralPanel({
			fragmentEntryLink: defaultFragmentEntryLink(false),
			segmentsExperienceId: null,
		});

		const input = getByLabelText('Heading Level');

		const wrapperDiv = input.parentElement.parentElement.parentElement;

		expect(wrapperDiv.querySelector('.sr-only')).toBeNull();
	});

	it('prefix values with segments when we have experiences', async () => {
		const {getByLabelText} = renderGeneralPanel({
			segmentsExperienceId: '1',
		});

		const input = getByLabelText('Heading Level');

		await fireEvent.change(input, {
			target: {value: 'h2'},
		});

		expect(FragmentService.updateConfigurationValues).toBeCalledWith(
			expect.objectContaining({
				editableValues: {
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {},
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
						headingLevel: 'h2',
					},
				},
				languageId: 'en_US',
			})
		);
	});

	it('prefix values with default experience when segmentsExperience is null', async () => {
		const {getByLabelText} = renderGeneralPanel({
			segmentsExperienceId: null,
		});

		const input = getByLabelText('Heading Level');

		await fireEvent.change(input, {
			target: {value: 'h2'},
		});

		expect(FragmentService.updateConfigurationValues).toBeCalledWith(
			expect.objectContaining({
				editableValues: {
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {},
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
						headingLevel: 'h2',
					},
				},
				languageId: 'en_US',
			})
		);
	});

	it('merges configuration values when a new one is added', async () => {
		const fragmentEntryLink = {
			comments: [],
			configuration: {
				fieldSets: [
					{
						fields: [
							{
								dataType: 'string',
								defaultValue: 'h1',
								description: '',
								label: 'Heading Level',
								name: 'headingLevel',
								type: 'select',
								typeOptions: {
									validValues: [
										{label: 'H1', value: 'h1'},
										{label: 'H2', value: 'h2'},
										{label: 'H3', value: 'h3'},
										{label: 'H4', value: 'h4'},
									],
								},
							},
							{
								dataType: 'string',
								defaultValue: 'default',
								description: '',
								label: 'Another thing',
								name: 'anotherThing',
								type: 'text',
							},
						],
						label: '',
					},
				],
			},
			defaultConfigurationValues: {
				headingLevel: 'h1',
			},
			editableValues: {
				[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {},
				[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
					anotherThing: 'test',
				},
			},
			fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
			name: 'Heading',
		};

		const {getByLabelText} = renderGeneralPanel({
			fragmentEntryLink,
			segmentsExperienceId: '0',
		});

		const input = getByLabelText('Heading Level');

		await fireEvent.change(input, {
			target: {value: 'h2'},
		});

		expect(FragmentService.updateConfigurationValues).toBeCalledWith(
			expect.objectContaining({
				editableValues: {
					[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {},
					[FREEMARKER_FRAGMENT_ENTRY_PROCESSOR]: {
						anotherThing: 'test',
						headingLevel: 'h2',
					},
				},
				languageId: 'en_US',
			})
		);
	});

	it('shows corresponding flag icon when localizable property is true', async () => {
		const {getByLabelText} = renderGeneralPanel({
			fragmentEntryLink: defaultFragmentEntryLink(true),
			segmentsExperienceId: null,
		});

		const input = getByLabelText('Heading Level');

		const wrapperDiv = input.closest(
			'.page-editor__sidebar__fieldset__field'
		);

		expect(wrapperDiv.querySelector('.sr-only')).toHaveTextContent('en-US');
	});

	it('renders the permission retriction message when the mapped item does not have permissions', () => {
		renderGeneralPanel({restrictedItemIds: new Set(['1'])});

		expect(
			screen.getByText(
				'this-content-cannot-be-displayed-due-to-permission-restrictions'
			)
		).toBeInTheDocument();
	});
});
