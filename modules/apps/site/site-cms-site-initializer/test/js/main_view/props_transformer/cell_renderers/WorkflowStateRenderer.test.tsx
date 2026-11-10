/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import WorkflowStateRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/WorkflowStateRenderer';

describe('WorkflowStateRenderer', () => {
	it('renders the Completed label when embedded.completed is true', () => {
		const {getByText} = render(
			<WorkflowStateRenderer itemData={{embedded: {completed: true}}} />
		);

		expect(getByText('completed')).toBeInTheDocument();
	});

	it('renders the Pending label when embedded.completed is false', () => {
		const {getByText} = render(
			<WorkflowStateRenderer itemData={{embedded: {completed: false}}} />
		);

		expect(getByText('pending')).toBeInTheDocument();
	});
});
