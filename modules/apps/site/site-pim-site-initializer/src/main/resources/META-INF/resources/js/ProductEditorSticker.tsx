/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import ProductSticker from './cell_renderers/ProductSticker';
import getImageURL from './cell_renderers/getImageURL';

import '../css/ProductEditorSticker.scss';

export default function ProductEditorSticker({
	objectEntryId,
	restContextPath,
}: {
	objectEntryId?: string;
	restContextPath?: string;
}) {
	const [imageURL, setImageURL] = useState<string | undefined>();

	useEffect(() => {
		if (!objectEntryId || !restContextPath) {
			return;
		}

		fetch(
			`${restContextPath}/${objectEntryId}?fields=images&nestedFields=images`
		)
			.then((response) => response.json())
			.then((objectEntry) =>
				setImageURL(getImageURL(objectEntry?.images))
			)
			.catch(() => setImageURL(undefined));
	}, [objectEntryId, restContextPath]);

	return (
		<ProductSticker
			className="product-editor-sticker"
			imageURL={imageURL}
			size="xxl"
		/>
	);
}
