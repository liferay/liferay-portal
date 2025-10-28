/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import ItemDragLayer from '../../../../src/main/resources/META-INF/resources/js/components/list/ItemDragLayer.es';

import '@testing-library/jest-dom';

describe('ItemDragLayer', () => {
	it('renders when dragging', () => {
		const {container} = render(
			<ItemDragLayer.DecoratedComponent dragging />
		);

		expect(container.firstChild).not.toBeNull();
		expect(container.firstChild).toBeVisible();
	});

	it('does not render by default', () => {
		const {container} = render(<ItemDragLayer.DecoratedComponent />);

		expect(container.firstChild).toBeNull();
	});
});
