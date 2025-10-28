/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useResource} from '@clayui/data-provider';
import {render} from '@testing-library/react';
import React from 'react';

import AssetVocabularyCategoriesSelector from '../../src/main/resources/META-INF/resources/js/asset_categories_selector/AssetVocabularyCategoriesSelector';

const DEFAULT_PROPS = {
	eventName: 'selectCategory',
	groupIds: [],
	inputName: '',
	portletURL: '',
};
jest.mock('@clayui/data-provider', () => ({
	useResource: jest.fn().mockImplementation(() => ({
		refetch: jest.fn(),
		resource: [],
	})),
}));

describe('AssetVocabularyCategoriesSelector', () => {
	it('refetch is not called in the first component render', () => {
		render(<AssetVocabularyCategoriesSelector {...DEFAULT_PROPS} />);

		const {refetch} = useResource();

		expect(refetch).not.toHaveBeenCalled();
	});
});
