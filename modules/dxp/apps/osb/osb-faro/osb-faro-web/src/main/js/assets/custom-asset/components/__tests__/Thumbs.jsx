import React from 'react';
import Thumbs from '../Thumbs';
import {fireEvent, render} from '@testing-library/react';

const items = [
	{
		selected: true,
		svg: 'cerebro_thumb_line_chart',
		text: 'this is a thumb 1',
		value: 'line'
	},
	{
		selected: false,
		svg: 'cerebro_thumb_line_chart',
		text: 'this is a thumb 2',
		value: 'line'
	},
	{
		selected: false,
		svg: 'cerebro_thumb_line_chart',
		text: 'this is a thumb 3',
		value: 'line'
	}
];

const defaultProps = {
	items,
	onSelectThumb: jest.fn()
};

jest.unmock('react-dom');

describe('AddReport', () => {
	it('should render', () => {
		const {container} = render(<Thumbs {...defaultProps} />);

		expect(container).toMatchSnapshot();
	});

	it('should select the third thumb when clicked', () => {
		const {getByTitle} = render(<Thumbs {...defaultProps} />);

		const listItem = getByTitle('this is a thumb 3');

		fireEvent.click(listItem.firstChild);

		expect(listItem).toHaveClass('selected');
	});
});
