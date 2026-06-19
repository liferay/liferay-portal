#!/usr/bin/env node
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable no-undef */

const COMMANDS = {
	'build': {
		description: `
		Builds current project.
`,
		parameters: '',
		script: './bundle/index.mjs',
	},
	'build:custom': {
		description: `
		Builds artifacts for the current project using a custom esbuild
		configuration.

		--serve builds and serves the bundle locally in 'watch' mode.
`,
		parameters: '[--serve]',
		script: './bundle/custom.mjs',
	},
	'build:theme': {
		description: `
		Build a theme project with liferay-theme-tasks and gulp.
`,
		script: './bundle/theme.mjs',
	},
	'format': {
		description: `
		Formats and lints source files with eslint and prettier in the current
		project or globally (when run from modules).

		If --check is passed no file is modified and the command just outputs
		what files need to be formatted.

		If --emit-suppressed is passed, the list of eslint errors will be logged
		to stdout in 'supressed_errors.txt' format. If --emit-suppressed-css is
		passed, the list of stylelint errors will be logged instead.

		See this help's introduction to find the meaning of --current-branch and
		--local-changes parameters.
`,
		parameters:
			'[--check] [--emit-suppressed] [--emit-suppressed-css] [--ignore-typescript] [{--current-branch|--local-changes}]',
		script: './format/index.mjs',
	},
	'format:file': {
		description: `
		Formats a single source file with eslint and prettier.
`,
		parameters: '<source file path>',
		script: './format/file.mjs',
	},
	'gitmerge:self': {
		description: `
		Implements a Git merge driver for node-scripts' package.json file.
`,
		parameters:
			'--current=<current file> --base=<base file> --other=<other file>',
		script: './gitmerge/self.mjs',
	},
	'gitmerge:setup': {
		description: `
		Adds gitmerge:self to .git/config file.
`,
		parameters: '',
		script: './gitmerge/setup.mjs',
	},
	'report:build': {
		description: `
		Generates an aggregated report of build timings.

		The <timings directory> arguments falls back to LIFERAY_NPM_SCRIPTS_TIMING environment
		variable when not provided.
`,
		parameters: '[<timings directory>]',
		script: './report/build.mjs',
	},
	'report:bundle-imports': {
		description: `
		Generate aggregated information about external imports found in each
		bundle.

		This report only shows information about packages, not symbols. If you
		need to find out the symbols imported from each bundle use
		report:source:imports instead.

		It is impossible to find symbols in bundled JavaScript files because our
		build process transforms everything into default and * imports
		internally.

		This task must be invoked after running 'CREATE_BUNDLE_REPORTS=yes ant
		all' (i.e: running 'ant all' with the environment variable
		'CREATE_BUNDLE_REPORTS' set to 'yes', so that JSON reports about bundle
		sizes are created inside the 'build' directory of each project).
`,
		parameters: '',
		script: './report/bundle-imports.mjs',
	},
	'report:bundle-sizes': {
		description: `
		Generate aggregated information about bundle sizes. Optionally report
		the size of internal files inside each bundle (if the --with-internals
		flag is provided).

		This task must be invoked after running 'CREATE_BUNDLE_REPORTS=yes ant
		all' (i.e: running 'ant all' with the environment variable
		'CREATE_BUNDLE_REPORTS' set to 'yes', so that JSON reports about bundle
		sizes are created inside the 'build' directory of each project).
`,
		parameters: '[--with-internals]',
		script: './report/bundle-sizes.mjs',
	},
	'report:dependencies': {
		description: `
		Generate aggregated information about bundled npm packages.

		This task must be invoked after running 'CREATE_BUNDLE_REPORTS=yes ant
		all' (i.e: running 'ant all' with the environment variable
		'CREATE_BUNDLE_REPORTS' set to 'yes', so that JSON reports about bundle
		sizes are created inside the 'build' directory of each project).
`,
		parameters: '',
		script: './report/dependencies.mjs',
	},
	'report:java-imports': {
		description: `
		Generate aggregated information about imported external packages and
		symbols by parsing Java and JSP source files.
`,
		parameters: '',
		script: './report/java-imports.mjs',
	},
	'report:source-imports': {
		description: `
		Generate aggregated information about imported external symbols by
		parsing JavaScript source files.

		Note that, in general, it is preferred to use report:bundle:imports
		because it shows dynamic imports. On the contrary, this report only
		shows static imports because it is focused in finding the symbols
		involved in each import, something that is impossible to know for
		dynamic imports.
`,
		parameters: '',
		script: './report/source-imports.mjs',
	},
	'setup': {
		description: `
		Setup working environment used by node-scripts (for example: download
		the binary Sass compiler when necessary).

		This task is usually invoked by yarn when locally installing
		node-scripts but it can also be run manually for troubleshooting
		purposes.
`,
		parameters: '',
		script: './setup/index.mjs',
	},
	'test': {
		description: `
		Runs unit tests in a single or multiple projects.

		When multiple projects are tested and --sync argument is given, project
		tests are run serially.
`,
		parameters: '[--sync]',
		script: './test/index.mjs',
	},
};

const command = process.argv[2];

if (COMMANDS[command] === undefined) {
	showHelpAndExit();
}

const {script} = COMMANDS[command];

const mainPromise = import(script);

mainPromise
	.then(({default: main}) => main())
	.catch((error) => {
		console.error(error);

		process.exit(1);
	});

function showHelpAndExit() {
	console.error(`
Usage: node-scripts <command>

	Where <command> is an action (like 'build', 'check', 'format', 'generate',
	'test', ...) optionally qualified by a subject (like ':custom', ':theme',
	...).

	Actions can usually be executed:

	  - Only per project (eg: build).
	  - Globally or per project based on the directory of invocation (eg:
	    format). Typically these commands run globally when invoked at 'modules'
	    and per project when invoked from a project's directory.

	Some actions may receive one of the arguments {--current-branch|
	--local-changes} when executed globally (at 'modules') to restrict the set
	of files to which they must be applied.

	The meaning of such flags is:

	  --current-branch: only check changed stuff that has been committed to the
	    active branch.
	  --local-changes: only check locally uncommitted changed stuff.

	The word "changed" should be interpreted as "changed as of master branch".


Available commands:
`);

	for (const [command, {description, parameters}] of Object.entries(
		COMMANDS
	)) {
		let line = '\t• ';

		line += command;

		if (parameters) {
			line += ` ${parameters}`;
		}

		line += '\n';
		line += description
			.split('\n')
			.map((line) => `\t  ${line.replaceAll('\t', '')}`)
			.join('\n');
		line += '\n';

		console.error(line);
	}

	process.exit(2);
}
