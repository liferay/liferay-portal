/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';

import {Header} from '../../../components/Header/Header';
import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import {getSiteURL} from '../../../utils/site';
import {StepType} from './PublisherGateSteps';

type PublisherGateSummaryProps = {
	children: JSX.Element;
	setStep: React.Dispatch<React.SetStateAction<StepType>>;
	submit: () => void;
};

const PublisherGateSummary: React.FC<PublisherGateSummaryProps> = ({
	children,
	setStep,
	submit,
}) => {
	return (
		<>
			<div className="publisher-gate-page-body">
				<Header
					description={i18n.translate(
						'review-the-new-publisher-info-and-the-liferay-marketplace-terms-before-proceeding'
					)}
					title={i18n.translate('complete-publisher-account-request')}
				/>
				{children}
				<div className="mt-5">
					<span>
						<p className="privacy-text text-justify">
							{i18n.translate(
								'by-requesting-a-publisher-account-you-agree-to-the'
							)}
							<span className="mx-2">
								{i18n.translate('liferay-s')}
							</span>
							<a
								className="d-inline-block"
								href="https://www.liferay.com/legal/marketplace-terms-of-service"
								target="_blank"
							>
								<strong>
									{i18n.translate('terms-of-service')}
								</strong>
							</a>
							<span className="mx-2">{`${i18n.translate('and')}`}</span>
							<a
								className="d-inline-block"
								href="https://www.liferay.com/privacy-policy"
								target="_blank"
							>
								<strong>
									{i18n.translate('privacy-policy')}
								</strong>
							</a>
							<span className="ml-2">
								{i18n.translate(
									'apply-to-your-use-of-this-service-the-name-on-your-liferay-account-will-be-used-in-this-liferay-marketplace-publisher-profile-it-may-appear-where-you-contribute-and-be-changed-at-any-time'
								)}
							</span>
							.
						</p>
					</span>
				</div>

				<hr className="mb-5 mt-8" />

				<div className="mb-8 purchased-solutions-button-container">
					<div className="align-items-center d-flex justify-content-between mb-4 w-100">
						<ClayButton
							className="p-3"
							displayType="unstyled"
							onClick={() => {
								window.location.href = `${Liferay.ThemeDisplay.getPortalURL()}${getSiteURL()}/home`;
							}}
						>
							{i18n.translate('cancel')}
						</ClayButton>

						<div>
							<ClayButton
								className="mr-4"
								displayType="secondary"
								onClick={() => setStep(StepType.FORM)}
							>
								{i18n.translate('back')}
							</ClayButton>

							<ClayButton onClick={() => submit()}>
								{i18n.translate('request-account')}
							</ClayButton>
						</div>
					</div>
				</div>
			</div>
		</>
	);
};

export default PublisherGateSummary;
