/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import List from '@clayui/list';
import {dateUtils, sub} from 'frontend-js-web';
import React from 'react';

import {IAssetObjectEntry} from '../../../common/types/AssetType';

const AssetVersionsListItem = ({items}: {items: IAssetObjectEntry[]}) => {
	return (
		<>
			{items.map((item) => (
				<List.Item flex key={item.id}>
					<List.ItemField expand>
						<List.ItemTitle>
							{sub(Liferay.Language.get('version-x'), [
								item.systemProperties.version.number,
							])}
						</List.ItemTitle>

						<List.ItemText>
							{sub(Liferay.Language.get('modified-by-x'), [
								item.creator.name,
							])}
						</List.ItemText>

						<List.ItemText>
							{dateUtils.format(
								new Date(item.dateModified),
								'P p'
							)}
						</List.ItemText>

						<List.ItemText>
							<Label displayType="success">
								{item.status.label_i18n}
							</Label>
						</List.ItemText>
					</List.ItemField>
				</List.Item>
			))}
		</>
	);
};

export default AssetVersionsListItem;
