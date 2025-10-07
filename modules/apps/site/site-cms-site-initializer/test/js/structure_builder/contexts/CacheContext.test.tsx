/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import '@testing-library/jest-dom/extend-expect';
import {render, waitFor} from '@testing-library/react';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import {useCache} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/CacheContext';
import {MockCacheProvider} from '../mocks/MockCacheProvider';
import {MockStateProvider} from '../mocks/MockStateProvider';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService',
	() => ({
		getSpaces: jest.fn(() => Promise.resolve([])),
	})
);

function MyComponent() {
	const {data: spaces} = useCache('spaces');

	return (
		<>
			{spaces.map((space) => (
				<span key={space.externalReferenceCode}>{space.name}</span>
			))}
		</>
	);
}

const renderComponent = ({
	spaces,
}: {
	spaces?: Space[];
} = {}) => {
	return render(
		<MockStateProvider>
			<MockCacheProvider spaces={spaces}>
				<MyComponent />
			</MockCacheProvider>
		</MockStateProvider>
	);
};

describe('StructureSettings', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		(global as any).Liferay.Language.direction = {
			en_US: 'rtl',
		};
	});

	it('does not call server if cache contains spaces', async () => {
		renderComponent({spaces: []});

		expect(SpaceService.getSpaces).not.toBeCalled();
	});

	it('calls server if cache does not contains spaces', async () => {
		renderComponent();

		await waitFor(() => {
			expect(SpaceService.getSpaces).toBeCalled();
		});
	});
});
