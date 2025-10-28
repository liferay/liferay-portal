/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {ControlsProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import CommentsSidebar from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/comments/components/CommentsSidebar';
import StoreMother from '../../../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

const renderComponent = ({activeItemIds = []} = {}) =>
	render(
		<StoreMother.Component>
			<ControlsProvider
				activeInitialState={{
					activeItemIds,
				}}
			>
				<CommentsSidebar />
			</ControlsProvider>
		</StoreMother.Component>
	);

describe('CommentsSidebar', () => {
	it('renders comments sidebar', () => {
		renderComponent();

		expect(
			screen.getByText('there-are-no-comments-yet')
		).toBeInTheDocument();
	});

	it('renders multiselect state when multiple items are selected', () => {
		renderComponent({activeItemIds: ['item-1', 'item-2']});

		expect(
			screen.getByText('multiple-page-elements-selected')
		).toBeInTheDocument();
	});
});
