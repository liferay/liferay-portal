/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {KeyboardMovementContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/KeyboardMovementContext';
import {StoreAPIContextProvider} from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import updateItemStyle from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/updateItemStyle';
import VisibilityButton from '../../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/VisibilityButton';

jest.mock(
	'../../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/updateItemStyle',
	() => jest.fn(() => () => Promise.resolve())
);

const renderComponent = () =>
	render(
		<StoreAPIContextProvider
			dispatch={() => {}}
			getState={() => ({
				selectedViewportSize: 'tablet',
			})}
		>
			<KeyboardMovementContextProvider>
				<VisibilityButton
					node={{
						hidden: true,
						id: 'fragment01',
						name: 'Test Fragment',
					}}
					selectedViewportSize="tablet"
				/>
			</KeyboardMovementContextProvider>
		</StoreAPIContextProvider>
	);

describe('VisibilityButton', () => {
	it('calls updateItemStyle when the visibility button is pressed', async () => {
		renderComponent();

		await userEvent.click(screen.getByLabelText('show-Test Fragment'));

		expect(updateItemStyle).toBeCalledWith(
			expect.objectContaining({
				itemIds: ['fragment01'],
				selectedViewportSize: 'tablet',
				styleName: 'display',
				styleValue: 'block',
			})
		);
	});
});
