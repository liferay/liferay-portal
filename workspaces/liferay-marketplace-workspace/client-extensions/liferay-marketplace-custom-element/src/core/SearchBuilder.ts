/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type Key = string;
type Value = string | number | boolean | null;

export type Operators =
	| 'contains'
	| 'eq'
	| 'ge'
	| 'gt'
	| 'lambda'
	| 'lambdaContains'
	| 'le'
	| 'lt'
	| 'ne'
	| 'startsWith';

export type SearchBuilderConstructor = {
	useURIEncode?: boolean;
};

export default class SearchBuilder {
	private lock: boolean = false;
	private query: string = '';
	private useURIEncode?: boolean = true;

	constructor({useURIEncode}: SearchBuilderConstructor = {}) {
		this.useURIEncode = useURIEncode;
	}

	static unquote(criteria: string) {
		return criteria.replaceAll("'", '');
	}

	/**
	 * @description Contains
	 * @example contains(title,'edmon')
	 */

	static contains(key: Key, value: Value) {
		return `contains(${key}, '${value}')`;
	}

	static eq(key: Key, value: Value) {
		return `${key} eq ${typeof value === 'boolean' ? value : `'${value}'`}`;
	}

	static in(key: Key, values: Value[]) {
		if (values) {
			const operator = `${key} in ({values})`;

			return operator
				.replace(
					'{values}',
					values
						.map((value) =>
							typeof value === 'number' ? value : `'${value}'`
						)
						.join(',')
				)
				.trim();
		}

		return '';
	}

	static lambda(key: Key, value: Value) {
		return `(${key}/any(x:(x eq '${value}')))`;
	}

	static lambdaContains(key: Key, value: Value) {
		return `(${key}/any(x:contains(x, '${value}')))`;
	}

	static ne(key: Key, value: Value) {
		return `${key} ne '${value}'`;
	}

	static gt(key: Key, value: Value) {
		return `${key} gt ${value}`;
	}

	static ge(key: Key, value: Value) {
		return `${key} ge ${value}`;
	}

	static lt(key: Key, value: Value) {
		return `${key} lt ${value}`;
	}

	static le(key: Key, value: Value) {
		return `${key} le ${value}`;
	}

	static group(type: 'CLOSE' | 'OPEN') {
		return type === 'OPEN' ? '(' : ')';
	}

	static startsWith(key: Key, value: Value) {
		return `${key} startsWith '${value}'`;
	}

	public and() {
		return this.setContext('and');
	}

	public build() {
		const query = this.query.trim();

		if (query.endsWith('or') || query.endsWith('and')) {
			return query.substring(0, query.length - 3);
		}

		this.lock = true;

		return this.useURIEncode ? encodeURIComponent(query) : query;
	}

	public clone() {
		const clone = new SearchBuilder({useURIEncode: this.useURIEncode});

		clone.lock = this.lock;
		clone.query = this.query;

		return clone;
	}

	public contains(key: Key, value: Value) {
		return this.setContext(SearchBuilder.contains(key, value));
	}

	public eq(key: Key, value: Value, options = {unquote: false}) {
		const parseFn = options.unquote
			? SearchBuilder.unquote
			: (fn: any) => fn;

		return this.setContext(parseFn(SearchBuilder.eq(key, value)));
	}

	public lambda(key: Key, value: Value, options = {unquote: false}) {
		const parseFn = options.unquote
			? SearchBuilder.unquote
			: (fn: any) => fn;

		return this.setContext(parseFn(SearchBuilder.lambda(key, value)));
	}

	public lambdaContains(key: Key, value: Value, options = {unquote: false}) {
		const parseFn = options.unquote
			? SearchBuilder.unquote
			: (fn: any) => fn;

		return this.setContext(
			parseFn(SearchBuilder.lambdaContains(key, value))
		);
	}

	public gt(key: Key, values: Value) {
		return this.setContext(SearchBuilder.gt(key, values));
	}

	public group(type: 'CLOSE' | 'OPEN') {
		return this.setContext(SearchBuilder.group(type));
	}

	public lt(key: Key, values: Value) {
		return this.setContext(SearchBuilder.lt(key, values));
	}

	public in(key: Key, values: Value[]) {
		return this.setContext(SearchBuilder.in(key, values));
	}

	public inEqualNumbers(key: Key, values: Value[]) {
		if (!values.length) {
			return this;
		}

		this.setContext(SearchBuilder.group('OPEN'));

		const lastIndex = values.length - 1;

		values.map((value, index) => {
			this.setContext(SearchBuilder.eq(key, value).replaceAll("'", ''));

			if (lastIndex !== index) {
				this.or();
			}
		});

		return this.group('CLOSE');
	}

	public ne(key: Key, value: Value, options = {unquote: false}) {
		const parseFn = options.unquote
			? SearchBuilder.unquote
			: (fn: any) => fn;

		return this.setContext(parseFn(SearchBuilder.ne(key, value)));
	}

	public not() {
		return this.setContext('not');
	}

	private setContext(query: string) {
		if (!this.lock) {
			this.query += ` ${query}`;
		}

		return this;
	}

	public or() {
		return this.setContext('or');
	}
}
