import React from 'react';
import {render} from '@testing-library/react';
import {useRangeSelectorChange} from 'shared/hooks/useRangeSelectorChange';

jest.unmock('react-dom');

const mockPush = jest.fn();

jest.mock('shared/hooks/useHistoryAdapter', () => ({
	useHistoryAdapter: () => ({push: mockPush}),
}));

describe('useRangeSelectorChange', () => {
	it('should drop the custom bounds when a preset is selected', () => {
		window.history.replaceState(
			{},
			'',
			'/?rangeEnd=2026-02-20&rangeKey=CUSTOM&rangeStart=2026-02-10'
		);

		let handleRangeSelectorChange;

		const TestComponent = () => {
			({handleRangeSelectorChange} = useRangeSelectorChange());

			return null;
		};

		render(<TestComponent />);

		handleRangeSelectorChange({
			rangeEnd: null,
			rangeKey: '90',
			rangeStart: null,
		});

		expect(mockPush).toHaveBeenCalledWith('/?rangeKey=90');

		window.history.replaceState({}, '', '/');
	});
});
