/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {useMemo} from 'react';
import {Link, Outlet} from 'react-router-dom';

import AppPublish from '../../../../../components/AppPublish';
import PublishNav from '../../../../../components/AppPublish/Sidebar';
import Modal from '../../../../../components/Modal';
import {useSolutionContext} from '../../../../../context/SolutionContext';
import {ProductWorkflowStatusCode} from '../../../../../enums/Product';
import {useAccount} from '../../../../../hooks/data/useAccounts';
import i18n from '../../../../../i18n';
import usePublishHeader from '../../../hooks/usePublishHeader';
import usePublishNavigation from '../../../hooks/usePublishNavigation';
import usePublishSolutionSubmission from '../../../hooks/usePublishSolutionSubmission';
import {SOLUTION_FLOW_ITEMS} from '../constants';

import 'react-quill/dist/quill.snow.css';

import './PublishSolutionOutlet.scss';

const PublishSolutionOutlet = () => {
	usePublishHeader();

	const {data: account} = useAccount();
	const [context, dispatch] = useSolutionContext();

	const {
		activeIndex,
		activeRoute,
		isLastStep,
		onClickContinue,
		onClickPrevious,
		onExit,
		steps,
	} = usePublishNavigation({
		exitLink: '/solutions',
		flowItems: SOLUTION_FLOW_ITEMS,
	});

	const {onSave, onSaveAsDraft} = usePublishSolutionSubmission(
		context,
		dispatch
	);

	const {observer, onOpenChange, open} = useModal();
	const onExitModal = useModal();

	const parsedSchema = useMemo(() => {
		const parseSchema = activeRoute?.parseSchema;

		if (parseSchema) {
			return parseSchema(context);
		}

		return null;
	}, [activeRoute, context]);

	const isDisabled = parsedSchema ? !parsedSchema.success : false;

	const isDraft = (status: number) =>
		status === ProductWorkflowStatusCode.DRAFT;

	const isSaveAsDraft =
		!context._product || isDraft(context._product.productStatus);

	return (
		<AppPublish>
			<AppPublish.Navbar
				accountImage={account?.logoURL}
				accountName={account?.name as string}
				appImage={context.profile.file?.preview}
				appName={context.profile.name}
				display={{
					preview: true,
					saveAsDraft: isSaveAsDraft,
					submit:
						!!context._product &&
						!isDraft(context._product.productStatus),
				}}
				exitProps={{
					onClick: () => {
						isSaveAsDraft
							? onOpenChange(true)
							: onExitModal.onOpenChange(true);
					},
				}}
				previewProps={{
					disabled: false,
					onClick: () => alert('Preview...'),
				}}
				saveAsDraftProps={{
					disabled: isDisabled,
					onClick: onSaveAsDraft,
				}}
				submitProps={{
					onClick: onSave,
				}}
			/>

			<AppPublish.Body>
				<PublishNav activeIndex={activeIndex} items={steps} />

				<AppPublish.Content>
					<h1 className="header-title mb-4">{activeRoute.title}</h1>
					{activeRoute.description}

					<div className="mt-6 solutions-form">
						<Outlet />
					</div>
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
							disabled={isDisabled}
							displayType="primary"
							onClick={async () => {
								if (isLastStep) {
									return onSave().then(onExit);
								}

								onClickContinue();
							}}
						>
							{i18n.translate(
								isLastStep ? 'submit-solution' : 'continue'
							)}
						</ClayButton>
					</div>
				</AppPublish.Content>
			</AppPublish.Body>

			<Modal
				last={
					<>
						<ClayButton
							displayType="secondary"
							onClick={() => onSaveAsDraft().then(onExit)}
						>
							{i18n.translate('save-as-a-draft-exit')}
						</ClayButton>

						<Link className="btn btn-primary ml-2" to="/solutions">
							{i18n.translate('exit')}
						</Link>
					</>
				}
				observer={observer}
				size={'md' as any}
				title="Exit from creating a solution"
				visible={open}
			>
				<p>
					{i18n.translate(
						'all-progress-and-information-related-to-the-creation-of-the-solution-will-be-lost-unless-you-save-the-solution-as-a-draft-do-you-still-want-to-exit'
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
					title="Exit from creating a solution"
					visible={onExitModal.open}
				>
					<p>
						{i18n.translate(
							'all-progress-and-information-related-to-the-creation-of-the-solution-will-be-lost-do-you-still-want-to-exit'
						)}
					</p>
				</Modal>
			)}
		</AppPublish>
	);
};

export default PublishSolutionOutlet;
