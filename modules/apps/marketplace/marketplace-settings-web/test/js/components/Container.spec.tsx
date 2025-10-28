/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import Container from '../../../src/main/resources/META-INF/resources/js/components/Container';

test('render container', async () => {
	const onClickMock = jest.fn();

	const {container, queryByText} = render(
		<Container
			description="Marketplace Description"
			footer={<button onClick={onClickMock}>Connect</button>}
			title="Connect to the Marketplace"
		>
			<h5>Container Child</h5>
		</Container>
	);

	expect(queryByText('Connect to the Marketplace')).toBeInTheDocument();
	expect(queryByText('Marketplace Description')).toBeInTheDocument();

	expect(queryByText('Container Child')).toBeInTheDocument();

	const button = container.querySelector('button');

	fireEvent.click(button as HTMLButtonElement);

	expect(onClickMock).toBeCalledTimes(1);
});
