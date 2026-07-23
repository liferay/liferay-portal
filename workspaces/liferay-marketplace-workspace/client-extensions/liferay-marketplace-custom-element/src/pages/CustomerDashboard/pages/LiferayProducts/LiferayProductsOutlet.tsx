/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';

import {NavbarProps} from '../../../../components/Navbar';
import {useMarketplaceContext} from '../../../../context/MarketplaceContext';
import {
	CMP_ORDER_TYPES,
	OrderCustomFields,
	OrderTypes,
	OrderWorkflowStatusCode,
	orderTypeDocumentationURL,
} from '../../../../enums/Order';
import useGetProductByOrderId from '../../../../hooks/useGetProductByOrderId';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import {getSiteURL} from '../../../../utils/site';
import {safeJSONParse} from '../../../../utils/util';
import {BaseOutlet} from '../Apps/App/AppOutlet';

type ProductAndOrderPayload = NonNullable<
	ReturnType<typeof useGetProductByOrderId>['data']
>;

const getTabs = (data: ProductAndOrderPayload): NavbarProps['routes'] => {
	const {orderTypeExternalReferenceCode} = data?.placedOrder ?? {};

	if (orderTypeExternalReferenceCode === OrderTypes.AI_HUB) {
		return [];
	}

	const isCMP = CMP_ORDER_TYPES.includes(
		orderTypeExternalReferenceCode as OrderTypes
	);
	const isDSR = orderTypeExternalReferenceCode === OrderTypes.DSR;
	const isDXP = orderTypeExternalReferenceCode === OrderTypes.DXP;

	return [
		{
			name: i18n.translate('activation-keys'),
			path: '',
			visible: isCMP || isDSR || isDXP,
		},
		{
			name: i18n.translate('bundles'),
			path: 'bundles',
			visible: isDXP,
		},
		{
			name: i18n.translate('workspace'),
			path: 'workspace',
			visible: isDSR,
		},
	];
};

const LiferayProductsOutlet = () => {
	const {properties} = useMarketplaceContext();

	return (
		<BaseOutlet
			actionButtons={(props) => {
				let groupId;

				const appBeta =
					props?.marketplaceDeliveryProduct?.specificationValues
						?.APP_BETA;

				const orderType = props?.placedOrder
					?.orderTypeExternalReferenceCode as OrderTypes;

				const orderStatus = props?.placedOrder?.orderStatusInfo?.code;

				if (
					props?.marketplaceDeliveryProduct?.specificationValues
						?.SOLUTION_TYPE === 'liferay-data-platform'
				) {
					const orderMetadata = safeJSONParse(
						props.placedOrder?.customFields[
							OrderCustomFields.ORDER_METADATA
						] || '{}',
						{
							analyticsProject: {groupId: 0},
						}
					);

					groupId = orderMetadata?.analyticsProject?.groupId;
				}

				if (
					[
						OrderTypes.ADDONS,
						OrderTypes.AI_HUB,
						OrderTypes.CMP,
						OrderTypes.CMP_BETA,
						OrderTypes.DSR,
						OrderTypes.DXP,
					].includes(orderType)
				) {
					return (
						<div className="mt-6">
							{appBeta && (
								<ClayButton
									className="mr-2"
									displayType="secondary"
									onClick={() => {
										Liferay.Util.navigate(
											`${getSiteURL()}/product-feedback?orderId=${String(props?.placedOrder?.id)}`
										);
									}}
									outline
									size="regular"
								>
									{i18n.translate('share-beta-feedback')}
								</ClayButton>
							)}

							{orderType === OrderTypes.AI_HUB &&
								orderStatus ===
									OrderWorkflowStatusCode.COMPLETED && (
									<ClayButton
										className="mr-2"
										displayType="primary"
										onClick={() => {
											Liferay.Util.navigate(
												`${getSiteURL()}/product-purchase?productId=${props?.product?.productId}&aiHubTokens#/`
											);
										}}
										size="regular"
									>
										{i18n.translate('buy-liferay-tokens')}
									</ClayButton>
								)}

							{[OrderTypes.CMP_BETA, OrderTypes.DXP].includes(
								orderType
							) && (
								<ClayButton
									displayType="primary"
									onClick={() => {
										Liferay.Util.navigate(
											`${getSiteURL()}/product-purchase?productId=${props?.product?.productId}#/activation-key-form`
										);
									}}
									outline
									size="regular"
								>
									{i18n.translate('new-activation-key')}
								</ClayButton>
							)}

							{orderType === OrderTypes.CMP &&
								orderStatus ===
									OrderWorkflowStatusCode.COMPLETED && (
									<ClayButton
										displayType="primary"
										onClick={() => {
											Liferay.Util.navigate(
												`${getSiteURL()}/customer-dashboard#/order/${props?.placedOrder?.id}/create-license`
											);
										}}
										outline
										size="regular"
									>
										{i18n.translate('create-license-key')}
									</ClayButton>
								)}

							{groupId && (
								<ClayButton
									displayType="primary"
									onClick={() => {
										window.open(
											`${properties.analyticsCloudURL}/workspace/${groupId}`
										);
									}}
									outline
								>
									{i18n.translate('visit')}

									<ClayIcon
										className="ml-2"
										symbol="shortcut"
									/>
								</ClayButton>
							)}
						</div>
					);
				}

				if (
					props?.marketplaceDeliveryProduct?.specificationValues
						?.SOLUTION_TYPE === 'liferay-data-platform'
				) {
					const orderMetadata = safeJSONParse(
						props.placedOrder?.customFields?.ORDER_METADATA || '{}',
						{
							analyticsProject: {groupId: 0},
						}
					);

					const groupId = orderMetadata?.analyticsProject?.groupId;

					return (
						<ClayButton
							displayType="primary"
							onClick={() => {
								window.open(
									`${properties.analyticsCloudURL}/workspace/${groupId}`
								);
							}}
							outline
							size="regular"
						>
							{i18n.translate('go-to-liferay-data-platform')}
						</ClayButton>
					);
				}
			}}
			backTitle={i18n.translate('back-to-my-products')}
			backURL="../../products"
			description={(props) => {
				const documentationURL =
					orderTypeDocumentationURL[
						props?.placedOrder
							?.orderTypeExternalReferenceCode as OrderTypes
					];

				return (
					<>
						{props?.product?.shortDescription}

						{documentationURL && (
							<span className="d-block mt-2">
								{i18n.translate('need-help-getting-started?')}

								<a
									className="font-weight-bold ml-1"
									href={documentationURL}
									rel="noopener noreferrer"
									target="_blank"
								>
									{i18n.translate('view-the-documentation')}
								</a>
							</span>
						)}
					</>
				);
			}}
			routes={getTabs}
			showActions={false}
		/>
	);
};

export default LiferayProductsOutlet;
