/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getSpritemap} from '@liferay/frontend-icons-web';
import {openSimpleInputModal} from 'frontend-js-components-web';

export default function SelectLayoutPageTemplateEntryMasterLayoutVerticalCardPropsTransformer({
	additionalProps: {
		addLayoutPageTemplateEntryURL,
		dialogTitle,
		mainFieldLabel,
		mainFieldName,
		mainFieldPlaceholder,
	},
	portletNamespace: namespace,
	...otherProps
}) {
	return {
		...otherProps,
		onClick: (event) => {
			event.preventDefault();

			openSimpleInputModal({
				dialogTitle,
				formSubmitURL: addLayoutPageTemplateEntryURL,
				mainFieldLabel,
				mainFieldName,
				mainFieldPlaceholder,
				namespace,
				spritemap: getSpritemap(),
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
