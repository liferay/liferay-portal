/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import TaskStatusRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/TaskStatusRenderer';

describe('TaskStatusRenderer', () => {
	it('renders a success label when the task is completed', () => {
		render(<TaskStatusRenderer itemData={{embedded: {completed: true}}} />);

		expect(screen.getByText('completed').closest('.label')).toHaveClass(
			'label-inverse-success'
		);
	});

	it('renders an info label when the task is pending', () => {
		render(
			<TaskStatusRenderer itemData={{embedded: {completed: false}}} />
		);

		expect(screen.getByText('pending').closest('.label')).toHaveClass(
			'label-inverse-info'
		);
	});
});
