/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {useCallback, useMemo, useState} from 'react';
import ReactDOMServer from 'react-dom/server';

import githubIcon from '../../../../../../assets/icons/github_icon.svg';
import {Header} from '../../../../../../components/Header/Header';
import {NewAppPageFooterButtons} from '../../../../../../components/NewAppPageFooterButtons/NewAppPageFooterButtons';
import {PackageVersionModal} from '../../../../../../components/PackageVersionModal/PackageVersionModal';
import {RadioCard} from '../../../../../../components/RadioCard/RadioCard';
import {Section} from '../../../../../../components/Section/Section';
import {ProductEditionOption} from '../../../../../../enums/ProductEditionOption';
import {ProductSpecification} from '../../../../../../enums/ProductSpecification';
import {ProductType} from '../../../../../../enums/ProductType';
import {ProductUploadType} from '../../../../../../enums/ProductUploadType';
import {ProductVersionOption} from '../../../../../../enums/ProductVersionOption';
import {ProductVocabulary} from '../../../../../../enums/ProductVocabulary';
import i18n from '../../../../../../i18n';
import {
	createProductSpecification,
	createProductVirtualEntry,
	getCategories,
	getProductIdCategories,
	getProductSpecifications,
	getSpecification,
	getVocabularies,
	patchProductIdCategory,
	updateProductSpecification,
} from '../../../../../../utils/api';
import {useAppContext} from '../AppContext/AppManageState';
import {TYPES} from '../AppContext/actionTypes';
import {offeringTypesDescription} from './constants/offeringTypesDescriptions';

import './ProvideAppBuildPage.scss';
import {CardView} from '../../../../../../components/Card/CardView';
import {
	PRODUCT_SPECIFICATION_KEY,
	PRODUCT_WORKFLOW_STATUS_CODE,
} from '../../../../../../enums/Product';
import HeadlessCommerceAdminCatalogImpl from '../../../../../../services/rest/HeadlessCommerceAdminCatalog';
import {base64ToText, fileToBase64} from '../../../../../../utils/file';
import ResourceRequirements from './ResourceRequirements';
import UploadAppPackagesComponent from './components/UploadAppPackages';

type ProvideAppBuildPageProps = {
	onClickBack: () => void;
	onClickContinue: () => void;
};

type BodyProductSpecificationProps = {
	productId: number;
	specificationKey: string;
	value: number | string;
};

