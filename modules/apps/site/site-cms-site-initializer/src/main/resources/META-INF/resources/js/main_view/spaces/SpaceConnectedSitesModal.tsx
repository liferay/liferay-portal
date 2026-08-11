/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayModal from '@clayui/modal';
import ClaySticker from '@clayui/sticker';
import {ItemSelector} from '@liferay/frontend-js-item-selector-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useId, useMemo, useState} from 'react';

import {InputGroupWithSelect} from '../../common/components/InputGroupWithSelect';
import ConnectedSiteService from '../../common/services/ConnectedSiteService';
import {Site} from '../../common/types/Site';
import {SiteTemplate} from '../../common/types/SiteTemplate';
import {SITE_TEMPLATE_TYPE} from '../../common/utils/constants';

enum ConnectableKind {
	SITES = 'sites',
	SITE_TEMPLATES = 'site-templates',
}

const SITES_API_URL = `${location.origin}/o/headless-admin-site/v1.0/sites`;

const SITE_TEMPLATES_API_URL = `${location.origin}/o/headless-admin-site/v1.0/site-templates`;

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

const isSiteTemplate = (site: Site) => site.type === SITE_TEMPLATE_TYPE;

const getConnectionLabel = (site: Site) => {
	if (isSiteTemplate(site)) {
		return `${site.descriptiveName} (${Liferay.Language.get('site-template')})`;
	}

	if (site.stagingType === 'STAGING') {
		return `${site.descriptiveName} (${Liferay.Language.get('staging')})`;
	}

	return site.descriptiveName;
};

