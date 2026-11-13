/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EVENT_TYPES as CORE_EVENT_TYPES} from '../../../core/actions/eventTypes.es';
import {evaluate} from '../../../utils/evaluation.es';
import {PagesVisitor} from '../../../utils/visitors.es';

export default function nextPage({
	activePage,
	containerId,
	defaultLanguageId,
	editingLanguageId,
	formId,
	groupId,
	pages,
	portletNamespace,
	rules,
	selectedPage,
	title,
	viewMode,
}) {
	const isValidPage = (currentPage, pages) => {
		const visitor = new PagesVisitor(pages);
		let validPage = true;

		visitor.mapFields(
			({valid}, fieldIndex, columnIndex, rowIndex, pageIndex) => {
				if (currentPage === pageIndex && !valid) {
					validPage = false;
				}
			},
			true,
			true
		);

		return validPage;
	};

	return (dispatch) => {
		evaluate(null, {
			activePage,
			containerId,
			defaultLanguageId,
			editingLanguageId,
			formId,
			groupId,
			nextPage: activePage + 1,
			pages,
			portletNamespace,
			previousPage: activePage,
			rules,
			viewMode,
		}).then((evaluatedPages) => {
			let validPage = true;

			let currentPage = activePage;

			if (selectedPage) {
				while (validPage) {
					validPage = isValidPage(currentPage, evaluatedPages);

					if (currentPage === selectedPage) {
						break;
					}

					if (validPage) {
						currentPage++;
					}
				}
			}
			else {
				validPage = isValidPage(activePage, evaluatedPages);
			}

			if (validPage || selectedPage) {
				const nextActivePageIndex = selectedPage
					? evaluatedPages.findIndex(
							({enabled}, index) =>
								enabled && index === currentPage
						)
					: evaluatedPages.findIndex(
							({enabled}, index) => enabled && index > activePage
						);

				const activePageUpdated = Math.min(
					nextActivePageIndex,
					pages.length - 1
				);

				dispatch({
					payload: {activePage: activePageUpdated},
					type: CORE_EVENT_TYPES.PAGE.CHANGE,
				});

				dispatch({
					payload: evaluatedPages,
					type: CORE_EVENT_TYPES.PAGE.UPDATE,
				});

				Liferay.fire('ddmFormPageShow', {
					formId,
					formPageTitle: pages[activePageUpdated].title,
					page: activePageUpdated,
					title,
				});
			}
			else {
				const pageIndex = selectedPage ? currentPage : activePage;
				dispatch({
					payload: {newPages: evaluatedPages, pageIndex},
					type: CORE_EVENT_TYPES.PAGE.VALIDATION_FAILED,
				});
			}
		});
	};
}
