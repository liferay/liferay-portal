/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import {openSelectionModal} from 'frontend-js-components-web';
import React, {useState} from 'react';

export default function SelectCollection({
	assetListEntryId: initialAssetListEntryId,
	clearButtonEnabled: initialClearButtonEnabled,
	defaultTitle,
	infoListProviderKey: initialInfoListProviderKey,
	portletNamespace,
	selectEventName,
	title: initialTitle,
	url,
	addCollectionUrl
}) {
	const onChangeCollectionButtonClick = () => {
		openSelectionModal({
			iframeBodyCssClass: '',
			onSelect(selectedItem) {
				if (selectedItem && selectedItem.value) {
					const itemValue = JSON.parse(selectedItem.value);
					const nextValues = {};

					if (
						selectedItem.returnType ===
						'com.liferay.item.selector.criteria.InfoListItemSelectorReturnType'
					) {
						nextValues.assetListEntryId = itemValue.classPK;
						nextValues.infoListProviderKey = '';
					}
					else if (
						selectedItem.returnType ===
						'com.liferay.info.list.provider.item.selector.criterion.InfoListProviderItemSelectorReturnType'
					) {
						nextValues.assetListEntryId = 0;
						nextValues.infoListProviderKey = itemValue.key;
					}

					nextValues.title = itemValue.title;
					nextValues.clearButtonEnabled = true;
					setValues(nextValues);
				}
			},
			selectEventName,
			title: Liferay.Language.get('select-collection'),
			url: url.toString(),
		});
	};
	const onClearCollectionButtonClick = () => {
		setValues({
			assetListEntryId: 0,
			clearButtonEnabled: false,
			infoListProviderKey: '',
			title: defaultTitle,
		});
	};

	const onAddCollectionButtonClick = () => {
		openSelectionModal({
			iframeBodyCssClass: 'hide-control-menu',
			title: Liferay.Language.get('add-collection'),
			url: addCollectionUrl.toString()
		});
	}

	const [values, setValues] = useState({
		assetListEntryId: initialAssetListEntryId,
		clearButtonEnabled: initialClearButtonEnabled,
		infoListProviderKey: initialInfoListProviderKey,
		title: initialTitle,
	});

	return (
		<>
			<ClayInput
				name={`${portletNamespace}preferences--assetListEntryId--`}
				type="hidden"
				value={values.assetListEntryId}
			/>
			<ClayInput
				name={`${portletNamespace}preferences--infoListProviderKey--`}
				type="hidden"
				value={values.infoListProviderKey}
			/>

			<ClayForm.Group>
				<label htmlFor={`${portletNamespace}basicInputText`}>
					{Liferay.Language.get('collection')}
				</label>

				<div className="d-flex">
					<ClayInput
						className="c-mr-2"
						id={`${portletNamespace}basicInputText`}
						onClick={onChangeCollectionButtonClick}
						readOnly={true}
						value={values.title}
					/>

					<ClayButtonWithIcon
						aria-label={
							values.clearButtonEnabled
								? Liferay.Language.get('change-collection')
								: Liferay.Language.get('select-collection')
						}
						className="c-mr-2 flex-shrink-0"
						displayType="secondary"
						onClick={onChangeCollectionButtonClick}
						symbol={values.clearButtonEnabled ? 'change' : 'select'}
						title={
							values.clearButtonEnabled
								? Liferay.Language.get('change-collection')
								: Liferay.Language.get('select-collection')
						}
					/>

					{values.clearButtonEnabled ? (
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(
								'clear-collection'
							)}
							className="c-mr-2 flex-shrink-0"
							displayType="secondary"
							onClick={onClearCollectionButtonClick}
							symbol="times-circle"
							title={Liferay.Language.get('clear-collection')}
						/>
					) : null}

					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('add-collection')}
						className="flex-shrink-0"
						displayType="primary"
						onClick={onAddCollectionButtonClick}
						symbol="plus"
						title={Liferay.Language.get('add-collection')}
					/>

				</div>
			</ClayForm.Group>
		</>
	);
}
