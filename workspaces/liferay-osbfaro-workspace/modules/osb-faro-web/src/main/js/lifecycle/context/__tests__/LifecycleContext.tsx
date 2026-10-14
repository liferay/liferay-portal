import React from 'react';
import {act, renderHook} from '@testing-library/react';
import {LifecycleContextProvider, useLifecycle} from '../LifecycleContext';

jest.unmock('react-dom');

const wrapper = ({children}: {children: React.ReactNode}) => (
	<LifecycleContextProvider lifecycleId="1">
		{children}
	</LifecycleContextProvider>
);

describe('LifecycleContext', () => {
	it('should leave every filter empty by default', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		expect(result.current.filters).toEqual({
			countryFilter: '',
			filterString: '',
			industryFilter: '',
		});
	});

	it('should update a single filter and recompute filterString', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() => result.current.updateFilters({industryFilter: 'Tech'}));

		expect(result.current.filters).toEqual({
			countryFilter: '',
			filterString: "industry eq 'Tech'",
			industryFilter: 'Tech',
		});
	});

	it('should merge partial updates without clearing untouched filters', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() => result.current.updateFilters({industryFilter: 'Tech'}));
		act(() => result.current.updateFilters({countryFilter: 'US'}));

		expect(result.current.filters.filterString).toBe(
			"country eq 'US' and industry eq 'Tech'"
		);
	});

	it('should swap lifecycleStageFilter to the value provided', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() => result.current.updateFilters({lifecycleStageFilter: '4101'}));

		expect(result.current.filters.lifecycleStageFilter).toBe('4101');
	});

	it('should reset filters to their initial values', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() =>
			result.current.updateFilters({
				countryFilter: 'US',
				industryFilter: 'Tech',
				lifecycleStageFilter: '4101',
			})
		);
		act(() => result.current.resetFilters());

		expect(result.current.filters).toEqual({
			countryFilter: '',
			filterString: '',
			industryFilter: '',
		});
	});

	it('should set the stage and bump stageSelectionNonce when selectStage is called', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		expect(result.current.stageSelectionNonce).toBe(0);

		act(() => result.current.selectStage('4101'));

		expect(result.current.filters.lifecycleStageFilter).toBe('4101');
		expect(result.current.stageSelectionNonce).toBe(1);
	});

	it('should bump stageSelectionNonce even when selectStage is called with the same stage', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() => result.current.selectStage('4101'));
		act(() => result.current.selectStage('4101'));

		expect(result.current.filters.lifecycleStageFilter).toBe('4101');
		expect(result.current.stageSelectionNonce).toBe(2);
	});
});
