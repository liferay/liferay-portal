/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import ConditionNode from '../../../../src/main/resources/META-INF/resources/js/components/nodes/hexagon-nodes/ConditionNode';

describe('The ConditionNode component should', () => {
	it('Be rendered with condition icon and name', () => {
		const label = 'Condition Label';

		const {container, getByText} = render(
			<ReactFlowProvider>
				<ConditionNode
					data={{label, notifyVisibilityChange: () => () => {}}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.lexicon-icon-bolt')).toBeTruthy();
		expect(getByText('Condition Label')).toBeTruthy();
	});

	it('Be rendered as done', () => {
		const label = 'Condition Label';

		const {container} = render(
			<ReactFlowProvider>
				<ConditionNode
					data={{
						done: true,
						label,
						notifyVisibilityChange: () => () => {},
					}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.lexicon-icon-bolt')).toBeTruthy();
		expect(container.querySelector('.lexicon-icon-check')).toBeTruthy();
	});
});
