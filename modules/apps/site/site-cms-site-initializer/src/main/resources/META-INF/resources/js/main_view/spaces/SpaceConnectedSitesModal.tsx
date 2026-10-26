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
import {DesignLibrary} from '../../common/types/DesignLibrary';
import {Site} from '../../common/types/Site';
import {SiteTemplate} from '../../common/types/SiteTemplate';
import {
	DESIGN_LIBRARY_TYPE,
	SITE_TEMPLATE_TYPE,
	SITE_TYPE,
} from '../../common/utils/constants';

enum ConnectableKind {
	DESIGN_LIBRARIES = 'design-libraries',
	SITES = 'sites',
	SITE_TEMPLATES = 'site-templates',
}

const DESIGN_LIBRARIES_API_URL = `${location.origin}/o/headless-asset-library/v1.0/asset-libraries?filter=${encodeURIComponent(
	`type eq '${DESIGN_LIBRARY_TYPE}'`
)}`;

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

const isDesignLibrary = (site: Site) => site.type === DESIGN_LIBRARY_TYPE;

const isSiteTemplate = (site: Site) => site.type === SITE_TEMPLATE_TYPE;

const getConnectableType = (kind: ConnectableKind) => {
	if (kind === ConnectableKind.DESIGN_LIBRARIES) {
		return DESIGN_LIBRARY_TYPE;
	}

	if (kind === ConnectableKind.SITE_TEMPLATES) {
		return SITE_TEMPLATE_TYPE;
	}

	return SITE_TYPE;
};

const getConnectionLabel = (site: Site) => {
	if (isDesignLibrary(site)) {
		return `${site.descriptiveName} (${Liferay.Language.get('design-library')})`;
	}

	if (isSiteTemplate(site)) {
		return `${site.descriptiveName} (${Liferay.Language.get('site-template')})`;
	}

	if (site.stagingType === 'STAGING') {
		return `${site.descriptiveName} (${Liferay.Language.get('staging')})`;
	}

	return site.descriptiveName;
};

