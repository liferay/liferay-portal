/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import useSWR from 'swr';

import AppToolbar from '../../../../../components/AppPublish/Navbar';
import Loading from '../../../../../components/Loading';
import {AppFlowList} from '../../../../../components/NewAppFlowList/AppFlowList';
import {MarketplaceTaxonomyVocabularies} from '../../../../../entity/MarketplaceTaxonomyVocabulary';
import {
	ProductTypeVocabulary,
	ProductVocabulary,
} from '../../../../../enums/Product';
import {useAccount} from '../../../../../hooks/data/useAccounts';
import {Liferay} from '../../../../../liferay/liferay';
import HeadlessAdminTaxonomy from '../../../../../services/rest/HeadlessAdminTaxonomy';
import {useAppContext} from './AppContext/AppManageState';
import {initialFLowListItems} from './AppCreationFlowUtil';
import {ChoosePricingModelPage} from './ChoosePricingModelPage/ChoosePricingModelPage';
import {CreateNewAppPage} from './CreateNewAppPage/CreateNewAppPage';
import {DefineAppProfilePage} from './DefineAppProfilePage/DefineAppProfilePage';
import {InformLicensingTermsPage} from './InformLicensingTermsPage/InformLicensingTermsPage';
import {InformLicensingTermsPricePage} from './InformLicensingTermsPage/InformLicensingTermsPricePage';
import {ProvideAppBuildPage} from './ProvideAppBuildPage/ProvideAppBuildPage';
import {ProvideAppSupportAndHelpPage} from './ProvideAppSupportAndHelpPage/ProvideAppSupportAndHelpPage';
import {ProvideVersionDetailsPage} from './ProvideVersionDetailsPage/ProvideVersionDetailsPage';
import {ReviewAndSubmitAppPage} from './ReviewAndSubmitAppPage/ReviewAndSubmitAppPage';
import {CustomizeAppStorefrontPage} from './StorefrontPage/CustomizeAppStorefrontPage';

import './AppCreationFlow.scss';

type SetAppFlowListStateProps = {
	checkedItems?: string[];
	selectedItem: string;
};

type AppCreationFlowProps = {
	catalogId: string;
};

