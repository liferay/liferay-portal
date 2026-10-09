/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';
import React from 'react';

import AddLayoutPageTemplateEntryDesignLibraryModalContent, {
	AddLayoutPageTemplateEntryDesignLibraryModalContentProps,
} from '../AddLayoutPageTemplateEntryDesignLibraryModalContent';

type Props = Omit<
	AddLayoutPageTemplateEntryDesignLibraryModalContentProps,
	'closeModal' | 'mode'
>;

export default function openAddLayoutPageTemplateEntryDesignLibraryModal(
	props: Props
) {
	openModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) => (
			<AddLayoutPageTemplateEntryDesignLibraryModalContent
				{...props}
				closeModal={closeModal}
				mode="page-template"
			/>
		),
	});
}
