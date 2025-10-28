/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import Card from '../../../src/main/resources/META-INF/resources/js/components/Card';

describe('Card', () => {
	it('rendering component with props', () => {
		const {container, queryByText, rerender} = render(
			<Card highlight={true} title="title">
				children
			</Card>
		);

		expect(queryByText('children')).toBeInTheDocument();
		expect(queryByText('title')).toBeInTheDocument();
		expect(container.querySelector('.card-title-description')).toBeTruthy();

		rerender(<Card title="title">children</Card>);

		expect(container.querySelector('.card-title-description')).toBeFalsy();
	});
});
