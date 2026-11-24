import React from 'react';
import StopClickPropagation from '../StopClickPropagation';
import {cleanup, fireEvent, render} from '@testing-library/react';

describe('StopClickPropagation', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<StopClickPropagation />);

		expect(container).toMatchSnapshot();
	});

	it('should stop event propagation from reaching any parent elements', () => {
		const clickSpy = jest.fn();

		const {container} = render(
			<button onClick={clickSpy}>
				<StopClickPropagation />
			</button>
		);

		fireEvent.click(container.querySelector('span'));

		expect(clickSpy).not.toBeCalled();
	});
});
