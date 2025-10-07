/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import {useSelector} from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import StructureTreeContent from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/StructureTreeContent';
import getTreeNodes from '../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/getTreeNodes';

import '@testing-library/jest-dom/extend-expect';

jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/browser/components/page_structure/components/getTreeNodes'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext',
	() => ({
		useSelector: jest.fn(),
		useSelectorRef: jest.fn(() => ({current: {}})),
	})
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/selectors/selectCanUpdateEditables'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/selectors/selectCanUpdateItemConfiguration'
);
jest.mock(
	'../../../../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents'
);

const originalConsoleError = console.error;

describe('StructureTreeContent', () => {
	beforeEach(() => {
		console.error = jest.fn();
	});

	afterAll(() => {
		console.error = originalConsoleError;
	});

	test('should log an error in development mode and display "no content" alert when the root item is missing', () => {
		process.env.NODE_ENV = 'development';
		const nonExistentMainId = 'non-existent-main';

		useSelector.mockImplementation((selector) => {
			const mockState = {
				layoutData: {
					items: {},
					rootItems: {main: nonExistentMainId},
				},
			};

			return selector(mockState);
		});

		getTreeNodes.mockReturnValue({children: []});

		render(
			<StructureTreeContent
				expandedKeys={[]}
				setExpandedKeys={jest.fn()}
			/>
		);

		expect(console.error).toHaveBeenCalledWith(
			`Root item with id ${nonExistentMainId} not found in layout data.`
		);

		expect(
			screen.getByText('there-is-no-content-on-this-page')
		).toBeInTheDocument();

		expect(getTreeNodes).toHaveBeenCalled();
		const getTreeNodesArgs = getTreeNodes.mock.calls[0];
		expect(getTreeNodesArgs[0]).toBeUndefined();
	});
});
