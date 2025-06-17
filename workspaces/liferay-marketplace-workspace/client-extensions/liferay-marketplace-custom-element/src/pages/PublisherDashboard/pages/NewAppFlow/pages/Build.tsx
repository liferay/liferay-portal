/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {useState} from 'react';

import {RadioCard} from '../../../../../components/RadioCard/RadioCard';
import {Section} from '../../../../../components/Section/Section';
import {
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import {
	ProductType,
	ProductWorkflowStatusCode,
} from '../../../../../enums/Product';
import i18n from '../../../../../i18n';
import {ProductTypeOptions} from '../../Apps/AppCreationFlow/ProvideAppBuildPage/constants/productTypes';
import CloudResourceRequirements from '../components/CloudResourceRequirements';
import {NewAppPackageVersionModal} from '../components/NewAppPackagesModal';
import NewAppUploadAppPackagesComponent from '../components/NewAppUploadPackage';
import {BUILD_UPLOAD_OPTIONS} from '../constants';

type ProductTypeOption = {
	description: string;
	label: string;
	value: ProductType;
};

const BuildContent = () => {
	const [
		{
			build: {appType, liferayPackages},
			loading,
		},
		dispatch,
	] = useNewAppContext();

	const [visibleSelectVersionModal, setVisibleSelectVersionModal] =
		useState(false);

	return (
		<>
			{appType === ProductType.CLOUD && (
				<Section
					className="d-flex justify-content-between mt-4"
					label={i18n.translate('resource-requirements')}
				>
					<CloudResourceRequirements />
				</Section>
			)}

			<Section
				className="d-flex flex-column form-radio-card"
				label={i18n.translate('app-build')}
			>
				{BUILD_UPLOAD_OPTIONS[
					appType === ProductType.CLOUD
						? ProductType.CLOUD
						: ProductType.DXP
				].map((card, index) => (
					<RadioCard
						description={card.description}
						disabled={card.disabled}
						icon={card.icon}
						key={index}
						onChange={() => {}}
						selected={'upload' === card.value}
						title={card.title}
						tooltip={card.tooltip}
					/>
				))}
			</Section>
			<Section
				description={i18n.translate(
					appType === ProductType.DXP
						? 'if-the-app-is-compatible-with-different-updates-of-74-please-upload-multiple-packages-for-each-update-or-update-compatibility-range'
						: 'select-a-local-file-to-upload'
				)}
				label={i18n.translate(
					appType === ProductType.DXP
						? 'upload-liferay-plugin-packages'
						: 'upload-zip-files'
				)}
				required
				tooltip={i18n.translate(
					appType === ProductType.DXP
						? 'only-jar-war-files-are-allowed-max-file-size-is-500mb'
						: 'you-can-upload-one-or-many-zip-files-max-total-size-is-500-mb'
				)}
				tooltipText={i18n.translate('more-info')}
			>
				<hr />

				{liferayPackages.map((liferayPackage, index) => (
					<div
						className="mt-4 provide-app-build-page-dropzone-container"
						key={index}
					>
						<div className="align-center d-flex font-weight-bold justify-content-between p-3 provide-app-build-page-dropzone-container-header">
							<span>
								{i18n.translate('package')} {index + 1}
							</span>
							<ClayButton
								displayType="unstyled"
								onClick={() => {
									const updatedLiferayPackages =
										liferayPackages.filter(
											(_, itemIndex) =>
												itemIndex !== index
										);

									dispatch({
										payload: {
											liferayPackages:
												updatedLiferayPackages,
										},
										type: NewAppTypes.SET_BUILD,
									});
								}}
							>
								{i18n.translate('remove')}
							</ClayButton>
						</div>

						<NewAppUploadAppPackagesComponent
							isProcessing={loading}
							liferayPackage={liferayPackage}
						/>

						<div className="p-4">
							<p className="font-weight-bold">
								{i18n.translate('compatible-versions')}
							</p>
							{liferayPackage.versions.map((version, index) => (
								<small key={index}>
									{version}
									{index + 1 <
										liferayPackage.versions.length &&
										','}{' '}
								</small>
							))}
						</div>
					</div>
				))}

				{!loading && (
					<ClayButton
						className="btn-block provide-app-build-page-add-package-button"
						displayType="secondary"
						onClick={() => setVisibleSelectVersionModal(true)}
					>
						<ClayIcon className="mr-1" symbol="plus" />
						{i18n.translate('add-packages')}
					</ClayButton>
				)}

				{visibleSelectVersionModal && (
					<NewAppPackageVersionModal
						currentVersions={[]}
						handleClose={() => setVisibleSelectVersionModal(false)}
					/>
				)}
			</Section>
		</>
	);
};

const Build = () => {
	const [active, setActive] = useState(false);

	const [
		{
			_product,
			build: {appType},
		},
		dispatch,
	] = useNewAppContext();

	const getType = (value: ProductType) => {
		if (!value) {
			return i18n.translate('choose-an-option');
		}

		const type = ProductTypeOptions.find(
			(option: ProductTypeOption) => option.value === value
		);

		return type ? type.label : 'Unknown';
	};

	const handleAppTypeChange = (value: ProductType) => {
		dispatch({
			payload: {
				appType: value,
			},
			type: NewAppTypes.SET_BUILD,
		});

		// handleResetAppPackages();

	};

	return (
		<div
			className={classNames('new-app-form-build', {
				'section-disabled':
					_product?.productStatus === ProductWorkflowStatusCode.DRAFT,
			})}
		>
			<Section
				label={i18n.translate('app-type')}
				required
				tooltipText={i18n.translate('more-info')}
			>
				<div className="provide-app-build-page-cloud-compatible-container">
					<ClayDropDown
						active={active}
						alignmentPosition={Align.BottomLeft}
						className="app-type-dropdown"
						onActiveChange={setActive}
						trigger={
							<ClayButton
								className="align-items-center app-type-dropdown d-flex justify-content-between"
								displayType="secondary"
								onClick={() => setActive(!active)}
							>
								<div className="align-items-center d-flex justify-content-between w-100">
									<span>{getType(appType)}</span>

									<ClayIcon symbol="caret-bottom" />
								</div>
							</ClayButton>
						}
					>
						<ClayDropDown.ItemList className="app-type-list-unstyled">
							{ProductTypeOptions.map(
								(option: ProductTypeOption) => (
									<ClayDropDown.Item
										key={option.value}
										onClick={() => {
											setActive(false);

											handleAppTypeChange(option.value);
										}}
									>
										<span className="d-flex flex-column">
											<strong>{option.label}</strong>
											<span>{option.description}</span>
										</span>
									</ClayDropDown.Item>
								)
							)}
						</ClayDropDown.ItemList>
					</ClayDropDown>
				</div>
			</Section>

			{appType && <BuildContent />}
		</div>
	);
};

export default Build;
