/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';
import {ReactFlowProvider} from 'react-flow-renderer';

import TaskNode from '../../../src/main/resources/META-INF/resources/js/components/nodes/TaskNode';

describe('The TaskNode component should', () => {
	it('Be rendered without any icon and without current-icon or done-icon class as default', () => {
		const {container} = render(
			<ReactFlowProvider>
				<TaskNode data={{notifyVisibilityChange: () => () => {}}} />
			</ReactFlowProvider>
		);

		expect(container.querySelector('.lexicon-icon-live')).not.toBeTruthy();
		expect(container.querySelector('.lexicon-icon-check')).not.toBeTruthy();
		expect(container.querySelector('.current-icon')).not.toBeTruthy();
		expect(container.querySelector('.done-icon')).not.toBeTruthy();
	});

	it('Be rendered with live icon and current-icon class when it receives the current prop', () => {
		const {container} = render(
			<ReactFlowProvider>
				<TaskNode
					data={{
						current: true,
						notifyVisibilityChange: () => () => {},
					}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.lexicon-icon-live')).toBeTruthy();
		expect(container.querySelector('.lexicon-icon-check')).not.toBeTruthy();
		expect(container.querySelector('.current-icon')).toBeTruthy();
		expect(container.querySelector('.done-icon')).not.toBeTruthy();
	});

	it('Be rendered with check icon and done-icon class when it receives the done prop', () => {
		const {container} = render(
			<ReactFlowProvider>
				<TaskNode
					data={{done: true, notifyVisibilityChange: () => () => {}}}
				/>
			</ReactFlowProvider>
		);

		expect(container.querySelector('.lexicon-icon-check')).toBeTruthy();
		expect(container.querySelector('.lexicon-icon-live')).not.toBeTruthy();
		expect(container.querySelector('.done-icon')).toBeTruthy();
		expect(container.querySelector('.current-icon')).not.toBeTruthy();
	});
});
