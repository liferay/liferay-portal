/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

type MessageBoardThread = {
	articleBody: string;
	headline: string;
	id: string;
	messageBoardRootMessageId?: number;
	status?: string;
};
