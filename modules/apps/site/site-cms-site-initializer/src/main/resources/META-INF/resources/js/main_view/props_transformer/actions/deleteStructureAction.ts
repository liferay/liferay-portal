/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import ApiHelper from '../../../common/services/ApiHelper';
import DeleteStructureModalContent from '../../modal/DeleteStructureModalContent';
import {executeAsyncItemAction} from '../utils/executeAsyncItemAction';

export default async function deleteStructureAction({
	deleteAction,
	getObjectDefinitionDeleteInfoURL,
	loadData,
	name,
	status,
}: {
	deleteAction: {href: string; method: string};
	getObjectDefinitionDeleteInfoURL: string;
	loadData: () => {};
	name: string;
	status: number;
}) {
	const deleteStructureToast = async () => {
		await executeAsyncItemAction({
			method: deleteAction.method,
			refreshData: loadData,
			successMessage: sub(
				Liferay.Language.get('x-was-deleted-successfully'),
				`<strong>${Liferay.Util.escapeHTML(name)}</strong>`
			),
			url: deleteAction.href,
		});
	};

	if (status !== 0) {
		await deleteStructureToast();

		return;
	}

	const {data, error} = await ApiHelper.get<{
		hasObjectRelationship: boolean;
		objectEntriesCount: number;
	}>(getObjectDefinitionDeleteInfoURL);

	if (!data || error) {
		return;
	}

	const {hasObjectRelationship, objectEntriesCount} = data;

	if (hasObjectRelationship) {
		openModal({
			bodyHTML: `<p>${sub(
				Liferay.Language.get(
					'x-is-currently-referenced-by-or-referencing-other-content-structures,-and-so-cannot-be-deleted'
				),
				`<strong>${Liferay.Util.escapeHTML(name)}</strong>`
			)}</p>`,
			buttons: [
				{
					displayType: 'warning',
					label: Liferay.Language.get('ok'),
					onClick: ({processClose}: {processClose: Function}) => {
						processClose();
					},
				},
			],
			size: 'md',
			status: 'warning',
			title: Liferay.Language.get('deletion-not-allowed'),
		});
	}
	else {
		openModal({
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				DeleteStructureModalContent({
					closeModal,
					name,
					onDelete: deleteStructureToast,
					usesCount: objectEntriesCount,
				}),
			size: 'md',
			status: 'danger',
		});
	}
}
