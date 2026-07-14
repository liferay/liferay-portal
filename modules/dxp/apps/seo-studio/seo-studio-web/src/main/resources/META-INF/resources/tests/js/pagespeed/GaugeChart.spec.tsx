/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import GaugeChart from '../../../js/pagespeed/GaugeChart';

describe('GaugeChart', () => {
	it('renders the label passed in', () => {
		render(<GaugeChart label="performance" score={75} />);

		expect(screen.getByText('performance')).toBeInTheDocument();
	});

	it('shows the "good" rating when the score is 90 or above', () => {
		render(<GaugeChart label="performance" score={92} />);

		expect(screen.getByText('good')).toBeInTheDocument();
	});

	it('shows the "needs-improvement" rating when the score is between 50 and 89', () => {
		render(<GaugeChart label="performance" score={75} />);

		expect(screen.getByText('needs-improvement')).toBeInTheDocument();
	});

	it('shows the "poor" rating when the score is below 50', () => {
		render(<GaugeChart label="performance" score={30} />);

		expect(screen.getByText('poor')).toBeInTheDocument();
	});

	it('renders without error when the score is zero', () => {
		render(<GaugeChart label="performance" score={0} />);

		expect(screen.getByText('poor')).toBeInTheDocument();
		expect(screen.getByText('performance')).toBeInTheDocument();
	});
});
