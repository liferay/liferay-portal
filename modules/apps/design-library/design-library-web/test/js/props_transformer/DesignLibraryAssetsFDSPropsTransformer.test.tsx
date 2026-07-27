/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DesignLibraryAssetsFDSPropsTransformer from '../../../src/main/resources/META-INF/resources/js/props_transformer/DesignLibraryAssetsFDSPropsTransformer';

const BASE_PROPS = {
	id: 'fds-design-library-resources',
	items: [],
} as any;

describe('DesignLibraryAssetsFDSPropsTransformer', () => {
	it('builds the creation menu from the design asset creation items', () => {
		const {creationMenu} = DesignLibraryAssetsFDSPropsTransformer({
			...BASE_PROPS,
			additionalProps: {
				addStyleBookEntryURL: '/style-book',
				canAddStyleBook: true,
			},
		});

		expect(creationMenu?.primaryItems.map((item) => item.label)).toEqual([
			'new-style-book',
		]);
	});

	it('adds an empty creation menu when creation is not allowed', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS).creationMenu
				?.primaryItems
		).toEqual([]);
	});

	it('exposes a single table view', () => {
		expect(
			DesignLibraryAssetsFDSPropsTransformer(BASE_PROPS).views?.map(
				(view) => view.name
			)
		).toEqual(['table']);
	});
});
