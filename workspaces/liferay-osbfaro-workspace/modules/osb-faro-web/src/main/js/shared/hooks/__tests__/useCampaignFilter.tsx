import React from 'react';
import {act, renderHook} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {useCampaignFilter} from '../useCampaignFilter';

jest.unmock('react-dom');

const renderFilter = (initialEntry = '/') =>
	renderHook(() => useCampaignFilter(), {
		wrapper: ({children}: {children?: React.ReactNode}) => (
			<MemoryRouter initialEntries={[initialEntry]}>
				{children}
			</MemoryRouter>
		),
	});

describe('useCampaignFilter', () => {

	// A memory router does not move the browser location, which is what the
	// write builds on, so put it back after every test.

	afterEach(() => window.history.pushState({}, '', '/'));

	it('should read the campaign from the URL', () => {
		const {result} = renderFilter('/?campaignId=7&campaignName=Spring');

		expect(result.current.campaignId).toBe('7');
		expect(result.current.campaignName).toBe('Spring');
	});

	it('should write the selected campaign to the URL', () => {
		const {result} = renderFilter();

		act(() => result.current.setCampaign({id: '7', name: 'Spring'}));

		expect(result.current.campaignId).toBe('7');
		expect(result.current.campaignName).toBe('Spring');
	});

	it('should clear the campaign from the URL', () => {
		window.history.pushState({}, '', '/?campaignId=7&campaignName=Spring');

		const {result} = renderFilter('/?campaignId=7&campaignName=Spring');

		act(() => result.current.setCampaign(null));

		expect(result.current.campaignId).toBeUndefined();
		expect(result.current.campaignName).toBeUndefined();
	});
});
