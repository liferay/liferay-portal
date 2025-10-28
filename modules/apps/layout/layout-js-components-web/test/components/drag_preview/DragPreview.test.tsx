/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import {DragPreview} from '../../../src/main/resources/META-INF/resources/js';

declare module Liferay {
	export const Language: {direction: {[key: string]: string}};
}

interface DragItem {
	icon?: string;
	name?: string;
}

jest.mock('react-dnd', () => ({
	...(jest.requireActual('react-dnd') as Object),
	useDragLayer: jest.fn(() => ({
		isDragging: true,
	})),
}));

const renderDragPreview = ({
	getIcon = (item: DragItem) => item?.icon || '',
	getLabel = (item: DragItem) => item?.name || 'element',
} = {}) =>
	render(

		// @ts-ignore

		<DndProvider backend={HTML5Backend}>
			<DragPreview getIcon={getIcon} getLabel={getLabel}></DragPreview>
		</DndProvider>
	);

describe('DragPreview', () => {
	beforeAll(() => {
		Liferay.Language.direction = {en_US: 'ltr'};
	});

	it('does not render Drag Preview if label and icon are empty', () => {
		const {container} = renderDragPreview({
			getIcon: () => '',
			getLabel: () => '',
		});

		expect(container).toBeEmpty();
	});
});
