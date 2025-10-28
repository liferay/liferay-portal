/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import DefaultPageHeader from '../../../src/main/resources/META-INF/resources/js/components/DefaultPageHeader';

describe('DefaultPageHeader', () => {
	it('shows the back button, page description and page title', () => {
		const {getByText} = render(
			<DefaultPageHeader
				description="Page description"
				hideBackButton
				title="Page title"
			/>
		);
		expect(getByText('back')).toBeInTheDocument();
		expect(getByText('Page description')).toBeInTheDocument();
		expect(getByText('Page title')).toBeInTheDocument();
	});
});
