/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	BLACKLISTED_FIELDS,
	ISchemas,
	getValidFields,
} from '../../src/main/resources/META-INF/resources/js/utils/getFields';
import {IField} from '../../src/main/resources/META-INF/resources/js/utils/types';

/*
 * A points to B using scalar field via $ref
 * A points to C using scalar field via 'x-parent-map' and implied target schema name
 * B points to C using scalar field via $ref
 * C points to A using array field via $ref
 */
const nestedSchemas: ISchemas = {
	A: {
		properties: {
			a_b: {
				'$ref': '#/components/schemas/B',

				'type': 'object',
				'x-parent-map': 'properties',
			},
			c: {
				'type': 'string',
				'x-parent-map': 'properties',
			},
		},
		type: 'object',
	},
	B: {
		properties: {
			b_c: {
				$ref: '#/components/schemas/C',
				type: 'object',
			},
		},
		type: 'object',
	},
	C: {
		properties: {
			c_a: {
				items: {
					$ref: '#/components/schemas/A',
				},
				type: 'array',
			},
		},
		type: 'object',
	},
	D: {
		properties: {
			d_e: {
				$ref: '#/components/schemas/E',
			},
		},
		type: 'object',
		['x-filterable']: ['d_e/code']
	},
	E: {
		properties: {
			'code': {
				readOnly: true,
				type: 'integer',
				format: 'int32'
			},
			'name': {
				readOnly: true,
				type: 'string',
			},
		},
		type: 'object',
		['x-filterable']: []
	},
	F: {
		properties: {
			'keywords': {
				items: {
					type: 'string'
				},
				readOnly: true,
				type: 'array',
			},
			'numbers': {
				items: {
					type: 'integer'
				},
				readOnly: true,
				type: 'array',
			},
		},
		type: 'object',
		['x-filterable']: ['keywords', 'numbers']
	},
} as ISchemas;

const simpleSchema = {
	Simple: {
		properties: {
			'code': {
				format: 'int32',
				type: 'integer',
			},
			'label': {
				type: 'string',
			},
			'label_i18n': {
				additionalProperties: {
					type: 'string',
				},
				type: 'object',
			},
			'old_codes': {
				items: {
					format: 'int32',
					type: 'integer',
				},
				type: 'array',
			},
			'scopeKey': {
				readOnly: true,
				type: 'string',
			},
			'userNames': {
				additionalProperties: {
					type: 'string',
				},
				type: 'object',
			},
			'x-class-name': {
				default: 'com.liferay.object.rest.dto.v1_0.Simple',
				type: 'string',
			},
			'x-schema-name': {
				default: 'Simple',
				readOnly: true,
				type: 'string',
			},
		},
		type: 'object',
	},
} as ISchemas;

function assertChildren(f: IField | undefined, childrenNames: string[]) {
	if (f && childrenNames.length) {
		expect(f.children).toBeDefined();
		expect(f.children?.length).toEqual(childrenNames.length);
		childrenNames.forEach(
			(childName, index) =>
				f.children && expect(f.children[index]?.name).toEqual(childName)
		);
	}
	else {
		expect(f).not.toHaveProperty('children');
	}
}

