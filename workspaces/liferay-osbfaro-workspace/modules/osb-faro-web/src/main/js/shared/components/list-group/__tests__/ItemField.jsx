import ItemField from '../ItemField';
import React from 'react';
import {render} from '@testing-library/react';

describe('ItemField', () => {
	it('should render', () => {
		const {container} = render(<ItemField />);

		expect(container).toMatchSnapshot();
	});
});
