/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {KeyedMutator} from 'swr';

import ListView from '../../../../components/ListView';
import Modal from '../../../../components/Modal';
import OrderStatus from '../../../../components/OrderStatus';
import Page from '../../../../components/Page';
import {useMarketplaceContext} from '../../../../context/MarketplaceContext';
import SearchBuilder from '../../../../core/SearchBuilder';
import {
	ProductTypeVocabulary,
	ProductWorkflowStatusCode,
	ProductWorkflowStatusLabel,
} from '../../../../enums/Product';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import HeadlessCommerceAdminCatalog from '../../../../services/rest/HeadlessCommerceAdminCatalog';
import {formatDate} from '../../../../utils/date';
import {getSiteURL} from '../../../../utils/site';
import {usePublisherDashboardOutletContext} from '../../PublisherDashboardOutlet';

const Solutions = () => {
	const [loading, setLoading] = useState(false);
	const [selectedApp, setSelectedApp] = useState<Product>({} as Product);
	const {catalogId} = usePublisherDashboardOutletContext();
	const {marketplaceUserAccount} = useMarketplaceContext();
	const modal = useModal();
	const navigate = useNavigate();

	const handleDeleteSolution = async (
		product: Product,
		mutate: KeyedMutator<APIResponse<Product>>
	) => {
		setLoading(true);

		try {
			await HeadlessCommerceAdminCatalog.deleteProduct(product.productId);

			mutate((response) => response, {revalidate: true});

			Liferay.Util.openToast({
				message: i18n.translate('request-sent-successfully'),
				type: 'success',
			});

			modal.onClose();

			setSelectedApp({} as Product);
		}
		catch {
			Liferay.Util.openToast({
				message: i18n.translate('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}

		setLoading(false);
	};

	return (
		<Page
			description="Manage and publish solutions on the Marketplace"
			rightButton={
				marketplaceUserAccount.isSolutionPublisher && (
					<ClayButton
						disabled={!(catalogId && catalogId > 0)}
						onClick={() => navigate('/solutions/publisher')}
					>
						New Solution Template
					</ClayButton>
				)
			}
			title="Solutions"
		>
			{!marketplaceUserAccount.isSolutionPublisher && (
				<ClayAlert displayType="warning">
					Dear <b>{Liferay.ThemeDisplay.getUserName()}</b>, Publishing
					solutions on the Liferay Solutions Marketplace is only
					available to existing Liferay partners currently. If you are
					a partner and wish to be able to publish your solutions here{' '}
					<a href={`${getSiteURL()}/publisher-gate`} target="_blank">
						please complete this form.
					</a>
				</ClayAlert>
			)}

			<ListView<Product>
				emptyStateProps={{
					className:
						'border px-4 py-6 d-flex align-items-center flex-column justify-content-center',
					description: marketplaceUserAccount.isSolutionPublisher
						? 'Click on “New Solution Template” to start.'
						: '',
					title: 'No Solutions Yet',
					type: 'BLANK',
				}}
				id={`publisher-solutions/${catalogId}`}
				resource={function getPublisherSolutions({page, pageSize}) {
					return HeadlessCommerceAdminCatalog.getProducts(
						new URLSearchParams({
							'accountId': '-1',
							'filter': new SearchBuilder()
								.eq('catalogId', catalogId as number, {
									unquote: true,
								})
								.and()
								.lambda(
									'categoryNames',
									ProductTypeVocabulary.SOLUTION
								)
								.build(),
							'images.accountId': '-1',
							'nestedFields': 'productSpecifications',
							'page': page.toString(),
							'pageSize': pageSize.toString(),
						})
					);
				}}
				tableProps={{
					actions: [
						{
							disabled: (prpoduct: Product) =>
								prpoduct.workflowStatusInfo.code ===
								ProductWorkflowStatusCode.PENDING,
							icon: 'pencil',
							name: i18n.translate('edit'),
							onClick: (product: Product) =>
								navigate(
									`/solutions/${product.productId}/publisher/profile`
								),
						},
						{
							disabled: (product: Product) =>
								product?.workflowStatusInfo?.code ===
								ProductWorkflowStatusCode.PENDING,
							icon: 'trash',
							name: i18n.translate('delete'),
							onClick: (product: Product) => {
								setSelectedApp(product);

								modal.onOpenChange(true);
							},
						},
					],
					columns: [
						{
							clickable: true,
							id: 'name',
							name: i18n.translate('name'),
							render: (name, {thumbnail}) => (
								<div style={{width: 200}}>
									<img
										alt="App Image"
										className="app-details-page-table-icon"
										draggable={false}
										height={32}
										src={thumbnail}
										width={32}
									/>

									<span className="font-weight-semi-bold ml-2">
										{name?.en_US}
									</span>
								</div>
							),
						},
						{
							id: 'productType',
							name: 'Solution Type',
							render: () => 'Page',
						},
						{
							id: 'modifiedDate',
							name: 'Last Updated',
							render: (modifiedDate) => (
								<b>{formatDate(modifiedDate)}</b>
							),
						},
						{
							id: 'workflowStatusInfo',
							name: i18n.translate('status'),
							render: (workflowStatusInfo) => (
								<OrderStatus
									orderStatus={workflowStatusInfo.label}
								>
									{
										ProductWorkflowStatusLabel[
											workflowStatusInfo.code as keyof typeof ProductWorkflowStatusLabel
										]
									}
								</OrderStatus>
							),
						},
					],
					navigateTo: (item) => `/solutions/${item.productId}`,
				}}
			>
				{(_, {mutate}) => (
					<Modal
						last={
							<>
								<ClayButton
									displayType="secondary"
									onClick={modal.onClose}
									size="sm"
								>
									{i18n.translate('cancel')}
								</ClayButton>

								<ClayButton
									className="ml-2"
									disabled={loading}
									displayType="danger"
									onClick={() =>
										handleDeleteSolution(
											selectedApp,
											mutate
										)
									}
									size="sm"
								>
									{i18n.translate('delete')}
								</ClayButton>
							</>
						}
						observer={modal.observer}
						size={'md' as any}
						status="danger"
						title={`${i18n.translate('deleting')} ${
							selectedApp.name?.en_US
						}`}
						visible={modal.open}
					>
						{i18n.sub(
							'x-will-be-deleted-and-this-action-cant-be-undone-are-you-sure-you-want-to-delete-it',
							selectedApp.name?.en_US
						)}
					</Modal>
				)}
			</ListView>
		</Page>
	);
};

export default Solutions;
