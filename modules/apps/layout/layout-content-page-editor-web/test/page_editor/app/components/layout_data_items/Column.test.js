/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import {Column} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';

const renderColumn = ({
	hasUpdatePermissions = true,
	lockedExperience = false,
} = {}) => {
	const column = {
		children: [],
		config: {},
		itemId: 'column',
		parentId: null,
		type: LAYOUT_DATA_ITEM_TYPES.column,
	};

	return render(
		<StoreAPIContextProvider
			getState={() => ({
				permissions: {
					LOCKED_SEGMENTS_EXPERIMENT: lockedExperience,
					UPDATE: hasUpdatePermissions,
				},
			})}
		>
			<Column item={column} />
		</StoreAPIContextProvider>
	);
};

describe('Column', () => {
	afterEach(cleanup);

	it('removes column borders if current experience is locked', () => {
		const {container} = renderColumn({lockedExperience: true});

		expect(container.querySelector('.page-editor__col__border')).toBe(null);
	});

	it('removes column borders if user has no permissions', () => {
		const {container} = renderColumn({hasUpdatePermissions: false});

		expect(container.querySelector('.page-editor__col__border')).toBe(null);
	});
});
