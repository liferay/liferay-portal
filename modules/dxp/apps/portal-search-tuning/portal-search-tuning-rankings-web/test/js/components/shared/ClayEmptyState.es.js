/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import ClayEmptyState from '../../../../src/main/resources/META-INF/resources/js/components/shared/ClayEmptyState.es';

import '@testing-library/jest-dom';

describe('ClayEmptyState', () => {
	it('renders', () => {
		const {container} = render(<ClayEmptyState />);

		expect(container).not.toBeNull();
	});

	it('displays a custom title', () => {
		const {getByText} = render(<ClayEmptyState title="Test Title" />);

		expect(getByText('Test Title')).toBeInTheDocument();
	});

	it('displays a custom description', () => {
		const {getByText} = render(
			<ClayEmptyState description="Test Description" />
		);

		expect(getByText('Test Description')).toBeInTheDocument();
	});
});
