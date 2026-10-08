/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

'use strict';

const config = {
	env: {
		es6: true,
	},
	extends: ['eslint:recommended', require.resolve('eslint-config-prettier')],
	overrides: [
		{
			files: ['*.{ts,tsx}'],
			parser: '@typescript-eslint/parser',
			rules: {
				'@typescript-eslint/member-ordering': [
					'error',
					{
						default: [],
						interfaces: {
							memberTypes: [
								'constructor',
								'method',
								'field',
								'signature',
							],
							order: 'alphabetically',
						},
						typeLiterals: {
							memberTypes: [
								'constructor',
								'method',
								'field',
								'signature',
							],
							order: 'alphabetically',
						},
					},
				],
				'@typescript-eslint/no-unused-vars': [
					'error',
					{
						argsIgnorePattern: '^_|^this$',
						varsIgnorePattern: '^_',
					},
				],
				'@typescript-eslint/no-use-before-define': [
					'error',
					{functions: false},
				],

				// These rules can be turned off because the corresponding
				// errors are caught by the TypeScript compiler itself.

				'no-redeclare': 'off',
				'no-undef': 'off',
				'no-unused-expressions': 'off',
				'no-unused-vars': 'off',
				'no-use-before-define': 'off',
			},
		},
		{
			files: ['*.d.ts'],
			rules: {
				'@typescript-eslint/no-unused-vars': 'off',
			},
		},
	],
	parser: '@babel/eslint-parser',
	parserOptions: {
		babelOptions: {
			babelrc: false,
			configFile: false,
			parserOpts: {
				plugins: ['flow', 'jsx'],
			},
		},
		ecmaVersion: 2018,
		requireConfigFile: false,
		sourceType: 'module',
	},
	plugins: [
		'@typescript-eslint/eslint-plugin',
		'no-for-of-loops',
		'no-only-tests',
		'notice',
		'promise',
		'sort-destructure-keys',
	],
	rules: {
		'@liferay/array-is-array': 'error',
		'@liferay/aui/no-all': 'error',
		'@liferay/aui/no-array': 'error',
		'@liferay/aui/no-each': 'error',
		'@liferay/aui/no-get-body': 'error',
		'@liferay/aui/no-io': 'error',
		'@liferay/aui/no-merge': 'error',
		'@liferay/aui/no-modal': 'error',
		'@liferay/aui/no-node': 'error',
		'@liferay/aui/no-object': 'error',
		'@liferay/aui/no-one': 'error',
		'@liferay/aui/no-url': 'error',
		'@liferay/destructure-requires': 'error',
		'@liferay/empty-line-between-elements': 'error',
		'@liferay/expect-assert': 'error',
		'@liferay/group-imports': 'error',
		'@liferay/import-extensions': 'error',
		'@liferay/imports-first': 'error',
		'@liferay/no-abbreviations': 'error',
		'@liferay/no-absolute-import': 'error',
		'@liferay/no-anonymous-exports': 'error',
		'@liferay/no-conditional-object-keys': 'error',
		'@liferay/no-duplicate-imports': 'error',
		'@liferay/no-dynamic-require': 'error',
		'@liferay/no-extraneous-dependencies': 'off',
		'@liferay/no-get-data-attribute': 'error',
		'@liferay/no-it-should': 'error',
		'@liferay/no-length-jsx-expression': 'error',
		'@liferay/no-require-and-call': 'error',
		'@liferay/no-use-strict-in-module': 'error',
		'@liferay/padded-test-blocks': 'error',
		'@liferay/prefer-length-check': 'error',
		'@liferay/ref-name-suffix': 'error',
		'@liferay/sort-exports': 'error',
		'@liferay/sort-import-destructures': 'error',
		'@liferay/sort-imports': 'error',
		'@liferay/use-state-naming-pattern': 'error',
		'curly': 'error',
		'default-case': 'error',
		'eqeqeq': 'error',
		'lines-around-comment': [
			'error',
			{
				afterBlockComment: false,
				afterLineComment: true,
				allowArrayEnd: true,
				allowArrayStart: true,
				allowBlockEnd: true,
				allowBlockStart: true,
				allowClassStart: true,
				allowObjectEnd: true,
				allowObjectStart: true,
				beforeBlockComment: true,
				beforeLineComment: true,
			},
		],
		'no-console': ['error', {allow: ['warn', 'error']}],
		'no-constant-condition': ['error', {checkLoops: false}],
		'no-control-regex': 'off',
		'no-eval': 'error',
		'no-for-of-loops/no-for-of-loops': 'off',
		'no-only-tests/no-only-tests': 'error',
		'no-return-assign': ['error', 'always'],
		'no-unused-expressions': 'error',
		'no-unused-vars': ['error', {argsIgnorePattern: '^_'}],
		'no-var': 'error',
		'object-shorthand': 'error',
		'one-var': ['error', 'never'],
		'padding-line-between-statements': [
			'error',
			{blankLine: 'always', next: 'return', prev: '*'},
		],
		'prefer-arrow-callback': ['error', {allowNamedFunctions: true}],
		'prefer-const': 'error',
		'prefer-object-spread': 'error',
		'promise/catch-or-return': ['error', {allowFinally: true}],
		'quote-props': ['error', 'consistent-as-needed'],
		'radix': 'error',
		'sort-destructure-keys/sort-destructure-keys': [
			'error',
			{caseSensitive: true},
		],
		'sort-keys': ['error', 'asc', {caseSensitive: true, natural: true}],
		'spaced-comment': ['error', 'always'],
		'valid-typeof': 'error',
	},
};

module.exports = config;
