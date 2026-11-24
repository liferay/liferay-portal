import React from 'react';
import StatusRenderer from '../StatusRenderer';
import {render} from '@testing-library/react';

describe('StatusRenderer', () => {
	it('should render', () => {
		const {container} = render(<StatusRenderer data={{status: 0}} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with custom class', () => {
		const {container} = render(<StatusRenderer className='custom-class' />);

		expect(container.querySelector('.custom-class')).toBeTruthy();
	});
});
