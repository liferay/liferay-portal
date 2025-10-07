/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ComponentProps} from 'react';
import {useNavigate} from 'react-router-dom';
import useSWR from 'swr';

import {useMarketplaceContext} from '../../../context/MarketplaceContext';
import SearchBuilder from '../../../core/SearchBuilder';
import {AccountType} from '../../../enums/Account';
import {ProductType, ProductWorkflowStatusCode} from '../../../enums/Product';
import useListTypeDefinition from '../../../hooks/useListTypeDefinition';
import useModalContext from '../../../hooks/useModalContext';
import HeadlessCommerceAdminCatalog from '../../../services/rest/HeadlessCommerceAdminCatalog';
import GraphQL from '../../../services/rest/HeadlessGraphQL';
import {safeJSONParse} from '../../../utils/util';
import ProjectsUsingMarketplaceModalBody from '../components/ProjectsUsingMarketplace';

const baseSearchBuilder = new SearchBuilder()
	.in('statusCode', [ProductWorkflowStatusCode.APPROVED])
	.and();

const appsAndConnectorSupportingQReleaseFilter = baseSearchBuilder
	.clone()
	.group('OPEN')
	.lambdaContains('specificationValues', '2025 Q')
	.or()
	.lambdaContains('specificationValues', '2024 Q')
	.or()
	.lambdaContains('specificationValues', '2023 Q')
	.group('CLOSE')
	.and()
	.not()
	.lambda('specificationValues', ProductType.LOW_CODE_CONFIGURATION)
	.build();

const lowCodeConfigurationsPublishedFilter = baseSearchBuilder
	.clone()
	.lambda('specificationValues', ProductType.LOW_CODE_CONFIGURATION)
	.build();

const technologyPartnershipIntegrationFilter = new SearchBuilder()
	.lambda('specificationValues', AccountType.TECHNOLOGY_PARTNER)
	.build();

const getAnnualTargetValues = (kpiTarget: string, value: number) => {
	if (kpiTarget.includes('/')) {
		const [current, total] = kpiTarget.split('/');

		return {
			annualTargetCurrent: Number(current),
			annualTargetTotal: Number(total),
		};
	}

	return {
		annualTargetCurrent: Number(value),
		annualTargetTotal: Number(kpiTarget),
	};
};

const queries = [
	HeadlessCommerceAdminCatalog.getProductsDashboardKPI(
		{
			appsAndConnectorSupportingQRelease:
				appsAndConnectorSupportingQReleaseFilter,
			lowCodeConfigurationsPublished:
				lowCodeConfigurationsPublishedFilter,
			partnershipIntegration: technologyPartnershipIntegrationFilter,
		},
		{
			appsAndConnectorSupportingQRelease: {
				body: ` items { catalogExternalReferenceCode, id, name, thumbnail } `,
				pageSize: -1,
			},
		}
	),
	HeadlessCommerceAdminCatalog.getCatalogs(
		new URLSearchParams({
			fields: 'externalReferenceCode,name',
			pageSize: '-1',
		})
	),
	GraphQL.metrics<{name: string; value: string}>(
		{
			group: 'c',
			name: 'reports',
			options: {
				body: `items { name, value }`,
				sort: 'dateCreated:desc',
			},
		},
		{
			projectsUsingMarketplace: SearchBuilder.eq(
				'name',
				'projectsUsingMarketplace'
			),
			totalAmount: SearchBuilder.eq('name', 'totalAmount'),
		}
	),
] as const;

