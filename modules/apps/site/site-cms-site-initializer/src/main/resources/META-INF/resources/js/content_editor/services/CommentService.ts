/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {objectToFormData} from 'frontend-js-web';

import ApiHelper, {RequestResult} from '../../common/services/ApiHelper';

export type Comments = Comment[];

export type Comment = {
	author: {
		fullName: string;
		portraitURL: string;
		userId: string;
	};
	body: string;
	children: Comment[];
	className: string;
	commentId: string;
	dateDescription: string;
	edited: boolean;
	negativeVotes: number;
	positiveVotes: number;
	rootComment: boolean;
};

async function addComment({
	content,
	parentCommentId = null,
	url,
}: {
	content: string;
	parentCommentId?: string | null;
	url: string;
}): Promise<RequestResult<Comment>> {
	return await ApiHelper.postFormData(
		objectToFormData({
			body: content,
			parentCommentId,
		}),
		url
	);
}

async function deleteComment({
	commentId,
	url,
}: {
	commentId: string;
	url: string;
}) {
	return await ApiHelper.postFormData(objectToFormData({commentId}), url);
}

async function editComment({
	commentId,
	content,
	url,
}: {
	commentId: string;
	content: string;
	url: string;
}): Promise<RequestResult<Comment>> {
	return await ApiHelper.postFormData(
		objectToFormData({body: content, commentId}),
		url
	);
}

async function getComments({
	url,
}: {
	url: string;
}): Promise<RequestResult<Comments>> {
	return await ApiHelper.get(url);
}

export default {
	addComment,
	deleteComment,
	editComment,
	getComments,
};
