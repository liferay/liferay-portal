/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo} from 'react';
import {useNavigate} from 'react-router-dom';

import {useMarketplaceContext} from '../../context/MarketplaceContext';
import {OrderCustomFields, OrderStatus} from '../../enums/Order';
import useModalContext from '../../hooks/useModalContext';
import i18n from '../../i18n';
import {Action} from '../../utils/constants';
import {useSSADashboardOutlet} from './SSADashboardOutlet';
import {ExtendRequestStatus} from './enums/SSATrials';
import ExpireSSAModal from './pages/ExpireSSAModal';
import ExtendRequestModal from './pages/ExtendRequestModal';
import ExtendSSATrialModal from './pages/ExtendSSATrialModal';

const getOrderExtendRequests =
	(trialExtendRequests: APIResponse<TrialExtend>['items']) =>
	(order: PlacedOrder) =>
		trialExtendRequests.filter(
			(item) =>
				item.r_orderToTrialExtensionRequest_commerceOrderId ===
				Number(order.id)
		);

const useSSAActions = () => {
	const {marketplaceUserAccount} = useMarketplaceContext();
	const modalContext = useModalContext();
	const navigate = useNavigate();

	const {selectedAccountId, ssaTrialExtend, ssaTrialExtendMutate} =
		useSSADashboardOutlet();

	return useMemo(() => {
		const _getOrderExtendRequests = getOrderExtendRequests(
			ssaTrialExtend?.items ?? []
		);

		return [
			{
				name: i18n.translate('details'),
				onClick: (order: PlacedOrder) =>
					navigate(`details/${order.id}`),
			},
			{
				disabled: (order: PlacedOrder) =>
					order.orderStatusInfo.label !== OrderStatus.IN_PROGRESS,
				name: i18n.translate('go-to-trial'),
				onClick: (order: PlacedOrder) =>
					window.open(
						`https://${
							order?.customFields?.[
								OrderCustomFields.TRIAL_VIRTUAL_HOST
							] as string
						}`
					),
			},
			{
				hidden: (order: PlacedOrder) => {
					if (!marketplaceUserAccount.isSSAAdmin) {
						return true;
					}

					const extendRequests = _getOrderExtendRequests(order);

					return (
						extendRequests[0]?.dueStatus?.key !==
						ExtendRequestStatus.PENDING
					);
				},
				name: i18n.translate('view-request'),
				onClick: (order: PlacedOrder, orderMutate) => {
					const extendRequests = _getOrderExtendRequests(order);

					if (!extendRequests.length) {
						return;
					}

					modalContext.onOpenModal({
						body: (
							<ExtendRequestModal
								mutatePlacedOrderPage={orderMutate}
								onClose={modalContext.onClose}
								order={order}
								ssaTrialExtendMutate={ssaTrialExtendMutate}
								trialExtend={extendRequests[0]}
								trialExtendCount={
									extendRequests.filter(
										({dueStatus}: TrialExtend) =>
											[
												ExtendRequestStatus.APPROVED,
												ExtendRequestStatus.AUTO_APPROVED,
											].includes(
												dueStatus?.key as ExtendRequestStatus
											)
									).length
								}
							/>
						),
						center: true,
					});
				},
			},
			{
				disabled: (order: PlacedOrder) => {
					const extendRequests = _getOrderExtendRequests(order);

					if (!extendRequests) {
						return true;
					}

					return (
						order.orderStatusInfo.label !==
							OrderStatus.IN_PROGRESS ||
						extendRequests[0]?.dueStatus.key ===
							ExtendRequestStatus.PENDING
					);
				},
				name: i18n.translate('extend-trial'),
				onClick: (order: PlacedOrder, orderMutate) => {
					const extendRequests = _getOrderExtendRequests(order);

					modalContext.onOpenModal({
						body: (
							<ExtendSSATrialModal
								accountId={selectedAccountId}
								firstExtendRequest={!extendRequests?.length}
								onClose={modalContext.onClose}
								order={order}
								orderMutate={orderMutate}
								ssaTrialExtendMutate={ssaTrialExtendMutate}
							/>
						),
						header: `Extend ${order.id} Trial`,
					});
				},
			},
			{
				disabled: (order: PlacedOrder) =>
					order.orderStatusInfo.label !== OrderStatus.IN_PROGRESS,
				name: i18n.translate('expire-trial'),
				onClick: (order: PlacedOrder, mutate) => {
					modalContext.onOpenModal({
						body: (
							<ExpireSSAModal
								accountId={selectedAccountId}
								mutate={mutate}
								onClose={modalContext.onClose}
								order={order}
							/>
						),
						header: `Expire ${order.id} Trial`,
						status: undefined,
					});
				},
			},
		] as Action[];
	}, [
		marketplaceUserAccount.isSSAAdmin,
		modalContext,
		navigate,
		selectedAccountId,
		ssaTrialExtend?.items,
		ssaTrialExtendMutate,
	]);
};

export default useSSAActions;
