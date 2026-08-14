/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	openConfirmModal,
	openModal,
	openToast,
} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';

import openDeleteLayoutModal from './openDeleteLayoutModal';

const ACTIONS = {
	convertToPageTemplate: () => {
		Liferay.fire('convertToPageTemplate');
	},

	copyLayout: ({copyLayoutURL}) => {
		openModal({
			height: '60vh',
			id: 'addLayoutDialog',
			size: 'md',
			title: Liferay.Language.get('copy-page'),
			url: copyLayoutURL,
		});
	},

	copyLayoutWithPermissions: ({copyLayoutURL}) => {
		openModal({
			height: '60vh',
			id: 'addLayoutDialog',
			size: 'md',
			title: Liferay.Language.get('copy-page-with-permissions'),
			url: copyLayoutURL,
		});
	},

	deleteLayout: ({deleteLayoutURL, message}) => {
		openDeleteLayoutModal({
			message,
			onDelete: () => {
				return fetch(deleteLayoutURL, {
					method: 'post',
				})
					.then((response) => response.json())
					.then(({errorMessage, redirectURL}) => {
						if (errorMessage) {
							openToast({
								message: errorMessage,
								title: Liferay.Language.get('error'),
								type: 'danger',
							});
						}
						else {
							navigate(redirectURL);
						}
					})
					.catch(() =>
						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							title: Liferay.Language.get('error'),
							type: 'danger',
						})
					);
			},
		});
	},

	discardDraft: ({discardDraftURL}) => {
		openConfirmModal({
			message: Liferay.Language.get(
				'are-you-sure-you-want-to-discard-current-draft-and-apply-latest-published-changes'
			),
			onConfirm: (isConfirmed) => {
				if (isConfirmed) {
					navigate(discardDraftURL);
				}
			},
		});
	},

	exportTranslation: ({exportTranslationURL}) => {
		navigate(exportTranslationURL);
	},

	permissionLayout: ({permissionLayoutURL}) => {
		openModal({
			title: Liferay.Language.get('permissions'),
			url: permissionLayoutURL,
		});
	},
};

export default ACTIONS;
