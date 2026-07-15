/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FDS_CONFIG_PARAM_NAME} from '../configInURL';

/**
 * Try to replace interpolated url arguments with item properties.
 * Set _redirect and/or backURL parameters to allow navigating back
 * to the FDS component that triggered the action
 *
 * @param url URI with an optional number of interpolated parameters
 * @param item object with properties that could match interpolated parameters
 * @param target string that indicates the type of the action: link, modal, sidepanel
 *
 * @example
 * url = '/o/data-sample/{id}
 * item = {
 *   name: 'test',
 *   id: 123
 * }
 *
 * Will return '/o/data-sample/123
 *
 * See test/utils/actionItems/formatActionURL.ts for more examples
 */

import getValueFromItem from '../getValueFromItem';

const formatActionURLTokenRegex = new RegExp(
	'(?<!' + FDS_CONFIG_PARAM_NAME + '=)(?:%7B|{)(.*?)(?:%7D|})',
	'g'
);

const protectFDSConfigRegex = new RegExp(
	'(' + FDS_CONFIG_PARAM_NAME + '=)([^&]*)',
	'g'
);

const isFullInterpolation = function (url: string | undefined): boolean {
	if (!url) {
		return false;
	}

	return url?.startsWith('{') && url?.endsWith('}');
};

function protectFDSConfigValues(url: string) {
	const protectedValues: string[] = [];

	const sanitizedURL = url.replace(protectFDSConfigRegex, (match, key) => {
		const value = match.substring(key.length);
		protectedValues.push(value);

		return `${key}__FDS_CONFIG_VALUE_${protectedValues.length - 1}__`;
	});

	const restore = (finalURL: string) => {
		return finalURL.replace(
			/__FDS_CONFIG_VALUE_(\d+)__/g,
			(match, index) => protectedValues[parseInt(index, 10)]
		);
	};

	return {restore, sanitizedURL};
}

export const replaceTokens = function (
	url: string | undefined,
	item: any
): string {
	if (!url) {
		return '';
	}

	if (!item) {
		return url;
	}

	const fullInterpolation = isFullInterpolation(url);

	const {restore, sanitizedURL} = protectFDSConfigValues(url);

	return restore(
		sanitizedURL.replace(formatActionURLTokenRegex, (match, key) => {
			const value = getValueFromItem(item, key.split('.'));

			return fullInterpolation ? value : encodeURIComponent(value);
		})
	);
};

/**
 * Rewrites the `redirect` and `backURL` parameters of `url` to the current
 * browser location, including portlet namespaced variants such as
 * `_<p_p_id>_redirect`. When the URL carries neither but has a `p_p_id`
 * parameter, namespaced `redirect` and `backURL` parameters are added.
 *
 * The values of those parameters are rendered on the server as a snapshot of
 * the page URL, so they predate any state the client writes to the URL
 * afterwards, such as the active view. Rewriting them at navigation time
 * allows navigating back to the page as the user left it.
 *
 * `matchURL` is the string inspected to decide whether `url` takes part in
 * redirect handling at all, and defaults to `url` itself. `formatActionURL`
 * passes the raw templated URL so that token interpolation cannot change
 * that decision.
 */
export const rewriteRedirectParams = function (
	url: string,
	matchURL: string = url
): string {
	const queryIndex = url.indexOf('?');

	if (queryIndex === -1) {
		return url;
	}

	const hashIndex = url.indexOf('#');

	const searchParams = new URLSearchParams(
		url.slice(queryIndex, hashIndex > queryIndex ? hashIndex : url.length)
	);

	const backURL = 'backURL';
	const redirect = 'redirect';
	const backURLRegexp = new RegExp(backURL);
	const redirectRegexp = new RegExp(redirect);

	const p_p_id = searchParams.get('p_p_id');

	let modifiedParams = false;

	const redirectionURL = window.location.href;

	if (redirectRegexp.test(matchURL) || backURLRegexp.test(matchURL)) {
		for (const key of searchParams.keys()) {
			if (redirectRegexp.test(key) || backURLRegexp.test(key)) {
				searchParams.set(key, redirectionURL);
				modifiedParams = true;
			}
		}
	}
	else if (p_p_id) {
		searchParams.set(`_${p_p_id}_${redirect}`, redirectionURL);
		searchParams.set(`_${p_p_id}_${backURL}`, redirectionURL);

		modifiedParams = true;
	}

	if (!modifiedParams) {
		return url;
	}

	return (
		url.slice(0, queryIndex) +
		'?' +
		searchParams.toString() +
		(hashIndex > -1 ? url.slice(hashIndex) : '')
	);
};

const formatActionURL = function (
	url: string | undefined,
	item: any,
	target?: string
): string {
	if (!url) {
		return '';
	}

	const replacedURL = replaceTokens(url, item);

	if (target !== 'link' || isFullInterpolation(url)) {
		return replacedURL;
	}

	return rewriteRedirectParams(replacedURL, url);
};

export default formatActionURL;
