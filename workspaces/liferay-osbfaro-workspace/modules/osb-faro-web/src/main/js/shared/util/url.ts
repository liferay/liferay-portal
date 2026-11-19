import Constants from 'shared/util/constants';

function createURL(href: string): URL {
	try {
		return new URL(href);
	}
	catch {
		return new URL(href, document.baseURI);
	}
}

function isDef(param: unknown): boolean {
	return param !== null && param !== undefined;
}

function addParam(url: URL, key: string, value: unknown): void {
	url.searchParams.delete(key);

	if (isDef(key) && isDef(value)) {
		url.searchParams.append(key, String(value));
	}
}

const {cur: defaultCur, orderDefault} = Constants.pagination;

/**
 * Represents the query filter parameters as an Immutable Map
 * with filter fields as Map keys and the field values as Map values.
 * @example new Map({devices: new Set(['desktop', 'mobile'])})
 * @typedef {Map.<Set>} FilterBy
 */

/**
 * Return the url with filter params added.
 * @param {FilterBy} filterBy - A Map of active filters.
 * @param {string} href - The url with filter params added.
 */
export function setUriFilterValues(
	filterBy: {
		forEach: (
			callback: (
				valueISet: {
					filter: (predicate: (value: unknown) => unknown) => {
						toArray: () => unknown[];
					};
				},
				key: string
			) => void
		) => void;
	},
	href = window.location.href
) {
	const uri = createURL(href);

	filterBy.forEach((valueISet, key) => {
		addParam(uri, key, valueISet.filter(Boolean).toArray());
	});

	return `${uri.pathname}${uri.search}`;
}

export function setUriQueryValue(href: string, name: string, value: unknown) {
	const uri = createURL(href);

	addParam(uri, name, value);

	return `${uri.pathname}${uri.search}`;
}

export function setUriQueryValues(
	values: {[key: string]: any},
	href = window.location.href
) {
	const uri = createURL(href);

	for (const [name, value] of Object.entries(values)) {
		addParam(uri, name, value);
	}

	return `${uri.pathname}${uri.search}`;
}

/**
 * Remove URL Query Param
 * @param {string} href
 * @param {string} names
 */
export function removeUriQueryParam(href: string, ...names: string[]) {
	const uri = createURL(href);

	for (const name of names) {
		uri.searchParams.delete(name);
	}

	return `${uri.pathname}${uri.search}`;
}

export function removePageParam(newPath: string, href = window.location.href) {
	const uri = createURL(href);

	if (newPath) {
		uri.pathname = newPath;
	}

	uri.searchParams.delete('page');

	return `${uri.pathname}${uri.search}`;
}

export function resetPaginationParams(
	newPath: string,
	href = window.location.href
) {
	const uri = createURL(href);

	if (newPath) {
		uri.pathname = newPath;
	}

	addParam(uri, 'page', defaultCur);
	addParam(uri, 'orderBy', orderDefault);
	addParam(uri, 'query', '');

	return `${uri.pathname}${uri.search}`;
}

export function reloadPage() {
	window.location.reload();
}
