/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import BorderStateNode from '../../../../src/main/resources/META-INF/resources/js/components/nodes/state-node/BorderStateNode';

describe('The BorderStateNode component should', () => {
	it('Be rendered with start-state class as default', () => {
		const {container} = render(
			<ReactFlowProvider>
				<BorderStateNode
					data={{
						notifyVisibilityChange: () => () => {},
					}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.start-state')).toBeTruthy();
	});

	it('Be rendered with end-state class as default', () => {
		const {container} = render(
			<ReactFlowProvider>
				<BorderStateNode
					data={{
						initial: false,
						notifyVisibilityChange: () => () => {},
					}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.end-state')).toBeTruthy();
	});
});
