/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import List from '@clayui/list';
import {dateUtils, navigate, sub} from 'frontend-js-web';
import React from 'react';

import StatusLabel from '../../../common/components/StatusLabel';
import {IAssetFile, IAssetObjectEntry} from '../../../common/types/AssetType';
import {VERSION_ACTIONS} from '../tab_content';
import {L_CONTENTS, L_FILES} from '../util/constants';

const AssetVersionsListItem = ({
	file,
	getAssetVersions,
	items,
}: {
	file?: IAssetFile;
	getAssetVersions: () => Promise<void>;
	items: IAssetObjectEntry[];
}) => {
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
							<StatusLabel label={item.status.label} />
						</List.ItemText>
					</List.ItemField>

					<ClayDropdown
						closeOnClick
						items={items}
						trigger={
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('actions')}
								borderless
								displayType="secondary"
								size="sm"
								symbol="ellipsis-v"
							/>
						}
					>
						{Object.entries(item.actions).map(([key]) =>
							VERSION_ACTIONS[key] ? (
								<ClayDropdown.Item
									key={key}
									onClick={async (event) => {
										await VERSION_ACTIONS[key]?.action(
											event,
											item,
											getAssetVersions
										);
									}}
								>
									<ClayIcon
										className="mr-2"
										symbol={VERSION_ACTIONS[key]?.icon}
									/>

									{VERSION_ACTIONS[key]?.name}
								</ClayDropdown.Item>
							) : null
						)}

						{file ? (
							<ClayDropdown.Item
								onClick={() => {
									if (file.link?.href) {
										navigate(file.link.href);
									}
								}}
							>
								<ClayIcon className="mr-2" symbol="download" />

								{Liferay.Language.get('download')}
							</ClayDropdown.Item>
						) : null}

						<ClayDropdown.Item
							onClick={(event) => {
								VERSION_ACTIONS[
									file === undefined ? L_CONTENTS : L_FILES
								].action(event, item, file);
							}}
						>
							<ClayIcon className="mr-2" symbol="view" />

							{Liferay.Language.get('view')}
						</ClayDropdown.Item>
					</ClayDropdown>
				</List.Item>
			))}
		</>
	);
};

export default AssetVersionsListItem;
