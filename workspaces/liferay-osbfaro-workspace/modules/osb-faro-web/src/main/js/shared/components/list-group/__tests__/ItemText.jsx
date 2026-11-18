import ItemText from '../ItemText';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const DefaultComponent = props => <ItemText {...props}>{'Content'}</ItemText>;

describe('ItemText', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render with subtext', () => {
		const {getByText} = render(<DefaultComponent subtext='subtext here' />);

		expect(getByText(/^content$/i)).toHaveClass('list-group-subtext');
	});
});
