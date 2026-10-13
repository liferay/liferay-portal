/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {PercentageOfTotal} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/PercentageOfTotal';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

const TEMPLATE = '{0} of {1} Assets';

function renderPercentage(count: number, total: number) {
	return render(
		<PercentageOfTotal count={count} template={TEMPLATE} total={total} />
	);
}

describe('[CMS Dashboard] PercentageOfTotal', () => {
	it('shows the share of the count over the total with one decimal', () => {
		renderPercentage(9, 350);

		expect(screen.getByText('2.6% of 350 Assets')).toBeInTheDocument();
	});

	it('shows 0% when the total is zero', () => {
		renderPercentage(0, 0);

		expect(screen.getByText('0% of 0 Assets')).toBeInTheDocument();
	});

	it('formats the total with the thousands separator of the language', () => {
		renderPercentage(9, 1280);

		expect(screen.getByText('0.7% of 1,280 Assets')).toBeInTheDocument();
	});

	it('fills the bar in proportion to the share', () => {
		const {container} = renderPercentage(11, 113);

		const bar = container.querySelector('.progress-bar');

		expect(bar).toHaveStyle({width: `${(11 / 113) * 100}%`});
	});
});
