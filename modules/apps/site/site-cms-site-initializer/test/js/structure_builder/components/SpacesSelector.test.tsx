/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import SpacesSelector from '../../../../src/main/resources/META-INF/resources/js/structure_builder/components/SpacesSelector';
import {Structure} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {MockCacheProvider} from '../mocks/MockCacheProvider';
import {MockStateProvider} from '../mocks/MockStateProvider';

function MockItemSelector({
	items,
	onItemsChange,
}: {
	items: Space[];
	onItemsChange: (items: Space[]) => void;
}) {
	return (
		<button
			data-testid="remove-space"
			onClick={() => onItemsChange(items.slice(0, 1))}
			type="button"
		>
			remove-space
		</button>
	);
}

function MockItemSelectorItem({children}: {children: React.ReactNode}) {
	return <div>{children}</div>;
}

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	ItemSelector: Object.assign(MockItemSelector, {
		Item: MockItemSelectorItem,
	}),
}));

jest.mock('frontend-js-components-web', () => {
	const actual = jest.requireActual('frontend-js-components-web');

	return {
		...actual,
		openToast: jest.fn(),
	};
});

const openToast = require('frontend-js-components-web').openToast as jest.Mock;

const SPACE_1: Space = {
	assetLibraryKey: 'space-1-key',
	creatorUserId: '1',
	description: 'Space 1',
	externalReferenceCode: 'space-1-erc',
	friendlyURL: '/asset-library-1',
	id: 1,
	name: 'Space 1',
	siteId: 1001,
};

const SPACE_2: Space = {
	assetLibraryKey: 'space-2-key',
	creatorUserId: '1',
	description: 'Space 2',
	externalReferenceCode: 'space-2-erc',
	friendlyURL: '/asset-library-2',
	id: 2,
	name: 'Space 2',
	siteId: 1002,
};

const STRUCTURE: Structure = {
	children: new Map(),
	erc: 'structure-erc',
	label: {en_US: 'Structure'} as any,
	name: 'Structure',
	path: '/my-structure',
	slug: '',
	spaces: [SPACE_1.externalReferenceCode, SPACE_2.externalReferenceCode],
	status: 'draft',
	system: false,
	type: 'L_CMS_CONTENT_STRUCTURES',
	uuid: getUuid(),
	workflows: {},
};

describe('SpacesSelector', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('does not allow removing a space with existing content', async () => {
		(SpaceService.getSpaceContents as jest.Mock).mockResolvedValue({
			data: {totalCount: 3},
			error: null,
		});

		const dispatch = jest.fn();

		render(
			<MockStateProvider dispatch={dispatch}>
				<MockCacheProvider spaces={[SPACE_1, SPACE_2]}>
					<SpacesSelector structure={STRUCTURE} />
				</MockCacheProvider>
			</MockStateProvider>
		);

		await userEvent.click(screen.getByTestId('remove-space'));

		await waitFor(() => {
			expect(SpaceService.getSpaceContents).toBeCalledWith({
				path: '/my-structure',
				siteId: SPACE_2.siteId,
			});
		});

		expect(openToast).toBeCalledWith(
			expect.objectContaining({
				message:
					'the-space-x-cannot-be-removed-because-it-has-content-created-from-this-structure',
				type: 'danger',
			})
		);

		expect(dispatch).not.toBeCalledWith(
			expect.objectContaining({
				spaces: [SPACE_1.externalReferenceCode],
				type: 'update-structure',
			})
		);
	});
});