export function AppCreationFlow({catalogId}: AppCreationFlowProps) {
	const [{appERC, appLogo, appName, appProductId, priceModel}] =
		useAppContext();
	const [appFlowListItems, setAppFlowListItems] =
		useState(initialFLowListItems);
	const [currentFlow, setCurrentFlow] = useState('create');
	const [isLoading, setLoading] = useState(false);

	const {data: account} = useAccount();
	const navigate = useNavigate();

	const {data: {areas = [], categories = [], productType, tags = []} = {}} =
		useSWR('/taxonomy-vocabularies', async () => {
			const data =
				await HeadlessAdminTaxonomy.getSiteTaxonomyVocabulariesWithCategories();

			const marketplaceTaxonomyVocabularies =
				new MarketplaceTaxonomyVocabularies(data.items);

			return {
				areas: marketplaceTaxonomyVocabularies
					.getVocabularyCategories(ProductVocabulary.APP_AREA)
					.map((taxonomyCategory) => ({
						...taxonomyCategory,
						label: taxonomyCategory.name,
						value: taxonomyCategory.name,
						vocabulary: ProductVocabulary.APP_AREA,
					})),
				categories:
					marketplaceTaxonomyVocabularies.getVocabularyCategories(
						ProductVocabulary.APP_CATEGORY
					),
				productType:
					marketplaceTaxonomyVocabularies.getVocabularyCategory(
						ProductVocabulary.PRODUCT_TYPE,
						ProductTypeVocabulary.APP
					),
				tags: marketplaceTaxonomyVocabularies
					.getVocabularyCategories(ProductVocabulary.APP_TAGS)
					.map((taxonomyCategory) => ({
						...taxonomyCategory,
						label: taxonomyCategory.name,
						value: taxonomyCategory.name,
						vocabulary: ProductVocabulary.APP_TAGS,
					})),
			};
		});

	const setAppFlowListState = ({
		checkedItems,
		selectedItem,
	}: SetAppFlowListStateProps) => {
		const newAppFlowListItems = appFlowListItems.map((appItem) => {
			if (checkedItems?.includes(appItem.name)) {
				return {
					...appItem,
					checked: true,
					selected: false,
				};
			}

			if (appItem.name === selectedItem) {
				return {
					...appItem,
					checked: false,
					selected: true,
				};
			}

			return {
				...appItem,
				checked: false,
				selected: false,
			};
		});

		setAppFlowListItems(newAppFlowListItems);
	};

	return (
		<div className="app-creation-flow-container">
			<AppToolbar
				accountImage={account?.logoURL}
				accountName={account?.name as string}
				appImage={appLogo?.preview}
				appName={appName}
				exitProps={{
					onClick: () => navigate('../'),
				}}
			/>

			<div className="app-creation-flow-body">
				{isLoading ? (
					<Loading />
				) : (
					<>
						<AppFlowList appFlowListItems={appFlowListItems} />

						{currentFlow === 'create' && (
							<CreateNewAppPage
								catalogId={catalogId}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: ['create'],
										selectedItem: 'profile',
									});

									setCurrentFlow('profile');
								}}
							/>
						)}

						{currentFlow === 'profile' && (
							<DefineAppProfilePage
								areas={areas as unknown as Categories[]}
								categories={categories}
								isLoading={isLoading}
								onClickBack={() => {
									setAppFlowListState({
										selectedItem: 'create',
									});
									setCurrentFlow('create');
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: ['create', 'profile'],
										selectedItem: 'build',
									});

									setCurrentFlow('build');
								}}
								productType={
									productType as unknown as Categories
								}
								setLoading={setLoading}
								tags={tags as unknown as Categories[]}
							/>
						)}

						{currentFlow === 'build' && (
							<ProvideAppBuildPage
								onClickBack={() => {
									setAppFlowListState({
										checkedItems: ['create'],
										selectedItem: 'profile',
									});

									setCurrentFlow('profile');
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
										],
										selectedItem: 'storefront',
									});

									setCurrentFlow('storefront');
								}}
							/>
						)}

						{currentFlow === 'storefront' && (
							<CustomizeAppStorefrontPage
								onClickBack={() => {
									setAppFlowListState({
										checkedItems: ['create', 'profile'],
										selectedItem: 'build',
									});

									setCurrentFlow('build');
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
										],
										selectedItem: 'version',
									});

									setCurrentFlow('version');
								}}
							/>
						)}

						{currentFlow === 'version' && (
							<ProvideVersionDetailsPage
								onClickBack={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
										],
										selectedItem: 'storefront',
									});

									setCurrentFlow('storefront');
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
										],
										selectedItem: 'pricing',
									});

									setCurrentFlow('pricing');
								}}
							/>
						)}

						{currentFlow === 'pricing' && (
							<ChoosePricingModelPage
								onClickBack={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
										],
										selectedItem: 'version',
									});

									setCurrentFlow('version');
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
											'pricing',
										],
										selectedItem: 'licensing',
									});

									setCurrentFlow('licensing');
								}}
							/>
						)}

						{currentFlow === 'licensing' && (
							<InformLicensingTermsPage
								onClickBack={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
										],
										selectedItem: 'pricing',
									});

									setCurrentFlow('pricing');
								}}
								onClickContinue={() => {
									if (priceModel.value !== 'Free') {
										setAppFlowListState({
											checkedItems: [
												'create',
												'profile',
												'build',
												'storefront',
												'version',
												'pricing',
											],
											selectedItem: 'licensing',
										});

										setCurrentFlow('licensingPrice');
									}
									else {
										setAppFlowListState({
											checkedItems: [
												'create',
												'profile',
												'build',
												'storefront',
												'version',
												'pricing',
												'licensing',
											],
											selectedItem: 'support',
										});

										setCurrentFlow('support');
									}
								}}
							/>
						)}

						{currentFlow === 'licensingPrice' && (
							<InformLicensingTermsPricePage
								onClickBack={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
											'pricing',
										],
										selectedItem: 'licensing',
									});

									setCurrentFlow('licensing');
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
											'pricing',
											'licensing',
										],
										selectedItem: 'support',
									});

									setCurrentFlow('support');
								}}
							/>
						)}

						{currentFlow === 'support' && (
							<ProvideAppSupportAndHelpPage
								onClickBack={() => {
									if (priceModel.value !== 'Free') {
										setAppFlowListState({
											checkedItems: [
												'create',
												'profile',
												'build',
												'storefront',
												'version',
												'pricing',
											],
											selectedItem: 'licensing',
										});

										setCurrentFlow('licensingPrice');
									}
									else {
										setAppFlowListState({
											checkedItems: [
												'create',
												'profile',
												'build',
												'storefront',
												'version',
												'pricing',
											],
											selectedItem: 'licensing',
										});

										setCurrentFlow('licensing');
									}
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
											'pricing',
											'licensing',
											'support',
										],
										selectedItem: 'submit',
									});

									setCurrentFlow('submit');
								}}
							/>
						)}

						{currentFlow === 'submit' && (
							<ReviewAndSubmitAppPage
								onClickBack={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
											'pricing',
											'licensing',
											'support',
										],
										selectedItem: 'support',
									});

									setCurrentFlow('support');
								}}
								onClickContinue={() => {
									setAppFlowListState({
										checkedItems: [
											'create',
											'profile',
											'build',
											'storefront',
											'version',
											'pricing',
											'licensing',
											'support',
											'submit',
										],
										selectedItem: '',
									});

									location.href = `${Liferay.ThemeDisplay.getLayoutURL().replace(
										'/create-new-app',
										'/publisher-dashboard'
									)}`;
								}}
								productERC={appERC}
								productId={appProductId}
							/>
						)}
					</>
				)}
			</div>
		</div>
	);
}
