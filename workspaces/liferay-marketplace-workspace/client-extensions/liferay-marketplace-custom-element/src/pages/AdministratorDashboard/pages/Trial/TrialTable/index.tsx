/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import {Status} from '@clayui/modal/lib/types';
import {formatDistance} from 'date-fns';

import {DashboardPage} from '../../../../../components/DashBoardPage/DashboardPage';
import {DashboardEmptyTable} from '../../../../../components/DashboardTable/DashboardEmptyTable';
import Loading from '../../../../../components/Loading';
import Table from '../../../../../components/Table/Table';
import {
	OrderCustomFields,
	OrderWorkflowStatusCode,
} from '../../../../../enums/Order';
import {useConfirmationModal} from '../../../../../hooks/useConfirmationModal';
import useModalContext from '../../../../../hooks/useModalContext';
import i18n from '../../../../../i18n';
import {Liferay} from '../../../../../liferay/liferay';
import trialOAuth2 from '../../../../../services/oauth/Trial';
import CommerceSelectAccount from '../../../../../services/rest/CommerceSelectAccount';
import HeadlessCommerceAdminOrder from '../../../../../services/rest/HeadlessCommerceAdminOrder';
import NewTrialModal from './NewTrialModal';
import TrialDetailsModal, {ORDER_STATUS_LABEL} from './TrialDetailsModal';

type TrialTableProps = {
	items: Order[];
	revalidate: () => void;
};

type DropDownItems = {
	id: number;
	name: string;
	onClick: (item?: Order) => void;
};

const safeRunner = async (promise: any) => {
	try {
		await promise;
	}
	catch {}
};

