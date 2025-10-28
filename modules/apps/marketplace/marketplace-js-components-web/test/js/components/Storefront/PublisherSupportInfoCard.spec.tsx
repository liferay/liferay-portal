/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import PublisherSupportInfoCard from '../../../../src/main/resources/META-INF/resources/js/components/Storefront/PublisherSupportInfoCard';

describe('PublisherSupportInfoCard', () => {
	it('rendering components with its props', () => {
		const {container, queryByText, rerender} = render(
			<PublisherSupportInfoCard
				title="title"
				urlImage="https://liferay.com/liferay-logo.png"
				value="value"
			/>
		);

		expect(container.querySelector('img')).toHaveAttribute(
			'src',
			'https://liferay.com/liferay-logo.png'
		);
		expect(container.querySelector('.lexicon-icon.symbol')).toBeFalsy();
		expect(queryByText('title')).toBeInTheDocument();
		expect(queryByText('value')).toBeInTheDocument();

		rerender(<PublisherSupportInfoCard symbol="symbol" value="value" />);

		expect(container.querySelector('.lexicon-icon-symbol')).toBeTruthy();
		expect(container.querySelector('img')).toBeFalsy();
		expect(queryByText('title')).toBeFalsy();
		expect(queryByText('value')).toBeInTheDocument();
	});
});
