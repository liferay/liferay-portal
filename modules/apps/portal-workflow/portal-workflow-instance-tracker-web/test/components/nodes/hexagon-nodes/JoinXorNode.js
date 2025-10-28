/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import JoinXorNode from '../../../../src/main/resources/META-INF/resources/js/components/nodes/hexagon-nodes/JoinXorNode';

describe('The JoinXorNode component should', () => {
	it('Be rendered with join xor icon and name', () => {
		const label = 'Join Xor Label';

		const {container, getByText} = render(
			<ReactFlowProvider>
				<JoinXorNode
					data={{label, notifyVisibilityChange: () => () => {}}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.lexicon-icon-arrow-xor')).toBeTruthy();
		expect(getByText('Join Xor Label')).toBeTruthy();
	});

	it('Be rendered as done', () => {
		const label = 'Join Xor Label';

		const {container} = render(
			<ReactFlowProvider>
				<JoinXorNode
					data={{
						done: true,
						label,
						notifyVisibilityChange: () => () => {},
					}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.lexicon-icon-arrow-xor')).toBeTruthy();
		expect(container.querySelector('.lexicon-icon-check')).toBeTruthy();
	});
});
