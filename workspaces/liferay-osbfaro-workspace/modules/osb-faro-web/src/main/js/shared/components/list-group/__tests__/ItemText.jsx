import ItemText from '../ItemText';
import React from 'react';
import {render} from '@testing-library/react';

const DefaultComponent = props => <ItemText {...props}>{'Content'}</ItemText>;

describe('ItemText', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});
});
