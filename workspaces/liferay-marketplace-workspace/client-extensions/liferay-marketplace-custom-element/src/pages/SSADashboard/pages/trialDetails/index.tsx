/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {useMemo} from 'react';
import {useParams} from 'react-router-dom';

import BackLink from '../../../../components/BackLink';
import {PageRenderer} from '../../../../components/Page';
import MarketplaceDeliveryOrder from '../../../../entity/MarketplaceDeliveryOrder';
import {MarketplaceDeliveryProduct} from '../../../../entity/MarketplaceDeliveryProduct';
import useGetProductByOrderId from '../../../../hooks/useGetProductByOrderId';
import i18n from '../../../../i18n';
import {safeJSONParse} from '../../../../utils/util';
import OrderDetailsHeader from '../../../CustomerDashboard/components/OrderDetailsHeader';
import useSSAActions from '../../useSSAActions';
import TrialDetailsBody from './TrialDetailsBody';

type TrialActionsProps = {
	mutatePlacedOrder: ReturnType<typeof useGetProductByOrderId>['mutate'];
	placedOrder: PlacedOrder;
};

function TrialActions({mutatePlacedOrder, placedOrder}: TrialActionsProps) {
	const actions = useSSAActions();

	return (
		<ClayDropDownWithItems
			className="align-items-center cursor-pointer d-flex h-100"
			items={
				actions
					.filter((_, index) => index > 0)
					.map((action) => {
						const disabled =
							typeof action.disabled === 'function'
								? action.disabled(placedOrder)
								: action.disabled;

						const hidden =
							typeof action.hidden === 'function'
								? action.hidden(placedOrder)
								: action.hidden;

						return {
							...action,
							disabled,
							hidden,
							label: action.name,
							onClick: () =>
								action?.onClick?.(
									placedOrder,
									mutatePlacedOrder
								),
						};
					}) as any[]
			}
			trigger={
				<ClayButton displayType="secondary">
					{i18n.translate('manage-trial')}

					<ClayIcon className="ml-2" symbol="angle-down-small" />
				</ClayButton>
			}
		/>
	);
}

export default function TrialDetails() {
	const {orderId} = useParams();
	const {
		data,
		error,
		isLoading,
		mutate: mutatePlacedOrder,
	} = useGetProductByOrderId(orderId as string);

	const product = data?.product as DeliveryProduct;
	const placedOrder = data?.placedOrder as PlacedOrder;

	const marketplaceProduct = useMemo(
		() => new MarketplaceDeliveryProduct(product),
		[product]
	);

	const marketplaceOrder = useMemo(
		() => new MarketplaceDeliveryOrder(placedOrder),
		[placedOrder]
	);

	const {projectId} = safeJSONParse(
		marketplaceOrder.customFields.TRIAL_SETTINGS,
		{
			projectId: '',
		}
	);

	if (!placedOrder || !marketplaceProduct) {
		return;
	}

	return (
		<PageRenderer
			className="app-details-header d-flex flex-column w-100"
			error={error}
			isLoading={isLoading}
		>
			<BackLink>{i18n.translate('back-to-the-list')}</BackLink>

			<div className="d-flex justify-content-between">
				<OrderDetailsHeader
					className="d-flex flex-row justify-content-between pb-3 pt-5"
					hasOrderDetails
					image={marketplaceOrder.productThumbnail}
					name={projectId}
					productOwner={marketplaceProduct.catalogName}
				/>

				<TrialActions
					mutatePlacedOrder={mutatePlacedOrder}
					placedOrder={placedOrder}
				/>
			</div>

			<TrialDetailsBody
				marketplaceOrder={marketplaceOrder}
				marketplaceProduct={marketplaceProduct}
				placedOrder={placedOrder}
				projectId={projectId}
			/>
		</PageRenderer>
	);
}