const getDisconnectedMessage = (site: Site) => {
	if (isDesignLibrary(site)) {
		return Liferay.Language.get(
			'design-library-x-was-successfully-disconnected-from-the-space'
		);
	}

	if (isSiteTemplate(site)) {
		return Liferay.Language.get(
			'site-template-x-was-successfully-disconnected-from-the-space'
		);
	}

	return Liferay.Language.get(
		'site-x-was-successfully-disconnected-from-the-space'
	);
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

		showSuccessMessage(
			sub(
				getDisconnectedMessage(site),
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
		...(isDesignLibrary(site)
			? []
			: [
					{
						label: site.searchable
							? Liferay.Language.get('make-unsearchable')
							: Liferay.Language.get('make-searchable'),
						onClick: changeSearchable,
					},
				]),
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
			{!isDesignLibrary(site) && (
				<span className="c-mr-3 text-2 text-secondary">
					{`${Liferay.Language.get('searchable-content')}: ${isSearchableLabel}`}
				</span>
			)}

			<ClayDropDownWithItems
				items={items}
				trigger={
					<ClayButtonWithIcon
						aria-label={sub(
							Liferay.Language.get('actions-for-x'),
							site.descriptiveName
						)}
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
	const [designLibrary, setDesignLibrary] = useState<DesignLibrary>();
	const [site, setSite] = useState<Site>();
	const [siteTemplate, setSiteTemplate] = useState<SiteTemplate>();
	const [disableConnectButton, setDisableConnectButton] =
		useState<boolean>(true);

	const isAlreadyConnected = (id: string) =>
		connections.some(
			(connection) =>
				connection.externalReferenceCode === id &&
				connection.type === getConnectableType(kind)
		);

	const sitesAPIURL = useMemo(() => {
		const url = new URL(SITES_API_URL);

		url.searchParams.set('active', 'true');

		connections
			.filter((connection) => connection.type === SITE_TYPE)
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
			.filter((connection) => connection.type === SITE_TEMPLATE_TYPE)
			.forEach((connection) =>
				url.searchParams.append(
					'excludedSiteExternalReferenceCodes',
					connection.externalReferenceCode
				)
			);

		return url.toString();
	}, [connections]);

	const resetSelection = () => {
		setDesignLibrary(undefined);
		setSite(undefined);
		setSiteTemplate(undefined);
		setDisableConnectButton(true);
	};

	const getSelection = () => {
		if (kind === ConnectableKind.DESIGN_LIBRARIES && designLibrary) {
			return {
				body: {},
				externalReferenceCode: designLibrary.externalReferenceCode,
				name: designLibrary.name,
				successMessage: Liferay.Language.get(
					'design-library-x-was-successfully-connected-to-the-space'
				),
			};
		}

		if (kind === ConnectableKind.SITES && site) {
			return {
				body: {searchable: true},
				externalReferenceCode: site.externalReferenceCode,
				name: site.descriptiveName,
				successMessage: Liferay.Language.get(
					'site-x-was-successfully-connected-to-the-space'
				),
			};
		}

		if (
			kind === ConnectableKind.SITE_TEMPLATES &&
			siteTemplate?.siteExternalReferenceCode
		) {
			return {
				body: {},
				externalReferenceCode: siteTemplate.siteExternalReferenceCode,
				name: siteTemplate.name,
				successMessage: Liferay.Language.get(
					'site-template-x-was-successfully-connected-to-the-space'
				),
			};
		}
	};

	const connect = async () => {
		const selection = getSelection();

		if (!selection) {
			return;
		}

		const {data, error} = await ConnectedSiteService.connectSiteToSpace(
			externalReferenceCode,
			selection.externalReferenceCode,
			selection.body
		);

		if (data) {
			onConnectionAdded(data);
			showSuccessMessage(
				sub(
					selection.successMessage,
					`<strong>${Liferay.Util.escapeHTML(selection.name)}</strong>`
				)
			);
		}
		else if (error) {
			showErrorMessage(error);
		}

		resetSelection();
	};

	const options = [
		{
			label: Liferay.Language.get('sites'),
			value: ConnectableKind.SITES,
		},
		{
			label: Liferay.Language.get('site-templates'),
			value: ConnectableKind.SITE_TEMPLATES,
		},
	];

	if (Liferay.FeatureFlags['LPD-57283']) {
		options.push({
			label: Liferay.Language.get('design-libraries'),
			value: ConnectableKind.DESIGN_LIBRARIES,
		});
	}

	return (
		<div className="align-items-end autofit-row c-gap-3 c-mb-4">
			<div className="autofit-col autofit-col-expand">
				<InputGroupWithSelect
					className="mb-0"
					label={Liferay.Language.get('type')}
					onSelectChange={(value) => {
						setKind(value as ConnectableKind);
						resetSelection();
					}}
					options={options}
					selectValue={kind}
				>
					{kind === ConnectableKind.SITES && (
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
					)}

					{kind === ConnectableKind.SITE_TEMPLATES && (
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

					{kind === ConnectableKind.DESIGN_LIBRARIES && (
						<ItemSelector
							apiURL={DESIGN_LIBRARIES_API_URL}
							id="connectableSelector"
							items={designLibrary ? [designLibrary] : []}
							itemsFilter={(item: DesignLibrary) =>
								!isAlreadyConnected(item.externalReferenceCode)
							}
							key={DESIGN_LIBRARIES_API_URL}
							onItemsChange={(items: DesignLibrary[]) => {
								if (items.length) {
									const item = items[0];
									setDesignLibrary(item);
									setDisableConnectButton(
										isAlreadyConnected(
											item.externalReferenceCode
										)
									);
								}
								else {
									setDesignLibrary(undefined);
									setDisableConnectButton(true);
								}
							}}
							placeholder={Liferay.Language.get(
								'select-a-design-library'
							)}
						>
							{(item: DesignLibrary) => (
								<ItemSelector.Item
									className="align-items-center d-flex"
									key={item.id}
									textValue={item.name}
								>
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
						existing.type === site.type
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
						existing.type === site.type
					)
			)
		);
	};

	const onConnectionChange = (site: Site) => {
		setConnections((current) =>
			current.map((existing) =>
				existing.externalReferenceCode === site.externalReferenceCode &&
				existing.type === site.type
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
				{Liferay.Language.get('connections')}
			</ClayModal.Header>

			<ClayModal.Body>
				{hasConnectSitesPermission && (
					<>
						<p className="c-mb-4 text-secondary">
							{Liferay.FeatureFlags['LPD-57283']
								? Liferay.Language.get(
										'connect-sites-site-templates-and-design-libraries-to-this-space'
									)
								: Liferay.Language.get(
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
							{Liferay.Language.get('nothing-is-connected-yet')}
						</h2>
					</div>
				) : (
					<>
						{hasConnectSitesPermission && (
							<label
								className="c-mb-2 c-mt-n2 d-block"
								id={listLabelId}
							>
								{Liferay.Language.get('connections')}
							</label>
						)}

						<ul
							aria-labelledby={listLabelId}
							className="list-unstyled mb-0"
						>
							{connections.map((connection) => (
								<li
									className="align-items-center c-py-2 d-flex font-weight-semi-bold justify-content-between text-3"
									key={`${connection.type}:${connection.externalReferenceCode}`}
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
