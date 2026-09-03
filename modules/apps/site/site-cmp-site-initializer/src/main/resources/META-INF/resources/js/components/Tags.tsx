/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-nameentifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AssetTags, IAssetObjectEntry} from '@liferay/site-cms-site-initializer';
import React, {useEffect, useState} from 'react';

import './Tags.scss';

const Tags = ({
	cmsGroupId,
	hasUpdatePermission,
	objectEntryKeywords,
}: {
	cmsGroupId: number;
	hasUpdatePermission: boolean;
	objectEntryKeywords: string[];
}) => {
	const [formId, setFormId] = useState<string | undefined>();
	const [keywords, setKeywords] = useState<string[]>(objectEntryKeywords);

	useEffect(() => {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		if (form) {
			setFormId(form.id);
		}
	}, []);

	return (
		<div className="lfr-cmp__tags-container">
			<AssetTags
				cmsGroupId={cmsGroupId}
				collapsable={false}
				hasUpdatePermission={hasUpdatePermission}
				inputSize="regular"
				key={keywords.join(',') || 'tags'}
				objectEntry={
					{
						keywords,
					} as IAssetObjectEntry
				}
				updateObjectEntry={async ({
					keywords,
				}: Partial<IAssetObjectEntry>): Promise<void> => {
					setKeywords(keywords ? [...keywords] : []);
				}}
			/>

			<input
				form={formId}
				name="assetTagNames"
				type="hidden"
				value={keywords.join(',')}
			/>
		</div>
	);
};

export default Tags;