const useKPI = () => {
	const {data: liferayVersionsPicklist} =
		useListTypeDefinition('LIFERAY-VERSIONS');

	const modal = useModalContext();
	const navigate = useNavigate();

	const liferayQuarterlyVersionEntries =
		liferayVersionsPicklist?.listTypeEntries.filter((entry) =>
			entry.externalReferenceCode.includes('Q')
		);

	const liferayQuarterlyVersionsAndConnectors = JSON.stringify({
		'specificationValues|liferayVersion':
			liferayQuarterlyVersionEntries?.map((entry) => entry.name),
	});

	const {
		properties: {kpi: anualTargetKPIs},
	} = useMarketplaceContext();

	const {
		kpiConnectorQuartelyRelease,
		kpiLowCodePublishedApps,
		kpiPartnershipIntegration,
		kpiProjectUsingMarketplaceApps,
		kpiQuartelyReleaseApps,
	} = anualTargetKPIs;

	return useSWR('metrics/kpi', async () => {
		const [
			{
				data: {
					metrics: {
						appsAndConnectorSupportingQRelease,
						lowCodeConfigurationsPublished,
						partnershipIntegration,
					},
				},
			},
			catalogsResponse,
			projectsKPI,
		] = await Promise.all(queries);

		const projectsUsingMarkeplaceApps = Object.entries(
			safeJSONParse(
				projectsKPI?.data?.metrics?.projectsUsingMarketplace?.items?.[0]
					?.value,
				{}
			)
		);

		const catalogs = Object.groupBy(
			appsAndConnectorSupportingQRelease.items.map((product) => ({
				...product,
				catalogName:
					catalogsResponse.items.find(
						(catalog) =>
							catalog.externalReferenceCode ===
							product.catalogExternalReferenceCode
					)?.name ?? product.externalReferenceCode,
			})),
			({catalogName}) => catalogName
		);

		const supportingQuartelyRelease = {
			...appsAndConnectorSupportingQRelease,
			totalCount: Object.keys(catalogs).length,
		};

		return {
			kpis: [
				{
					...getAnnualTargetValues(
						kpiProjectUsingMarketplaceApps,
						projectsUsingMarkeplaceApps.length
					),
					colors: ['#9CE269', '#D4F3BE'],
					onClick: projectsUsingMarkeplaceApps.length
						? () =>
								modal.onOpenModal({
									body: (
										<ProjectsUsingMarketplaceModalBody
											projectsUsingMarkeplaceApps={
												projectsUsingMarkeplaceApps as ComponentProps<
													typeof ProjectsUsingMarketplaceModalBody
												>['projectsUsingMarkeplaceApps']
											}
										/>
									),
									header: 'New Projects Using Marketplace Apps',
									size: 'lg',
								})
						: null,
					title: 'New Projects Using Marketplace Apps',
				},
				{
					onClick: () =>
						navigate(
							`/publishers?filter={"customFields/AccountType":["${AccountType.TECHNOLOGY_PARTNER}"]}&filterSchema=administratorPublishers`
						),
					...getAnnualTargetValues(
						kpiPartnershipIntegration,
						partnershipIntegration.totalCount
					),
					colors: ['#FFB46E', '#FFE9D4'],
					externalPage: true,
					title: 'Technology Partnership With Integrations',
				},
				{
					onClick: () =>
						modal.onOpenModal({
							body: (
								<ol>
									{Object.entries(catalogs).map(
										([catalog, products = []], index) => (
											<li key={index}>
												<span className="font-weight-bold">
													{catalog}
												</span>

												<ol>
													{products.map(
														(
															{name},
															productIndex
														) => (
															<li
																key={
																	productIndex
																}
															>
																{name.en_US}
															</li>
														)
													)}
												</ol>
											</li>
										)
									)}
								</ol>
							),
							header: `Publisher With Apps Supporting Quarterly Release (${supportingQuartelyRelease.totalCount})`,
						}),
					...getAnnualTargetValues(
						kpiQuartelyReleaseApps,
						supportingQuartelyRelease.totalCount
					),
					colors: ['#4B9BFF', '#B1D4FF'],
					title: 'Publisher With Apps Supporting Quarterly Release',
				},
				{
					...getAnnualTargetValues(
						kpiConnectorQuartelyRelease,
						appsAndConnectorSupportingQRelease.totalCount
					),
					colors: ['#FF73C3', '#FFE1F0'],
					externalPage: true,
					onClick: () =>
						navigate(
							`/apps?filter=${liferayQuarterlyVersionsAndConnectors}&filterSchema=administratorApps`
						),
					title: 'Apps & Connectors Supporting Quarterly Release',
				},
				{
					...getAnnualTargetValues(
						kpiLowCodePublishedApps,
						lowCodeConfigurationsPublished.totalCount
					),
					colors: ['#FFD76E', '#FFF3D4'],
					externalPage: true,
					onClick: () =>
						navigate(
							`/apps?filter={"specificationValues|appType":"${ProductType.LOW_CODE_CONFIGURATION}"}&filterSchema=administratorApps`
						),
					title: 'Low Code Configurations Published',
				},
			],
			projectsKPI: {
				...projectsKPI,
				totalAmount: safeJSONParse(
					projectsKPI?.data.metrics.totalAmount.items?.[0]
						?.value as string,
					{USD: 0}
				),
			},
		};
	});
};

export default useKPI;
