/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import {dateUtils, sub} from 'frontend-js-web';
import React, {useCallback, useContext} from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../context';
import {ASSET_TYPE} from '../util/constants';

const getAssetLanguages = (title_i18n: {[key: string]: string} = {}) => {
	const assetLanguages = Object.keys(title_i18n);

	if (assetLanguages.length) {
		return Object.keys(title_i18n).map((key) => key.replace('_', '-'));
	}

	return [];
};

const formatDate = (date: string) => {
	return dateUtils.format(new Date(date), 'P p');
};

const AssetMetadata = () => {
	const {
		objectEntries = [],
		type,
		title_i18n = {},
	}: IAssetTypeInfoPanelContext = useContext(AssetTypeInfoPanelContext);

	const [{embedded: objectEntry}]: ISearchAssetObjectEntry[] = objectEntries;

	const copyText = useCallback(
		(event: any) => {
			event.preventDefault();

			if (window?.navigator?.clipboard) {
				window.navigator.clipboard.writeText(
					objectEntry.file?.link?.href as string
				);
			}
		},
		[objectEntry]
	);

	const assetLanguages = getAssetLanguages(title_i18n);

	return (
		<ClayPanel
			className="asset-metadata"
			collapsable={false}
			displayTitle={
				<ClayPanel.Header className="border-bottom">
					<ClayPanel.Title className="panel-title text-secondary">
						{Liferay.Language.get('metadata')}
					</ClayPanel.Title>
				</ClayPanel.Header>
			}
			displayType="unstyled"
			showCollapseIcon={false}
		>
			<ClayPanel.Body>
				{type === ASSET_TYPE.FILES && (
					<div className="asset-metadata-section mt-0">
						<p className="asset-metadata-section-title">
							{Liferay.Language.get('url')}
						</p>

						<ClayInput.Group className="mt-1">
							<ClayInput.GroupItem prepend>
								<ClayInput
									disabled={true}
									placeholder={objectEntry.file?.link?.href}
									type="text"
								/>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem append shrink>
								<ClayButtonWithIcon
									data-clipboard-text={
										objectEntry.file?.link?.href
									}
									displayType="secondary"
									onClick={copyText}
									symbol="copy"
								></ClayButtonWithIcon>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</div>
				)}

				<div className="asset-metadata-section">
					<p className="asset-metadata-section-title">
						{Liferay.Language.get('author')}
					</p>

					<p>{objectEntry?.creator?.name}</p>
				</div>

				<div className="asset-metadata-section">
					<p className="asset-metadata-section-title">
						{Liferay.Language.get('created')}
					</p>

					<p>
						{sub(Liferay.Language.get('x-by-x'), [
							formatDate(objectEntry.dateCreated as string),
							objectEntry.creator?.name,
						])}
					</p>
				</div>

				<div className="asset-metadata-section">
					<p className="asset-metadata-section-title">
						{Liferay.Language.get('modified')}
					</p>

					<p>{formatDate(objectEntry.dateModified as string)}</p>
				</div>

				{type !== ASSET_TYPE.FOLDER && (
					<>
						<div className="asset-metadata-section">
							<p className="asset-metadata-section-title">
								{Liferay.Language.get('expiration-date')}
							</p>

							<p>
								{objectEntry?.expirationDate
									? formatDate(objectEntry?.expirationDate)
									: Liferay.Language.get('never-expire')}
							</p>
						</div>

						<div className="asset-metadata-section">
							<p className="asset-metadata-section-title">
								{Liferay.Language.get('review-date')}
							</p>

							<p>
								{objectEntry?.reviewDate
									? formatDate(objectEntry?.reviewDate)
									: Liferay.Language.get('never-review')}
							</p>
						</div>

						{assetLanguages.length ? (
							<div className="asset-metadata-section">
								<p className="asset-metadata-section-title">
									{Liferay.Language.get(
										'languages-translated-into'
									)}
								</p>

								{assetLanguages.map((locale, index) => (
									<div className="d-flex mt-1" key={index}>
										<ClayIcon
											className="mr-2 mt-1"
											symbol={locale.toLowerCase()}
										/>

										<span>{locale}</span>
									</div>
								))}
							</div>
						) : null}
					</>
				)}
			</ClayPanel.Body>
		</ClayPanel>
	);
};

export default AssetMetadata;
