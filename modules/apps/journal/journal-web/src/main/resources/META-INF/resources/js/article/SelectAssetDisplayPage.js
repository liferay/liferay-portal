/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import React, {useEffect, useState} from 'react';

import AssetDisplayPageSelector from './AssetDisplayPageSelector';
import PreviewButton from './PreviewButton';

function getLabel(displayPageType) {
	if (displayPageType === DISPLAY_PAGE_TYPE.default) {
		return Liferay.Language.get('default');
	}
	else if (displayPageType === DISPLAY_PAGE_TYPE.specific) {
		return Liferay.Language.get('specific');
	}
	else {
		return Liferay.Language.get('none');
	}
}

const DISPLAY_PAGE_TYPE = {
	default: 1,
	none: 0,
	specific: 2,
};

export default function SelectAssetDisplayPage({
	assetDisplayPageId,
	assetDisplayPageType,
	defaultDisplayPageName,
	layoutUuid,
	newArticle,
	portletNamespace: namespace,
	saveAsDraftURL,
	selectAssetDisplayPageEventName,
	selectAssetDisplayPageURL,
	specificAssetDisplayPageName,
}) {
	const [selectedDisplayPageType, setSelectedDisplayPageType] =
		useState(assetDisplayPageType);

	const [assetDisplayPageSelected, setAssetDisplayPageSelected] = useState(
		() => {
			if (assetDisplayPageType === DISPLAY_PAGE_TYPE.specific) {
				return {
					assetDisplayPageId,
					layoutUuid,
					name: specificAssetDisplayPageName,
				};
			}

			return null;
		}
	);

	const ITEMS = [
		{
			label: getLabel(DISPLAY_PAGE_TYPE.default),
			value: DISPLAY_PAGE_TYPE.default,
		},
		{
			label: getLabel(DISPLAY_PAGE_TYPE.specific),
			value: DISPLAY_PAGE_TYPE.specific,
		},
		{
			label: getLabel(DISPLAY_PAGE_TYPE.none),
			value: DISPLAY_PAGE_TYPE.none,
		},
	];

	useEffect(() => {
		Liferay.component(
			`${namespace}SelectAssetDisplayPage`,
			{},
			{destroyOnNavigate: true}
		);

		return () => {
			Liferay.destroyComponent(`${namespace}SelectAssetDisplayPage`);
		};
	}, [namespace]);

	return (
		<>
			<ClayForm.Group>
				<ClaySelectWithOption
					aria-label={Liferay.Language.get(
						'select-display-page-type'
					)}
					id={`${namespace}selectDisplayPageType`}
					onChange={(event) =>
						setSelectedDisplayPageType(
							parseInt(event.target.value, 10)
						)
					}
					options={ITEMS}
					value={selectedDisplayPageType}
				/>
			</ClayForm.Group>

			<ClayInput
				name={`${namespace}layoutUuid`}
				type="hidden"
				value={assetDisplayPageSelected?.layoutUuid ?? ''}
			/>

			<ClayInput
				name={`${namespace}assetDisplayPageId`}
				type="hidden"
				value={assetDisplayPageSelected?.assetDisplayPageId ?? ''}
			/>

			<ClayInput
				name={`${namespace}displayPageType`}
				type="hidden"
				value={selectedDisplayPageType ?? ''}
			/>

			{selectedDisplayPageType === DISPLAY_PAGE_TYPE.default && (
				<ClayForm.Group className="mb-2">
					<ClayInput
						aria-label={Liferay.Language.get('display-page-name')}
						readOnly
						value={
							defaultDisplayPageName ||
							Liferay.Language.get(
								'no-default-display-page-template'
							)
						}
					/>
				</ClayForm.Group>
			)}

			{selectedDisplayPageType === DISPLAY_PAGE_TYPE.specific && (
				<AssetDisplayPageSelector
					assetDisplayPageSelected={assetDisplayPageSelected}
					disabled={false}
					namespace={namespace}
					selectAssetDisplayPageEventName={
						selectAssetDisplayPageEventName
					}
					selectAssetDisplayPageURL={selectAssetDisplayPageURL}
					setAssetDisplayPageSelected={setAssetDisplayPageSelected}
				/>
			)}

			{selectedDisplayPageType !== DISPLAY_PAGE_TYPE.none && (
				<PreviewButton
					disabled={
						(selectedDisplayPageType ===
							DISPLAY_PAGE_TYPE.specific &&
							!assetDisplayPageSelected) ||
						(selectedDisplayPageType ===
							DISPLAY_PAGE_TYPE.default &&
							!defaultDisplayPageName)
					}
					getPreviewURL={({previewURL}) => previewURL}
					namespace={namespace}
					newArticle={newArticle}
					saveAsDraftURL={saveAsDraftURL}
				/>
			)}
		</>
	);
}
