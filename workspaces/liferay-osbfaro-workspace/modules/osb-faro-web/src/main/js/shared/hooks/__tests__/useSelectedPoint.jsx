import React from 'react';
import {render} from '@testing-library/react';
import {useSelectedPoint} from 'shared/hooks/useSelectedPoint';

describe('useSelectedPoint', () => {
	it('should not have value on the first render', () => {
		let result = null;

		const Component = () => {
			result = useSelectedPoint();

			return null;
		};

		render(<Component />);

		jest.runAllTimers();

		expect(result.hasSelectedPoint).toBeFalse();
		expect(result.selectedPoint).toBeFalsy();
	});

	it('should return dispatched value', () => {
		let result = null;

		const Component = () => {
			result = useSelectedPoint();

			return null;
		};

		render(<Component />);

		const newPoint = 1;

		jest.runAllTimers();

		result.onPointSelect(newPoint);

		jest.runAllTimers();

		expect(result.hasSelectedPoint).toBeTrue();
		expect(result.selectedPoint).toBe(newPoint);
	});
});
