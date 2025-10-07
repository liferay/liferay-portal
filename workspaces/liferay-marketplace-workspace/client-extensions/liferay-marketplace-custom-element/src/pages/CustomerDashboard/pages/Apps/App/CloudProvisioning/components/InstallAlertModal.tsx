/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';

import Modal from '../../../../../../../components/Modal';
import i18n from '../../../../../../../i18n';

type UninstallModalProps = {
	modal: ReturnType<typeof useModal>;
};

const InstallAlertModal = ({modal}: UninstallModalProps) => (
	<Modal
		first={
			<ClayButton
				className="ml-2 rounded-lg"
				displayType="unstyled"
				key="install-cancel-footer"
				onClick={modal.onClose}
				size="sm"
			>
				{i18n.translate('cancel')}
			</ClayButton>
		}
		last={
			<ClayButton
				className="ml-2 rounded-lg"
				displayType="primary"
				key="install-done-footer"
				onClick={modal.onClose}
				size="sm"
			>
				{i18n.translate('done')}
			</ClayButton>
		}
		observer={modal.observer}
		size={'md' as any}
		title={i18n.translate('no-cloud-projects-available')}
		visible={modal.open}
	>
		<p>
			{`${i18n.translate(
				'you-do-not-have-access-to-cloud-project'
			)} ${i18n.translate('this-may-restrict-the-functionality-available-to-you')}`}
		</p>
	</Modal>
);

export default InstallAlertModal;
