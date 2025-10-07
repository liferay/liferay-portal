/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {contains} from '../../src/main/resources/META-INF/resources/utils/configInURL';
import {EConfigInURLKeys} from '../../src/main/resources/META-INF/resources/utils/types';

describe('contains utility', () => {
	describe('emptiness', () => {
		it('returns true when both subset and superset are empty objects', () => {
			expect(contains({}, {})).toBeTruthy();
		});

		it('returns false when superset is empty object and subset is not', () => {
			expect(contains({[EConfigInURLKeys.DELTA]: 1}, {})).toBeFalsy();
		});

		it('returns true when subset is empty but superset is not', () => {
			expect(
				contains({}, {[EConfigInURLKeys.VIEW_NAME]: 'foo'})
			).toBeTruthy();
		});
	});

	describe('containment with different values, objects', () => {
		it('returns false when both subset and superset has the property with a different value', () => {
			expect(
				contains(
					{[EConfigInURLKeys.DELTA]: 1},
					{[EConfigInURLKeys.DELTA]: 2}
				)
			).toBeFalsy();
		});

		it('returns false when superset has the property with a undefined value', () => {
			expect(
				contains(
					{[EConfigInURLKeys.DELTA]: 1},
					{[EConfigInURLKeys.DELTA]: undefined}
				)
			).toBeFalsy();
		});

		it('returns false when superset has the property with a undefined value', () => {
			expect(
				contains(
					{[EConfigInURLKeys.DELTA]: 1},
					{[EConfigInURLKeys.VIEW_NAME]: 'foo'}
				)
			).toBeFalsy();
		});
	});

	describe('containment with same values, objects', () => {
		it('returns true when both superset and subset are deep equals', () => {
			expect(
				contains(
					{[EConfigInURLKeys.DELTA]: 1},
					{[EConfigInURLKeys.DELTA]: 1}
				)
			).toBeTruthy();
		});

		it('returns true when both superset and subset are deep equals, different order', () => {
			expect(
				contains(
					{
						[EConfigInURLKeys.VIEW_NAME]: 'foo',
						[EConfigInURLKeys.DELTA]: 1,
					},
					{
						[EConfigInURLKeys.DELTA]: 1,
						[EConfigInURLKeys.VIEW_NAME]: 'foo',
					}
				)
			).toBeTruthy();
		});

		it('returns true when subset is strictly contained in superset', () => {
			expect(
				contains(
					{[EConfigInURLKeys.DELTA]: 1},
					{
						[EConfigInURLKeys.DELTA]: 1,
						[EConfigInURLKeys.VIEW_NAME]: 'foo',
					}
				)
			).toBeTruthy();
		});

		it('returns true when subset is strictly contained in superset, different order', () => {
			expect(
				contains(
					{[EConfigInURLKeys.DELTA]: 1},
					{
						[EConfigInURLKeys.VIEW_NAME]: 'foo',
						[EConfigInURLKeys.DELTA]: 1,
					}
				)
			).toBeTruthy();
		});
	});

	describe('partial overlap, objects', () => {
		it('returns false when subset is a superset of the superset', () => {
			expect(
				contains(
					{
						[EConfigInURLKeys.VIEW_NAME]: 'foo',
						[EConfigInURLKeys.DELTA]: 1,
					},
					{[EConfigInURLKeys.DELTA]: 1}
				)
			).toBeFalsy();
		});
	});

	describe('emptiness, arrays', () => {
		it('returns true when both subset and superset are empty arrays', () => {
			expect(
				contains(
					{[EConfigInURLKeys.ACTIVE_SORTS]: []},
					{[EConfigInURLKeys.ACTIVE_SORTS]: []}
				)
			).toBeTruthy();
		});

		it('returns false when superset is empty object and subset is not', () => {
			expect(
				contains({[EConfigInURLKeys.ACTIVE_SORTS]: []}, {})
			).toBeFalsy();
		});

		it('returns false when subset is empty but superset is not', () => {
			expect(
				contains(
					{[EConfigInURLKeys.ACTIVE_SORTS]: []},
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: 'foo'}]}
				)
			).toBeFalsy();
		});
	});

	describe('containment with different values, arrays', () => {
		it('returns false when both subset and superset has array with a different value', () => {
			expect(
				contains(
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: 'foo'}]},
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: 'bar'}]}
				)
			).toBeFalsy();
		});

		it('returns false when superset has the property with a undefined value', () => {
			expect(
				contains(
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: 'foo'}]},
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: undefined}]}
				)
			).toBeFalsy();
		});

		it('returns false when superset has the property with a undefined value', () => {
			expect(
				contains(
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: 'foo'}]},
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{direction: 'desc'}]}
				)
			).toBeFalsy();
		});
	});

	describe('containment with same values, arrays', () => {
		it('returns true when both superset and subset are deep equals', () => {
			expect(
				contains(
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{direction: 'desc', key: 'foo'},
						],
					},
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{direction: 'desc', key: 'foo'},
						],
					}
				)
			).toBeTruthy();
		});

		it('returns true when both superset and subset are deep equals, different order', () => {
			expect(
				contains(
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{key: 'foo'},
							{direction: 'desc'},
						],
					},
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{direction: 'desc'},
							{key: 'foo'},
						],
					}
				)
			).toBeTruthy();
		});

		it('returns true when subset is strictly contained in superset', () => {
			expect(
				contains(
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{direction: 'desc'},
							{key: 'bar'},
						],
					},
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{direction: 'desc', key: 'foo'},
							{key: 'bar'},
						],
					}
				)
			).toBeTruthy();
		});

		it('returns false when subset is not strictly contained in superset', () => {
			expect(
				contains(
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: 'foo'}]},
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{direction: 'desc'},
							{key: 'foo'},
						],
					}
				)
			).toBeFalsy();
		});
	});

	describe('partial overlap, arrays', () => {
		it('returns false when subset is a superset of the superset', () => {
			expect(
				contains(
					{
						[EConfigInURLKeys.ACTIVE_SORTS]: [
							{direction: 'desc'},
							{key: 'foo'},
						],
					},
					{[EConfigInURLKeys.ACTIVE_SORTS]: [{key: 'foo'}]}
				)
			).toBeFalsy();
		});
	});
});
