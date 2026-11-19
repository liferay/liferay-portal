/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getSpritemap} from '@liferay/frontend-icons-web';

import openContentTypeModal from '../commands/openContentTypeModal';

export default function SelectDisplayPageMasterLayoutVerticalCardPropsTransformer({
	additionalProps: {addDisplayPageURL, mappingTypes, title},
	portletNamespace: namespace,
	...otherProps
}) {
	return {
		...otherProps,
		onClick: (event) => {
			event.preventDefault();

			openContentTypeModal({
				formSubmitURL: addDisplayPageURL,
				mappingTypes,
				namespace,
				spritemap: getSpritemap(),
				title,
			});
		},
		onKeyDown: (event) => {
			if (
				event.nativeEvent.code === 'Enter' ||
				event.nativeEvent.code === 'Space'
			) {
				event.preventDefault();
				event.target.click();
			}
		},
		role: 'button',
		tabIndex: '0',
	};
}
