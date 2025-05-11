/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useMemo, useState} from 'react';
import {useForm} from 'react-hook-form';
import {useNavigate, useParams} from 'react-router-dom';
import {z} from 'zod';

import FooterButtons from '../../../../../../../components/FooterButtons';
import {useMarketplaceContext} from '../../../../../../../context/MarketplaceContext';
import {Analytics} from '../../../../../../../core/Analytics';
import {MarketplaceDeliveryProduct} from '../../../../../../../entity/MarketplaceDeliveryProduct';
import useGetProductByOrderId from '../../../../../../../hooks/useGetProductByOrderId';
import {Liferay} from '../../../../../../../liferay/liferay';
import zodSchema from '../../../../../../../schema/zod';
import provisioningOAuth2 from '../../../../../../../services/oauth/Provisioning';
import ProductCard from '../../../../../../GetApp/components/ProductCard/ProductCard';
import StepWizard from '../../../../../../GetApp/components/StepWizard/StepWizard';
import AccountEmailInfo from './AccountInfo';
import LicenseDetails from './LicenseDetails';
import SelectSubscription from './SelectSubscription';
import {CreateLicenseForm, StepCreateLicense, StepsInformation} from './types';

import './index.scss';
import {formatDate} from '../../../../../../../utils/date';

type ExtendBannerProps = {
	subscription: {
		endDate?: string;
		name: string;
		startDate: string;
	};
};

const ExtendBanner: React.FC<ExtendBannerProps> = ({subscription}) => (
	<>
		<div className="align-items-center d-flex mb-3 row">
			<small className="col-6 col-md-4 font-weight-bold m-0">
				Key type
			</small>
			<small className="col-6 col-md-4 subscription-banner-text text-capitalize">
				{subscription?.name}
			</small>
		</div>

		<div className="align-items-center d-flex row">
			<small className="col-6 col-md-4 font-weight-bold m-0">
				Start Date - Exp. Date
			</small>
			<small className="col-6 col-md-4 subscription-banner-text text-nowrap">
				{formatDate(subscription?.startDate)} &ndash;{' '}
				{subscription?.endDate
					? formatDate(subscription?.endDate)
					: 'DNE'}
			</small>
		</div>
	</>
);

const stepsInformation: StepsInformation = {
	[StepCreateLicense.SUBSCRIPTION]: {
		backStep: StepCreateLicense.SUBSCRIPTION,
		nextStep: StepCreateLicense.LICENSE_KEY_DETAILS,
		stepTitle: 'Subscription',
		title: 'Subscription',
	},
	[StepCreateLicense.LICENSE_KEY_DETAILS]: {
		backStep: StepCreateLicense.SUBSCRIPTION,
		nextStep: StepCreateLicense.SUBSCRIPTION,
		stepTitle: 'License Key Details',
		title: 'License Key Details',
	},
};

