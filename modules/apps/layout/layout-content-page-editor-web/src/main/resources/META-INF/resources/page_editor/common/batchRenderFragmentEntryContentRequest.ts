/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FragmentEntryLink} from '../app/actions/addFragmentEntryLinks';
import FragmentService from '../app/services/FragmentService';

type DataType = Parameters<
	typeof FragmentService.renderFragmentEntryLinksContent
>[number]['data'][number];

type RenderedFragmentEntryLink = Pick<
	FragmentEntryLink,
	'content' | 'editableTypes' | 'fragmentEntryLinkId'
>;

let timeoutId: any = null;
let args: Array<DataType> = [];
let callbacks: Array<
	(renderedFragmentEntryLink: RenderedFragmentEntryLink) => void
> = [];

export default function batchRenderFragmentEntryContentRequest(
	languageId: Liferay.Language.Locale,
	segmentsExperienceId: string,
	data: DataType,
	callback: (renderedFragmentEntryLink: RenderedFragmentEntryLink) => void
) {
	args = [...args, data];
	callbacks = [...callbacks, callback];

	if (timeoutId) {
		clearTimeout(timeoutId);
	}

	timeoutId = setTimeout(() => doCall(languageId, segmentsExperienceId), 100);
}

function doCall(
	languageId: Liferay.Language.Locale,
	segmentsExperienceId: string
) {
	timeoutId = 0;
	const currentArguments = args;
	const currentCallbacks = callbacks;

	args = [];
	callbacks = [];

	FragmentService.renderFragmentEntryLinksContent({
		data: currentArguments,
		languageId,
		segmentsExperienceId,
	}).then((responses) => {
		responses.forEach((response, index) => {
			currentCallbacks[index](response);
		});
	});
}
