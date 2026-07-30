/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import DesignLibraryConnectedSitesModal from '../modal/DesignLibraryConnectedSitesModal';
import DesignLibraryManageMembersModal from '../modal/DesignLibraryManageMembersModal';
import confirmAndDeleteEntryAction from '../props_transformer/actions/confirmAndDeleteEntryAction';

function refreshDataSets(dataSetIds?: string[]) {
	(dataSetIds || []).forEach((dataSetId) => {
		Liferay.fire('fds-update-display', {id: dataSetId});
	});
}

export function confirmDeleteDesignLibrary({
	descriptiveName,
	href,
	redirect,
}: {
	descriptiveName: string;
	href: string;
	redirect?: string;
}) {
	confirmAndDeleteEntryAction({
		bodyHTML: `
			<p>${Liferay.Language.get('delete-design-library-confirmation-body-main')}</p>
			<p>${Liferay.Language.get('delete-design-library-confirmation-body-warning')}</p>
		`,
		deleteAction: {
			href,
			method: 'DELETE',
		},
		redirect,
		successMessage: sub(
			Liferay.Language.get('x-was-successfully-deleted'),
			`<strong>${Liferay.Util.escapeHTML(descriptiveName)}</strong>`
		),
		title: sub(
			Liferay.Language.get('delete-design-library-confirmation-title'),
			descriptiveName
		),
	});
}

export function openConnectedSitesModal({
	externalReferenceCode,
	refreshDataSetIds,
}: {
	externalReferenceCode: string;
	refreshDataSetIds?: string[];
}) {
	let changed = false;

	openModal({
		contentComponent: () =>
			DesignLibraryConnectedSitesModal({
				externalReferenceCode,
				onChange: () => {
					changed = true;
				},
			}),
		onClose: () => {
			if (changed) {
				refreshDataSets(refreshDataSetIds);
			}
		},
		size: 'md',
	});
}

export function openManageMembersModal({
	externalReferenceCode,
	hasAssignMembersPermission,
	headerTitle,
	ownerId,
	refreshDataSetIds,
}: {
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	headerTitle: string;
	ownerId: string;
	refreshDataSetIds?: string[];
}) {
	let changed = false;

	openModal({
		contentComponent: () =>
			DesignLibraryManageMembersModal({
				externalReferenceCode,
				hasAssignMembersPermission,
				headerTitle,
				onChange: () => {
					changed = true;
				},
				ownerId,
			}),
		onClose: () => {
			if (changed) {
				refreshDataSets(refreshDataSetIds);
			}
		},
		size: 'md',
	});
}