const CreateLicense = () => {
	const [loading, setLoading] = useState(false);
	const [step, setStep] = useState(StepCreateLicense.SUBSCRIPTION);
	const {orderId} = useParams();
	const {myUserAccount} = useMarketplaceContext();
	const {data} = useGetProductByOrderId(orderId as string);

	const navigate = useNavigate();
	const product = data?.product;

	const productCreatorAccountName = product?.catalogName || '';

	const {
		formState: {errors},
		getValues,
		register,
		setValue,
		watch,
	} = useForm<CreateLicenseForm>({
		defaultValues: {
			description: '',
			hostname: '',
			ipAddress: '',
			macAddress: '',
			subscription: undefined,
		},
	});

	const {description, hostname, ipAddress, macAddress, subscription} =
		watch();

	useEffect(() => {
		if (product) {
			const {familyName, givenName} = myUserAccount;

			setValue(
				'description',
				`${givenName} ${familyName} - ${product.name} - ${subscription?.name}`
			);
		}
	}, [myUserAccount, product, setValue, subscription?.name]);

	const disableContinueButton =
		(ipAddress === '' && hostname === '' && macAddress === '') ||
		description === '';

	const inputProps = {
		errors,
		register,
		required: true,
	};

	const handleNextButton = useCallback(
		async (form: z.infer<typeof zodSchema.generateLicenseKey>) => {
			setLoading(true);

			const marketplaceProduct = new MarketplaceDeliveryProduct(
				product as DeliveryProduct
			);

			try {
				const licenseKey = await provisioningOAuth2.createLicenseKey({
					licenseEntry: {
						description: form.description,
						hostName: form.hostname,
						ipAddresses: form.ipAddress.replaceAll('\n', ','),
						macAddresses: form.macAddress.replaceAll('\n', ','),
						orderId: orderId as string,
						productId:
							marketplaceProduct.specificationValues
								.APP_ENTRY_UUID || undefined,
						productPurchaseKey: form.subscription
							?.productPurchasedKey as string,
						productVersion:
							form.subscription?.productVersion ||
							marketplaceProduct.specificationValues
								.APP_VERSION ||
							'1.0.0',
					},
					skuId: form.subscription?.skuId as number,
					type: form.subscription?.name as string,
				});

				Liferay.Util.openToast({
					message: 'License Key created successfully',
					type: 'success',
				});

				Analytics.track('CREATE_LICENSE_KEY', {
					licenseType: licenseKey.licenseType,
					productName: product?.name,
					type: form.subscription?.name,
				});

				navigate(`/order/${orderId}/licenses`);

				await provisioningOAuth2.downloadLicenseKey(licenseKey.id);

				Analytics.track('DOWNLOAD_LICENSE_KEY', {
					licenseType: licenseKey.licenseType,
					productName: product?.name,
				});
			}
			catch {
				Liferay.Util.openToast({
					message: 'Something went wrong to create a License Key',
					type: 'danger',
				});
			}

			setLoading(false);
		},
		[navigate, orderId, product]
	);

	const buttonsInfo = useMemo(
		() => ({
			cancelButton: {
				displayType: 'unstyled',
				onClick: () => navigate('..'),
				show: true,
			},
			customizedButton: {
				displayType: 'secondary',
				onClick: () => setStep(StepCreateLicense.SUBSCRIPTION),
				show: step !== StepCreateLicense.SUBSCRIPTION,
				text: 'Back',
			},
			nextButton: {
				className: 'ml-6',
				disabled:
					loading ||
					(!subscription &&
						step === StepCreateLicense.SUBSCRIPTION) ||
					(disableContinueButton &&
						step !== StepCreateLicense.SUBSCRIPTION),
				displayType: 'primary',
				onClick: () => {
					if (step === StepCreateLicense.SUBSCRIPTION) {
						return setStep(StepCreateLicense.LICENSE_KEY_DETAILS);
					}

					handleNextButton(getValues());
				},
				show: true,
				text: 'Generate Key',
			},
		}),
		[
			disableContinueButton,
			getValues,
			handleNextButton,
			loading,
			navigate,
			step,
			subscription,
		]
	);

	return (
		<div className="align-items-center d-flex flex-column mb-6 mkt-create-license mt-6">
			<div className="mt-6 product-card-content">
				<ProductCard
					ExtendBanner={<ExtendBanner subscription={subscription} />}
					RightSideBanner={
						<AccountEmailInfo userAccount={myUserAccount} />
					}
					creatorAccountName={productCreatorAccountName}
					product={product as DeliveryProduct}
					showExtendBanner={
						step === StepCreateLicense.LICENSE_KEY_DETAILS
					}
				/>
			</div>

			<div className="d-flex flex-column generate-license-content justify-content-center mb-7 mt-7 p-6">
				<div className="align-self-center h1">
					Generate License Key(s)
				</div>

				<div className="d-flex justify-content-center mb-6 mt-6">
					<StepWizard
						className="col-8"
						currentStep={step}
						stepsInformation={stepsInformation}
						wizardSteps={{
							[StepCreateLicense.SUBSCRIPTION]:
								step !== StepCreateLicense.SUBSCRIPTION,
							[StepCreateLicense.LICENSE_KEY_DETAILS]: false,
						}}
					/>
				</div>

				<div>
					{step === StepCreateLicense.SUBSCRIPTION ? (
						<SelectSubscription
							onSelectSubscription={(subscription: any) => {
								setValue('subscription', subscription);
							}}
							selectedSubscriptionValue={subscription}
						/>
					) : (
						<LicenseDetails inputProps={inputProps} />
					)}
				</div>

				<FooterButtons
					className="d-flex justify-content-between mt-6"
					dataButtons={buttonsInfo}
				/>
			</div>
		</div>
	);
};

export default CreateLicense;
