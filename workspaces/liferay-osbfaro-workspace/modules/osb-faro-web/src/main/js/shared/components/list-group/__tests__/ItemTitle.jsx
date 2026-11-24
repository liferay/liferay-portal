import ItemTitle from '../ItemTitle';
import React from 'react';
import {render} from '@testing-library/react';

describe('ItemTitle', () => {
	it('should render', () => {
		const {container} = render(<ItemTitle />);

		expect(container).toMatchSnapshot();
	});

	it('should render with children', () => {
		const stringToMatch = 'Item title text';

		const {queryByText} = render(
			<ItemTitle>
				<span>{stringToMatch}</span>
			</ItemTitle>
		);

		expect(queryByText(stringToMatch)).toBeTruthy();
	});
});
