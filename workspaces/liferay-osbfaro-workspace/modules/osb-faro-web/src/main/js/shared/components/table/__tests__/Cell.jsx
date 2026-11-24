import Cell from '../Cell';
import React from 'react';
import {render} from '@testing-library/react';

describe('Cell', () => {
	it('should render', () => {
		const {container} = render(
			<Cell title>
				<span>{'Test'}</span>
			</Cell>
		);
		expect(container).toMatchSnapshot();
	});

	it('should render as a normal cell', () => {
		const {container} = render(
			<Cell>
				<span>{'Test'}</span>
			</Cell>
		);

		expect(container.querySelector('.table-title')).toBeFalsy();
	});
});
