/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';

import DesignLibraryConnectedSitesModal from '../modal/DesignLibraryConnectedSitesModal';
import DesignLibraryManageMembersModal from '../modal/DesignLibraryManageMembersModal';
import confirmAndDeleteEntryAction from '../props_transformer/actions/confirmAndDeleteEntryAction';

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
}: {
	externalReferenceCode: string;
}) {
	openModal({
		contentComponent: () =>
			DesignLibraryConnectedSitesModal({externalReferenceCode}),
		onClose: () => window.location.reload(),
		size: 'md',
	});
}

export function openManageMembersModal({
	externalReferenceCode,
	hasAssignMembersPermission,
	headerTitle,
	ownerId,
}: {
	externalReferenceCode: string;
	hasAssignMembersPermission: boolean;
	headerTitle: string;
	ownerId: string;
}) {
	openModal({
		contentComponent: () =>
			DesignLibraryManageMembersModal({
				externalReferenceCode,
				hasAssignMembersPermission,
				headerTitle,
				ownerId,
			}),
		onClose: () => window.location.reload(),
		size: 'md',
	});
}
