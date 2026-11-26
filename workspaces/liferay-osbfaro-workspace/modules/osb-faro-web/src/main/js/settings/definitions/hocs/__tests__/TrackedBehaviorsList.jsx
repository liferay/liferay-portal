import React from 'react';
import TrackedBehaviorsList from '../TrackedBehaviorsList';
import {cleanup, render} from '@testing-library/react';

describe('TrackedBehaviorsList', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<TrackedBehaviorsList authorized />);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});

	it('should render with disabled restrict access checkboxes if the user is not authorized', () => {
		const {container} = render(
			<TrackedBehaviorsList authorized={false} groupId='23' />
		);

		jest.runAllTimers();

		expect(
			container.querySelectorAll('input[type=checkbox]:disabled').length
		).toEqual(4);
	});

	it('should render the restrict access checkbbox in a checked state if the behavior has restricted access', () => {
		const {container} = render(
			<TrackedBehaviorsList authorized groupId='23' />
		);

		jest.runAllTimers();

		expect(
			container.querySelectorAll('input[type=checkbox]')[0].checked
		).toBeTrue();
	});

	it('should NOT contain a search bar or filter capability', () => {
		const {container} = render(
			<TrackedBehaviorsList authorized groupId='23' />
		);

		jest.runAllTimers();

		expect(container.querySelector('.input-group.search')).toBeNull();
		expect(container.querySelector('.filter-and-order-root')).toBeNull();
	});
});
