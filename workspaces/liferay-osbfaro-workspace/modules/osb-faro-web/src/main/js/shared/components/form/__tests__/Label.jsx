import Label from '../Label';
import React from 'react';
import {render} from '@testing-library/react';

describe('Label', () => {
	it('should render', () => {
		const {container} = render(<Label />);

		expect(container).toMatchSnapshot();
	});

	it('should render as required', () => {
		const {container} = render(<Label required />);

		expect(container.querySelector('.required')).toBeTruthy();
	});

	it('should render with a popover component', () => {
		const {container} = render(<Label info='foo bar baz' />);

		expect(container.querySelector('.info-popover-root')).toBeTruthy;
	});
});
