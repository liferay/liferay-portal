import {act, renderHook} from '@testing-library/react';
import {useHorizontalScroll} from '../useHorizontalScroll';

jest.unmock('react-dom');

const buildTrack = ({
	clientWidth,
	scrollLeft = 0,
	scrollWidth,
}: {
	clientWidth: number;
	scrollLeft?: number;
	scrollWidth: number;
}) => {
	const track = document.createElement('div');

	const item = document.createElement('div');

	Object.defineProperties(track, {
		clientWidth: {value: clientWidth},
		scrollLeft: {value: scrollLeft, writable: true},
		scrollWidth: {value: scrollWidth},
	});

	Object.defineProperty(item, 'offsetWidth', {value: 184});

	track.appendChild(item);

	track.scrollBy = jest.fn();

	return track;
};

describe('useHorizontalScroll', () => {
	it('cannot scroll while every item fits', () => {
		const {result} = renderHook(() => useHorizontalScroll(3));

		(result.current.trackRef as any).current = buildTrack({
			clientWidth: 600,
			scrollWidth: 600,
		});

		act(() => result.current.updateArrows());

		expect(result.current.canScrollNext).toBe(false);
		expect(result.current.canScrollPrevious).toBe(false);
	});

	it('can only scroll forward at the start of an overflowing track', () => {
		const {result} = renderHook(() => useHorizontalScroll(5));

		(result.current.trackRef as any).current = buildTrack({
			clientWidth: 400,
			scrollWidth: 1000,
		});

		act(() => result.current.updateArrows());

		expect(result.current.canScrollNext).toBe(true);
		expect(result.current.canScrollPrevious).toBe(false);
	});

	it('can only scroll back at the end of an overflowing track', () => {
		const {result} = renderHook(() => useHorizontalScroll(5));

		(result.current.trackRef as any).current = buildTrack({
			clientWidth: 400,
			scrollLeft: 600,
			scrollWidth: 1000,
		});

		act(() => result.current.updateArrows());

		expect(result.current.canScrollNext).toBe(false);
		expect(result.current.canScrollPrevious).toBe(true);
	});

	it('scrolls by one item in either direction', () => {
		const {result} = renderHook(() => useHorizontalScroll(5));

		const track = buildTrack({clientWidth: 400, scrollWidth: 1000});

		(result.current.trackRef as any).current = track;

		act(() => result.current.scrollNext());
		act(() => result.current.scrollPrevious());

		expect(track.scrollBy).toHaveBeenNthCalledWith(1, {
			behavior: 'smooth',
			left: 184,
		});
		expect(track.scrollBy).toHaveBeenNthCalledWith(2, {
			behavior: 'smooth',
			left: -184,
		});
	});
});
