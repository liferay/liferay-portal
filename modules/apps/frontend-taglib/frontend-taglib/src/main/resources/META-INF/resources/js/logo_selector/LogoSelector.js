/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

export default function LogoSelector({
	defaultLogoURL,
	description,
	disabled,
	label,
	logoName: initialLogoName,
	logoURL: initialLogoURL,
	portletNamespace,
	selectLogoURL,
}) {
	const [changingLogo, setChangingLogo] = useState(false);
	const [values, setValues] = useState({
		deleteLogo: false,
		fileEntryId: '',
		logoName:
			initialLogoURL === defaultLogoURL
				? Liferay.Language.get('default')
				: initialLogoName ||
					sub(Liferay.Language.get('custom-x'), label),
		logoURL: initialLogoURL,
	});

	const onChangeLogo = () => {
		Liferay.Util.openModal({
			id: `${portletNamespace}changeLogo`,
			iframeBodyCssClass: 'dialog-with-footer',
			size: 'full-screen',
			title: sub(Liferay.Language.get('upload-x'), label),
			url: selectLogoURL.replace(
				escape('[$CURRENT_LOGO_URL$]'),
				escape(logoURL)
			),
		});

		setChangingLogo(true);
	};

	const onClearLogo = () => {
		setValues({
			deleteLogo: true,
			fileEntryId: '',
			logoName: Liferay.Language.get('default'),
			logoURL: defaultLogoURL,
		});
	};

	useEffect(() => {
		const handleChangeLogo = ({
			fileEntryId,
			previewURL,
			tempImageFileName,
		}) => {
			if (changingLogo) {
				setValues({
					deleteLogo: false,
					fileEntryId,
					logoName: tempImageFileName,
					logoURL: previewURL,
				});

				setChangingLogo(false);
			}
		};

		Liferay.on('changeLogo', handleChangeLogo);

		return () => {
			Liferay.detach('changeLogo', handleChangeLogo);
		};
	}, [changingLogo]);

	const {deleteLogo, fileEntryId, logoName, logoURL} = values;

	return (
		<>
			<input
				name={`${portletNamespace}deleteLogo`}
				type="hidden"
				value={deleteLogo}
			/>

			<input
				name={`${portletNamespace}fileEntryId`}
				type="hidden"
				value={fileEntryId}
			/>

			{description && (
				<p
					className="text-secondary"
					id={`${portletNamespace}description`}
				>
					{description}
				</p>
			)}

			{logoURL ? (
				<img
					alt={sub(Liferay.Language.get('current-x'), label)}
					className="logo-selector-img mb-3"
					src={logoURL}
				/>
			) : (
				<p className="text-secondary">{Liferay.Language.get('none')}</p>
			)}

			<ClayForm.Group>
				<label htmlFor={`${portletNamespace}logoName`}>{label}</label>

				<div className="d-flex">
					<ClayInput
						aria-describedby={
							description
								? `${portletNamespace}description`
								: null
						}
						className="mr-2"
						id={`${portletNamespace}logoName`}
						readOnly
						value={logoName || Liferay.Language.get('custom-x')}
					/>

					<ClayButtonWithIcon
						aria-label={sub(
							Liferay.Language.get('change-x'),
							label
						)}
						className="flex-shrink-0 mr-2"
						disabled={disabled}
						displayType="secondary"
						onClick={onChangeLogo}
						symbol="change"
						title={sub(Liferay.Language.get('change-x'), label)}
					/>

					<ClayButtonWithIcon
						aria-label={sub(Liferay.Language.get('clear-x'), label)}
						className="flex-shrink-0"
						disabled={logoURL === defaultLogoURL || disabled}
						displayType="secondary"
						onClick={onClearLogo}
						symbol="times-circle"
						title={sub(Liferay.Language.get('clear-x'), label)}
					/>
				</div>
			</ClayForm.Group>
		</>
	);
}
