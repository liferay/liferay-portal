/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {openToast} from 'frontend-js-components-web';
import React, {useState} from 'react';

import IntegrationNameCellRenderer from './cell_renderers/IntegrationNameCellRenderer';
import IntegrationStatusCellRenderer from './cell_renderers/IntegrationStatusCellRenderer';

import './Integrations.scss';

interface IntegrationType {
	configurationURL: string;
	disabled: boolean;
	id: string;
	name: string;
}

interface Props {
	fdsId: string;
	integrationTypes: IntegrationType[];
	integrationsURL: string;
	items: any[];
	itemsActions: any[];
	views: any[];
}

export default function Integrations({
	fdsId,
	integrationTypes,
	integrationsURL,
	items,
	itemsActions,
	views,
}: Props) {
	const [active, setActive] = useState(false);

	const handleActionClick = ({
		action: {
			data: {id: actionId},
		},
		itemData,
	}: {
		action: {data: {id: string}};
		itemData: {configurationURL: string; id: number};
	}) => {
		if (actionId === 'edit') {
			window.location.assign(itemData.configurationURL);

			return;
		}

		if (actionId === 'remove') {
			Liferay.Util.fetch(`${integrationsURL}/${itemData.id}`, {
				method: 'DELETE',
			})
				.then((response) => {
					if (!response.ok) {
						throw new Error();
					}

					openToast({
						message: Liferay.Language.get(
							'your-request-completed-successfully'
						),
						type: 'success',
					});

					window.location.reload();
				})
				.catch(() => {
					openToast({
						message: Liferay.Language.get(
							'an-unexpected-error-occurred'
						),
						type: 'danger',
					});
				});
		}
	};

	return (
		<div className="p-3 p-md-4">
			<div className="sheet">
				<div className="sheet-header">
					<div className="autofit-row autofit-row-center">
						<div className="autofit-col autofit-col-expand">
							<h2 className="sheet-title">
								{Liferay.Language.get('integrations')}
							</h2>
						</div>

						<div className="autofit-col">
							<ClayDropDown
								active={active}
								menuElementAttrs={{
									className: 'integrations-add-menu',
								}}
								onActiveChange={setActive}
								trigger={
									<ClayButton
										className="add-integration-button"
										displayType="primary"
									>
										<span className="inline-item inline-item-before">
											{Liferay.Language.get(
												'add-integration'
											)}
										</span>

										<ClayIcon symbol="caret-bottom" />
									</ClayButton>
								}
							>
								<ClayDropDown.ItemList>
									{integrationTypes.map((integrationType) => (
										<ClayDropDown.Item
											disabled={integrationType.disabled}
											href={
												integrationType.disabled
													? undefined
													: integrationType.configurationURL
											}
											key={integrationType.id}
										>
											{integrationType.name}
										</ClayDropDown.Item>
									))}
								</ClayDropDown.ItemList>
							</ClayDropDown>
						</div>
					</div>
				</div>

				{items.length ? (
					<FrontendDataSet
						appURL={`${Liferay.ThemeDisplay.getPortalURL()}/o/frontend-data-set-taglib/app`}
						customRenderers={{
							tableCell: [
								{
									component: IntegrationNameCellRenderer,
									name: 'integrationNameCellRenderer',
									type: 'internal',
								},
								{
									component: IntegrationStatusCellRenderer,
									name: 'integrationStatusCellRenderer',
									type: 'internal',
								},
							],
						}}
						id={fdsId}
						items={items}
						itemsActions={itemsActions}
						onActionDropdownItemClick={handleActionClick}
						showManagementBar={false}
						showPagination={false}
						showSearch={false}
						views={views}
					/>
				) : (
					<div className="integrations-empty text-center">
						<img
							alt=""
							className="integrations-empty-image"
							src="/o/cms-theme/images/states/empty_state_reduced_motion.svg"
						/>

						<div className="integrations-empty-title">
							{Liferay.Language.get(
								'no-integrations-have-been-added-yet'
							)}
						</div>

						<div className="integrations-empty-description">
							{Liferay.Language.get('add-your-first-integration')}
						</div>
					</div>
				)}
			</div>
		</div>
	);
}
