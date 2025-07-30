/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const CUSTOM_RANGE_BUCKET_TEXT = 'custom-range';

const FACET_TERM_CLASS = 'facet-term';

const FACET_TERM_SELECTED_CLASS = 'facet-term-selected';

/**
 * Gets the ID by checking the `data-term-id` attribute and then `id` if
 * `data-term-id` is not defined.
 *
 * The default layout continues to use `data-term-id` in case the
 * original ID format `${namespace}_term_${index}` is expected, but
 * newer layouts (ADT) sometimes only use `id`.
 */
function _getTermId(term) {
	return term.dataset.termId || term.id;
}

/**
 * Converts a NodeList to an array of nodes. This allows array
 * methods to be performed.
 * @param {NodeList} nodeList
 */
function _transformNodeListToArray(nodeList) {
	const nodeArray = [];

	nodeList.forEach((node) => nodeArray.push(node));

	return nodeArray;
}

export const FacetUtil = {
	addURLParameter(key, value, parameterArray) {
		key = encodeURIComponent(key);
		value = encodeURIComponent(value);

		parameterArray[parameterArray.length] = [key, value].join('=');

		return parameterArray;
	},

	changeSelection(event) {
		event.preventDefault();

		const form = event.currentTarget.form;

		if (!form) {
			return;
		}

		// Disable checkboxes across all facets to avoid multiple
		// selections. Only the most recent selection will be added
		// since the page needs to be reloaded before another selection
		// can be made.

		const allFacetTerms = document.querySelectorAll(`.${FACET_TERM_CLASS}`);

		allFacetTerms.forEach((term) => {
			Liferay.Util.toggleDisabled(term, true);
		});

		const currentSelectedTermId = _getTermId(event.currentTarget);

		const facetTerms = document.querySelectorAll(
			`#${form.id} .${FACET_TERM_CLASS}`
		);

		const selectedTerms = _transformNodeListToArray(facetTerms)
			.filter((term) => {
				if (term.type === 'checkbox') {
					return term.checked;
				}

				const isCurrentTarget =
					_getTermId(term) === currentSelectedTermId;

				const isSelected = Array.prototype.includes.call(
					term.classList,
					FACET_TERM_SELECTED_CLASS
				);

				return isCurrentTarget ? !isSelected : isSelected;
			})
			.map((term) => {
				const termId = _getTermId(term);

				const isCurrentTarget = termId === currentSelectedTermId;

				return this.getSelectedTerm(termId, form, isCurrentTarget);
			});

		this.selectTerms(form, selectedTerms);
	},

	changeSingleSelection(event) {
		event.preventDefault();

		const form = event.currentTarget.form;

		if (!form) {
			return;
		}

		// Disable checkboxes across all facets to avoid multiple
		// selections. Only the most recent selection will be added
		// since the page needs to be reloaded before another selection
		// can be made.

		const allFacetTerms = document.querySelectorAll(`.${FACET_TERM_CLASS}`);

		allFacetTerms.forEach((term) => {
			Liferay.Util.toggleDisabled(term, true);
		});

		const termId = _getTermId(event.currentTarget);

		this.selectTerms(form, [this.getSelectedTerm(termId, form, true)]);
	},

	clearSelections(event) {
		event.preventDefault();

		const form = event.currentTarget.form;

		if (!form) {
			return;
		}

		const selections = [];

		this.selectTerms(form, selections);
	},

	enableInputs(inputs) {
		inputs.forEach((term) => {
			Liferay.Util.toggleDisabled(term, false);
		});
	},

	getSelectedTerm(termId, form, isCurrentTarget) {
		if (termId === CUSTOM_RANGE_BUCKET_TEXT) {

			// For the special case of a range facet, the
			// termId is 'custom-range' and the parameters are
			// prefixed with 'from' and 'to'. This will return
			// an array [from, to] for the custom range.

			// If the term is not the current target, apply the
			// existing range from the URL parameters. This is set
			// in setURLParameters so use '[]' as a placeholder.

			if (!isCurrentTarget) {
				return [];
			}

			// If the term is the current target, use the current
			// values in the inputs.
			// General range defaults to [0, 0] if no value is set.
			// Date range defaults to the last 24 hours if no value is set.

			const fromValue = form.querySelector('[id$=fromInput]').value;
			const toValue = form.querySelector('[id$=toInput]').value;

			if (form.querySelector('.aggregation-type').value === 'range') {
				return [fromValue || 0, toValue || 0];
			}

			let endDate = new Date();
			let startDate = new Date(
				endDate - 1000 * 60 * 60 * 24 // 24 hours
			);

			if (fromValue && toValue) {
				endDate = new Date(toValue);
				startDate = new Date(fromValue);
			}

			return [
				startDate.toISOString().split('T')[0],
				endDate.toISOString().split('T')[0],
			];
		}
		else {
			return termId;
		}
	},

	isCustomRangeValid(event) {
		const form = event.currentTarget.form;

		const fromInputValue = form.querySelector('[id$=fromInput]').value;
		const toInputValue = form.querySelector('[id$=toInput]').value;

		return (
			fromInputValue &&
			toInputValue &&
			Number(fromInputValue) <= Number(toInputValue)
		);
	},

	queryParameterAndUpdateValue(form, search, selections) {
		const formParameterNameElement = document.querySelector(
			'#' + form.id + ' input.facet-parameter-name'
		);

		const startParameterNameElement = document.querySelector(
			'#' + form.id + ' input.start-parameter-name'
		);

		if (startParameterNameElement) {
			search = this.removeStartParameter(
				startParameterNameElement.value,
				search
			);
		}

		search = this.updateQueryString(
			formParameterNameElement.value,
			selections,
			search
		);

		return search;
	},

	removeAllFacetParameters(queryString) {
		let search = queryString;

		const allForms = document.getElementsByTagName('form');

		const formsWithInputFacetParameter = Array.from(allForms).filter(
			(form) => {
				return (
					form.querySelector('input.facet-parameter-name') !== null
				);
			}
		);

		const selections = [];

		formsWithInputFacetParameter.forEach((form) => {
			search = this.queryParameterAndUpdateValue(
				form,
				search,
				selections
			);
		});

		return search;
	},

	removeStartParameter(startParameterName, queryString) {
		let search = queryString;

		const hasQuestionMark = search[0] === '?';

		if (hasQuestionMark) {
			search = search.substr(1);
		}

		const parameterArray = search.split('&').filter((item) => {
			return item.trim() !== '';
		});

		const newParameters = this.removeURLParameters(
			startParameterName,
			parameterArray
		);

		search = newParameters.join('&');

		if (hasQuestionMark) {
			search = '?' + search;
		}

		return search;
	},

	removeURLParameters(key, parameterArray) {
		key = encodeURIComponent(key);

		return parameterArray.filter((item) => {
			const itemSplit = item.split('=');

			return !(itemSplit && itemSplit[0] === key);
		});
	},

	selectTerms(form, selections) {
		const url = new URL(window.location.href);

		url.search = this.queryParameterAndUpdateValue(
			form,
			window.location.search,
			selections
		);

		Liferay.Util.navigate(url.toString());
	},

	setURLParameter(url, name, value) {
		const parts = url.split('?');

		const address = parts[0];

		let queryString = parts[1];

		if (!queryString) {
			queryString = '';
		}

		queryString = this.updateQueryString(name, [value], queryString);

		return address + '?' + queryString;
	},

	setURLParameters(key, selections, parameterArray) {
		let newParameters = this.removeURLParameters(key, parameterArray);

		newParameters = this.removeURLParameters(key + 'From', newParameters);

		newParameters = this.removeURLParameters(key + 'To', newParameters);

		selections.forEach((item) => {
			if (Array.isArray(item)) {
				if (!item.length) {

					// With empty ranges, grab the existing range
					// from parameterArray.

					const from = parameterArray.find((param) =>
						param.includes(key + 'From')
					);
					const to = parameterArray.find((param) =>
						param.includes(key + 'To')
					);

					item = [from.split('=')[1], to.split('=')[1]];
				}

				newParameters = this.addURLParameter(
					key + 'From',
					item[0],
					newParameters
				);

				newParameters = this.addURLParameter(
					key + 'To',
					item[1],
					newParameters
				);
			}
			else {
				newParameters = this.addURLParameter(key, item, newParameters);
			}
		});

		return newParameters;
	},

	updateQueryString(key, selections, queryString) {
		let search = queryString;

		const hasQuestionMark = search[0] === '?';

		if (hasQuestionMark) {
			search = search.substr(1);
		}

		const parameterArray = search.split('&').filter((item) => {
			return item.trim() !== '';
		});

		const newParameters = this.setURLParameters(
			key,
			selections,
			parameterArray
		);

		search = newParameters.join('&');

		if (hasQuestionMark) {
			search = '?' + search;
		}

		return search;
	},
};

export default function ({namespace: portletNamespace}) {
	Liferay.namespace('Search').FacetUtil = FacetUtil;

	if (portletNamespace) {
		FacetUtil.enableInputs(
			document.querySelectorAll(
				`[id^="${portletNamespace}fm"] .facet-term`
			)
		);
	}
}
