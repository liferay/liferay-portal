/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import ContentSection from '../../../../../../src/main/resources/META-INF/resources/revamp/js/components/forms/content_selector/ContentSection';
import {PreviewPortletDataHandlerSection} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';
import {SECTION_KEY_CONTENT} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';

function getPreviewPortletDataHandlerSection(
	name: string,
	counts: {additionCount?: number; deletionCount?: number} = {}
): PreviewPortletDataHandlerSection {
	return {
		...counts,
		label: name,
		name,
		previewPortletDataHandlers: [{label: 'Handler', name: 'handler'}],
	};
}

const previewPortletDataHandlerSection: PreviewPortletDataHandlerSection = {
	label: SECTION_KEY_CONTENT,
	name: SECTION_KEY_CONTENT,
	previewPortletDataHandlers: [
		{
			label: 'Handler',
			name: 'handler',
			previewPortletDataHandlerControls: [
				{label: 'Child 1', name: 'child1', type: 'Boolean'},
				{label: 'Child 2', name: 'child2', type: 'Boolean'},
			],
		},
	],
};

describe('ContentSection', () => {
	it('renders a compact scrollable body for sections in COMPACT_SECTION_NAMES', () => {
		const {container} = render(
			<ContentSection
				onChange={jest.fn()}
				previewPortletDataHandlerSection={getPreviewPortletDataHandlerSection(
					'objects'
				)}
				sectionSelection={undefined}
			/>
		);

		expect(
			container.querySelector('.content-section-scroll')
		).not.toBeNull();
	});

	it('renders a regular body for sections not in COMPACT_SECTION_NAMES', () => {
		const {container} = render(
			<ContentSection
				onChange={jest.fn()}
				previewPortletDataHandlerSection={getPreviewPortletDataHandlerSection(
					'web-content'
				)}
				sectionSelection={undefined}
			/>
		);

		expect(container.querySelector('.content-section-scroll')).toBeNull();
	});

	it('uses singular count labels for a single addition and deletion', () => {
		const {queryByText} = render(
			<ContentSection
				onChange={jest.fn()}
				previewPortletDataHandlerSection={getPreviewPortletDataHandlerSection(
					'web-content',
					{
						additionCount: 1,
						deletionCount: 1,
					}
				)}
				sectionSelection={undefined}
				showDeletions
			/>
		);

		expect(queryByText('x-item')).not.toBeNull();
		expect(queryByText('x-deletion')).not.toBeNull();
		expect(queryByText('x-items')).toBeNull();
		expect(queryByText('x-deletions')).toBeNull();
	});

	it('renders the handler description and tag', () => {
		const {queryByText} = render(
			<ContentSection
				onChange={jest.fn()}
				previewPortletDataHandlerSection={{
					label: SECTION_KEY_CONTENT,
					name: SECTION_KEY_CONTENT,
					previewPortletDataHandlers: [
						{
							description: 'Child 1, Child 2',
							label: 'Root Handler',
							name: 'handler',
							tag: 'root-object',
						},
					],
				}}
				sectionSelection={undefined}
			/>
		);

		expect(queryByText('Child 1, Child 2')).not.toBeNull();
		expect(queryByText('root-object')).not.toBeNull();
	});

	it('hides the comments and ratings footer when nothing is selected', () => {
		const {queryByText} = render(
			<ContentSection
				commentsAndRatingsEnabled
				onChange={jest.fn()}
				previewPortletDataHandlerSection={
					previewPortletDataHandlerSection
				}
				sectionSelection={undefined}
			/>
		);

		expect(queryByText('comments-and-ratings')).toBeNull();
	});

	it('shows the comments and ratings footer when a handler is fully selected', () => {
		const {queryByText} = render(
			<ContentSection
				commentsAndRatingsEnabled
				onChange={jest.fn()}
				previewPortletDataHandlerSection={
					previewPortletDataHandlerSection
				}
				sectionSelection={{handler: {child1: true, child2: true}}}
			/>
		);

		expect(queryByText('comments-and-ratings')).not.toBeNull();
	});

	it('shows the comments and ratings footer when a lower-level node is selected', () => {
		const {queryByText} = render(
			<ContentSection
				commentsAndRatingsEnabled
				onChange={jest.fn()}
				previewPortletDataHandlerSection={
					previewPortletDataHandlerSection
				}
				sectionSelection={{handler: {child1: true}}}
			/>
		);

		expect(queryByText('comments-and-ratings')).not.toBeNull();
	});
});
