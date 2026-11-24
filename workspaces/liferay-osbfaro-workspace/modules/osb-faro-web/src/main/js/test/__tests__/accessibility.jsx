import React from 'react';
import {
	expectNoAccessibilityViolations,
	getAccessibilityViolations,
} from '../accessibility';
import {render} from '@testing-library/react';

describe('accessibility helpers', () => {
	it('should report a button without an accessible name', async () => {
		const {container} = render(<button type='button' />);

		expect(await getAccessibilityViolations(container)).toEqual([
			expect.objectContaining({id: 'button-name'}),
		]);
	});

	it('should leave out the excluded selectors', async () => {
		const {container} = render(
			<div>
				<button aria-label='Save' type='button' />

				<div className='third-party'>
					<button type='button' />
				</div>
			</div>
		);

		await expectNoAccessibilityViolations(container, {
			exclude: ['.third-party'],
		});
	});

	it('should give the fake timers back to the suite', async () => {
		const {container} = render(<button aria-label='Save' type='button' />);

		await getAccessibilityViolations(container);

		const callback = jest.fn();

		setTimeout(callback, 1000);
		jest.advanceTimersByTime(1000);

		expect(callback).toHaveBeenCalled();
	});
});
