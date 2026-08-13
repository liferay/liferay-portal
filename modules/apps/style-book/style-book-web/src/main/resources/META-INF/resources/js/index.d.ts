/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {ReactElement} from 'react';

type FrontendTokenDefinitionProvider = {
	name: string;
	themeId: string;
};

interface AddStyleBookModalProps {
	addStyleBookEntryURL: string;
	closeModal: () => void;
	frontendTokenDefinitionProviders?: Array<FrontendTokenDefinitionProvider>;
	namespace: string;
}

export const AddStyleBookEntryDesignLibraryModalContent: (
	props: AddStyleBookModalProps
) => ReactElement;

export const AddStyleBookModalContent: (
	props: AddStyleBookModalProps
) => ReactElement;
