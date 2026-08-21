/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {eq} from '../src/main/resources/META-INF/resources/main/detection/operators/eq';
import {gt} from '../src/main/resources/META-INF/resources/main/detection/operators/gt';
import {gte} from '../src/main/resources/META-INF/resources/main/detection/operators/gte';
import {includes} from '../src/main/resources/META-INF/resources/main/detection/operators/includes';
import {lt} from '../src/main/resources/META-INF/resources/main/detection/operators/lt';
import {lte} from '../src/main/resources/META-INF/resources/main/detection/operators/lte';
import {notEq} from '../src/main/resources/META-INF/resources/main/detection/operators/not_eq';
import {notIncludes} from '../src/main/resources/META-INF/resources/main/detection/operators/not_includes';

describe('operators', () => {
	it('operator eq works with boolean, string and number, fails for Set', async () => {
		expect(eq('a', 'a')).toBe(true);
		expect(eq('a', 'b')).toBe(false);

		expect(eq(1, 1)).toBe(true);
		expect(eq(1, 2)).toBe(false);

		expect(eq(true, true)).toBe(true);
		expect(eq(true, false)).toBe(false);

		expect(() => eq(new Set(['a']) as any, new Set(['a']))).toThrow();
	});

	it('operator gt works with string and number, fails for Set', async () => {
		expect(gt('b', 'a')).toBe(true);
		expect(gt('a', 'b')).toBe(false);

		expect(gt(2, 1)).toBe(true);
		expect(gt(1, 2)).toBe(false);

		expect(() => gt(new Set(['b']) as any, new Set(['a']))).toThrow();
	});

	it('operator gte works with string and number, fails for Set', async () => {
		expect(gte('b', 'a')).toBe(true);
		expect(gte('a', 'a')).toBe(true);
		expect(gte('a', 'b')).toBe(false);

		expect(gte(2, 1)).toBe(true);
		expect(gte(1, 1)).toBe(true);
		expect(gte(1, 2)).toBe(false);

		expect(() => gte(new Set(['a']) as any, new Set(['a']))).toThrow();
	});

	it('operator includes works with string and Set, fails for number', async () => {
		expect(includes('hello world', 'world')).toBe(true);
		expect(includes('hello world', 'xyz')).toBe(false);

		expect(includes(new Set(['a', 'b']), 'a')).toBe(true);
		expect(includes(new Set(['a', 'b']), 'c')).toBe(false);

		expect(() => includes(1 as any, 1 as any)).toThrow();
	});

	it('operator lt works with string and number, fails for Set', async () => {
		expect(lt('a', 'b')).toBe(true);
		expect(lt('b', 'a')).toBe(false);

		expect(lt(1, 2)).toBe(true);
		expect(lt(2, 1)).toBe(false);

		expect(() => lt(new Set(['a']) as any, new Set(['b']))).toThrow();
	});

	it('operator lte works with string and number, fails for Set', async () => {
		expect(lte('a', 'b')).toBe(true);
		expect(lte('a', 'a')).toBe(true);
		expect(lte('b', 'a')).toBe(false);

		expect(lte(1, 2)).toBe(true);
		expect(lte(1, 1)).toBe(true);
		expect(lte(2, 1)).toBe(false);

		expect(() => lte(new Set(['a']) as any, new Set(['a']))).toThrow();
	});

	it('operator not_eq works with boolean, string and number, fails for Set', async () => {
		expect(notEq('a', 'b')).toBe(true);
		expect(notEq('a', 'a')).toBe(false);

		expect(notEq(1, 2)).toBe(true);
		expect(notEq(1, 1)).toBe(false);

		expect(notEq(true, false)).toBe(true);
		expect(notEq(true, true)).toBe(false);

		expect(() => notEq(new Set(['a']) as any, new Set(['a']))).toThrow();
	});

	it('operator not_includes works with string and Set, fails for number', async () => {
		expect(notIncludes('hello world', 'xyz')).toBe(true);
		expect(notIncludes('hello world', 'world')).toBe(false);

		expect(notIncludes(new Set(['a', 'b']), 'c')).toBe(true);
		expect(notIncludes(new Set(['a', 'b']), 'a')).toBe(false);

		expect(() => notIncludes(1 as any, 1 as any)).toThrow();
	});
});
