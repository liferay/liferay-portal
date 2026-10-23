import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import Form, {
	validateMaxLength,
	validateMinLength,
	validateRequired,
} from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import ModalAutoFocus from 'shared/components/ModalAutoFocus';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {FormikHelpers} from 'formik';
import {sequence} from 'shared/util/promise';

interface IAddChannelModalProps {
	observer: Observer;
	onClose: () => void;
	onSubmit: (
		values: {name: string},
		helpers: FormikHelpers<{name: string}>
	) => void;
}

const AddChannelModal: React.FC<IAddChannelModalProps> = ({
	observer,
	onClose,
	onSubmit,
}) => (
	<ClayModal className="add-channel-modal" observer={observer}>
		<Form
			initialValues={{
				name: '',
			}}
			onSubmit={onSubmit}
		>
			{({handleSubmit, isSubmitting, isValid}) => (
				<Form.Form onSubmit={handleSubmit}>
					<ClayModal.Header>
						{Liferay.Language.get('new-property')}
					</ClayModal.Header>

					<ClayModal.Body>
						<ModalAutoFocus selector="input[name=name]" />

						<Form.Input
							label={Liferay.Language.get('property-name')}
							name="name"
							validate={sequence([
								validateRequired,
								validateMaxLength(65),
								validateMinLength(3),
							])}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									className="button-root"
									displayType="secondary"
									onClick={onClose}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									className="button-root"
									disabled={isSubmitting || !isValid}
									displayType="primary"
									type="submit"
								>
									{isSubmitting && (
										<Loading align={Align.Left} />
									)}

									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</Form.Form>
			)}
		</Form>
	</ClayModal>
);

export default AddChannelModal;
