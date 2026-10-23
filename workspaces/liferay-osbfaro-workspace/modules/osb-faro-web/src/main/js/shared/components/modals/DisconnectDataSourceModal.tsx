import ConfirmationModal from 'shared/components/ConfirmationModal';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {Alert} from 'shared/types';
import {Text} from '@clayui/core';

interface IDisconnectDataSourceModalProps {
	addAlert: (alert: {
		alertType: any;
		message: string;
		timeout?: boolean;
	}) => any;
	errorMessage: string;
	message: React.ReactNode;
	observer: Observer;
	onClose: () => void;
	onSubmit: () => Promise<unknown> | unknown;
}

/**
 * The confirmation to disconnect a data source. `onSubmit` disconnects it and
 * runs what the caller needs next. The modal closes and shows the success
 * alert once that finishes, and shows `errorMessage` when it fails.
 */
const DisconnectDataSourceModal: React.FC<IDisconnectDataSourceModalProps> = ({
	addAlert,
	errorMessage,
	message,
	observer,
	onClose,
	onSubmit,
}) => (
	<ConfirmationModal
		message={
			<Text as="p" size={4}>
				{message}
			</Text>
		}
		observer={observer}
		onClose={onClose}
		onSubmit={async () => {
			try {
				await onSubmit();

				addAlert({
					alertType: Alert.Types.Success,
					message: Liferay.Language.get('data-source-disconnected'),
				});

				onClose();
			}
			catch (error) {
				addAlert({
					alertType: Alert.Types.Error,
					message: errorMessage,
				});
			}
		}}
		submitButtonDisplay="warning"
		submitMessage={Liferay.Language.get('disconnect')}
		title={Liferay.Language.get('disconnect-data-source')}
		type="warning"
	/>
);

export default DisconnectDataSourceModal;
