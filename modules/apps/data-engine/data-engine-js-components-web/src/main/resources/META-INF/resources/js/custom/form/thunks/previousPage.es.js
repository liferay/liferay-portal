/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EVENT_TYPES as CORE_EVENT_TYPES} from '../../../core/actions/eventTypes.es';
import {evaluate} from '../../../utils/evaluation.es';

export default function previousPage({
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
	return (dispatch) => {
		evaluate(null, {
			activePage,
			containerId,
			defaultLanguageId,
			editingLanguageId,
			formId,
			groupId,
			nextPage: activePage - 1,
			pages,
			portletNamespace,
			previousPage: activePage,
			rules,
			viewMode,
		}).then((evaluatedPages) => {
			let previousActivePageIndex = activePage;

			if (typeof selectedPage === 'number') {
				previousActivePageIndex = selectedPage;
			}
			else {
				for (let i = activePage - 1; i > -1; i--) {
					if (evaluatedPages[i].enabled) {
						previousActivePageIndex = i;

						break;
					}
				}
			}

			const activePageUpdated = Math.max(previousActivePageIndex, 0);

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
		});
	};
}
