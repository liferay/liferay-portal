/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';

import i18n from '../../../../../i18n';
import {ProductTypeOptions} from '../../../pages/Apps/AppCreationFlow/ProvideAppBuildPage/constants/productTypes';
import {AppReviewProps} from '../AppReview';
import AppReviewSection from '../AppReviewSection';

const Build = ({
	context,
	editNavigate,
	isLastSection,
	required = false,
}: AppReviewProps) => {
	const productTypeOption = ProductTypeOptions.find(
		(productType) => productType.value === context.build.appType
	);

	return (
		<AppReviewSection
			editNavigate={editNavigate}
			isLastSection={isLastSection}
			required={required}
			title={i18n.translate('build')}
		>
			{productTypeOption && (
				<>
					<div className="border p-4 rounded-lg">
						<div>
							<div className="align-items-center d-flex">
								<span className="app-review-pricing-title mr-2">
									{productTypeOption?.label}
								</span>
							</div>

							<span className="app-review-pricing-description">
								{productTypeOption?.description}
							</span>
						</div>
					</div>
					{context.build.liferayPackages.map((liferayPackage) => {
						return (
							<div
								className="d-flex flex-column pt-4"
								key={liferayPackage.id}
							>
								<div className="align-items-center d-flex">
									<div className="app-review-file-container">
										<ClayIcon
											aria-label="Folder Icon"
											className="app-review-file-container-icon"
											symbol="document-text"
										/>
									</div>

									<span className="app-review-file-name ml-3">
										{liferayPackage?.file?.fileName}
									</span>
								</div>
								<div className="p-4">
									<p className="font-weight-bold mb-0">
										{i18n.translate('compatible-versions')}
									</p>
									{liferayPackage.versions.map(
										(version, index) => (
											<small key={index}>
												{version}
												{index + 1 <
													liferayPackage.versions
														.length && ','}{' '}
											</small>
										)
									)}
								</div>
							</div>
						);
					})}
				</>
			)}
		</AppReviewSection>
	);
};

export default Build;
