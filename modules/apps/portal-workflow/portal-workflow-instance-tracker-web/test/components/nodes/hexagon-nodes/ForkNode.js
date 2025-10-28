/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import ForkNode from '../../../../src/main/resources/META-INF/resources/js/components/nodes/hexagon-nodes/ForkNode';

describe('The ForkNode component should', () => {
	it('Be rendered with split icon and name', () => {
		const label = 'Fork label';

		const {container, getByText} = render(
			<ReactFlowProvider>
				<ForkNode
					data={{label, notifyVisibilityChange: () => () => {}}}
				/>
			</ReactFlowProvider>
		);

		expect(
			container.querySelector('.lexicon-icon-arrow-split')
		).toBeTruthy();
		expect(getByText('Fork label')).toBeTruthy();
	});

	it('Be rendered as done', () => {
		const label = 'Fork label';

		const {container} = render(
			<ReactFlowProvider>
				<ForkNode
					data={{
						done: true,
						label,
						notifyVisibilityChange: () => () => {},
					}}
				/>
			</ReactFlowProvider>
		);

		expect(
			container.querySelector('.lexicon-icon-arrow-split')
		).toBeTruthy();
		expect(container.querySelector('.lexicon-icon-check')).toBeTruthy();
	});
});
