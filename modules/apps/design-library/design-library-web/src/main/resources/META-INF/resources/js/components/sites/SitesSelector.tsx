/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import React, {useMemo, useState} from 'react';

import {Site} from '../../types';

export default function SitesSelector({
	connectSite,
	connectedSites,
}: {
	connectSite: ({
		setDisableConnectButton,
		setSite,
		site,
	}: {
		setDisableConnectButton: React.Dispatch<React.SetStateAction<boolean>>;
		setSite: React.Dispatch<React.SetStateAction<Site | undefined>>;
		site?: Site;
	}) => Promise<void>;
	connectedSites: Site[];
}) {
	const [site, setSite] = useState<Site>();
	const [disableConnectButton, setDisableConnectButton] =
		useState<boolean>(true);

	const apiURL = useMemo(() => {
		const url = new URL(
			'/o/headless-admin-site/v1.0/sites',
			location.origin
		);

		url.searchParams.set('active', 'true');

		connectedSites.forEach((connectedSite) =>
			url.searchParams.append(
				'excludedExternalReferenceCodes',
				connectedSite.externalReferenceCode
			)
		);

		return url.toString();
	}, [connectedSites]);

	return (
		<div className="p-4">
			<div className="align-items-end autofit-row c-gap-3">
				<div className="autofit-col autofit-col-expand">
					<label htmlFor="siteSelector">
						{Liferay.Language.get('site')}
					</label>

					<ItemSelector
						apiURL={apiURL}
						id="siteSelector"
						items={site ? [site] : []}
						key={apiURL}
						onItemsChange={(items: Site[]) => {
							const item = items[0];

							setSite(item);
							setDisableConnectButton(!item);
						}}
						placeholder={Liferay.Language.get('select-a-site')}
					>
						{(item: Site) => (
							<ItemSelector.Item
								key={item.id}
								textValue={Liferay.Util.escapeHTML(
									item.descriptiveName
								)}
							>
								{Liferay.Util.escapeHTML(item.descriptiveName)}
							</ItemSelector.Item>
						)}
					</ItemSelector>
				</div>

				<div className="autofit-col">
					<ClayButton
						disabled={disableConnectButton}
						onClick={() =>
							connectSite({
								setDisableConnectButton,
								setSite,
								site,
							})
						}
					>
						{Liferay.Language.get('connect')}
					</ClayButton>
				</div>
			</div>
		</div>
	);
}
