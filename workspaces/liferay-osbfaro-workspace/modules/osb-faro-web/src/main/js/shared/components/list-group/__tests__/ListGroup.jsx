import ListGroup from '../index';
import React from 'react';
import {render} from '@testing-library/react';

describe('ListGroup', () => {
	it('should render', () => {
		const {container} = render(<ListGroup />);

		expect(container).toMatchSnapshot();
	});

	it('should render with children', () => {
		const stringToMatch = 'List Item';

		const {queryByText} = render(
			<ListGroup>
				<li>{stringToMatch}</li>
			</ListGroup>
		);

		expect(queryByText(stringToMatch)).toBeTruthy();
	});
});
