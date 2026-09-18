/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ConnectorStatusRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/ConnectorStatusRenderer';

describe('ConnectorStatusRenderer', () => {
	it('labels an active connector as active', () => {
		render(<ConnectorStatusRenderer value={true} />);

		expect(screen.getByText('active')).toBeInTheDocument();
	});

	it('labels an inactive connector as inactive', () => {
		render(<ConnectorStatusRenderer value={false} />);

		expect(screen.getByText('inactive')).toBeInTheDocument();
	});
});
