/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {cancelDebounce, debounce} from 'frontend-js-web';
import {useRef} from 'react';

import {client} from './client.es';
import lang from './lang.es';

export function dateToInternationalHuman(ISOString, language) {
	const date = new Date(ISOString);

	const options = {
		day: 'numeric',
		hour: '2-digit',
		minute: '2-digit',
		month: 'short',
		year: 'numeric',
	};

	const intl = new Intl.DateTimeFormat(language, options);

	return intl.format(date);
}

export function dateToBriefInternationalHuman(
	ISOString,
	localeKey = navigator.language
) {
	const date = new Date(ISOString);

	const intl = new Intl.DateTimeFormat(localeKey, {
		day: '2-digit',
		month: '2-digit',
		year: '2-digit',
	});

	return intl.format(date);
}

export function deleteCache() {
	client.cache.clear();
}

export function deleteCacheKey(query, variables) {
	const keyObj = {
		operation: {
			query,
			variables,
		},
	};
	keyObj.fetchOptions = {};
	client.cache.delete(keyObj);
}

export function timeDifference(previous, current = new Date()) {
	const msPerMinute = 60 * 1000;
	const msPerHour = msPerMinute * 60;
	const msPerDay = msPerHour * 24;
	const msPerMonth = msPerDay * 30;
	const msPerYear = msPerDay * 365;

	const elapsed = current - new Date(previous);

	if (elapsed < msPerMinute) {
		return lang.sub(Liferay.Language.get('asked-x-seconds-ago-by'), [
			Math.round(elapsed / 1000),
		]);
	}
	else if (elapsed < msPerHour) {
		return lang.sub(Liferay.Language.get('asked-x-minutes-ago-by'), [
			Math.round(elapsed / msPerMinute),
		]);
	}
	else if (elapsed < msPerDay) {
		return lang.sub(Liferay.Language.get('asked-x-hours-ago-by'), [
			Math.round(elapsed / msPerHour),
		]);
	}
	else if (elapsed < msPerMonth) {
		return lang.sub(Liferay.Language.get('asked-x-days-ago-by'), [
			Math.round(elapsed / msPerDay),
		]);
	}
	else if (elapsed < msPerYear) {
		return lang.sub(Liferay.Language.get('asked-x-months-ago-by'), [
			Math.round(elapsed / msPerMonth),
		]);
	}
	else {
		return lang.sub(Liferay.Language.get('asked-x-years-ago-by'), [
			Math.round(elapsed / msPerYear),
		]);
	}
}

export function getErrorObjectsByStatusCode(message, title) {
	return [
		{
			code: 404,
			message,
			title,
		},
	];
}

export function getErrorObject(statusCode, message, title) {
	const errorObject = getErrorObjectsByStatusCode(message, title).find(
		(errorObject) => errorObject.code === statusCode
	);

	return errorObject
		? errorObject
		: {
				code: statusCode,
				message: Liferay.Language.get('error'),
			};
}

export function useDebounceCallback(callback, milliseconds) {
	const callbackRef = useRef(debounce(callback, milliseconds));

	return [callbackRef.current, () => cancelDebounce(callbackRef.current)];
}

export function normalizeRating(aggregateRating) {
	return (
		aggregateRating &&
		Math.trunc(
			aggregateRating.ratingCount *
				normalize(aggregateRating.ratingAverage)
		)
	);
}

export function normalize(ratingValue) {
	return ratingValue * 2 - 1;
}

export function stringToSlug(text) {
	const whiteSpaces = /\s+/g;

	return text.replace(whiteSpaces, '-').toLowerCase();
}

export function slugToText(slug) {
	if (!slug) {
		return slug;
	}

	const hyphens = /-+/g;

	return slug.replace(hyphens, ' ').toLowerCase();
}

export function historyPushWithSlug(push) {
	return (url) => push(stringToSlug(url));
}

export function navigateWithSlug(navigate) {
	return (url) => navigate(stringToSlug(url));
}

export function stripHTML(text) {
	if (!text) {
		return '';
	}

	const htmlTags = /<([^>]+>)/g;
	const nonBreakableSpace = '&nbsp;';
	const newLines = /\r?\n|\r/g;

	return (
		text
			.replace(htmlTags, '')
			.replace(nonBreakableSpace, ' ')
			.replace(newLines, '') || ''
	);
}

export function toCSSString(text) {
	const specialCharacters = /[^\d A-Za-z]/gu;

	return `"${text.replace(
		specialCharacters,
		(character) => `\\${character.codePointAt(0).toString(16)} `
	)}"`;
}

export function getFullPath(path) {
	const href = window.location.href;
	const indexOf = href.indexOf('#');

	if (indexOf !== -1) {
		return href.substring(0, indexOf);
	}

	return href.substring(0, href.indexOf(path));
}

export function getBasePath(path) {
	const origin = window.location.origin.length;

	const href = window.location.href;
	const indexOf = href.indexOf('#');

	if (indexOf !== -1) {
		return href.substring(origin, indexOf);
	}

	return href.substring(origin, href.indexOf(path));
}

export function getBasePathWithHistoryRouter(friendlyURLPath) {
	const href = window.location.href;
	const appPath = '/questions';

	if (!href.includes(friendlyURLPath)) {
		return normalizeUrl(href) + friendlyURLPath + appPath;
	}
	else if (!href.includes(appPath)) {
		return normalizeUrl(href) + appPath;
	}

	return href;
}

function normalizeUrl(url) {
	if (!url) {
		return url;
	}

	return url[url.length - 1] === '/' ? url.substring(0, url.length - 1) : url;
}

export function getContextLink(url) {
	let link = window.location.href;

	if (link.indexOf('#') !== -1) {
		link = `${getFullPath()}?redirectTo=/%23/questions/${url}/`;
	}
	else {
		link = `${getFullPath('questions')}questions/${url}/`;
	}

	return {
		headers: {Link: encodeURI(link)},
	};
}

/**
 * Assign the properties for error, used by graphql-hooks/APIError.
 * @param {Object} error
 * @param {Object} error.fetchError
 * @param {string?} error.fetchError.message
 * @param {Object[]} error.graphQLErrors
 * @param {string} error.graphQLErrors[].message
 * @param {Object} error.httpError
 */

export function processGraphQLError(error) {
	const _error = {
		message: error.message ?? '',
		type: 'danger',
	};

	if (error.fetchError) {
		_error.message = error.fetchError.message;
	}

	if (error.graphQLErrors) {
		for (const graphQLError of error.graphQLErrors) {
			_error.message += `${graphQLError.message} -`;
		}
	}

	if (error.httpError) {
		console.error('Forbidden ', error.httpError);

		_error.message = Liferay.Language.get('an-unexpected-error-occurred');
	}

	openToast(_error);
}
