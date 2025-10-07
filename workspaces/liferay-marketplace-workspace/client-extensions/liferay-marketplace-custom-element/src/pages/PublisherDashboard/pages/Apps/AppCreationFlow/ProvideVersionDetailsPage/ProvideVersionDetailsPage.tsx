/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Header} from '../../../../../../components/Header/Header';
import {Input} from '../../../../../../components/Input/Input';
import {NewAppPageFooterButtons} from '../../../../../../components/NewAppPageFooterButtons/NewAppPageFooterButtons';
import {Section} from '../../../../../../components/Section/Section';
import {ProductLicense} from '../../../../../../enums/Product';
import i18n from '../../../../../../i18n';
import {
	createAppSKU,
	getOptions,
	patchSKUById,
	postOption,
	postOptionValue,
	postProductOption,
} from '../../../../../../utils/api';
import {
	createSkuName,
	getCloudOptionBody,
	getCloudProductOptionBody,
	getDxpOptionBody,
	getDxpProductOptionBody,
	getLicenceTypesObject,
	getOptionDeveloperBody,
	getOptionStandardBody,
	getOptionTrialBody,
} from '../../../../../../utils/util';
import {useAppContext} from '../AppContext/AppManageState';
import {ActionTypes} from '../AppContext/actionTypes';

import './ProvideVersionDetailsPage.scss';

import {useState} from 'react';
import useSWR from 'swr';

type ProvideVersionDetailsPageProps = {
	onClickBack: () => void;
	onClickContinue: () => void;
};

export function ProvideVersionDetailsPage({
	onClickBack,
	onClickContinue,
}: ProvideVersionDetailsPageProps) {
	const [isProcessing, setProcessing] = useState(false);

	const [
		{
			appNotes,
			appProductId,
			appType,
			appVersion,
			optionId,
			productOptionId,
		},
		dispatch,
	] = useAppContext();

	const {data: options = []} = useSWR('/publish-product/options', () =>
		getOptions()
	);

	const isCloud = appType.value === 'cloud';
	const isDXP = appType.value === 'dxp';

	const createVersionDescription = async (skuId: number) => {
		await patchSKUById(skuId, {
			customFields: [
				{
					customValue: {
						data: appNotes,
					},
					dataType: 'Text',
					name: 'Version Description',
				},
				{
					customValue: {
						data: appVersion,
					},
					dataType: 'Text',
					name: 'Version',
				},
			],
		});
	};

	const createProductOptions = async () => {
		const cloudOption = options.find(
			({key}) => key === ProductLicense.CLOUD
		);

		const dxpOption = options.find(({key}) => key === ProductLicense.DXP);

		const targetOption = isCloud ? cloudOption : dxpOption;

		let newOptionId: number;

		if (!optionId && !targetOption) {
			newOptionId = await postOption(
				isCloud ? getCloudOptionBody() : getDxpOptionBody()
			);
		}
		else {
			newOptionId = optionId ?? targetOption!.id;
		}

		const productOption = isCloud
			? getCloudProductOptionBody(newOptionId)
			: getDxpProductOptionBody(newOptionId);

		const newProductOptionId = await postProductOption(
			appProductId,
			productOption
		);

		dispatch({
			payload: {value: newOptionId},
			type: ActionTypes.UPDATE_OPTION_ID,
		});

		dispatch({
			payload: {value: newProductOptionId},
			type: ActionTypes.UPDATE_PRODUCT_OPTION_ID,
		});

		const [developerOptionId, standardOptionId, trialOptionId] =
			await Promise.all([
				isDXP
					? postOptionValue(
							getOptionDeveloperBody(),
							newProductOptionId
						)
					: {},
				postOptionValue(getOptionStandardBody(), newProductOptionId),
				postOptionValue(getOptionTrialBody(), newProductOptionId),
			]);

		return {
			developerOptionId,
			newProductOptionId,
			standardOptionId,
			trialOptionId,
		};
	};

	const getSkuBody = (
		sku: string,
		skuProductOptions: Awaited<ReturnType<typeof createProductOptions>>,
		skuName = sku
	) => {
		let value;

		const payload = {
			appProductId,
			body: {
				published: true,
				purchasable: true,
				sku: skuName,
				skuOptions: [
					{
						key: skuProductOptions.newProductOptionId,
						value,
					},
				],
			},
		};

		if (sku === 'DEVELOPER') {
			value = skuProductOptions.developerOptionId;
		}

		if (sku === 'STANDARD') {
			value = skuProductOptions.standardOptionId;
		}

		if (sku === 'TRIAL') {
			value = skuProductOptions.trialOptionId;
		}

		payload.body.skuOptions[0].value = value;

		return payload;
	};

	const createSkus = async (
		skuProductOptions: Awaited<ReturnType<typeof createProductOptions>>
	) => {
		for (const sku of getLicenceTypesObject()) {
			const response = await createAppSKU(
				getSkuBody(
					sku.name,
					skuProductOptions,
					createSkuName(appProductId, appVersion, sku.code)
				)
			);

			if (sku.name === 'TRIAL') {
				dispatch({
					payload: {value: response.id},
					type: ActionTypes.UPDATE_SKU_TRIAL_ID,
				});
			}

			createVersionDescription(response.id);
		}
	};

	return (
		<div className="provide-version-details-page-container">
			<div className="provide-version-details-page-header">
				<Header
					description={i18n.translate(
						'define-version-information-for-your-app-this-will-inform-users-about-this-versions-updates-on-the-storefront'
					)}
					title={i18n.translate('provide-version-details')}
				/>
			</div>

			<Section
				label={i18n.translate('app-version')}
				tooltip={i18n.translate(
					'when-adding-app-versions-you-can-use-your-own-numbering-system-but-be-sure-it-is-consistent-and-understandable-by-the-customer'
				)}
				tooltipText={i18n.translate('more-info')}
			>
				<Input
					helpMessage={i18n.translate(
						'this-is-the-first-version-of-the-app-to-be-published'
					)}
					label={i18n.translate('version')}
					onChange={({target}) =>
						dispatch({
							payload: {value: target.value},
							type: ActionTypes.UPDATE_APP_VERSION,
						})
					}
					placeholder="0.0.0"
					required
					tooltip={i18n.translate(
						'specify-your-apps-version-this-will-help-the-user-understand-the-latest-version-of-your-app-offered-on-the-marketplace'
					)}
					value={appVersion}
				/>

				<Input
					component="textarea"
					label={i18n.translate('notes')}
					onChange={({target}) =>
						dispatch({
							payload: {value: target.value},
							type: ActionTypes.UPDATE_APP_NOTES,
						})
					}
					placeholder={i18n.translate('enter-app-description')}
					required
					tooltip={i18n.translate(
						'notes-pertaining-to-the-release-of-the-project-these-will-be-displayed-when-the-customer-goes-to-purchase-and-or-update-the-app'
					)}
					value={appNotes}
				/>
			</Section>

			<NewAppPageFooterButtons
				disableContinueButton={!appVersion || !appNotes || isProcessing}
				isLoading={isProcessing}
				onClickBack={() => onClickBack()}
				onClickContinue={async () => {
					if (!productOptionId) {
						setProcessing(true);

						const skuProductOptions = await createProductOptions();
						await createSkus(skuProductOptions);

						setProcessing(false);
					}

					onClickContinue();
				}}
			/>
		</div>
	);
}
