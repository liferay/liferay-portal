/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayModal from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useId, useState} from 'react';

import ConnectedSiteService from '../../common/services/ConnectedSiteService';
import {Site} from '../../common/types/Site';

const showErrorMessage = (message: string) => {
	openToast({
		message,
		type: 'danger',
	});
};

const ConnectedSiteActions = ({
	externalReferenceCode,
	onSiteChange,
	onSiteDisconnected,
	site,
}: {
	externalReferenceCode: string;
	onSiteChange: (site: Site) => void;
	onSiteDisconnected: (site: Site) => void;
	site: Site;
}) => {
	const {searchable} = site;

	const disconnectSite = async () => {
		const {error} = await ConnectedSiteService.disconnectSiteFromSpace(
			externalReferenceCode,
			site.externalReferenceCode
		);

		if (error) {
			showErrorMessage(error);

			return;
		}

		onSiteDisconnected?.(site);
	};

	const changeSearchable = async () => {
		const {data, error} = await ConnectedSiteService.connectSiteToSpace(
			externalReferenceCode,
			site.externalReferenceCode,
			String(!searchable)
		);

		if (data) {
			onSiteChange(data);
		}
		else if (error) {
			showErrorMessage(error);
		}
	};

	const isSearchableLabel = searchable
		? Liferay.Language.get('yes')
		: Liferay.Language.get('no');

	return (
		<div className="align-items-center d-flex">
			<span className="c-mr-3 text-2 text-secondary">
				{`${Liferay.Language.get('searchable-content')}: ${isSearchableLabel}`}
			</span>

			<ClayDropDownWithItems
				items={[
					{
						label: searchable
							? Liferay.Language.get('make-unsearchable')
							: Liferay.Language.get('make-searchable'),
						onClick: changeSearchable,
					},
					{
						label: Liferay.Language.get('disconnect'),
						onClick: disconnectSite,
					},
				]}
				trigger={
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('site-actions')}
						borderless
						displayType="secondary"
						size="xs"
						symbol="ellipsis-v"
						title={Liferay.Language.get('actions')}
					/>
				}
			/>
		</div>
	);
};

const SitesSelector = ({
	externalReferenceCode,
	onSiteConnected,
}: {
	externalReferenceCode: string;
	onSiteConnected: (site: Site) => void;
}) => {
	const [value, setValue] = useState('');
	const [sites, setSites] = useState<Site[]>([]);
	const [siteSelected, setSiteSelected] = useState<Site>();

	const connectSiteToSpace = async () => {
		if (siteSelected) {
			const {data, error} = await ConnectedSiteService.connectSiteToSpace(
				externalReferenceCode,
				siteSelected.externalReferenceCode
			);

			if (data) {
				onSiteConnected(data);
			}
			else if (error) {
				showErrorMessage(
					error ||
						Liferay.Language.get('unable-to-connect-site-to-space')
				);
			}
		}
	};

	useEffect(() => {
		const fetchSites = async () => {
			const {data} = await ConnectedSiteService.getAllSites();

			if (data) {
				setSites(data.items);
			}
		};

		fetchSites();
	}, []);

	return (
		<div className="p-4">
			<div className="align-items-end autofit-row c-gap-3">
				<div className="autofit-col autofit-col-expand">
					<label htmlFor="siteSelector">
						{Liferay.Language.get('site')}
					</label>

					<Autocomplete
						allowsCustomValue
						id="siteSelector"
						menuTrigger="focus"
						onChange={setValue}
						placeholder={Liferay.Language.get('select-a-site')}
						value={value}
					>
						{sites.map((site) => (
							<Autocomplete.Item
								key={site.id}
								onClick={() => {
									setSiteSelected(site);
								}}
							>
								{site.name}
							</Autocomplete.Item>
						))}
					</Autocomplete>
				</div>

				<div className="autofit-col">
					<ClayButton onClick={connectSiteToSpace}>
						{Liferay.Language.get('connect')}
					</ClayButton>
				</div>
			</div>
		</div>
	);
};

export default function SpaceConnectedSitesModal({
	externalReferenceCode,
	hasConnectSitesPermission = true,
}: {
	externalReferenceCode: string;
	hasConnectSitesPermission?: boolean;
}) {
	const [connectedSites, setConnectedSites] = useState<Site[]>([]);
	const listLabelId = useId();

	useEffect(() => {
		const fetchConnectedSitesToSpace = async () => {
			const {data} =
				await ConnectedSiteService.getConnectedSitesFromSpace(
					externalReferenceCode
				);

			if (data) {
				setConnectedSites(data.items);
			}
		};

		fetchConnectedSitesToSpace();
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

	const onSiteChange = (site: Site) => {
		setConnectedSites((currentConnectedSites) =>
			currentConnectedSites.map((currentSite) =>
				currentSite.id === site.id ? site : currentSite
			)
		);
	};

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('all-sites')}
			</ClayModal.Header>

			{hasConnectSitesPermission && (
				<ClayModal.Item>
					<SitesSelector
						externalReferenceCode={externalReferenceCode}
						onSiteConnected={onSiteConnected}
					/>
				</ClayModal.Item>
			)}

			<ClayModal.Body>
				{!connectedSites.length ? (
					<div className="text-center">
						<h2 className="font-weight-semi-bold text-4">
							{Liferay.Language.get('no-sites-are-connected-yet')}
						</h2>

						{hasConnectSitesPermission && (
							<p className="text-3">
								{Liferay.Language.get(
									'connect-sites-to-this-space'
								)}
							</p>
						)}
					</div>
				) : (
					<>
						{hasConnectSitesPermission && (
							<label
								className="c-mb-2 c-mt-n2 d-block"
								id={listLabelId}
							>
								{Liferay.Language.get('connected-sites')}
							</label>
						)}

						<ul
							aria-labelledby={listLabelId}
							className="list-unstyled mb-0"
						>
							{connectedSites.map((site) => {
								return (
									<li
										className="align-items-center c-py-2 d-flex font-weight-semi-bold justify-content-between text-3"
										key={site.id}
									>
										<div className="align-items-center d-flex">
											<ClaySticker
												className="c-mr-2"
												displayType="secondary"
												shape="circle"
												size="sm"
											>
												<ClaySticker.Image
													alt=""
													src={site.logo}
												/>
											</ClaySticker>

											{site.name}
										</div>

										{hasConnectSitesPermission && (
											<ConnectedSiteActions
												externalReferenceCode={
													externalReferenceCode
												}
												onSiteChange={onSiteChange}
												onSiteDisconnected={
													onSiteDisconnected
												}
												site={site}
											/>
										)}
									</li>
								);
							})}
						</ul>
					</>
				)}
			</ClayModal.Body>
		</>
	);
}
