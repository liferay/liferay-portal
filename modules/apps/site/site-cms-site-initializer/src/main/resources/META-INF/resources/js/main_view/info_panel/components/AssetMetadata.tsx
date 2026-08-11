/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import {ClayButtonWithIcon} from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayPanel from '@clayui/panel';
import {sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
} from 'react';

import SpaceService from '../../../common/services/SpaceService';
import {Space} from '../../../common/types/Space';
import dateFormat from '../../../common/utils/dateFormat';
import {isReviewDateOverdue} from '../../../common/utils/reviewDateStatus';
import {displayErrorToast} from '../../../common/utils/toastUtil';
import {SpaceSticker} from '../../../index';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../context';
import ObjectEntryService from '../services/ObjectEntryService';
import {getAssetLanguages} from '../util';
import {ASSET_TYPE} from '../util/constants';

const AssetMetadata = () => {
	const DATE_PATTERN = useMemo(
		() => ({
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit',
			month: '2-digit',
			timeZone: Liferay.ThemeDisplay.getTimeZone(),
			year: 'numeric',
		}),
		[]
	);

	const {actions, asset, breadcrumbProps, type}: IAssetTypeInfoPanelContext =
		useContext(AssetTypeInfoPanelContext);

	const copyText = useCallback(
		(event: any) => {
			event.preventDefault();

			if (window?.navigator?.clipboard) {
				window.navigator.clipboard.writeText(
					asset.file?.link?.href as string
				);
			}
		},
		[asset]
	);

	const assetLanguages = useMemo(
		() => getAssetLanguages(asset.title_i18n),
		[asset.title_i18n]
	);

	const [numberOfAssets, setNumberOfAssets] = useState<number>(0);
	const [space, setSpace] = useState<Space>({} as Space);

	useEffect(() => {
		const getNumberOfAssets = async () => {
			if (actions?.get?.href && type === ASSET_TYPE.FOLDER) {
				const {data} = await ObjectEntryService.getObjectEntry(
					actions.get.href,
					'numberOfObjectEntries,numberOfObjectEntryFolders'
				);

				if (
					data?.numberOfObjectEntries !== null &&
					data?.numberOfObjectEntries !== undefined &&
					data?.numberOfObjectEntryFolders !== null &&
					data?.numberOfObjectEntryFolders !== undefined
				) {
					setNumberOfAssets(
						data.numberOfObjectEntries +
							data.numberOfObjectEntryFolders
					);
				}
			}
		};

		const getSpace = async () => {
			const externalReferenceCode =
				asset?.systemProperties?.scope?.externalReferenceCode ||
				asset.scope?.externalReferenceCode ||
				'';

			const space = await SpaceService.getSpace(externalReferenceCode);

			if (space) {
				setSpace(space);
			}
		};

		try {
			getNumberOfAssets();

			getSpace();
		}
		catch (_error) {
			displayErrorToast();
		}
	}, [actions, asset, type]);

	const reviewOverdue = isReviewDateOverdue(asset?.reviewDate as string);

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
					<>
						<div className="asset-metadata-section mt-0">
							<p className="d-block font-weight-bold mb-0">
								{Liferay.Language.get('url')}
							</p>

							<ClayInput.Group className="mb-3 mt-1">
								<ClayInput.GroupItem prepend>
									<ClayInput
										disabled={true}
										placeholder={asset.file?.link?.href}
										type="text"
									/>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem append shrink>
									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'copy'
										)}
										data-clipboard-text={
											asset.file?.link?.href
										}
										displayType="secondary"
										onClick={copyText}
										symbol="copy"
									/>
								</ClayInput.GroupItem>
							</ClayInput.Group>
						</div>

						{asset?.file?.extension && (
							<div className="asset-metadata-section mt-3">
								<p className="d-block font-weight-bold mb-0">
									{Liferay.Language.get('extension[file]')}
								</p>

								<p className="d-block">
									{asset.file?.extension}
								</p>
							</div>
						)}

						{asset?.file?.size && (
							<div className="asset-metadata-section mt-3">
								<p className="d-block font-weight-bold mb-0">
									{Liferay.Language.get('size')}
								</p>

								<p className="d-block">{asset.file?.size}</p>
							</div>
						)}

						{asset?.file?.metadata?.resolution && (
							<div className="asset-metadata-section mt-3">
								<p className="d-block font-weight-bold mb-0">
									{Liferay.Language.get('resolution')}
								</p>

								<p className="mb-0">
									{asset.file?.metadata?.resolution}
								</p>
							</div>
						)}

						{asset?.file?.metadata?.aspectRatio && (
							<div className="asset-metadata-section mt-3">
								<p className="d-block font-weight-bold mb-0">
									{Liferay.Language.get('aspect-ratio')}
								</p>

								<p className="d-block">
									{asset.file?.metadata?.aspectRatio}
								</p>
							</div>
						)}
					</>
				)}

				{type === ASSET_TYPE.FOLDER && (
					<div
						className="asset-metadata-section mt-3"
						data-testid="number-of-assets"
					>
						<p className="d-block font-weight-bold mb-0">
							{Liferay.Language.get('number-of-assets')}
						</p>

						<p className="d-block">{numberOfAssets}</p>
					</div>
				)}

				<div className="asset-metadata-section">
					<p className="d-block font-weight-bold mb-2">
						{Liferay.Language.get('location')}
					</p>

					<div className="d-flex space-breadcrumb">
						<SpaceSticker
							className="mr-2"
							displayType={space.settings?.logoColor}
							hideName={true}
							name={Liferay.Language.get(`${space.name}`)}
							size="sm"
						/>

						<ClayBreadcrumb
							className="p-0"
							items={
								breadcrumbProps?.breadcrumbItems?.map(
									(breadcrumbItem: any) => ({
										active: false,
										label: Liferay.Language.get(
											breadcrumbItem.label
										),
									})
								) ?? []
							}
						/>
					</div>
				</div>

				<div className="asset-metadata-section mt-3">
					<p className="d-block font-weight-bold mb-0 mt-3">
						{Liferay.Language.get('author')}
					</p>

					<p className="d-block">{asset?.creator?.name}</p>
				</div>

				<div className="asset-metadata-section mt-3">
					<p className="d-block font-weight-bold mb-0">
						{Liferay.Language.get('created')}
					</p>

					<p className="d-block">
						{sub(Liferay.Language.get('x-by-x'), [
							dateFormat(
								DATE_PATTERN,
								asset.dateCreated as string
							),
							asset.creator?.name,
						])}
					</p>
				</div>

				<div className="asset-metadata-section mt-3">
					<p className="d-block font-weight-bold mb-0">
						{Liferay.Language.get('modified')}
					</p>

					<p className="d-block">
						{dateFormat(DATE_PATTERN, asset.dateModified as string)}
					</p>
				</div>

				{type !== ASSET_TYPE.FOLDER && (
					<>
						{asset?.displayDate && (
							<div className="asset-metadata-section mt-3">
								<p className="d-block font-weight-bold mb-0">
									{Liferay.Language.get('display-date')}
								</p>

								<p className="d-block">
									{dateFormat(
										DATE_PATTERN,
										asset?.displayDate as string
									)}
								</p>
							</div>
						)}

						<div className="asset-metadata-section mt-3">
							<p className="d-block font-weight-bold mb-0">
								{Liferay.Language.get('expiration-date')}
							</p>

							<p className="d-block">
								{asset?.expirationDate
									? dateFormat(
											DATE_PATTERN,
											asset?.expirationDate as string
										)
									: Liferay.Language.get('never-expire')}
							</p>
						</div>

						<div className="asset-metadata-section mt-3">
							<p className="d-block font-weight-bold mb-0">
								{Liferay.Language.get('review-date')}
							</p>

							<p
								className={
									reviewOverdue
										? 'd-block text-warning'
										: 'd-block'
								}
							>
								{asset?.reviewDate
									? dateFormat(
											DATE_PATTERN,
											asset?.reviewDate as string
										)
									: Liferay.Language.get('never-review')}
							</p>
						</div>

						{assetLanguages.length ? (
							<div className="asset-metadata-section mt-3">
								<p className="d-block font-weight-bold mb-0">
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
