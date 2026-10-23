import DisconnectDataSourceModal from 'shared/components/modals/DisconnectDataSourceModal';
import React, {useCallback} from 'react';
import {disconnect} from 'shared/api/data-source';
import {useModal} from '@clayui/modal';

interface IUseDisconnectDataSourceParams {
	addAlert: (params: {
		alertType: any;
		message: string;
		timeout?: boolean;
	}) => any;
	beforeSubmit?: () => Promise<any> | any;
	groupId: string;
	id: string;
	onSubmit: () => Promise<any> | any;
}

const useDisconnectDataSource = ({
	addAlert,
	beforeSubmit,
	groupId,
	id,
	onSubmit,
}: IUseDisconnectDataSourceParams) => {
	const modal = useModal();

	const handleDisconnect = useCallback(
		() => modal.onOpenChange(true),
		[modal.onOpenChange]
	);

	const disconnectModal = modal.open ? (
		<DisconnectDataSourceModal
			addAlert={addAlert}
			errorMessage={Liferay.Language.get(
				'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
			)}
			message={Liferay.Language.get(
				'this-action-will-stop-syncing-data-from-this-data-source-to-this-liferay-data-platform-workspace.-the-data-that-was-already-synced-will-remain-available-in-the-properties-the-data-source-was-connected-to.-are-you-sure-you-want-to-continue'
			)}
			observer={modal.observer}
			onClose={() => modal.onOpenChange(false)}
			onSubmit={async () => {
				if (beforeSubmit) {
					await beforeSubmit();
				}

				await disconnect({groupId, id});

				await onSubmit();
			}}
		/>
	) : null;

	return {disconnectModal, handleDisconnect};
};

export {useDisconnectDataSource};
