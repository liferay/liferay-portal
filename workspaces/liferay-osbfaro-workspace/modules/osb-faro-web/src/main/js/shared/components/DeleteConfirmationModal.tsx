import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import Form, {validateInputMessage} from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import ModalAutoFocus from 'shared/components/ModalAutoFocus';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {sub} from 'shared/util/lang';

interface IDeleteConfirmationModalProps {
	children?: React.ReactNode;
	className?: string;
	deleteButtonLabel?: string;
	deleteConfirmationText: string;
	disabled?: boolean;
	observer: Observer;
	onClose: () => void;
	onSubmit: () => void;
	title?: React.ReactNode;
}

const DeleteConfirmationModal: React.FC<IDeleteConfirmationModalProps> = ({
	children,
	className,
	deleteButtonLabel = Liferay.Language.get('delete'),
	deleteConfirmationText,
	disabled = false,
	observer,
	onClose,
	onSubmit,
	title = Liferay.Language.get('confirm'),
}) => (
	<ClayModal className={className} observer={observer} status="warning">
		<Form
			initialValues={{
				delete: '',
			}}
			onSubmit={onSubmit}
		>
			{({handleSubmit, isSubmitting, isValid}) => (
				<Form.Form onSubmit={handleSubmit}>
					<ClayModal.Header>{title}</ClayModal.Header>

					<ClayModal.Body>
						<div className="text-secondary">{children}</div>

						<div className="font-weight-bold mb-3">
							{sub(
								Liferay.Language.get('copy-the-following-x'),
								[
									<span
										className="font-weight-normal text-secondary"
										key="deleteConfirmationText"
									>
										{deleteConfirmationText}
									</span>,
								],
								false
							)}
						</div>

						<ModalAutoFocus selector="input[name=delete]" />

						<Form.Input
							data-testid="delete-confirmation-input"
							disabled={disabled}
							name="delete"
							validate={validateInputMessage(
								deleteConfirmationText
							)}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={onClose}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									disabled={
										disabled || !isValid || isSubmitting
									}
									displayType="warning"
									type="submit"
								>
									{isSubmitting && (
										<Loading align={Align.Left} />
									)}

									{deleteButtonLabel}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</Form.Form>
			)}
		</Form>
	</ClayModal>
);

export default DeleteConfirmationModal;
