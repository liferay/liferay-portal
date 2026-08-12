/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FRAGMENT_ENTRY_TYPES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/fragmentEntryTypes';
import canBeDuplicated from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/canBeDuplicated';
import getMockFragmentEntryLink from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/getMockFragmentEntryLink';
import getMockFragmentItem from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/getMockFragmentItem';
import {LayoutData} from '../../../../src/main/resources/META-INF/resources/page_editor/types/layout_data/LayoutData';

const LAYOUT_DATA = {items: {}} as LayoutData;

describe('canBeDuplicated', () => {
	it('can not duplicate a Stepper fragment', () => {
		const stepper = getMockFragmentItem({
			fragmentEntryLinkId: 'stepper-fragment',
		});

		const fragmentEntryLinks = {
			'stepper-fragment': getMockFragmentEntryLink({
				fieldTypes: ['stepper'],
				fragmentEntryLinkId: 'stepper-fragment',
				fragmentEntryType: FRAGMENT_ENTRY_TYPES.input,
			}),
		};

		expect(
			canBeDuplicated(fragmentEntryLinks, stepper, LAYOUT_DATA, () => [])
		).toBe(false);
	});

	it('can only duplicate instanceable widgets', () => {
		const instanceableWidget = getMockFragmentItem({
			fragmentEntryLinkId: 'instanceable',
		});

		const nonInstanceableWidget = getMockFragmentItem({
			fragmentEntryLinkId: 'nonInstanceable',
		});

		const fragmentEntryLinks = {
			instanceable: getMockFragmentEntryLink({
				editableValues: {portletId: 'instanceable'},
				fragmentEntryLinkId: 'instanceable',
			}),
			nonInstanceable: getMockFragmentEntryLink({
				editableValues: {portletId: 'nonInstanceable'},
				fragmentEntryLinkId: 'nonInstanceable',
			}),
		};

		const widgets = [
			{
				categories: [],
				path: '',
				portlets: [
					{
						highlighted: false,
						instanceable: true,
						portletId: 'instanceable',
						portletItems: [],
						title: '',
					},
					{
						highlighted: false,
						instanceable: false,
						portletId: 'nonInstanceable',
						portletItems: [],
						title: '',
					},
				],
				title: '',
			},
		];

		expect(
			canBeDuplicated(
				fragmentEntryLinks,
				instanceableWidget,
				LAYOUT_DATA,
				() => widgets
			)
		).toBe(true);

		expect(
			canBeDuplicated(
				fragmentEntryLinks,
				nonInstanceableWidget,
				LAYOUT_DATA,
				() => widgets
			)
		).toBe(false);
	});
});
