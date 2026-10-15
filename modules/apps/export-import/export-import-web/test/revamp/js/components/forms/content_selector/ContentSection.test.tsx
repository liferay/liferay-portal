/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import ContentSection from '../../../../../../src/main/resources/META-INF/resources/revamp/js/components/forms/content_selector/ContentSection';
import {PortletDataHandlerSection} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/types/portletDataHandler';
import {SECTION_KEY_CONTENT} from '../../../../../../src/main/resources/META-INF/resources/revamp/js/utils/contentSelection';

function getPortletDataHandlerSection(
	key: string,
	counts: {additionCount?: number; deletionCount?: number} = {}
): PortletDataHandlerSection {
	return {
		...counts,
		key,
		label: key,
		portletDataHandlers: [{label: 'Handler', name: 'handler'}],
	};
}

const portletDataHandlerSection: PortletDataHandlerSection = {
	key: SECTION_KEY_CONTENT,
	label: SECTION_KEY_CONTENT,
	portletDataHandlers: [
		{
			label: 'Handler',
			name: 'handler',
			portletDataHandlerControls: [
				{label: 'Child 1', name: 'child1', type: 'Boolean'},
				{label: 'Child 2', name: 'child2', type: 'Boolean'},
			],
		},
	],
};

describe('ContentSection', () => {
	it('renders a compact scrollable body for sections in SECTION_KEYS_COMPACT', () => {
		const {container} = render(
			<ContentSection
				onChange={jest.fn()}
				portletDataHandlerSection={getPortletDataHandlerSection(
					'objects'
				)}
				sectionSelection={undefined}
			/>
		);

		expect(
			container.querySelector('.content-section-scroll')
		).not.toBeNull();
	});

	it('renders a regular body for sections not in SECTION_KEYS_COMPACT', () => {
		const {container} = render(
			<ContentSection
				onChange={jest.fn()}
				portletDataHandlerSection={getPortletDataHandlerSection(
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
				portletDataHandlerSection={getPortletDataHandlerSection(
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

	it('hides the deletion count label when deletions are not shown', () => {
		const {queryByText} = render(
			<ContentSection
				onChange={jest.fn()}
				portletDataHandlerSection={getPortletDataHandlerSection(
					'web-content',
					{additionCount: 1, deletionCount: 1}
				)}
				sectionSelection={undefined}
			/>
		);

		expect(queryByText('x-item')).not.toBeNull();
		expect(queryByText('x-deletion')).toBeNull();
	});

	it('renders the handler addition and deletion counts', () => {
		const {queryByText} = render(
			<ContentSection
				onChange={jest.fn()}
				portletDataHandlerSection={{
					key: SECTION_KEY_CONTENT,
					label: SECTION_KEY_CONTENT,
					portletDataHandlers: [
						{
							additionCount: 2,
							deletionCount: 1,
							label: 'Handler',
							name: 'handler',
						},
					],
				}}
				sectionSelection={undefined}
				showDeletions
			/>
		);

		expect(queryByText('x-items')).not.toBeNull();
		expect(queryByText('x-deletion')).not.toBeNull();
	});

	it('hides the comments and ratings footer when nothing is selected', () => {
		const {queryByText} = render(
			<ContentSection
				commentsAndRatingsEnabled
				onChange={jest.fn()}
				portletDataHandlerSection={portletDataHandlerSection}
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
				portletDataHandlerSection={portletDataHandlerSection}
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
				portletDataHandlerSection={portletDataHandlerSection}
				sectionSelection={{handler: {child1: true}}}
			/>
		);

		expect(queryByText('comments-and-ratings')).not.toBeNull();
	});

	it('hides the comments and ratings footer when comments and ratings are disabled', () => {
		const {queryByText} = render(
			<ContentSection
				commentsAndRatingsEnabled={false}
				onChange={jest.fn()}
				portletDataHandlerSection={portletDataHandlerSection}
				sectionSelection={{handler: {child1: true, child2: true}}}
			/>
		);

		expect(queryByText('comments-and-ratings')).toBeNull();
	});
});
