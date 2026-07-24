/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DesignLibraryResourcesFDSPropsTransformer from '../../../src/main/resources/META-INF/resources/js/props_transformer/DesignLibraryResourcesFDSPropsTransformer';

const BASE_PROPS = {
	id: 'fds-design-library-resources',
	items: [],
} as any;

describe('DesignLibraryResourcesFDSPropsTransformer', () => {
	it('does not add a creation menu (creation lives in the Add Asset header)', () => {
		expect(
			DesignLibraryResourcesFDSPropsTransformer(BASE_PROPS).creationMenu
		).toBeUndefined();
	});

	it('exposes a single table view', () => {
		expect(
			DesignLibraryResourcesFDSPropsTransformer(BASE_PROPS).views?.map(
				(view) => view.name
			)
		).toEqual(['table']);
	});
});
