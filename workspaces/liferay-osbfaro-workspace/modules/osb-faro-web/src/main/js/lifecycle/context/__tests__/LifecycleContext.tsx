import React from 'react';
import {act, renderHook} from '@testing-library/react';
import {LifecycleContextProvider, useLifecycle} from '../LifecycleContext';
import {LifecycleStages} from 'contacts/pages/account/utils/constants';

jest.unmock('react-dom');

const wrapper = ({children}: {children: React.ReactNode}) => (
	<LifecycleContextProvider lifecycleId="1">
		{children}
	</LifecycleContextProvider>
);

describe('LifecycleContext', () => {
	it('should default lifecycleStageFilter to AT_RISK and leave other filters empty', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		expect(result.current.filters).toEqual({
			countryFilter: '',
			filterString: '',
			industryFilter: '',
			lifecycleStageFilter: LifecycleStages.AT_RISK,
		});
	});

	it('should update a single filter and recompute filterString', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() => result.current.updateFilters({industryFilter: 'Tech'}));

		expect(result.current.filters).toEqual({
			countryFilter: '',
			filterString: "industry eq 'Tech'",
			industryFilter: 'Tech',
			lifecycleStageFilter: LifecycleStages.AT_RISK,
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

		act(() =>
			result.current.updateFilters({
				lifecycleStageFilter: LifecycleStages.AWARE,
			})
		);

		expect(result.current.filters.lifecycleStageFilter).toBe(
			LifecycleStages.AWARE
		);
	});

	it('should reset filters to their initial values', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() =>
			result.current.updateFilters({
				countryFilter: 'US',
				industryFilter: 'Tech',
				lifecycleStageFilter: LifecycleStages.AWARE,
			})
		);
		act(() => result.current.resetFilters());

		expect(result.current.filters).toEqual({
			countryFilter: '',
			filterString: '',
			industryFilter: '',
			lifecycleStageFilter: LifecycleStages.AT_RISK,
		});
	});

	it('should set the stage and bump stageSelectionNonce when selectStage is called', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		expect(result.current.stageSelectionNonce).toBe(0);

		act(() => result.current.selectStage(LifecycleStages.AWARE));

		expect(result.current.filters.lifecycleStageFilter).toBe(
			LifecycleStages.AWARE
		);
		expect(result.current.stageSelectionNonce).toBe(1);
	});

	it('should bump stageSelectionNonce even when selectStage is called with the same stage', () => {
		const {result} = renderHook(() => useLifecycle(), {wrapper});

		act(() => result.current.selectStage(LifecycleStages.AWARE));
		act(() => result.current.selectStage(LifecycleStages.AWARE));

		expect(result.current.filters.lifecycleStageFilter).toBe(
			LifecycleStages.AWARE
		);
		expect(result.current.stageSelectionNonce).toBe(2);
	});
});
