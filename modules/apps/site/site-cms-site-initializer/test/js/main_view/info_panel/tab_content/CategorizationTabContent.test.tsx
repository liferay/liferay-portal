/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, waitFor} from '@testing-library/react';
import React from 'react';

import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/context';
import ObjectEntryService from '../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/ObjectEntryService';
import CategorizationTabContent from '../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/tab_content/CategorizationTabContent';

const mockAssetCategorizationSections = jest.fn();

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/components/AssetCategorizationSections',
	() => ({
		__esModule: true,
		default: (props: unknown) => {
			mockAssetCategorizationSections(props);

			return null;
		},
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/services/ObjectEntryService',
	() => ({
		__esModule: true,
		default: {
			getObjectEntry: jest.fn(),
		},
	})
);

function mockGetObjectEntry(statusLabel: string) {
	(ObjectEntryService.getObjectEntry as jest.Mock).mockResolvedValue({
		data: {
			keywords: [],
			status: {code: 0, label: statusLabel},
			taxonomyCategoryBriefs: [],
		},
	});
}

function renderCategorizationTabContent() {
	render(
		<AssetTypeInfoPanelContext.Provider
			value={
				{
					actions: {
						get: {href: '/get'},
						update: {href: '/update'},
					},
					asset: {externalReferenceCode: 'ASSET-1'},
					assetLibrary: {groupId: 1},
					cmsGroupId: 2,
				} as unknown as IAssetTypeInfoPanelContext
			}
		>
			<CategorizationTabContent />
		</AssetTypeInfoPanelContext.Provider>
	);
}

describe('CategorizationTabContent', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('allows editing the categorization of an approved asset', async () => {
		mockGetObjectEntry('approved');

		renderCategorizationTabContent();

		await waitFor(() =>
			expect(mockAssetCategorizationSections).toHaveBeenCalledWith(
				expect.objectContaining({hasUpdatePermission: true})
			)
		);
	});

	it('disables editing the categorization of an expired asset', async () => {
		mockGetObjectEntry('expired');

		renderCategorizationTabContent();

		await waitFor(() =>
			expect(mockAssetCategorizationSections).toHaveBeenCalledWith(
				expect.objectContaining({hasUpdatePermission: false})
			)
		);
	});
});
