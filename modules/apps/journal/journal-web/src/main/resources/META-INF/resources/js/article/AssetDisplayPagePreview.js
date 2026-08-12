/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {openSelectionModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

import AssetDisplayPageSelector from './AssetDisplayPageSelector';
import PreviewButton from './PreviewButton';

export default function AssetDisplayPagePreview({
	newArticle,
	portletNamespace: namespace,
	previewURL,
	saveAsDraftURL,
	selectAssetDisplayPageEventName,
	selectAssetDisplayPageURL,
	selectSiteEventName,
	siteItemSelectorURL,
	sites,
	sitesCount,
}) {
	const [selectedSite, setSelectedSite] = useState();
	const [active, setActive] = useState(false);
	const [assetDisplayPageSelected, setAssetDisplayPageSelected] = useState();

	const siteInputId = `${namespace}siteInput`;
	const siteLabelId = `${namespace}siteLabel`;
	const siteValueId = `${namespace}siteValue`;

	const items = useMemo(() => {
		return [
			{groupId: 0, name: `- ${Liferay.Language.get('not-selected')} -`},
			...sites,
		].map((site) => ({
			label: site.name,
			onClick: () => {
				setActive(false);
				setSelectedSite({groupId: site.groupId, name: site.name});

				if (site.groupId !== selectedSite?.groupId) {
					setAssetDisplayPageSelected(null);
				}
			},
		}));
	}, [sites, selectedSite?.groupId]);

	return (
		<>
			<label
				className="control-label"
				htmlFor={siteInputId}
				id={siteLabelId}
			>
				{Liferay.Language.get('site')}
			</label>
			<ClayDropDown
				active={active}
				onActiveChange={setActive}
				trigger={
					<ClayButton
						aria-labelledby={`${siteLabelId} ${siteValueId}`}
						className="bg-light form-control-select text-left w-100"
						displayType="secondary"
						id={siteInputId}
						type="button"
					>
						<span id={siteValueId}>
							{selectedSite?.name ||
								`- ${Liferay.Language.get('not-selected')} -`}
						</span>
					</ClayButton>
				}
			>
				{items.map((item) => (
					<ClayDropDown.Item key={item.label} onClick={item.onClick}>
						{item.label}
					</ClayDropDown.Item>
				))}

				{sitesCount > sites.length && (
					<>
						<ClayDropDown.Caption>
							{sub(
								Liferay.Language.get('showing-x-of-x-items'),
								sites.length,
								sitesCount
							)}
						</ClayDropDown.Caption>

						<div className="d-flex w-100">
							<ClayButton
								className="flex-grow-1 mx-3"
								displayType="secondary"
								onClick={() => {
									openSelectionModal({
										containerProps: {
											className: 'cadmin',
										},
										onSelect(selectedItem) {
											setSelectedSite({
												groupId: selectedItem.groupid,
												name: selectedItem.groupdescriptivename,
											});
										},
										selectEventName: selectSiteEventName,
										title: Liferay.Language.get(
											'select-site'
										),
										url: siteItemSelectorURL,
									});
								}}
								size="sm"
								type="button"
							>
								{Liferay.Language.get('more')}
							</ClayButton>
						</div>
					</>
				)}
			</ClayDropDown>

			<AssetDisplayPageSelector
				assetDisplayPageSelected={assetDisplayPageSelected}
				disabled={!selectedSite}
				namespace={namespace}
				selectAssetDisplayPageEventName={
					selectAssetDisplayPageEventName
				}
				selectAssetDisplayPageURL={selectAssetDisplayPageURL}
				selectedSite={selectedSite}
				setAssetDisplayPageSelected={setAssetDisplayPageSelected}
			/>

			<PreviewButton
				disabled={!assetDisplayPageSelected}
				getPreviewURL={({classPK, version}) => {
					const url = new URL(previewURL);

					url.searchParams.set('classPK', classPK);
					url.searchParams.set(
						'selPlid',
						assetDisplayPageSelected?.plid
					);
					url.searchParams.set('version', version);

					return url;
				}}
				namespace={namespace}
				newArticle={newArticle}
				saveAsDraftURL={saveAsDraftURL}
			/>
		</>
	);
}