describe('getValidFields', () => {
	it('Function is defined', () => {
		expect(getValidFields).toBeDefined();
	});

	it('Returns only valid fields from the schemas of an application', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'Simple',
			schemas: simpleSchema,
			visitedFields: [],
		});

		const expectedValidFields = result.map((item) => item.label);

		expect(Object.keys(simpleSchema.Simple.properties)).toEqual([
			'code',
			'label',
			'label_i18n',
			'old_codes',
			'scopeKey',
			'userNames',
			'x-class-name',
			'x-schema-name',
		]);

		expect(expectedValidFields).toEqual([
			'code',
			'label',
			'old_codes',
			'userNames',
		]);

		expect(expectedValidFields).not.toContain('label_i18n');

		expect(expectedValidFields).not.toContain(BLACKLISTED_FIELDS);
	});

	it('Non scalar (arrays and object) fields are not sortable, scalar fields are', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'Simple',
			schemas: simpleSchema,
			visitedFields: [],
		});

		const expectedValidScalarFields = result.filter(
			(item) => item.type !== 'object' && item.type !== 'array'
		);

		expect(expectedValidScalarFields.map((item) => item.sortable)).toEqual([
			true,
			true,
		]);

		const expectedValidNonScalarFields = result.filter(
			(item) => item.type === 'object' || item.type === 'array'
		);

		expect(
			expectedValidNonScalarFields.map((item) => item.sortable)
		).toEqual([false, false]);
	});

	it('Include children properties from schemas referenced via $ref property, no loops', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'A',
			schemas: nestedSchemas,
			visitedFields: [],
		});

		/* Excerpt of tree for field a_b:
			{
				name: "a_b.*",
				children:[
					{
						name: "a_b.b_c.*",
						children:[
							{
								name: "a_b.b_c.c_a[]*",
								children:[
									{
										name: "a_b.b_c.c_a[]a_b.*",
										// no children, B schema already visited!
									},
									{
										name: "a_b.b_c.c_a[]c.*",
										// no children, C schema already visited!
									}
								]
							}
						]
					}
				]
			};
		 */

		const a_b = result.find((item) => item.label === 'a_b');
		expect(a_b?.name).toEqual('a_b.*');

		if (a_b) {
			assertChildren(a_b, ['a_b.b_c.*']);

			const b_c = a_b.children && a_b?.children[0];
			if (b_c) {
				assertChildren(b_c, ['a_b.b_c.c_a[]*']);

				const c_a = b_c.children && b_c.children[0];
				if (c_a) {
					assertChildren(c_a, [
						'a_b.b_c.c_a[]a_b.*',
						'a_b.b_c.c_a[]c.*',
					]);

					// no loops

					if (c_a.children && c_a.children.length) {
						assertChildren(c_a.children[0], []);
						assertChildren(c_a.children[1], []);
					}
				}
			}
		}
	});

	it('Include children properties from schemas referenced via x-map-properties property, no loops', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'A',
			schemas: nestedSchemas,
			visitedFields: [],
		});

		/* Excerpt of tree for field c
			{
				name: "c.*",
				children: [
					{
						name: "c.c_a[]*",
						children:[
							{
								name: "c.c_a[]a_b.*",
								children:[
									{
										name: "c.c_a[]a_b.b_c.*",
										// no children, C schema already visited!
									}
								]
							},
							{
								name: "c.c_a[]c.*",
								// no children, C schema already visited!
							}
						]
					}
				]
			};
		*/

		const c = result.find((item) => item.label === 'c');
		expect(c?.name).toEqual('c.*');
		c && assertChildren(c, ['c.c_a[]*']);

		if (c) {
			const c_a = c.children && c.children[0];
			assertChildren(c_a, ['c.c_a[]a_b.*', 'c.c_a[]c.*']);
			if (c_a) {
				const a_b = c_a.children && c_a.children[0];
				assertChildren(a_b, ['c.c_a[]a_b.b_c.*']);

				// no loops

				a_b?.children && assertChildren(a_b.children[0], []);
				c_a.children && assertChildren(c_a.children[1], []);
			}
		}
	});

	it('Schema D include children properties from schemas reference and should be filterable', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'D',
			schemas: nestedSchemas,
			visitedFields: [],
			filterablePaths: nestedSchemas['D']['x-filterable'] || []
		});

		const d = result.find((item) => item.label === 'd_e');
		expect(d?.name).toEqual('d_e.*');
		expect(d?.filterable).toEqual(true);

	});

	it('Schema D has children and one of those should be named d_e.code and filterable', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'D',
			schemas: nestedSchemas,
			visitedFields: [],
			filterablePaths: nestedSchemas['D']['x-filterable'] || []
		});

		const d = result.find((item) => item.label === 'd_e');
		d?.children && expect(d?.children[0]?.name).toEqual('d_e.code');
		d?.children && expect(d?.children[0]?.filterable).toEqual(true);

	});

	it('Schema D has children and one of those should be named d_e.name and its not filterable', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'D',
			schemas: nestedSchemas,
			visitedFields: [],
			filterablePaths: nestedSchemas['D']['x-filterable'] || []
		});

		const d = result.find((item) => item.label === 'd_e');
		d?.children && expect(d?.children[1]?.name).toEqual('d_e.name');
		d?.children && expect(d?.children[1]?.filterable).toEqual(false);

	});

	it('Schema F has keywords property and it is an array and should be filterable', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'F',
			schemas: nestedSchemas,
			visitedFields: [],
			filterablePaths: nestedSchemas['F']['x-filterable'] || []
		});

		const f = result.find((item) => item.label === 'keywords');
		expect(f?.filterable).toEqual(true);
		expect(f?.entityFieldType).toEqual('array/type:string');

	});

	it('Schema F has numbers property and it is an array and should be filterable', () => {
		const result = getValidFields({
			contextPath: '',
			schemaName: 'F',
			schemas: nestedSchemas,
			visitedFields: [],
			filterablePaths: nestedSchemas['F']['x-filterable'] || []
		});

		const f = result.find((item) => item.label === 'numbers');
		expect(f?.filterable).toEqual(true);
		expect(f?.entityFieldType).toEqual('array/type:integer');

	});

});
