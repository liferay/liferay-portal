import {act, renderHook} from '@testing-library/react';
import {resetBodyWidth, setBodyWidth} from 'test/body-width';
import {useBodyWidthBelow} from '../useBodyWidthBelow';
import {useIsMobile} from '../useIsMobile';

jest.unmock('react-dom');

describe('useBodyWidthBelow', () => {
	afterEach(resetBodyWidth);

	it('compares the body width strictly by default', () => {
		setBodyWidth(768);

		const {result} = renderHook(() => useBodyWidthBelow(768));

		expect(result.current).toBe(false);
	});

	it('includes the width itself when inclusive', () => {
		setBodyWidth(1280);

		const {result} = renderHook(() =>
			useBodyWidthBelow(1280, {inclusive: true})
		);

		expect(result.current).toBe(true);
	});

	it('follows the body width on resize', () => {
		setBodyWidth(1440);

		const {result} = renderHook(() =>
			useBodyWidthBelow(1280, {inclusive: true})
		);

		expect(result.current).toBe(false);

		setBodyWidth(1024);

		act(() => {
			window.dispatchEvent(new Event('resize'));
		});

		expect(result.current).toBe(true);
	});
});

describe('useIsMobile', () => {
	afterEach(resetBodyWidth);

	it('is mobile below 768px', () => {
		setBodyWidth(767);

		expect(renderHook(() => useIsMobile()).result.current).toBe(true);
	});

	it('is not mobile at 768px', () => {
		setBodyWidth(768);

		expect(renderHook(() => useIsMobile()).result.current).toBe(false);
	});
});