const ConnectableActions = ({
	externalReferenceCode,
	onConnectionChange,
	onConnectionDisconnected,
	site,
}: {
	externalReferenceCode: string;
	onConnectionChange: (site: Site) => void;
	onConnectionDisconnected: (site: Site) => void;
	site: Site;
}) => {
	const disconnect = async () => {
		const {error} = await ConnectedSiteService.disconnectSiteFromSpace(
			externalReferenceCode,
			site.externalReferenceCode
		);

		if (error) {
			showErrorMessage(error);

			return;
		}

		onConnectionDisconnected(site);

		const successMessage = isSiteTemplate(site)
			? Liferay.Language.get(
					'site-template-x-was-successfully-disconnected-from-the-space'
				)
			: Liferay.Language.get(
					'site-x-was-successfully-disconnected-from-the-space'
				);

		showSuccessMessage(
			sub(
				successMessage,
				`<strong>${Liferay.Util.escapeHTML(site.descriptiveName)}</strong>`
			)
		);
	};

	const changeSearchable = async () => {
		const {data, error} = await ConnectedSiteService.connectSiteToSpace(
			externalReferenceCode,
			site.externalReferenceCode,
			{searchable: !site.searchable}
		);

		if (data) {
			onConnectionChange(data);
		}
		else if (error) {
			showErrorMessage(error);
		}
	};

	const items = [
		{
			label: site.searchable
				? Liferay.Language.get('make-unsearchable')
				: Liferay.Language.get('make-searchable'),
			onClick: changeSearchable,
		},
		{
			label: Liferay.Language.get('disconnect'),
			onClick: disconnect,
		},
	];

	const isSearchableLabel = site.searchable
		? Liferay.Language.get('yes')
		: Liferay.Language.get('no');

	return (
		<div className="align-items-center d-flex">
			<span className="c-mr-3 text-2 text-secondary">
				{`${Liferay.Language.get('searchable-content')}: ${isSearchableLabel}`}
			</span>

			<ClayDropDownWithItems
				items={items}
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

const ConnectableSelector = ({
	connections,
	externalReferenceCode,
	onConnectionAdded,
}: {
	connections: Site[];
	externalReferenceCode: string;
	onConnectionAdded: (site: Site) => void;
}) => {
	const [kind, setKind] = useState<ConnectableKind>(ConnectableKind.SITES);
	const [site, setSite] = useState<Site>();
	const [siteTemplate, setSiteTemplate] = useState<SiteTemplate>();
	const [disableConnectButton, setDisableConnectButton] =
		useState<boolean>(true);

	const isAlreadyConnected = (id: string) =>
		connections.some(
			(connection) =>
				connection.externalReferenceCode === id &&
				isSiteTemplate(connection) ===
					(kind === ConnectableKind.SITE_TEMPLATES)
		);

	const sitesAPIURL = useMemo(() => {
		const url = new URL(SITES_API_URL);

		url.searchParams.set('active', 'true');

		connections
			.filter((connection) => !isSiteTemplate(connection))
			.forEach((connection) =>
				url.searchParams.append(
					'excludedExternalReferenceCodes',
					connection.externalReferenceCode
				)
			);

		return url.toString();
	}, [connections]);

	const siteTemplatesAPIURL = useMemo(() => {
		const url = new URL(SITE_TEMPLATES_API_URL);

		url.searchParams.set('active', 'true');

		connections
			.filter((connection) => isSiteTemplate(connection))
			.forEach((connection) =>
				url.searchParams.append(
					'excludedSiteExternalReferenceCodes',
					connection.externalReferenceCode
				)
			);

		return url.toString();
	}, [connections]);

	const resetSelection = () => {
		setSite(undefined);
		setSiteTemplate(undefined);
		setDisableConnectButton(true);
	};

	const connect = async () => {
		if (kind === ConnectableKind.SITES && site) {
			const {data, error} = await ConnectedSiteService.connectSiteToSpace(
				externalReferenceCode,
				site.externalReferenceCode
			);

			if (data) {
				onConnectionAdded(data);
				showSuccessMessage(
					sub(
						Liferay.Language.get(
							'site-x-was-successfully-connected-to-the-space'
						),
						`<strong>${Liferay.Util.escapeHTML(site.descriptiveName)}</strong>`
					)
				);
			}
			else if (error) {
				showErrorMessage(error);
			}

			resetSelection();

			return;
		}

		if (
			kind === ConnectableKind.SITE_TEMPLATES &&
			siteTemplate?.siteExternalReferenceCode
		) {
			const {data, error} = await ConnectedSiteService.connectSiteToSpace(
				externalReferenceCode,
				siteTemplate.siteExternalReferenceCode,
				{}
			);

			if (data) {
				onConnectionAdded(data);
				showSuccessMessage(
					sub(
						Liferay.Language.get(
							'site-template-x-was-successfully-connected-to-the-space'
						),
						`<strong>${Liferay.Util.escapeHTML(siteTemplate.name)}</strong>`
					)
				);
			}
			else if (error) {
				showErrorMessage(error);
			}

			resetSelection();
		}
	};

	return (
		<div className="align-items-end autofit-row c-gap-3 c-mb-4">
			<div className="autofit-col autofit-col-expand">
				<InputGroupWithSelect
					className="mb-0"
					label={Liferay.Language.get('sites')}
					onSelectChange={(value) => {
						setKind(value as ConnectableKind);
						resetSelection();
					}}
					options={[
						{
							label: Liferay.Language.get('sites'),
							value: ConnectableKind.SITES,
						},
						{
							label: Liferay.Language.get('site-templates'),
							value: ConnectableKind.SITE_TEMPLATES,
						},
					]}
					selectValue={kind}
				>
					{kind === ConnectableKind.SITES ? (
						<ItemSelector
							apiURL={sitesAPIURL}
							id="connectableSelector"
							items={site ? [site] : []}
							key={sitesAPIURL}
							onItemsChange={(items: Site[]) => {
								if (items.length) {
									const item = items[0];
									setSite(item);
									setDisableConnectButton(
										isAlreadyConnected(
											item.externalReferenceCode
										)
									);
								}
								else {
									setSite(undefined);
									setDisableConnectButton(true);
								}
							}}
							placeholder={Liferay.Language.get('select-a-site')}
						>
							{(item: Site) => (
								<ItemSelector.Item
									className="align-items-center d-flex"
									key={item.id}
									textValue={item.descriptiveName}
								>
									<ClaySticker
										className="c-mr-2"
										displayType="secondary"
										shape="circle"
										size="sm"
									>
										<ClaySticker.Image
											alt=""
											src={item.logo}
										/>
									</ClaySticker>

									{item.descriptiveName}
								</ItemSelector.Item>
							)}
						</ItemSelector>
					) : (
						<ItemSelector
							apiURL={siteTemplatesAPIURL}
							id="connectableSelector"
							items={siteTemplate ? [siteTemplate] : []}
							key={siteTemplatesAPIURL}
							onItemsChange={(items: SiteTemplate[]) => {
								if (items.length) {
									const item = items[0];
									setSiteTemplate(item);
									setDisableConnectButton(
										!item.siteExternalReferenceCode ||
											isAlreadyConnected(
												item.siteExternalReferenceCode
											)
									);
								}
								else {
									setSiteTemplate(undefined);
									setDisableConnectButton(true);
								}
							}}
							placeholder={Liferay.Language.get(
								'select-a-site-template'
							)}
						>
							{(item: SiteTemplate) => (
								<ItemSelector.Item
									className="align-items-center d-flex"
									key={item.id}
									textValue={item.name}
								>
									<ClaySticker
										className="c-mr-2"
										displayType="secondary"
										shape="circle"
										size="sm"
									>
										<ClaySticker.Image
											alt=""
											src={item.logo}
										/>
									</ClaySticker>

									{item.name}
								</ItemSelector.Item>
							)}
						</ItemSelector>
					)}
				</InputGroupWithSelect>
			</div>

			<div className="autofit-col">
				<ClayButton disabled={disableConnectButton} onClick={connect}>
					{Liferay.Language.get('connect')}
				</ClayButton>
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
	const [connections, setConnections] = useState<Site[]>([]);
	const listLabelId = useId();

	useEffect(() => {
		const fetchConnections = async () => {
			const {data} =
				await ConnectedSiteService.getConnectedSitesFromSpace(
					externalReferenceCode
				);

			if (data) {
				const sorted = [...data.items].sort((a, b) =>
					a.descriptiveName.localeCompare(b.descriptiveName)
				);

				setConnections(sorted);
			}
		};

		fetchConnections();
	}, [externalReferenceCode]);

	const onConnectionAdded = (site: Site) => {
		setConnections((current) => {
			if (
				current.some(
					(existing) =>
						existing.externalReferenceCode ===
							site.externalReferenceCode &&
						isSiteTemplate(existing) === isSiteTemplate(site)
				)
			) {
				return current;
			}

			const next = [...current, site];

			next.sort((a, b) =>
				a.descriptiveName.localeCompare(b.descriptiveName)
			);

			return next;
		});
	};

	const onConnectionDisconnected = (site: Site) => {
		setConnections((current) =>
			current.filter(
				(existing) =>
					!(
						existing.externalReferenceCode ===
							site.externalReferenceCode &&
						isSiteTemplate(existing) === isSiteTemplate(site)
					)
			)
		);
	};

	const onConnectionChange = (site: Site) => {
		setConnections((current) =>
			current.map((existing) =>
				existing.externalReferenceCode === site.externalReferenceCode &&
				isSiteTemplate(existing) === isSiteTemplate(site)
					? site
					: existing
			)
		);
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('all-sites')}
			</ClayModal.Header>

			<ClayModal.Body>
				{hasConnectSitesPermission && (
					<>
						<p className="c-mb-4 text-secondary">
							{Liferay.Language.get(
								'connect-sites-and-site-templates-to-this-space'
							)}
						</p>

						<ConnectableSelector
							connections={connections}
							externalReferenceCode={externalReferenceCode}
							onConnectionAdded={onConnectionAdded}
						/>
					</>
				)}

				{!connections.length ? (
					<div className="text-center">
						<h2 className="font-weight-semi-bold text-4">
							{Liferay.Language.get('no-sites-are-connected-yet')}
						</h2>
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
							{connections.map((connection) => (
								<li
									className="align-items-center c-py-2 d-flex font-weight-semi-bold justify-content-between text-3"
									key={`${isSiteTemplate(connection) ? 'st' : 's'}:${connection.externalReferenceCode}`}
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
												src={connection.logo}
											/>
										</ClaySticker>

										{getConnectionLabel(connection)}
									</div>

									{hasConnectSitesPermission && (
										<ConnectableActions
											externalReferenceCode={
												externalReferenceCode
											}
											onConnectionChange={
												onConnectionChange
											}
											onConnectionDisconnected={
												onConnectionDisconnected
											}
											site={connection}
										/>
									)}
								</li>
							))}
						</ul>
					</>
				)}
			</ClayModal.Body>
		</>
	);
}
