/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addParams} from 'frontend-js-web';

type CommentsURLs = {
	addCommentURL: string;
	editCommentURL: string;
	getCommentsURL: string;
	getMentionUsersURL?: string;
};

export default function getAssetCommentsURLs(
	{
		addCommentURL,
		editCommentURL,
		getCommentsURL,
		getMentionUsersURL,
	}: CommentsURLs,
	className: string,
	classPK: number | string
): CommentsURLs {
	const params = {className, classPK};

	return {
		addCommentURL: addParams(params, addCommentURL),
		editCommentURL: addParams(params, editCommentURL),
		getCommentsURL: addParams(params, getCommentsURL),
		getMentionUsersURL:
			getMentionUsersURL && addParams(params, getMentionUsersURL),
	};
}
