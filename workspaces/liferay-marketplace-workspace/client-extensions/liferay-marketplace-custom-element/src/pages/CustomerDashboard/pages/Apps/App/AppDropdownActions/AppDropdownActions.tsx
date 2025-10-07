/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DropDown from '@clayui/drop-down';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {useNavigate} from 'react-router-dom';

import {Analytics} from '../../../../../../core/Analytics';
import MarketplaceDeliveryOrder from '../../../../../../entity/MarketplaceDeliveryOrder';
import i18n from '../../../../../../i18n';
import {Liferay} from '../../../../../../liferay/liferay';

type AppDropdownActionsProps = {
	placedOrder: PlacedOrder;
};

function AppDropdownActions({placedOrder}: AppDropdownActionsProps) {
	const navigate = useNavigate();

	const {canDownload, dxpProvisioningEnabled, isFreeApp, isOrderCompleted} =
		new MarketplaceDeliveryOrder(placedOrder);

	const [placedOrderItem] = placedOrder.placedOrderItems || [{}];

	const {name, virtualItemURLs} = placedOrderItem || {};
	const virtualURL = virtualItemURLs?.[0] || '';

	const {account, id} = placedOrder;
	const metadata = {account, productName: name || ''};

	return (
		<DropDown.ItemList>
			{dxpProvisioningEnabled && (
				<>
					<ClayTooltipProvider>
						<DropDown.Item
							data-tooltip-align="left"
							disabled={!isOrderCompleted}
							onClick={() =>
								navigate(`/order/${id}/create-license`)
							}
							title={
								isOrderCompleted
									? undefined
									: i18n.translate(
											'the-order-must-be-completed-before-licensing-this-app.'
										)
							}
						>
							{i18n.translate('create-license-key')}
						</DropDown.Item>
					</ClayTooltipProvider>

					<DropDown.Item
						disabled={isFreeApp}
						onClick={() => navigate(`/order/${id}/licenses`)}
					>
						{i18n.translate('manage-license-keys')}
					</DropDown.Item>
				</>
			)}

			{!canDownload && (
				<DropDown.Item
					onClick={() => navigate(`/order/${id}/cloud-provisioning`)}
				>
					{i18n.translate('cloud-provisioning')}
				</DropDown.Item>
			)}

			{canDownload && (
				<ClayTooltipProvider>
					<DropDown.Item
						data-tooltip-align="left"
						disabled={!isOrderCompleted}
						onClick={() => {
							navigate(`/order/${id}/download`);
							if (!virtualURL?.trim()) {
								Analytics.track(
									'VIRTUAL_URL_NOT_FOUND',
									metadata
								);

								return Liferay.Util.openToast({
									message: i18n.translate(
										'file-not-available-for-download'
									),
									type: 'danger',
								});
							}

							Analytics.track('DOWNLOAD_APP', metadata);
							window.open(virtualURL);
						}}
						title={
							!isOrderCompleted
								? i18n.translate(
										'this-order-must-be-completed-before-downloading-this-app.'
									)
								: undefined
						}
					>
						{i18n.translate('download-app')}
					</DropDown.Item>
				</ClayTooltipProvider>
			)}
		</DropDown.ItemList>
	);
}

export default AppDropdownActions;
