/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import LocalizedTextDataRenderer from '../../../components/FDSPropsTransformer/FDSDataRenderers/LocalizedTextDataRenderer';

describe('The LocalizedTextDataRenderer component', () => {
	it('renders the value the REST layer resolved for the request locale', () => {
		const {container} = render(
			<LocalizedTextDataRenderer
				itemData={{
					name: 'alpha',
					name_i18n: {en_US: 'alpha', pt_BR: 'zulu'},
				}}
				options={{fieldName: 'name'}}
			/>
		);

		expect(container).toHaveTextContent('alpha');
	});

	it('renders nothing when the request and default languages have no value', () => {
		const {container} = render(
			<LocalizedTextDataRenderer
				itemData={{name: '', name_i18n: {de_DE: 'delta'}}}
				options={{fieldName: 'name'}}
			/>
		);

		expect(container.textContent).toBe('');
		expect(container).not.toHaveTextContent('delta');
	});
});
