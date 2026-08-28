/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * Selective version resolutions allowed in the global "modules/package.json", mapped to the reason
 * why each one is needed.
 *
 * Every key must be selective, that is, it must name the path to the resolved dependency through
 * at least one of its parents (see
 * https://classic.yarnpkg.com/lang/en/docs/selective-version-resolutions/). A bare package name
 * applies to the whole dependency tree, which turns into a silent downgrade the moment any package
 * starts requiring a newer version, and hides the fact that the tree no longer agrees with what the
 * "package.json" files declare.
 *
 * A resolution is a last resort. Before adding one, check whether the version can be fixed by
 * upgrading the package that pulls the dependency in, by declaring an explicit version in the
 * "package.json" that owns it, or simply by letting the lockfile re-resolve: when the declared
 * ranges already admit the version you want, no resolution is needed at all.
 */
export default {

	// Neither chain requires a version this high on its own, so these entries hold websocket-driver
	// up until the packages that pull it in start requiring it themselves.
	//
	// See https://liferay.atlassian.net/browse/LPD-102337 for more information.

	'liferay-theme-tasks/**/websocket-driver':
		'Holds the LiveReload server of the theme gulp tasks at a version the tiny-lr chain does not require on its own.',
	'webpack-dev-server/**/websocket-driver':
		'Holds the sockjs transports of the AMD loader dev server at a version the sockjs chain does not require on its own.',
};
