/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

import AddStyleBookModalContent from './AddStyleBookModalContent';

type FrontendTokenDefinitionProvider = {name: string; themeId: string};

type Props = {
	addStyleBookEntryURL: string;
	frontendTokenDefinitionProviders?: Array<FrontendTokenDefinitionProvider>;
	namespace: string;
};

export default function getStyleBookCreationItems({
	addStyleBookEntryURL,
	frontendTokenDefinitionProviders = [],
	namespace,
}: Props): Array<{label: string; onClick: () => void}> {
	return [
		{
			label: Liferay.Language.get('new-style-book'),
			onClick: () =>
				openModal({
					contentComponent: ({
						closeModal,
					}: {
						closeModal: () => void;
					}) =>
						AddStyleBookModalContent({
							addStyleBookEntryURL,
							closeModal,
							frontendTokenDefinitionProviders,
							namespace,
						}),
				}),
		},
	];
}
