/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal, openToast} from 'frontend-js-components-web';
import {escapeHTML, sub} from 'frontend-js-web';

import DesignLibraryService from '../../services/DesignLibraryService';
import {DesignLibrary} from '../../types';

type DeletableDesignLibraries = Array<Pick<DesignLibrary, 'actions' | 'name'>>;

function getConfirmationMessage(items: DeletableDesignLibraries) {
	if (items.length === 1) {
		const [{name}] = items;

		return {
			bodyMessage: Liferay.Language.get(
				'delete-design-library-confirmation-body-main'
			),
			successMessage: sub(
				Liferay.Language.get('x-was-successfully-deleted'),
				`<strong>${escapeHTML(name)}</strong>`
			),
			title: sub(
				Liferay.Language.get(
					'delete-design-library-confirmation-title'
				),
				name
			),
		};
	}

	return {
		bodyMessage: Liferay.Language.get(
			'delete-design-libraries-confirmation-body-main'
		),
		successMessage: sub(
			Liferay.Language.get(
				'x-design-libraries-were-successfully-deleted'
			),
			items.length
		),
		title: sub(
			Liferay.Language.get(
				'delete-x-design-libraries-confirmation-title'
			),
			items.length
		),
	};
}

export default function confirmAndDeleteEntriesAction({
	items,
	loadData,
}: {
	items: DeletableDesignLibraries;
	loadData?: () => void;
}) {
	const {bodyMessage, successMessage, title} = getConfirmationMessage(items);

	return openModal({
		bodyHTML: `
			<p>${bodyMessage}</p>
			<p>${Liferay.Language.get('delete-design-library-confirmation-body-warning')}</p>
		`,
		buttons: [
			{
				autoFocus: true,
				displayType: 'secondary',
				label: Liferay.Language.get('cancel'),
				type: 'cancel',
			},
			{
				displayType: 'danger',
				label: Liferay.Language.get('delete'),
				onClick: async ({processClose}: {processClose: () => void}) => {
					processClose();

					const results = await Promise.allSettled(
						items.flatMap((item) =>
							item.actions?.delete
								? DesignLibraryService.remove(
										item.actions.delete
									)
								: []
						)
					);

					const deletedCount = results.filter(
						(result) => result.status === 'fulfilled'
					).length;

					if (deletedCount === items.length) {
						openToast({message: successMessage, type: 'success'});
					}
					else if (deletedCount) {
						openToast({
							message: sub(
								Liferay.Language.get(
									'x-of-x-design-libraries-were-deleted'
								),
								deletedCount,
								items.length
							),
							type: 'warning',
						});
					}
					else {
						openToast({
							message: Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
							type: 'danger',
						});
					}

					if (deletedCount) {
						loadData?.();
					}
				},
			},
		],
		role: 'alert',
		status: 'danger',
		title,
	});
}
