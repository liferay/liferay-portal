/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {openSelectionModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

const MasterLayoutInput = ({masterLayout, onClick, portletNamespace}) => (
	<ClayForm.Group className="c-mb-0 flex-grow-1">
		{masterLayout.scopeLabel ? (
			<div
				aria-describedby={`${portletNamespace}masterLayoutScope`}
				aria-labelledby={`${portletNamespace}masterLayoutLabel`}
				aria-readonly="true"
				className="align-items-center bg-white form-control form-control-tag-group"
				id={`${portletNamespace}masterLayout`}
				onClick={onClick}
				role="textbox"
				tabIndex={0}
			>
				<span className="text-truncate">{masterLayout.name}</span>

				<ClayLabel
					aria-label={sub(
						Liferay.Language.get('master-from-x-design-library'),
						masterLayout.scopeLabel
					)}
					className="c-ml-2"
					displayType="content-5"
					id={`${portletNamespace}masterLayoutScope`}
					inverse
					large
					withClose={false}
				>
					<ClayLabel.ItemBefore>
						<ClaySticker className="bg-white rounded" size="xs">
							<ClayIcon symbol="books-brush" />
						</ClaySticker>
					</ClayLabel.ItemBefore>

					<ClayLabel.ItemExpand>
						{masterLayout.scopeLabel}
					</ClayLabel.ItemExpand>
				</ClayLabel>
			</div>
		) : (
			<ClayInput
				id={`${portletNamespace}masterLayout`}
				onClick={onClick}
				readOnly
				value={masterLayout.name}
			/>
		)}
	</ClayForm.Group>
);

export default function MasterLayoutConfiguration({
	changeMasterLayoutURL,
	editMasterLayoutURL,
	isReadOnly,
	masterLayoutName,
	masterLayoutPageTemplateEntryERC,
	masterLayoutPageTemplateEntryScopeERC,
	masterLayoutPageTemplateEntryScopeLabel,
	portletNamespace,
}) {
	const [masterLayout, setMasterLayout] = useState({
		erc: masterLayoutPageTemplateEntryERC,
		name: masterLayoutName,
		scopeERC: masterLayoutPageTemplateEntryScopeERC,
		scopeLabel: masterLayoutPageTemplateEntryScopeLabel,
	});

	const handleChangeMasterButtonClick = () => {
		if (isReadOnly) {
			return;
		}

		openSelectionModal({
			iframeBodyCssClass: '',
			onSelect(selectedItem) {
				if (selectedItem) {
					const itemValue = JSON.parse(selectedItem.value);

					setMasterLayout({
						erc: itemValue.masterLayoutPageTemplateEntryERC,
						name: itemValue.name,
						scopeERC:
							itemValue.masterLayoutPageTemplateEntryScopeERC,
						scopeLabel:
							itemValue.masterLayoutPageTemplateEntryScopeLabel,
					});
				}
			},
			selectEventName: `${portletNamespace}selectMasterLayout`,
			title: Liferay.Language.get('select-master'),
			url: changeMasterLayoutURL,
		});
	};

	useEffect(() => {
		const customCSS = document.getElementById(
			`${portletNamespace}customCSS`
		);

		if (customCSS) {
			if (!masterLayout.erc) {
				customCSS.classList.remove('hide');
			}
			else {
				customCSS.classList.add('hide');
			}
		}

		const themeContainer = document.getElementById(
			`${portletNamespace}themeContainer`
		);

		if (themeContainer) {
			const sheet = themeContainer.closest('.sheet');

			if (!masterLayout.erc) {
				sheet.classList.remove('hide');

				sheet.removeAttribute('aria-hidden');
			}
			else {
				sheet.classList.add('hide');

				sheet.setAttribute('aria-hidden', 'true');
			}
		}
	}, [masterLayout.erc, portletNamespace]);

	return (
		<>
			<input
				name={`${portletNamespace}masterLayoutPageTemplateEntryERC`}
				type="hidden"
				value={masterLayout.erc}
			/>

			<input
				name={`${portletNamespace}masterLayoutPageTemplateEntryScopeERC`}
				type="hidden"
				value={masterLayout.scopeERC}
			/>

			<label
				htmlFor={`${portletNamespace}masterLayout`}
				id={`${portletNamespace}masterLayoutLabel`}
			>
				{Liferay.Language.get('master')}
			</label>

			{editMasterLayoutURL && masterLayout.erc ? (
				<div className="d-flex">
					<MasterLayoutInput
						masterLayout={masterLayout}
						onClick={handleChangeMasterButtonClick}
						portletNamespace={portletNamespace}
					/>

					<ClayLink
						aria-label={Liferay.Language.get('edit-master')}
						button={{monospaced: true}}
						className="c-ml-2"
						disabled={isReadOnly}
						displayType="secondary"
						href={editMasterLayoutURL}
					>
						<ClayIcon symbol="pencil" />
					</ClayLink>

					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('change-master')}
						className="c-ml-2"
						disabled={isReadOnly}
						displayType="secondary"
						onClick={handleChangeMasterButtonClick}
						symbol="change"
					/>
				</div>
			) : (
				<div className="d-flex">
					<MasterLayoutInput
						masterLayout={masterLayout}
						onClick={handleChangeMasterButtonClick}
						portletNamespace={portletNamespace}
					/>

					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('change-master')}
						className="c-ml-2"
						disabled={isReadOnly}
						displayType="secondary"
						onClick={handleChangeMasterButtonClick}
						symbol="change"
					/>
				</div>
			)}
		</>
	);
}
