/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import React from 'react';

import {useData} from '../../../../src/main/resources/META-INF/resources/js/hooks/useData';

const DataStatus = () => {
	const {dataDefinition, dataLayout} = useData({
		dataDefinitionId: '1',
		dataLayoutId: '2',
	});

	return dataDefinition && dataLayout ? 'loaded' : 'loading';
};

describe('useData', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		jest.useRealTimers();
	});

	it('loads the data when the server takes longer than six seconds to answer', async () => {
		const slowResponse = () =>
			new Promise((resolve) =>
				setTimeout(() => resolve(JSON.stringify({})), 10000)
			);

		fetch.mockResponseOnce(slowResponse);
		fetch.mockResponseOnce(slowResponse);

		render(<DataStatus />);

		expect(screen.getByText('loading')).toBeInTheDocument();

		await act(async () => {
			jest.advanceTimersByTime(10000);
		});

		expect(await screen.findByText('loaded')).toBeInTheDocument();
	});
});
