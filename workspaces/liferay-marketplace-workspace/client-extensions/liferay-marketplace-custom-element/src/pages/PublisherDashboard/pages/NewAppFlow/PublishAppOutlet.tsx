/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {useMemo, useState} from 'react';
import {Link, Outlet} from 'react-router-dom';

import AppPublish from '../../../../components/AppPublish';
import {Checkbox} from '../../../../components/Checkbox/Checkbox';
import ExternalLink from '../../../../components/ExternalLink';
import Modal from '../../../../components/Modal';
import {useNewAppContext} from '../../../../context/NewAppContext';
import {ProductWorkflowStatusCode} from '../../../../enums/Product';
import {useAccount} from '../../../../hooks/data/useAccounts';
import i18n from '../../../../i18n';
import usePublishAppSubmission from '../../hooks/usePublishAppSubmission';
import usePublishHeader from '../../hooks/usePublishHeader';
import usePublishNavigation from '../../hooks/usePublishNavigation';
import {APP_FLOW_ITEMS} from './constants';

import './PublishAppOutlet.scss';

type Context = ReturnType<typeof useNewAppContext>[0];

const getFlowItems = (context: Context) =>
	APP_FLOW_ITEMS.filter((item) => item.visible(context));

const isRequiredDraftFormFilled = (context: Context) =>
	APP_FLOW_ITEMS.filter((item) => item.saveAsDraftRequired).every(
		(item) => item.parseSchema && item.parseSchema(context).success
	);

const PublishAppOutlet = () => {
	usePublishHeader();

	const [checkedUserAgreement, setCheckedUserAgreement] = useState(false);
	const [context, dispatch] = useNewAppContext();
	const {data: account} = useAccount();
	const {observer, onOpenChange, open} = useModal();
	const {onSave, onSaveAsDraft} = usePublishAppSubmission(context, dispatch);
	const onExitModal = useModal();
	const isEditingApp =
		context?._product &&
		context._product.productStatus === ProductWorkflowStatusCode.APPROVED;

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
		flowItems: getFlowItems(context),
	});

	const canSaveAsDraft =
		!context?._product && isRequiredDraftFormFilled(context);

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
					saveAsDraft: canSaveAsDraft,
				}}
				exitProps={{
					onClick: () => {
						canSaveAsDraft
							? onOpenChange(true)
							: onExitModal.onOpenChange(true);
					},
				}}
				saveAsDraftProps={{
					disabled: isValidSchema || !canSaveAsDraft,
					onClick: onSaveAsDraft,
				}}
				submitProps={{
					onClick: onSave,
				}}
			/>

			<AppPublish.Body>
				<AppPublish.Sidebar activeIndex={activeIndex} items={steps} />

				<AppPublish.Content>
					{isEditingApp && activeRoute.alertText && (
						<ClayAlert displayType="info">
							{activeRoute.alertText}
						</ClayAlert>
					)}

					<h1 className="header-title mb-4">
						{activeRoute.title(isEditingApp)}
					</h1>

					{activeRoute.description(isEditingApp)}

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

			<Modal
				last={
					<>
						<ClayButton
							disabled={isValidSchema || !canSaveAsDraft}
							displayType="secondary"
							onClick={() => onSaveAsDraft().then(onExit)}
						>
							{i18n.translate('save-as-a-draft-exit')}
						</ClayButton>

						<Link className="btn btn-primary ml-2" to="/">
							{i18n.translate('exit')}
						</Link>
					</>
				}
				observer={observer}
				size={'md' as any}
				title="Exit from creating an app"
				visible={open}
			>
				<p>
					{i18n.translate(
						'all-progress-and-information-related-to-the-creation-of-the-app-will-be-lost-unless-you-save-the-app-as-a-draft-do-you-still-want-to-exit'
					)}
				</p>
			</Modal>

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
					title="Exit from creating an App"
					visible={onExitModal.open}
				>
					<p>
						{i18n.translate(
							'all-progress-and-information-related-to-the-creation-of-the-app-will-be-lost-do-you-still-want-to-exit'
						)}
					</p>
				</Modal>
			)}
		</AppPublish>
	);
};
export default PublishAppOutlet;
