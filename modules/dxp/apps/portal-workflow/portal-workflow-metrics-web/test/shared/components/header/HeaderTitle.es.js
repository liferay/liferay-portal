/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import HeaderTitle from '../../../../src/main/resources/META-INF/resources/js/shared/components/header/HeaderTitle.es';

import '@testing-library/jest-dom';

describe('The HeaderTitle component should', () => {
	test('Render with title "Metrics" and rerender with "Single Approver"', () => {
		const body = document.createElement('div');

		body.innerHTML = '<div id="workflow" data-testid="workflow"></div>';

		document.body.appendChild(body);

		document.title = 'Metrics';

		const containerWrapper = document.getElementById('workflow');

		const {getByTestId, rerender} = render(
			<HeaderTitle container={containerWrapper} title="Metrics" />
		);

		const container = getByTestId('workflow');

		expect(container.children[0]).toHaveTextContent('Metrics');
		expect(document.title).toBe('Metrics');

		rerender(
			<HeaderTitle container={containerWrapper} title="Single Approver" />
		);

		expect(container.children[0]).toHaveTextContent('Single Approver');
		expect(document.title).toBe('Single Approver');
	});
});
