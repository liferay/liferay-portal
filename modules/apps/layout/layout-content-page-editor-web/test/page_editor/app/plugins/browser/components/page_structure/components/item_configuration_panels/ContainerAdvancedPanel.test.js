/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateItemConfig from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';
import ContainerAdvancedPanel from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/ContainerAdvancedPanel';

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

function renderComponent(
	itemConfig = {
		tablet: {styles: {}},
	}
) {
	return render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				languageId: 'en_US',
				layoutData: {
					items: [],
				},
				permissions: {UPDATE: true},
				segmentsExperienceId: '0',
				selectedViewportSize: 'desktop',
			})}
		>
			<ContainerAdvancedPanel
				item={{
					children: [],
					config: itemConfig,
					itemId: 'container-id',
					parentId: 'parent-id',
					type: LAYOUT_DATA_ITEM_TYPES.container,
				}}
			/>
		</StoreAPIContextProvider>,
		{
			baseElement: document.body,
		}
	);
}

describe('ContainerAdvancedPanel', () => {
	it('renders stored html tag', () => {
		renderComponent({htmlTag: 'div'});

		expect(screen.getByLabelText('html-tag')).toHaveValue('div');
	});

	it('calls dispatch method with selected html tag', async () => {
		renderComponent();

		const htmlTagSelect = screen.getByLabelText('html-tag');

		fireEvent.change(htmlTagSelect, {
			target: {value: 'section'},
		});

		expect(updateItemConfig).toBeCalledWith(
			expect.objectContaining({
				itemConfig: {
					htmlTag: 'section',
				},
			})
		);
	});
});
