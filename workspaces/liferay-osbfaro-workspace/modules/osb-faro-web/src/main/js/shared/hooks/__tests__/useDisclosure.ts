import {act, renderHook} from '@testing-library/react';
import {useDisclosure} from '../useDisclosure';

jest.unmock('react-dom');

describe('useDisclosure', () => {
	it('starts closed by default', () => {
		const {result} = renderHook(() => useDisclosure());

		expect(result.current.isOpen).toBe(false);
	});

	it('starts open when initialOpen is true', () => {
		const {result} = renderHook(() => useDisclosure({initialOpen: true}));

		expect(result.current.isOpen).toBe(true);
	});

	it('opens and closes', () => {
		const {result} = renderHook(() => useDisclosure());

		act(() => result.current.open());

		expect(result.current.isOpen).toBe(true);

		act(() => result.current.close());

		expect(result.current.isOpen).toBe(false);
	});

	it('toggles between open and closed', () => {
		const {result} = renderHook(() => useDisclosure());

		act(() => result.current.toggle());

		expect(result.current.isOpen).toBe(true);

		act(() => result.current.toggle());

		expect(result.current.isOpen).toBe(false);
	});

	it('toggles twice in the same update back to the original state', () => {
		const {result} = renderHook(() => useDisclosure());

		act(() => {
			result.current.toggle();
			result.current.toggle();
		});

		expect(result.current.isOpen).toBe(false);
	});

	it('sets the open state from a boolean', () => {
		const {result} = renderHook(() => useDisclosure());

		act(() => result.current.setIsOpen(true));

		expect(result.current.isOpen).toBe(true);

		act(() => result.current.setIsOpen(false));

		expect(result.current.isOpen).toBe(false);
	});

	it('keeps the same functions across renders', () => {
		const {rerender, result} = renderHook(() => useDisclosure());

		const {close, open, setIsOpen, toggle} = result.current;

		act(() => result.current.open());

		rerender();

		expect(result.current.close).toBe(close);
		expect(result.current.open).toBe(open);
		expect(result.current.setIsOpen).toBe(setIsOpen);
		expect(result.current.toggle).toBe(toggle);
	});
});
