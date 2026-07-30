/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ContentType} from '../components/ContentTypeSelectorMessageBalloon';
import {Message} from '../types';

export default function buildContentTypeMessage(
	contentTypes: ContentType[]
): Message {
	return {
		contentTypes,
		sender: 'assistant',
		text: Liferay.Language.get(
			'what-type-of-content-do-you-want-to-generate'
		),
	};
}
