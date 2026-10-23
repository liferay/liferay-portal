import DeleteConfirmationModal from 'shared/components/DeleteConfirmationModal';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {Alert} from 'shared/types';
import {UNAUTHORIZED_ACCESS} from 'shared/util/request';

export interface IPropertyConfirmationModalProps {
	addAlert: (alert: {
		alertType: any;
		message: string;
		timeout?: boolean;
	}) => any;
	observer: Observer;
	onClose: () => void;
	onSubmit: () => Promise<unknown>;
}

interface IProps extends IPropertyConfirmationModalProps {
	confirmationText: string;
	deleteButtonLabel: string;
	instruction: string;
	title: string;
}

/**
 * The typed confirmation behind the actions that remove the data of a
 * property, which warns that the historical events cannot be recovered.
 * `onSubmit` returns the request, so that a failure shows an error alert.
 */
const PropertyConfirmationModal: React.FC<IProps> = ({
	addAlert,
	confirmationText,
	deleteButtonLabel,
	instruction,
	observer,
	onClose,
	onSubmit,
	title,
}) => (
	<DeleteConfirmationModal
		deleteButtonLabel={deleteButtonLabel}
		deleteConfirmationText={confirmationText}
		observer={observer}
		onClose={onClose}
		onSubmit={() =>
			onSubmit().catch((error) =>
				addAlert({
					alertType: Alert.Types.Error,
					message:
						error.message === UNAUTHORIZED_ACCESS
							? Liferay.Language.get('unauthorized-access')
							: Liferay.Language.get('error'),
					timeout: false,
				})
			)
		}
		title={title}
	>
		<>
			<p>
				<strong>{instruction}</strong>
			</p>

			<p>
				{Liferay.Language.get(
					'this-will-result-in-the-complete-removal-of-this-propertys-historical-events.-you-will-not-be-able-to-undo-this-operation'
				)}
			</p>
		</>
	</DeleteConfirmationModal>
);

export default PropertyConfirmationModal;
