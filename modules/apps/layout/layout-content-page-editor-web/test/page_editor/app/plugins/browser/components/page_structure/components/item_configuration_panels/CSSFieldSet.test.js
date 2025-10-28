/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {VIEWPORT_SIZES} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import CSSFieldSet from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/item_configuration_panels/CSSFieldSet';

const ITEM = {
	config: {
		cssClasses: ['otherClass1', 'otherClass2'],
	},
	itemId: 'itemId',
	type: LAYOUT_DATA_ITEM_TYPES.container,
};

const renderCSSFieldSet = ({permissions}) => {
	return render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				languageId: 'en_US',
				layoutData: {
					items: {
						itemId: {
							...ITEM,
						},
					},
				},
				permissions,
				selectedViewportSize: VIEWPORT_SIZES.desktop,
			})}
		>
			<CSSFieldSet item={ITEM} />
		</StoreAPIContextProvider>
	);
};

describe('CSSFieldSet', () => {
	it('shows css collapse with UPDATE permission', () => {
		renderCSSFieldSet({permissions: {UPDATE: true}});

		expect(screen.getByText('css')).toBeInTheDocument();
	});

	it('does not show css collapse with UPDATE permission', () => {
		renderCSSFieldSet({permissions: {UPDATE_LAYOUT_BASIC: true}});

		expect(screen.queryByText('css')).not.toBeInTheDocument();
	});

	it('does not show css collapse with UPDATE permission', () => {
		renderCSSFieldSet({permissions: {UPDATE_LAYOUT_LIMITED: true}});

		expect(screen.queryByText('css')).not.toBeInTheDocument();
	});
});
