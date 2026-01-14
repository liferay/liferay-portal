/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {dateUtils, sub} from 'frontend-js-web';
import React from 'react';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';

export default function AdditionalItemInfoRenderer({
	itemData,
}: {
	itemData: any;
}) {
	const isFolder =
		itemData?.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;

	let text: string;

	if (isFolder) {
		const articleLength = itemData.embedded.numberOfObjectEntries ?? 0;
		const subfoldersLength =
			itemData.embedded.numberOfObjectEntryFolders ?? 0;

		const articleLabel =
			articleLength === 1
				? sub(Liferay.Language.get('x-article'), articleLength)
				: sub(Liferay.Language.get('x-articles'), articleLength);

		const subfolderLabel =
			subfoldersLength === 1
				? sub(Liferay.Language.get('x-folder'), subfoldersLength)
				: sub(Liferay.Language.get('x-folders'), subfoldersLength);

		text = `${articleLabel} · ${subfolderLabel}`;
	}
	else {
		text = sub(
			Liferay.Language.get('modified-x-by-x'),
			dateUtils.fromNow(new Date(itemData.dateModified)),
			itemData.embedded.creator.name
		);
	}

	return (
		<p className="c-mb-0 c-ml-4 c-mt-n1 c-pl-3 text-3 text-secondary">
			{text}
		</p>
	);
}
