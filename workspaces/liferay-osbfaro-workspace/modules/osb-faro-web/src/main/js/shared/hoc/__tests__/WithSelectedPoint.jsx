import React from 'react';
import withSelectedPoint from '../WithSelectedPoint';
import {fireEvent, render} from '@testing-library/react';

describe('withSelectedPoint', () => {
	it('should render the wrapped component', () => {
		const WrappedComponent = withSelectedPoint(() => <div>{'foo'}</div>);

		const {container} = render(<WrappedComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should pass the selected point to the wrapped component', () => {
		const WrappedComponent = withSelectedPoint(
			({hasSelectedPoint, onPointSelect, selectedPoint}) => (
				<div>
					<span>
						{hasSelectedPoint
							? 'There is a selected point'
							: 'There is no selected point'}
					</span>
					<button onClick={() => onPointSelect({index: 1})}>
						{'bar'}
						{selectedPoint}
					</button>
				</div>
			)
		);

		const {getByText} = render(<WrappedComponent hasSelectedPoint />);

		fireEvent.click(getByText('bar'));

		expect(getByText('bar1')).toBeTruthy();
		expect(getByText('There is a selected point'));
	});
});
