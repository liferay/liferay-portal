import React from 'react';
import Sticker, {getDisplayForId, hashCode} from '../Sticker';
import {cleanup, render} from '@testing-library/react';

describe('Sticker', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<Sticker symbol='file' />);
		expect(container).toMatchSnapshot();
	});
});

describe('getDisplayForId', () => {
	it('should return a display type for a given number or string', () => {
		expect(getDisplayForId(1234)).toBe('chartSeaGreen');
		expect(getDisplayForId('fooBar')).toBe('chartLimeGreen');
	});

	it('should accept a custom array of displays', () => {
		const displays = ['customDisplayFoo', 'customDisplayBar'];

		expect(getDisplayForId(1, displays)).toBe('customDisplayBar');
		expect(getDisplayForId(2, displays)).toBe('customDisplayFoo');
	});

	it('should return a result if given an invalid id parameter', () => {
		expect(getDisplayForId(undefined)).toBe('chartBlue');
		expect(getDisplayForId(null)).toBe('chartBlue');
	});
});

describe('hashCode', () => {
	it('should return a unique number for a given string', () => {
		expect(hashCode('foo')).toBe(324);
		expect(hashCode('bar')).toBe(309);
		expect(hashCode('baz')).toBe(317);
	});
});
