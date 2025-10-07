/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {useNavigate} from 'react-router-dom';

import Modal from '../../../../components/Modal';
import {NewAppTypes, useNewAppContext} from '../../../../context/NewAppContext';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';
import AppPublish from '../../../../services/actions/AppPublish';
import HeadlessCommerceAdminCatalog from '../../../../services/rest/HeadlessCommerceAdminCatalog';
import BasePublishAppOutlet from '../../BasePublishAppOutlet';
import {NEW_APP_BUILD_FLOW_ITEMS} from './constants';

const NewAppBuildOutlet = () => {
	const navigate = useNavigate();

	const [context, dispatch] = useNewAppContext();

	const onSave = async () => {
		dispatch({payload: true, type: NewAppTypes.SET_LOADING});

		const appPublish = new AppPublish(context);

		const product = context._product;

		if (product) {
			try {
				await appPublish.processLiferayPackages(product);

				await HeadlessCommerceAdminCatalog.updateProduct(
					context._product?.productId as number,
					{
						name: {en_US: product.name.en_US},
					}
				);
			}
			catch {
				Liferay.Util.openToast({
					message: 'Something went wrong when sending a new build',
					type: 'danger',
				});
			}
		}

		dispatch({payload: false, type: NewAppTypes.SET_LOADING});
	};

	const onExitModal = useModal();

	return (
		<BasePublishAppOutlet
			canSaveAsDraft={false}
			context={context}
			flowItems={NEW_APP_BUILD_FLOW_ITEMS}
			isEditingApp
			onClickExit={() => onExitModal.onOpenChange(true)}
			onSave={onSave}
		>
			{onExitModal.open && (
				<Modal
					last={
						<ClayButton
							className="btn btn-primary ml-2"
							displayType="primary"
							onClick={() => navigate('/')}
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
		</BasePublishAppOutlet>
	);
};
export default NewAppBuildOutlet;
