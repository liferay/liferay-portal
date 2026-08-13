/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AddStyleBookModalContent from './AddStyleBookModalContent';

type FrontendTokenDefinitionProvider = {
	name: string;
	themeId: string;
};

export type AddStyleBookEntryDesignLibraryModalContentProps = {
	addStyleBookEntryURL: string;
	closeModal: () => void;
	frontendTokenDefinitionProviders?: Array<FrontendTokenDefinitionProvider>;
	namespace: string;
};

export default function AddStyleBookEntryDesignLibraryModalContent(
	props: AddStyleBookEntryDesignLibraryModalContentProps
) {
	return <AddStyleBookModalContent {...props} />;
}
