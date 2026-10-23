import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import Loading, {Align} from 'shared/components/Loading';
import React, {useState} from 'react';
import type {Observer, Size, Status} from '@clayui/modal/lib/types';
import {ButtonProps} from '@clayui/button';

export interface IConfirmationModalProps {
	cancelMessage?: string;
	className?: string;
	closeAfterSubmit?: boolean;
	message: React.ReactNode;
	observer: Observer;
	onClose: () => void;
	onSubmit: () => any;
	size?: Size;
	submitButtonDisplay?: ButtonProps['displayType'];
	submitMessage?: string;
	title?: React.ReactNode;
	type?: Status;
}

const ConfirmationModal: React.FC<IConfirmationModalProps> = ({
	cancelMessage = Liferay.Language.get('cancel'),
	className,
	closeAfterSubmit = true,
	message,
	observer,
	onClose,
	onSubmit,
	size,
	submitButtonDisplay = 'primary',
	submitMessage = Liferay.Language.get('continue'),
	title = Liferay.Language.get('confirm'),
	type,
}) => {
	const [submitting, setSubmitting] = useState<boolean>(false);

	return (
		<ClayModal
			className={className}
			observer={observer}
			size={size}
			status={type}
		>
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body>{message}</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{cancelMessage}
						</ClayButton>

						<ClayButton
							displayType={submitButtonDisplay}
							onClick={() => {
								setSubmitting(true);

								const submitVal: any = onSubmit();

								if (submitVal instanceof Promise) {
									submitVal
										.then(() => {
											setSubmitting(false);

											closeAfterSubmit && onClose();
										})
										.catch(() => {
											setSubmitting(false);
										});
								}
								else {
									setSubmitting(false);

									closeAfterSubmit && onClose();
								}
							}}
						>
							{submitting && <Loading align={Align.Left} />}

							{submitMessage}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default ConfirmationModal;
