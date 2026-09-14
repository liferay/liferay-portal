/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render} from '@testing-library/react';
import React from 'react';

import {EVENT_TYPES} from '../../../../src/main/resources/META-INF/resources/js/core/actions/eventTypes.es';
import ResizableColumn from '../../../../src/main/resources/META-INF/resources/js/core/components/ResizableColumn.es';
import {FormNoopProvider} from '../../../../src/main/resources/META-INF/resources/js/core/hooks/useForm.es';

const ROW_WIDTH = 1200;

const currentLoc = {columnIndex: 0, pageIndex: 0, rowIndex: 0};

const renderResizableColumn = () => {
	const onAction = jest.fn();

	const rowRef = {
		current: {
			clientWidth: ROW_WIDTH,
			getBoundingClientRect: () => ({left: 0, right: ROW_WIDTH}),
		},
	};

	const {container} = render(
		<FormNoopProvider onAction={onAction}>
			<ResizableColumn
				currentLoc={currentLoc}
				disabled={false}
				instanceId="field1"
				onResizing={jest.fn()}
				resizeInfoRef={{current: null}}
				rowRef={rowRef}
			>
				<div>Field</div>
			</ResizableColumn>
		</FormNoopProvider>
	);

	return {
		onAction,
		rightHandle: container.querySelector('.ddm-resize-handle-right'),
	};
};

describe('ResizableColumn', () => {
	afterEach(() => {
		document.dir = '';
	});

	it('measures the target column from the left edge of the row', () => {
		const {onAction, rightHandle} = renderResizableColumn();

		fireEvent.mouseDown(rightHandle);
		fireEvent.mouseMove(document.body, {clientX: 300});

		expect(onAction).toHaveBeenCalledWith({
			payload: {column: 3, direction: 'right', loc: [currentLoc]},
			type: EVENT_TYPES.DND.RESIZE,
		});
	});

	it('measures the target column from the right edge of the row in right-to-left languages', () => {
		document.dir = 'rtl';

		const {onAction, rightHandle} = renderResizableColumn();

		fireEvent.mouseDown(rightHandle);
		fireEvent.mouseMove(document.body, {clientX: 300});

		expect(onAction).toHaveBeenCalledWith({
			payload: {column: 9, direction: 'right', loc: [currentLoc]},
			type: EVENT_TYPES.DND.RESIZE,
		});
	});
});
