/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, within} from '@testing-library/react';
import React from 'react';

import Item from '../../../../src/main/resources/META-INF/resources/js/components/list/Item.es';

import '@testing-library/jest-dom';

/* eslint-disable no-unused-vars */
jest.mock('react-dnd', () => ({
	DragSource: (element) => (element) => element,
	DropTarget: (element) => (element) => element,
}));

/* eslint-enable no-unused-vars */

const HIDE_BUTTON_LABEL = 'hide-result';
const UNPIN_BUTTON_LABEL = 'unpin-result';

const onBlurFn = jest.fn();
const onClickHideFn = jest.fn();
const onClickPinFn = jest.fn();
const onFocusFn = jest.fn();

function renderTestItem(props) {
	return render(
		<Item
			addedResult={false}
			author="Test Test"
			clicks={289}
			date="Apr 18 2018, 11:04 AM"
			deleted={false}
			description="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod"
			hidden={false}
			icon="web-content"
			id={101}
			index={1}
			key={101}
			onBlur={onBlurFn}
			onClickHide={onClickHideFn}
			onClickPin={onClickPinFn}
			onDragHover={jest.fn()}
			onFocus={onFocusFn}
			onMove={jest.fn()}
			onRemoveSelect={jest.fn()}
			onSelect={jest.fn()}
			pinned={true}
			reorder={true}
			selected={true}
			title="This is a Web Content Example"
			type="Web Content"
			{...props}
		/>
	);
}

describe('Item', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it.each`
		name             | selector                   | text
		${'subtext'}     | ${'list-group-subtext'}    | ${['Test TestApr 18 2018, 11:04 AM', '[Web Content]']}
		${'title'}       | ${'text-truncate-inline'}  | ${['This is a Web Content Example']}
		${'description'} | ${'list-item-description'} | ${['Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod']}
		${'view count'}  | ${'click-count'}           | ${['289']}
	`('shows the appropriate $name', ({selector, text}) => {
		const {container} = renderTestItem();

		text.forEach((txt, i) =>
			expect(
				container.querySelectorAll(`.${selector}`)[i]
			).toHaveTextContent(txt)
		);
	});

	it.each`
		fcn              | title                 | fcnName
		${onClickHideFn} | ${HIDE_BUTTON_LABEL}  | ${'onClickHideFn'}
		${onClickPinFn}  | ${UNPIN_BUTTON_LABEL} | ${'onClickPinFn'}
	`(
		'calls the $fcnName function when the button gets clicked on',
		({fcn, title}) => {
			const {container} = renderTestItem();

			fireEvent.click(within(container).getByTitle(title));

			expect(fcn.mock.calls.length).toBe(1);
		}
	);

	it('calls the onFocus event when focused', () => {
		const {getByTestId} = renderTestItem();

		fireEvent.focus(getByTestId('101'));

		expect(onFocusFn.mock.calls.length).toBe(1);
	});

	it('calls the onBlur event when un-focused', () => {
		const {getByTestId} = renderTestItem();

		fireEvent.blur(getByTestId('101'));

		expect(onBlurFn.mock.calls.length).toBe(1);
	});

	it('does not call onFocus when a button within is focused', () => {
		const {getByTitle} = renderTestItem();

		fireEvent.focus(getByTitle(UNPIN_BUTTON_LABEL));

		expect(onFocusFn.mock.calls.length).toBe(0);
	});

	it('renders a link to viewURL', () => {
		const viewURL = '#';

		const {container} = renderTestItem({viewURL});

		expect(container.querySelector('a').getAttribute('href')).toBe(viewURL);
	});

	it('displays a deleted label if deleted is true', () => {
		const {container} = renderTestItem({deleted: true});

		expect(container).toHaveTextContent('deleted');
	});
});
