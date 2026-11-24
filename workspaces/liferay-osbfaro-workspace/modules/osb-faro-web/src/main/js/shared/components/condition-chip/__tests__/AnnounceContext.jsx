import {act, renderHook} from '@testing-library/react';
import {useAnnouncement} from '../AnnounceContext';

describe('useAnnouncement', () => {
	it('changes the announcement when the same message is announced again', () => {
		const {result} = renderHook(() => useAnnouncement());

		act(() => result.current.announce('Removed City'));

		const firstAnnouncement = result.current.announcement;

		act(() => result.current.announce('Removed City'));

		expect(result.current.announcement).not.toBe(firstAnnouncement);
		expect(result.current.announcement.trim()).toBe('Removed City');

		act(() => result.current.announce('Removed City'));

		expect(result.current.announcement).toBe(firstAnnouncement);
	});

	it('announces a new message as given', () => {
		const {result} = renderHook(() => useAnnouncement());

		act(() => result.current.announce('Added City'));
		act(() => result.current.announce('Added Price'));

		expect(result.current.announcement).toBe('Added Price');
	});
});
