import {renderHook} from '@testing-library/react';
import {useLocation} from 'react-router-dom';
import {useQueryParams} from '../useQueryParams';

jest.mock('react-router-dom', () => ({
	useLocation: jest.fn(),
}));

describe('useQueryParams', () => {
	it('returns an empty object if no query parameters are present', () => {
		(useLocation as jest.Mock).mockReturnValue({search: ''});
		const {result} = renderHook(() => useQueryParams());

		expect(result.current).toEqual({});
	});

	it('parses query parameters correctly', () => {
		(useLocation as jest.Mock).mockReturnValue({
			search: '?name=Marcio&age=30',
		});
		const {result} = renderHook(() => useQueryParams());

		expect(result.current).toEqual({age: '30', name: 'Marcio'});
	});

	it('decodes query parameters correctly', () => {
		(useLocation as jest.Mock).mockReturnValue({
			search: '?name=Marcio+Fonseca&age=30',
		});
		const {result} = renderHook(() => useQueryParams());

		expect(result.current).toEqual({age: '30', name: 'Marcio Fonseca'});
	});
});
