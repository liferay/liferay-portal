/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {useMemo} from 'react';
import {Link} from 'react-router-dom';

import Modal from '../../../../components/Modal';
import {useNewAppContext} from '../../../../context/NewAppContext';
import {ProductWorkflowStatusCode} from '../../../../enums/Product';
import i18n from '../../../../i18n';
import BasePublishAppOutlet from '../../BasePublishAppOutlet';
import usePublishAppSubmission from '../../hooks/usePublishAppSubmission';
import usePublishHeader from '../../hooks/usePublishHeader';
import usePublishNavigation from '../../hooks/usePublishNavigation';
import {APP_FLOW_ITEMS} from './constants';

type Context = ReturnType<typeof useNewAppContext>[0];

const getFlowItems = (context: Context) =>
	APP_FLOW_ITEMS.filter((item) => item.visible(context));

const isRequiredDraftFormFilled = (context: Context) =>
	APP_FLOW_ITEMS.filter((item) => item.saveAsDraftRequired).every(
		(item) => item.parseSchema && item.parseSchema(context).success
	);

const PublishAppOutlet = () => {
	usePublishHeader();

	const [context, dispatch] = useNewAppContext();
	const {observer, onOpenChange, open} = useModal();
	const {onSave, onSaveAsDraft} = usePublishAppSubmission(context, dispatch);
	const onExitModal = useModal();
	const isEditingApp =
		context?._product &&
		context._product.productStatus === ProductWorkflowStatusCode.APPROVED;

	const {activeRoute, onExit} = usePublishNavigation({
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
		<BasePublishAppOutlet
			canSaveAsDraft={canSaveAsDraft}
			context={context}
			flowItems={getFlowItems(context)}
			isEditingApp={!!isEditingApp}
			onClickExit={
				canSaveAsDraft
					? () => onOpenChange(true)
					: () => onExitModal.onOpenChange(true)
			}
			onSave={onSave}
			onSaveAsDraft={onSaveAsDraft}
		>
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
		</BasePublishAppOutlet>
	);
};
export default PublishAppOutlet;
