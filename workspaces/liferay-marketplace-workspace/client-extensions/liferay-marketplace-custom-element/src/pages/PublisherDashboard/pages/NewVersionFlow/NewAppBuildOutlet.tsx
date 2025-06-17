/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {useMemo, useState} from 'react';
import {Outlet} from 'react-router-dom';

import AppPublish from '../../../../components/AppPublish';
import {Checkbox} from '../../../../components/Checkbox/Checkbox';
import ExternalLink from '../../../../components/ExternalLink';
import Modal from '../../../../components/Modal';
import {useNewAppContext} from '../../../../context/NewAppContext';
import {useAccount} from '../../../../hooks/data/useAccounts';
import i18n from '../../../../i18n';
import usePublishAppSubmission from '../../hooks/usePublishAppSubmission';
import usePublishHeader from '../../hooks/usePublishHeader';
import usePublishNavigation from '../../hooks/usePublishNavigation';
import {NEW_APP_BUILD_FLOW_ITEMS} from './constants';

import '../NewAppFlow/PublishAppOutlet.scss';

const NewAppBuildOutlet = () => {
	usePublishHeader();

	const {data: account} = useAccount();
	const [context, dispatch] = useNewAppContext();
	const [checkedUserAgreement, setCheckedUserAgreement] = useState(false);

	const {
		activeIndex,
		activeRoute,
		isLastStep,
		onClickContinue,
		onClickPrevious,
		onExit,
		steps,
	} = usePublishNavigation({
		exitLink: '/',
		flowItems: NEW_APP_BUILD_FLOW_ITEMS,
	});

	const {onSave} = usePublishAppSubmission(context, dispatch);

	const onExitModal = useModal();

	const parsedSchema = useMemo(() => {
		const parseSchema = activeRoute?.parseSchema;

		if (parseSchema) {
			return parseSchema(context);
		}

		return null;
	}, [activeRoute, context]);

	const isValidSchema = parsedSchema ? !parsedSchema.success : false;

	if (context.loading) {
		return null;
	}

	return (
		<AppPublish>
			<AppPublish.Navbar
				accountImage={account?.logoURL}
				accountName={account?.name as string}
				appImage={context.profile.file?.preview}
				appName={context.profile.name}
				appStatus={context._product?.productStatus}
				display={{
					preview: true,
					saveAsDraft: false,
				}}
				exitProps={{
					onClick: () => onExitModal.onOpenChange(true),
				}}
				saveAsDraftProps={{
					disabled: true,
				}}
				submitProps={{
					onClick: onSave,
				}}
			/>

			<AppPublish.Body>
				<AppPublish.Sidebar activeIndex={activeIndex} items={steps} />

				<AppPublish.Content>
					{activeRoute.alertText && (
						<ClayAlert displayType="info">
							{activeRoute.alertText}
						</ClayAlert>
					)}

					<h1 className="header-title mb-4">{activeRoute.title}</h1>

					{activeRoute.description}

					<div className="mt-6 new-app-form">
						<Outlet />
					</div>

					{isLastStep && (
						<div className="app-review-page-agreement">
							<Checkbox
								checked={checkedUserAgreement}
								onChange={() => {
									setCheckedUserAgreement(
										!checkedUserAgreement
									);
								}}
							/>

							<span>
								<span className="app-review-page-agreement-highlight">
									{'Attention: this cannot be undone. '}
								</span>
								I am aware I cannot edit any data or information
								regarding this app submission until Liferay
								completes its review process and I agree with
								the Liferay Marketplace{' '}
								<ExternalLink href="https://www.liferay.com/legal/marketplace-terms-of-service">
									terms
								</ExternalLink>
								{' and '}
								<ExternalLink href="https://www.liferay.com/privacy-policy">
									privacy
								</ExternalLink>
							</span>
						</div>
					)}

					<hr className="my-6" />

					<div className="d-flex justify-content-end">
						{activeIndex !== 0 && (
							<ClayButton
								className="mr-4"
								displayType="secondary"
								onClick={onClickPrevious}
							>
								{i18n.translate('back')}
							</ClayButton>
						)}

						<ClayButton
							disabled={
								isLastStep
									? !checkedUserAgreement
									: isValidSchema
							}
							displayType="primary"
							onClick={() => {
								if (isLastStep) {
									return onSave().then(onExit);
								}

								onClickContinue();
							}}
						>
							{i18n.translate(isLastStep ? 'submit' : 'continue')}
						</ClayButton>
					</div>
				</AppPublish.Content>
			</AppPublish.Body>

			{onExitModal.open && (
				<Modal
					last={
						<ClayButton
							className="btn btn-primary ml-2"
							displayType="primary"
							onClick={onExit}
						>
							{i18n.translate('exit')}
						</ClayButton>
					}
					observer={onExitModal.observer}
					size={'md' as any}
					title={i18n.translate('exit-from-providing-app-build')}
					visible={onExitModal.open}
				>
					<p>
						{i18n.translate(
							'all-progress-and-information-related-to-the-creation-of-the-new-app-build-will-be-lost-do-you-still-want-to-exit'
						)}
					</p>
				</Modal>
			)}
		</AppPublish>
	);
};
export default NewAppBuildOutlet;
