/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import ModelInfo from '../../../src/main/resources/META-INF/resources/js/components/model-info/ModelInfo';

describe('The ModelInfo component should', () => {
	let label;
	let value;

	afterEach(cleanup);

	it('render with ID label', () => {
		label = 'ID';
		value = 'Valid ID';

		const {debug, queryByText} = render(
			<ModelInfo label={label} value={value} />
		);

		debug();

		const id = queryByText('ID:');
		const idValue = queryByText('Valid ID');

		expect(id).toBeTruthy();
		expect(idValue).toBeTruthy();
	});

	it('render with Version label', () => {
		label = 'Version';
		value = 'Valid Version';

		const {queryByText} = render(<ModelInfo label={label} value={value} />);

		const version = queryByText(`${label}:`);
		const versionValue = queryByText('Valid Version');

		expect(version).toBeTruthy();
		expect(versionValue).toBeTruthy();
	});
});
