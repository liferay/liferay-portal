/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useEffect, useState} from 'react';

import {Checkbox} from '../../../../../../components/Checkbox/Checkbox';
import {Header} from '../../../../../../components/Header/Header';
import {NewAppPageFooterButtons} from '../../../../../../components/NewAppPageFooterButtons/NewAppPageFooterButtons';
import {Section} from '../../../../../../components/Section/Section';
import {getTierPrice} from '../../../../../../utils/api';
import {
	getThumbnailByProductAttachment,
	showAppImage,
} from '../../../../../../utils/util';
import {CardSectionsBody} from './CardSectionsBody';
import {App, supportAndHelpMap} from './ReviewAndSubmitAppPageUtil';

import './ReviewAndSubmitAppPage.scss';
import {useMarketplaceContext} from '../../../../../../context/MarketplaceContext';
import {MarketplaceCategories} from '../../../../../../enums/Categories';
import {ProductSpecificationKey} from '../../../../../../enums/Product';
import {Liferay} from '../../../../../../liferay/liferay';
import HeadlessCommerceAdminCatalog from '../../../../../../services/rest/HeadlessCommerceAdminCatalog';
import {getProductCategoriesByVocabularyName} from '../../../../../../utils/productUtils';

type ReviewAndSubmitAppPageProps = {
	onClickBack?: () => void;
	onClickContinue?: () => void;
	productERC?: string;
	productId?: number;
	readonly?: boolean;
};

export function ReviewAndSubmitAppPage({
	onClickBack = () => {},
	onClickContinue = () => {},
	productERC,
	productId,
	readonly = false,
}: ReviewAndSubmitAppPageProps) {
	const {channel} = useMarketplaceContext();
	const accountId = Liferay.CommerceContext.account?.accountId;

	const [checked, setChecked] = useState(false);
	const [app, setApp] = useState<App>();
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		const getData = async () => {
			setLoading(true);

			const product =
				await HeadlessCommerceAdminCatalog.getProductByExternalReferenceCode(
					productERC as string,
					new URLSearchParams({
						nestedFields:
							'attachments,images,skus,productSpecifications',
					})
				);

			const {
				categories = [],
				productSpecifications = [],
				skus = [],
			} = product;

			const productCategories = getProductCategoriesByVocabularyName(
				categories,
				MarketplaceCategories.MARKETPLACE_APP_CATEGORY
			);

			const productTags = getProductCategoriesByVocabularyName(
				categories,
				MarketplaceCategories.MARKETPLACE_APP_TAGS
			);

			const isCloud =
				productSpecifications.some(
					({specificationKey, value}) =>
						specificationKey === ProductSpecificationKey.APP_TYPE &&
						(value.en_US === 'cloud' ||
							(value as unknown as string) === 'cloud')
				) ?? false;

			let sku = skus[0];

			const tierPrices = await getTierPrice(
				channel?.id,
				product?.productId,
				Number(accountId)
			);

			if (isCloud) {
				sku = skus.find(
					({skuOptions: [trialOption]}) => trialOption?.value === 'no'
				) as SKU;
			}

			const dataProduct = {
				'cpu': '',
				'license-type': '',
				'price-model': '',
				'ram': '',
				'type': '',
				'version': '',
				'versionDescription': '',
			};

			sku?.customFields?.forEach(({customValue, name}) => {
				if (name === 'Version') {
					dataProduct.version = customValue.data as string;
				}

				if (name === 'Version Description') {
					dataProduct.versionDescription = customValue.data as string;
				}
			});

			const supportAndHelpCardInfos: {
				icon: string;
				link: string;
				title: string;
			}[] = [];

			productSpecifications.forEach((specification) => {
				const {specificationKey, value} = specification;
				const localizedValue = value['en_US'];

				if (
					[
						'appdocumentationurl',
						'appinstallationguideurl',
						'appusagetermsurl',
						'publisherwebsiteurl',
						'supportemailaddress',
						'supportphone',
						'supporturl',
					].includes(specificationKey)
				) {
					supportAndHelpCardInfos.push({
						...(supportAndHelpMap.get(specificationKey) as {
							icon: string;
							title: string;
						}),
						link: localizedValue,
					});
				}

				(dataProduct as any)[specificationKey as string] =
					localizedValue;
			});

			const attachment = product.attachments.find((attachment) => {
				return (attachment.tags || []).indexOf('app icon') < 0;
			});

			const thumbnail = showAppImage(
				getThumbnailByProductAttachment(product.images)
			);

			const newApp = {
				attachmentTitle: attachment?.title['en_US'] as string,
				categories: productCategories,
				description: product.description['en_US'],
				name: product.name['en_US'],
				price: sku?.price as number,
				resourceRequirements: {
					cpu: dataProduct.cpu,
					ram: dataProduct.ram,
				},
				skus,
				storefront: (product.images || []).filter(
					(image) => image.galleryEnabled
				),
				supportAndHelp: supportAndHelpCardInfos,
				tags: productTags,
				thumbnail,
				tierPrice: tierPrices,
				...dataProduct,
			};

			setApp(newApp);

			setLoading(false);
		};

		getData();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [productERC, productId]);

	return (
		<div className="review-and-submit-app-page-container">
			{!readonly && (
				<div className="review-and-submit-app-page-header">
					<Header
						description="Please, review before submitting. Once sent, you will not be able to edit any information until this submission is completely reviewed by Liferay."
						title="Review and submit app"
					/>
				</div>
			)}

			<Section
				disabled={readonly}
				label={readonly ? '' : 'App Submission'}
				tooltip={readonly ? '' : 'More info'}
				tooltipText={readonly ? '' : 'More Info'}
			>
				<div className="review-and-submit-app-page-card-container">
					{!readonly && (
						<div className="review-and-submit-app-page-card-header">
							<div className="review-and-submit-app-page-card-header-left-content">
								<div className="review-and-submit-app-page-card-header-icon-container">
									<img
										alt="New App logo"
										className="review-and-submit-app-page-card-header-icon"
										src={showAppImage(app?.thumbnail)}
									/>
								</div>

								<div className="review-and-submit-app-page-card-header-title">
									<span className="review-and-submit-app-page-card-header-title-text">
										{app?.name}
									</span>

									<span className="review-and-submit-app-page-card-header-title-version">
										{app?.version}
									</span>
								</div>
							</div>
						</div>
					)}

					<div className="review-and-submit-app-page-card-body">
						{loading ? (
							<ClayLoadingIndicator
								displayType="primary"
								shape="circle"
								size="md"
							/>
						) : (
							<CardSectionsBody
								app={app as App}
								readonly={readonly}
							/>
						)}
					</div>
				</div>
			</Section>

			{!readonly && (
				<div className="review-and-submit-app-page-agreement">
					<Checkbox
						checked={checked}
						onChange={() => {
							setChecked(!checked);
						}}
					></Checkbox>

					<span>
						<span className="review-and-submit-app-page-agreement-highlight">
							{'Attention: this cannot be undone. '}
						</span>
						I am aware I cannot edit any data or information
						regarding this app submission until Liferay completes
						its review process and I agree with the Liferay
						Marketplace <a href="#">terms</a> and{' '}
						<a href="#">privacy</a>
					</span>
				</div>
			)}

			{!readonly && (
				<NewAppPageFooterButtons
					continueButtonText="Submit App"
					disableContinueButton={!checked}
					onClickBack={() => onClickBack()}
					onClickContinue={() => onClickContinue()}
					showBackButton={true}
				/>
			)}
		</div>
	);
}
