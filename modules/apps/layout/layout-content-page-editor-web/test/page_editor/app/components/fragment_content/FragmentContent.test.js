/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {EDITABLE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/editableFragmentEntryProcessor';

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';

import FragmentContent from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_content/FragmentContent';
import {BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/backgroundImageFragmentEntryProcessor';
import {VIEWPORT_SIZES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {
	ControlsProvider,
	useSelectItem,
} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {EditableProcessorContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/EditableProcessorContext';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import resolveEditableValue from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/editable_value/resolveEditableValue';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/services/serviceFetch',
	() => jest.fn(() => Promise.resolve({}))
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/editable_value/resolveEditableValue',
	() => jest.fn(() => Promise.resolve(['Default content']))
);

const FRAGMENT_ENTRY_LINK_ID = '1';

const getFragmentEntryLink = ({
	content = '<lfr-editable id="editable-id" class="page-editor__editable" type="img">Default content</lfr-editable>',
	editableValues = {
		[EDITABLE_FRAGMENT_ENTRY_PROCESSOR]: {
			'editable-id': {},
		},
	},
	fragmentEntryType = 'component',
} = {}) => ({
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
				],
				label: '',
			},
		],
	},
	content,
	defaultConfigurationValues: {
		headingLevel: 'h1',
	},
	editableValues,
	fragmentEntryLinkId: FRAGMENT_ENTRY_LINK_ID,
	fragmentEntryType,
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

const renderFragmentContent = ({
	activeItemId,
	fragmentEntryLink,
	hasUpdatePermissions = true,
	lockedExperience = false,
	viewportSize = VIEWPORT_SIZES.desktop,
}) => {
	const state = {
		fragmentEntryLinks: {
			[FRAGMENT_ENTRY_LINK_ID]: fragmentEntryLink,
		},
		languageId: 'en_US',
		permissions: {
			LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
			UPDATE: hasUpdatePermissions,
		},
		segmentsExperienceId: '0',
		selectedViewportSize: viewportSize,
	};

	const ref = React.createRef();

	const AutoSelect = () => {
		useSelectItem()(activeItemId);

		return null;
	};

	return render(
		<StoreAPIContextProvider dispatch={() => {}} getState={() => state}>
			<EditableProcessorContextProvider>
				<ControlsProvider>
					<AutoSelect />

					<FragmentContent
						elementRef={ref}
						fragmentEntryLinkId={FRAGMENT_ENTRY_LINK_ID}
						getPortals={() => []}
						item={item}
					/>
				</ControlsProvider>
			</EditableProcessorContextProvider>
		</StoreAPIContextProvider>
	);
};

describe('FragmentContent', () => {
	beforeEach(() => {
		resolveEditableValue.mockClear();
	});

	it('renders the fragment content', async () => {
		const fragmentEntryLink = getFragmentEntryLink();

		await act(async () => {
			renderFragmentContent({fragmentEntryLink});
		});

		const editableContent = document.body.querySelector('#editable-id');

		expect(editableContent.outerHTML).toBe(fragmentEntryLink.content);
	});

	it('calls resolve editable values with the correct parameters when content has a editable', async () => {
		const fragmentEntryLink = getFragmentEntryLink();

		await act(async () => {
			renderFragmentContent({fragmentEntryLink});
		});

		expect(resolveEditableValue).toBeCalledWith(
			{},
			'en_US',
			expect.any(Function)
		);
	});

	it('calls resolve editable values with the correct parameters when content has a data-lfr-editable-id', async () => {
		const fragmentEntryLink = getFragmentEntryLink({
			content:
				'<p data-lfr-editable-id="editable-id" data-lfr-editable-type="text">Default content</p>',
		});

		await act(async () => {
			renderFragmentContent({fragmentEntryLink});
		});

		expect(resolveEditableValue).toBeCalledWith(
			{},
			'en_US',
			expect.any(Function)
		);
	});

	it('calls resolve editable values with the correct parameters when content has a background image editable', async () => {
		const fragmentEntryLink = getFragmentEntryLink({
			content: '<div data-lfr-background-image-id="background-id"></div>',

			editableValues: {
				[BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR]: {
					'background-id': {
						defaultValue: 'image.jpg',
					},
				},
			},
		});

		await act(async () => {
			renderFragmentContent({fragmentEntryLink});
		});

		expect(resolveEditableValue).toBeCalledWith(
			{defaultValue: 'image.jpg'},
			'en_US',
			expect.any(Function)
		);
	});

	it('shows widgets topper even without update permissions', async () => {
		const fragmentEntryLink = getFragmentEntryLink();

		await act(async () => {
			renderFragmentContent({
				fragmentEntryLink,
				hasUpdatePermissions: false,
			});
		});

		expect(
			document.body.querySelector(
				'.page-editor__fragment-content--portlet-topper-hidden'
			)
		).toBeInTheDocument();
	});

	it('has the data-tooltip-floating attribute if the fragment is of type text', async () => {
		const fragmentEntryLink = getFragmentEntryLink({
			content:
				'<lfr-editable class="page-editor__editable" id="editable-id" type="text">Default content</lfr-editable>',
		});

		await act(async () => {
			renderFragmentContent({fragmentEntryLink});
		});

		const editableContent = document.body.querySelector('#editable-id');

		expect(editableContent.dataset.tooltipFloating).toBe('true');
	});

	it('does not hide old widget topper for embedded widgets', async () => {
		const fragmentEntryLink = getFragmentEntryLink({
			content:
				'<lfr-editable class="page-editor__editable" id="editable-id" type="text">Default content</lfr-editable>',
			fragmentEntryType: 'whatever-that-is-not-widget',
		});

		await act(async () => {
			renderFragmentContent({fragmentEntryLink});
		});

		expect(
			document.querySelector(
				'.page-editor__fragment-content--portlet-topper-hidden'
			)
		).not.toBeInTheDocument();
	});
});
