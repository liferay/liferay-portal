/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useId, useState} from 'react';

import {ConnectedSitesList, SitesSelector} from '../components/sites';
import ConnectedSiteService from '../services/ConnectedSiteService';
import {Site} from '../types';

const showErrorMessage = (message: string) => {
	openToast({
		message,
		type: 'danger',
	});
};

const showSuccessMessage = (message: string) => {
	openToast({
		message,
		type: 'success',
	});
};

export default function DesignLibraryConnectedSitesModal({
	externalReferenceCode,
	onChange,
}: {
	externalReferenceCode: string;
	onChange?: () => void;
}) {
	const [connectedSites, setConnectedSites] = useState<Site[]>([]);
	const listLabelId = useId();

	useEffect(() => {
		const fetchConnectedSitesToDesignLibrary = async () => {
			const {items} =
				await ConnectedSiteService.getConnectedSitesFromDesignLibrary(
					externalReferenceCode
				);

			if (items.length) {
				setConnectedSites(items);
			}
		};

		fetchConnectedSitesToDesignLibrary();
	}, [externalReferenceCode]);

	const onSiteConnected = (site: Site) => {
		setConnectedSites((currentConnectedSites) => {
			if (
				currentConnectedSites.some(
					(prevSite) => prevSite.id === site.id
				)
			) {
				return currentConnectedSites;
			}

			return [...currentConnectedSites, site];
		});
	};

	const onSiteDisconnected = (site: Site) => {
		setConnectedSites((currentConnectedSites) =>
			currentConnectedSites.filter(
				(currentSite) => currentSite.id !== site.id
			)
		);
	};

	const connectSite = async ({site}: {site?: Site}) => {
		if (site) {
			try {
				const data =
					await ConnectedSiteService.connectSiteToDesignLibrary(
						externalReferenceCode,
						site.externalReferenceCode
					);
				onSiteConnected(data);
				onChange?.();
				showSuccessMessage(
					sub(
						Liferay.Language.get(
							'site-x-was-successfully-connected-to-the-design-library'
						),
						`<strong>${Liferay.Util.escapeHTML(site.descriptiveName)}</strong>`
					)
				);
			}
			catch (error: any) {
				showErrorMessage(
					error?.title ??
						error?.message ??
						Liferay.Language.get(
							'unable-to-connect-site-to-design-library'
						)
				);
			}
		}
	};

	const disconnectSite = async function (site: Site) {
		try {
			await ConnectedSiteService.disconnectSiteFromDesignLibrary(
				externalReferenceCode,
				site.externalReferenceCode
			);
			onSiteDisconnected?.(site);
			onChange?.();
			showSuccessMessage(
				sub(
					Liferay.Language.get(
						'site-x-was-successfully-disconnected-from-the-design-library'
					),
					`<strong>${Liferay.Util.escapeHTML(site.descriptiveName)}</strong>`
				)
			);
		}
		catch (error: any) {
			showErrorMessage(
				error?.title ??
					error?.message ??
					Liferay.Language.get(
						'unable-to-disconnect-site-from-design-library'
					)
			);
		}
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('connected-sites')}
			</ClayModal.Header>

			<ClayModal.Item>
				<SitesSelector
					connectSite={connectSite}
					connectedSites={connectedSites}
				/>
			</ClayModal.Item>

			<ClayModal.Body>
				<ConnectedSitesList
					connectedSites={connectedSites}
					disconnectSite={disconnectSite}
					listLabelId={listLabelId}
				/>
			</ClayModal.Body>
		</>
	);
}
