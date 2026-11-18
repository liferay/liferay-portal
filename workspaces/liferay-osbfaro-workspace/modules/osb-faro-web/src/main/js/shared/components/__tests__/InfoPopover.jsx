import InfoPopover from '../InfoPopover';
import React from 'react';
import ReactDOM from 'react-dom';
import {cleanup, render} from '@testing-library/react';

jest.unmock('react-dom');

ReactDOM.createPortal = jest.fn();

describe('InfoPopover', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<InfoPopover content='foo content' title='foo title' />
		);
		expect(container).toMatchSnapshot();
	});

	it('should be reachable with the keyboard', () => {
		const {getByRole} = render(
			<InfoPopover content='foo content' title='foo title' />
		);

		expect(getByRole('button', {name: 'foo title'})).toHaveAttribute(
			'tabindex',
			'0'
		);
	});

	it('should fall back to a generic label when there is no title', () => {
		const {getByRole} = render(<InfoPopover content='foo content' />);

		expect(
			getByRole('button', {name: /^more information$/i})
		).toBeInTheDocument();
	});
});
