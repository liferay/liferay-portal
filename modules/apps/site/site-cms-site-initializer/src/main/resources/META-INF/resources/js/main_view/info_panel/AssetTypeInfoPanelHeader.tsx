/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import {AssetTypeInfoPanelContext, IAssetTypeInfoPanelContext} from './context';
import {ASSET_TYPE} from './util/constants';

const renderTitle = ({
	objectEntries,
	title,
	title_i18n,
	type,
}: IAssetTypeInfoPanelContext) => {
	if (!objectEntries?.length) {
		return <>{Liferay.Language.get('no-assets-selected')}</>;
	}
	else if (objectEntries?.length > 1) {
		return (
			<>
				{sub(
					Liferay.Language.get('x-assets-selected'),
					objectEntries.length
				)}
			</>
		);
	}
	else if (
		type === ASSET_TYPE.FILES ||
		type === ASSET_TYPE.CONTENTS ||
		type === ASSET_TYPE.FOLDER
	) {
		return (
			<>
				{!title_i18n
					? title
					: title_i18n[
							Liferay.ThemeDisplay.getLanguageId() as keyof typeof title_i18n
						] || title}
			</>
		);
	}

	return null;
};

const AssetTypeInfoPanelHeader = () => {
	const context = useContext(AssetTypeInfoPanelContext);

	return (
		<SidePanel.Header>
			<SidePanel.Title>
				<span className="text-nowrap">
					{context.objectEntries?.length === 1 && (
						<ClayIcon
							className={classnames('asset-icon', {
								'asset-icon-files':
									context.type === ASSET_TYPE.FILES,
							})}
							symbol={context.icon || ''}
						></ClayIcon>
					)}

					<span className="text-truncate-inline">
						<h3 className="asset-title inline-item-after text-truncate">
							{renderTitle(context)}
						</h3>
					</span>
				</span>
			</SidePanel.Title>
		</SidePanel.Header>
	);
};

export default AssetTypeInfoPanelHeader;