const TrialTable: React.FC<TrialTableProps> = ({items, revalidate}) => {
	const modalContext = useModalContext();
	const confirmationModal = useConfirmationModal();

	const onDeleteTrial = async (order: Order) => {
		await safeRunner(HeadlessCommerceAdminOrder.deleteOrder(order.id));
		await safeRunner(trialOAuth2.deleteTrial(order.id));

		await revalidate();
	};

	const onClickDetails = (order: Order) => {
		modalContext.onOpenModal({
			body: <TrialDetailsModal order={order} />,
			center: true,
			footer: [
				null,
				null,
				<ClayButton
					aria-label="close"
					displayType="secondary"
					key={0}
					onClick={modalContext.onClose}
					size="sm"
				>
					{i18n.translate('close')}
				</ClayButton>,
			],
			header: i18n.translate('trial-details'),
			size: 'md',
		});
	};

	const itemsDropdown = [
		{
			name: i18n.translate('view-details'),
			onClick: onClickDetails,
		},
		{
			name: i18n.translate('go-to-trial'),
			onClick: (order: Order) =>
				window.open(
					`https://${
						order?.customFields?.[
							OrderCustomFields.TRIAL_VIRTUAL_HOST
						]
					}`
				),
		},
		{
			name: i18n.translate('customer-dashboard'),
			onClick: (order: Order) =>
				CommerceSelectAccount.selectAccount(order?.accountId).then(
					() => {
						Liferay.CommerceContext.account = {
							accountId: order?.accountId,
							accountName: '',
						};

						Liferay.Util.navigate(
							Liferay.ThemeDisplay.getLayoutURL().replace(
								'/administrator-dashboard',
								`/customer-dashboard`
							) + '#/solutions'
						);
					}
				),
		},
		{
			name: i18n.translate('delete'),
			onClick: (order: Order) => {
				confirmationModal.openModal({
					body: (
						<p>
							{i18n.sub(
								'x-will-be-deleted-and-this-action-cant-be-undone-are-you-sure-you-want-to-delete-it',
								`Order ${order.id}`
							)}
						</p>
					),
					header: i18n.translate('confirm-deletion'),
					onConfirm: () => onDeleteTrial(order),
				});
			},
		},
	];

	return (
		<DashboardPage
			buttonMessage={i18n.translate('new-trial')}
			messages={{description: '', title: i18n.translate('trials')}}
			onButtonClick={() =>
				modalContext.onOpenModal({
					body: (
						<NewTrialModal
							onClose={modalContext.onClose}
							revalidate={revalidate}
						/>
					),
					header: i18n.translate('new-trial'),
				})
			}
		>
			{items.length ? (
				<Table
					className="mt-3"
					columns={[
						{
							key: 'id',
							render: (id) => (
								<span className="font-weight-bold">{id}</span>
							),
							title: i18n.translate('id'),
						},
						{
							key: 'orderItems',
							render: (orderItems) => orderItems[0]?.name.en_US,
							title: i18n.translate('product'),
						},
						{
							key: 'account',
							render: (account) => account?.name,
							title: i18n.translate('user-account'),
						},
						{
							key: 'orderStatusInfo',
							render: (orderStatusInfo) => (
								<div className="align-items-center d-flex">
									<ClayLabel
										className="text-nowrap"
										displayType={
											ORDER_STATUS_LABEL[
												orderStatusInfo?.label as keyof typeof ORDER_STATUS_LABEL
											] as Status
										}
									>
										{orderStatusInfo?.label_i18n}
									</ClayLabel>

									{[
										OrderWorkflowStatusCode.ON_HOLD,
										OrderWorkflowStatusCode.PROCESSING,
									].includes(orderStatusInfo.code) && (
										<Loading
											displayType="primary"
											shape="circle"
											size="sm"
										/>
									)}
								</div>
							),
							title: i18n.translate('trial-status'),
						},
						{
							key: 'createDate',
							render: (createDate) => (
								<span className="ml-2 text-capitalize text-nowrap">
									{createDate &&
										formatDistance(
											new Date(createDate),
											Date.now(),
											{addSuffix: true}
										)}
								</span>
							),
							title: i18n.translate('created-at'),
						},
						{
							key: 'customFields',
							render: (customFields) => (
								<span className="ml-2 text-capitalize text-nowrap">
									{customFields['trial-start-date'] &&
										formatDistance(
											new Date(
												customFields['trial-start-date']
											),
											Date.now(),
											{addSuffix: true}
										)}
								</span>
							),
							title: i18n.translate('start-date'),
						},
						{
							key: 'customFields',
							render: (customFields) => (
								<span className="ml-2 text-capitalize text-nowrap">
									{customFields['trial-end-date'] &&
										formatDistance(
											new Date(
												customFields['trial-end-date']
											),
											Date.now(),
											{addSuffix: true}
										)}
								</span>
							),
							title: i18n.translate('expiration-date'),
						},
						{
							align: 'right',
							key: 'accountId',
							render: (_, order) => (
								<DropDown
									closeOnClick
									filterKey="name"
									trigger={
										<ClayButton
											aria-label="Action Dropdown"
											displayType="unstyled"
										>
											<ClayIcon symbol="ellipsis-v" />
										</ClayButton>
									}
								>
									<DropDown.ItemList items={itemsDropdown}>
										{(dropDownItem: unknown) => {
											const item =
												dropDownItem as DropDownItems;

											return (
												<DropDown.Item
													key={item.name}
													onClick={() =>
														item.onClick(order)
													}
												>
													{item?.name}
												</DropDown.Item>
											);
										}}
									</DropDown.ItemList>
								</DropDown>
							),
							title: '',
						},
					]}
					rows={items}
				/>
			) : (
				<div className="mt-3">
					<DashboardEmptyTable
						description1={i18n.translate(
							'purchase-and-install-new-apps-and-they-will-show-up-here'
						)}
						description2={i18n.translate(
							'click-on-add-apps-to-start'
						)}
						icon="grid"
						title={i18n.translate('no-orders-yet')}
					/>
				</div>
			)}
		</DashboardPage>
	);
};

export default TrialTable;