export function ProvideAppBuildPage({
	onClickBack,
	onClickContinue,
}: ProvideAppBuildPageProps) {
	const [isProcessing, setProcessing] = useState(false);

	const [
		{
			appBuild,
			appERC,
			appProductId,
			appType,
			buildAppPackages,
			resourceRequirements,
			virtualSettingId,
		},
		dispatch,
	] = useAppContext();

	const [visibleSelectVersionModal, setVisibleSelectVersionModal] =
		useState(false);

	const bodySpecification = useMemo(
		() => [
			{
				productId: appProductId,
				specificationKey: 'cpu',
				value: resourceRequirements.cpu,
			},
			{
				productId: appProductId,
				specificationKey: 'ram',
				value: resourceRequirements.ram,
			},
		],
		[appProductId, resourceRequirements.cpu, resourceRequirements.ram]
	);

	const handleResetAppPackages = useCallback(
		() =>
			dispatch({
				type: TYPES.RESET_APP_PACKAGES,
			}),
		[dispatch]
	);

	const handleRemovePackageVersion = (removedVersion: string) => {
		dispatch({
			payload: {
				isRemoved: true,
				versionName: removedVersion,
			},
			type: TYPES.UPLOAD_BUILD_PACKAGE_FILES,
		});
	};

	const submitAppBuildCategories = async () => {
		const vocabulariesResponse = await getVocabularies();

		const categories = await getProductIdCategories({
			appId: appProductId.toString(),
		});

		let newCategories: Categories[] = [];

		let marketplaceLiferayPlatformOfferingId = 0;
		let marketplaceLiferayVersionId = 0;
		let marketplaceEditionId = 0;

		vocabulariesResponse.items.forEach(
			(vocab: {id: number; name: string}) => {
				if (
					vocab.name === ProductVocabulary.LIFERAY_PLATFORM_OFFERING
				) {
					marketplaceLiferayPlatformOfferingId = vocab.id;
				}

				if (vocab.name === ProductVocabulary.LIFERAY_VERSION) {
					marketplaceLiferayVersionId = vocab.id;
				}

				if (vocab.name === ProductVocabulary.EDITION) {
					marketplaceEditionId = vocab.id;
				}
			}
		);

		const platformOfferingList = await getCategories({
			vocabId: marketplaceLiferayPlatformOfferingId,
		});

		const platformOfferingLabels = (
			offeringTypesDescription[
				appType.value as ProductType
			] as unknown as OfferingType[]
		)
			?.filter((type) => !type.disabled)
			.map((type) => type.label);

		const fullyManagedOption = platformOfferingList.filter(
			(platformOffering) =>
				platformOfferingLabels.includes(platformOffering.name)
		);

		fullyManagedOption.forEach((managedOption) => {
			newCategories.push({
				externalReferenceCode: managedOption?.externalReferenceCode,
				id: managedOption.id,
				name: managedOption.name,
				vocabulary: ProductVocabulary.LIFERAY_PLATFORM_OFFERING,
			});
		});

		if (appType.value === ProductType.CLOUD) {
			const liferayVersionList = await getCategories({
				vocabId: marketplaceLiferayVersionId,
			});

			const liferayVersionOption = liferayVersionList.find(
				(item) => item.name === ProductVersionOption['7.4x']
			);

			if (liferayVersionOption) {
				newCategories.push({
					externalReferenceCode:
						liferayVersionOption?.externalReferenceCode,
					id: liferayVersionOption.id,
					name: liferayVersionOption.name,
					vocabulary: ProductVocabulary.LIFERAY_VERSION,
				});
			}

			const marketplaceEditionList = await getCategories({
				vocabId: marketplaceEditionId,
			});

			const marketplaceEditionOption = marketplaceEditionList.find(
				(item) => item.name === ProductEditionOption.EE
			);

			if (marketplaceEditionOption) {
				newCategories.push({
					externalReferenceCode:
						marketplaceEditionOption?.externalReferenceCode,
					id: marketplaceEditionOption.id,
					name: marketplaceEditionOption.name,
					vocabulary: ProductVocabulary.EDITION,
				});
			}

			newCategories = [...categories.items, ...newCategories];
		}
		else {
			newCategories = [
				...categories.items.filter((category) => {
					if (
						category.vocabulary !==
							ProductVocabulary.EDITION.toLowerCase() &&
						category.vocabulary !==
							ProductVocabulary.LIFERAY_VERSION.toLowerCase()
					) {
						return category;
					}
				}),
				...newCategories,
			];
		}

		await patchProductIdCategory({
			appId: appProductId.toString(),
			body: newCategories,
		});
	};

	const submitAppBuildPackages = async () => {
		const items = [];
		const liferayVersionSpecifications = [];

		const dataProductSpecifications = await getProductSpecifications({
			appProductId,
		});

		const filteredProductSpecifications = dataProductSpecifications.filter(
			(specification) =>
				specification.specificationKey !==
				PRODUCT_SPECIFICATION_KEY.LIFERAY_VERSION
		);

		for (const versionKey in buildAppPackages) {
			const appPackagesByVersion = buildAppPackages[versionKey];

			for (const appPackage of appPackagesByVersion) {
				items.push({
					attachment: base64ToText(
						(await fileToBase64(appPackage.file)) as string
					),
					fileName: appPackage.fileName,
					id: appPackage.id,
					version: versionKey,
				});
				liferayVersionSpecifications.push({
					specificationKey: PRODUCT_SPECIFICATION_KEY.LIFERAY_VERSION,
					value: {
						en_US: versionKey,
					},
				});
			}
		}

		await HeadlessCommerceAdminCatalogImpl.updateProductByExternalReferenceCode(
			appERC,
			{
				productSpecifications: [
					...filteredProductSpecifications,
					...liferayVersionSpecifications,
				],
				productStatus: PRODUCT_WORKFLOW_STATUS_CODE.DRAFT,
			}
		);

		for (const item of items) {
			const {attachment, fileName, id, version} = item;

			if (!attachment) {
				continue;
			}

			const formData = new FormData();
			const blob = new Blob([attachment]);

			formData.append('file', blob, fileName);
			formData.append(
				'productVirtualSettingsFileEntry',
				JSON.stringify({attachment, version})
			);

			await createProductVirtualEntry({
				body: formData,
				callback: (progress) => {
					buildAppPackages[version] = buildAppPackages[version].map(
						(file) =>
							file.id === id
								? {
										...file,
										progress,
										uploaded: progress === 100,
									}
								: file
					);

					dispatch({
						payload: buildAppPackages,
						type: TYPES.UPDATE_BUILD_PACKAGE_FILES,
					});
				},
				virtualSettingId,
			});

			return;
		}
	};

	const submitAppBuildCloudResourceRequirements = async (
		productSpecifications: BodyProductSpecificationProps[]
	) => {
		const dataSpecificationList = await getProductSpecifications({
			appProductId,
		});

		for (const productSpecification of productSpecifications) {
			const dataSpecification = dataSpecificationList?.find(
				(specification) =>
					specification?.specificationKey ===
					productSpecification.specificationKey
			);

			const fn = dataSpecification?.id
				? updateProductSpecification
				: createProductSpecification;

			await fn({
				body: {
					specificationKey: productSpecification.specificationKey,
					value: {en_US: productSpecification.value},
				},
				id: dataSpecification?.id || appProductId,
			});
		}
	};

	const submitAppBuildTypeSpecification = async () => {
		if (appType.id) {
			return updateProductSpecification({
				body: {
					specificationKey: ProductSpecification.TYPE.toLowerCase(),
					value: {en_US: appType.value},
				},
				id: appType.id,
			});
		}

		const dataSpecification = await getSpecification('type');

		const {id} = await createProductSpecification({
			body: {
				specificationId: dataSpecification.id,
				specificationKey: dataSpecification.key,
				value: {en_US: appType.value},
			},
			id: appProductId,
		});

		dispatch({
			payload: {id, value: appType.value},
			type: TYPES.UPDATE_APP_LXC_COMPATIBILITY,
		});
	};

	const handleAppTypeChange = (value: ProductType) => {
		dispatch({
			payload: {
				id: appType.id,
				value,
			},
			type: TYPES.UPDATE_APP_LXC_COMPATIBILITY,
		});

		handleResetAppPackages();
	};

	const buildAppPackageVersions = Object.keys(buildAppPackages ?? {});
	const buildAppPackageValues = Object.values(buildAppPackages ?? {}) ?? [];
	const isCloud = appType.value === 'cloud';

	const isResourceRequirementsValid = isCloud
		? bodySpecification.every(({value}) => value)
		: true;

	const disableContinueButton =
		isProcessing ||
		!isResourceRequirementsValid ||
		!buildAppPackageValues.length ||
		buildAppPackageValues.some((versionEntry) => !versionEntry?.length);

	const BuildAppPackageVersionsComponent = () => (
		<>
			{buildAppPackageVersions.map((version, index) => (
				<div
					className="mt-4 provide-app-build-page-dropzone-container"
					key={index}
				>
					<div className="align-center d-flex font-weight-bold justify-content-between p-3 provide-app-build-page-dropzone-container-header">
						<span>{version}</span>
						<ClayButton
							displayType="unstyled"
							onClick={() => handleRemovePackageVersion(version)}
						>
							{i18n.translate('remove-a-version')}
						</ClayButton>
					</div>

					<UploadAppPackagesComponent
						isProcessing={isProcessing}
						versionName={version}
					/>
				</div>
			))}

			{!isProcessing && (
				<ClayButton
					className="btn-block provide-app-build-page-add-package-button"
					displayType="secondary"
					onClick={() => setVisibleSelectVersionModal(true)}
				>
					<ClayIcon className="mr-1" symbol="plus" />
					{i18n.translate('add-packages')}
				</ClayButton>
			)}
		</>
	);

	return (
		<div className="provide-app-build-page-container">
			<Header
				description={i18n.translate(
					'use-one-of-the-following-methods-to-provide-your-app-builds'
				)}
				title={i18n.translate('provide-app-build')}
			/>

			<Section
				label={i18n.translate('cloud-compatible')}
				required
				tooltip={i18n.translate(
					'a-cloud-app-is-a-client-extension-delivered-as-a-deployed-service-to-liferay-saas-and-liferay-paas-customers-dxp-apps-include-jar-based-collection-meant-to-run-within-liferay-dxp-fragments-client-extensions-that-do-not-require-dedicated-resources'
				)}
				tooltipText={i18n.translate('more-info')}
			>
				<div className="provide-app-build-page-cloud-compatible-container">
					<RadioCard
						description={i18n.translate(
							'create-a-cloud-app-to-be-delivered-as-a-live-service'
						)}
						icon="check-circle"
						onChange={() => handleAppTypeChange(ProductType.CLOUD)}
						selected={appType.value === ProductType.CLOUD}
						title={i18n.translate('yes')}
						tooltip={ReactDOMServer.renderToString(
							<span>
								{i18n.translate(
									'the-app-submission-is-compatible-with-liferay-experience-cloud-and'
								)}
								<a
									href="https://learn.liferay.com/web/guest/w/dxp/building-applications/client-extensions#client-extensions"
									target="_blank"
								>
									{i18n.translate('client-extensions')}
								</a>
								.
							</span>
						)}
					/>

					<RadioCard
						description={i18n.translate(
							'create-a-dxp-app-to-be-delivered-as-a-download'
						)}
						icon="times-circle"
						onChange={() => handleAppTypeChange(ProductType.DXP)}
						selected={appType.value === ProductType.DXP}
						title={i18n.translate('no')}
						tooltip={i18n.translate(
							'the-app-submission-is-integrates-with-liferay-dxp-version-7-4-or-later'
						)}
					/>
				</div>
			</Section>

			{appType.value && (
				<>
					<Section label={i18n.translate('compatible-offering')}>
						{(
							offeringTypesDescription[
								appType.value as ProductType
							] as unknown as OfferingType[]
						).map((type) => {
							return type.disabled ? null : (
								<CardView
									description={type.description}
									title={type.label}
								/>
							);
						})}
					</Section>

					{isCloud && (
						<Section
							label={i18n.translate('resource-requirements')}
							required
							tooltip={i18n.translate(
								'cloud-apps-must-state-resource-requirements-if-your-app-has-no-additional-cpu-or-ram-requirements-please-enter-0'
							)}
							tooltipText={i18n.translate('more-info')}
						>
							<div className="provide-app-build-page-resource-requirements">
								<ResourceRequirements />
							</div>
						</Section>
					)}

					<Section
						label={i18n.translate('app-build')}
						required
						tooltip={i18n.translate(
							'an-app-build-is-your-compiled-or-non-compiled-code-submitted-on-behalf-of-your-account-to-the-marketplace-once-submitted-it-will-be-reviewed-and-tested-by-our-marketplace-administrators-for-approval-in-the-marketplace'
						)}
						tooltipText={i18n.translate('more-info')}
					>
						<div className="provide-app-build-page-app-build-radio-container">
							<RadioCard
								description={i18n.translate(
									appType.value === ProductType.CLOUD
										? 'use-any-local-zip-files-to-upload-max-file-size-is-500-mb'
										: 'please-be-sure-to-specify-liferay-compatibility-through-the-appropriate-properties-or-xml-files-in-your-plugin'
								)}
								icon="upload"
								onChange={() =>
									dispatch({
										payload: {
											value: ProductUploadType.ZIP_UPLOAD,
										},
										type: TYPES.UPDATE_APP_BUILD,
									})
								}
								selected={
									appBuild === ProductUploadType.ZIP_UPLOAD
								}
								title={
									appType.value === ProductType.CLOUD
										? i18n.translate('via-zip-upload')
										: i18n.translate(
												'via-liferay-plugin-packages'
											)
								}
								tooltip={ReactDOMServer.renderToString(
									<span>
										{i18n.translate(
											'zip-files-must-be-in-universal-file-format-archive-uffa-the-specially-structured-zip-encoded-archive-used-to-package-client-extension-project-outputs-this-format-must-support-the-following-use-cases-deliver-batch-engine-data-files-compatible-with-all-deployment-targets-deliver-dxp-configuration-resource-compatible-with-all-deployment-targets-deliver-static-resources-compatible-with-all-deployment-targets-deliver-the-infrastructure-metadata-necessary-to-deploy-to-lxc-sm-for-more-information-see'
										)}

										<a href="https://learn.liferay.com/web/guest/w/dxp/building-applications/client-extensions/working-with-client-extensions#working-with-client-extensions">
											{i18n.translate('liferay-learn')}
										</a>
									</span>
								)}
							/>

							<RadioCard
								description={i18n.translate(
									'use-any-build-from-any-available-liferay-experience-cloud-account-requires-lxc-account'
								)}
								disabled
								icon="cloud"
								onChange={() =>
									dispatch({
										payload: {value: ProductUploadType.LXC},
										type: TYPES.UPDATE_APP_BUILD,
									})
								}
								selected={appBuild === ProductUploadType.LXC}
								title={i18n.translate(
									'via-liferay-experience-cloud-integration'
								)}
								tooltip={i18n.translate(
									'in-the-future-you-will-be-able-to-submit-your-app-directly-from-liferay-experience-cloud-projects'
								)}
							/>

							<RadioCard
								description={i18n.translate(
									'use-any-build-from-your-computer-connecting-with-a-github-provider'
								)}
								disabled
								icon={githubIcon}
								onChange={() => {
									dispatch({
										payload: {
											value: ProductUploadType.GITHUB,
										},
										type: TYPES.UPDATE_APP_BUILD,
									});
								}}
								selected={appBuild === ProductUploadType.GITHUB}
								title={i18n.translate('via-github-repo')}
								tooltip={i18n.translate(
									'in-the-future-you-will-be-able-to-submit-your-app-source-code-for-additional-support-and-partnership-opportunities-with-liferay'
								)}
							/>
						</div>
					</Section>

					<Section
						description={i18n.translate(
							appType.value === ProductType.CLOUD
								? 'select-a-local-file-to-upload'
								: 'if-the-app-is-compatible-with-different-updates-of-74-please-upload-multiple-packages-for-each-update-or-update-compatibility-range'
						)}
						label={i18n.translate(
							appType.value === ProductType.CLOUD
								? 'upload-zip-files'
								: 'upload-liferay-plugin-packages'
						)}
						required
						tooltip={i18n.translate(
							appType.value === ProductType.CLOUD
								? 'you-can-upload-one-or-many-zip-files-max-total-size-is-500-mb'
								: 'only-jar-war-files-are-allowed-max-file-size-is-500mb'
						)}
						tooltipText={i18n.translate('more-info')}
					>
						<BuildAppPackageVersionsComponent />

						{visibleSelectVersionModal && (
							<PackageVersionModal
								appProductId={appProductId}
								currentVersions={buildAppPackageVersions}
								handleClose={() =>
									setVisibleSelectVersionModal(false)
								}
							/>
						)}
					</Section>
				</>
			)}

			<NewAppPageFooterButtons
				disableContinueButton={disableContinueButton}
				isLoading={isProcessing}
				onClickBack={() => onClickBack()}
				onClickContinue={async () => {
					setProcessing(true);

					try {
						await submitAppBuildCategories();
						await submitAppBuildPackages();
						await submitAppBuildTypeSpecification();

						if (isCloud) {
							await submitAppBuildCloudResourceRequirements(
								bodySpecification
							);
						}
					}
					catch (error) {
						console.error(
							'Something went wrong to buildCategores | buildTypeSpecifications | buildPackages | buildClouldResourceRequirements'
						);
					}

					setProcessing(false);

					onClickContinue();
				}}
				showBackButton
			/>
		</div>
	);
}
