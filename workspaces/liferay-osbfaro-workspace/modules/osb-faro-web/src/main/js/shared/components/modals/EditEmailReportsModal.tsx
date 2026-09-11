import ClayButton from '@clayui/button';
import Form from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import Modal from '../modal';
import React from 'react';
import {
	Frequency,
	FREQUENCIES,
	FREQUENCY_KEYS,
	Report,
} from 'settings/channels/components/EmailReports';
import {Modal as ModalTypes} from 'shared/types';

interface IEditEmailReportsModalProps
	extends React.HTMLAttributes<HTMLElement> {
	onCancel: ModalTypes.close;
	onSave: (report: Report) => void;
	report: Report | null;
}

const EditEmailReportsModal: React.FC<IEditEmailReportsModalProps> = ({
	onCancel,
	onSave,
	report,
}) => (
	<Modal>
		<Modal.Header title={Liferay.Language.get('configure-email-reports')} />

		<Form<Report>
			initialValues={{
				enabled: report?.enabled ?? false,
				frequency: report?.frequency ?? Frequency.Monthly,
			}}
			onSubmit={onSave}
		>
			{({handleSubmit, isSubmitting, isValid, values}) => (
				<Form.Form onSubmit={handleSubmit}>
					<Modal.Body>
						<Form.Group
							autoFit
							className="align-items-center d-flex"
						>
							<div>
								<div className="font-weight-semibold mb-2">
									{Liferay.Language.get(
										'enable-email-reports'
									)}
								</div>

								<div className="text-secondary">
									{Liferay.Language.get(
										'enable-email-reports-to-configure-how-frequently-you-would-like-to-receive-notifications-about-this-property-activities.-other-users-will-not-be-affected-by-this-action'
									)}
								</div>
							</div>
							<div>
								<Form.GroupItem className="ml-3">
									<Form.ToggleSwitch name="enabled" />
								</Form.GroupItem>
							</div>
						</Form.Group>
						<Form.Group autoFit>
							<Form.GroupItem>
								<Form.Select
									disabled={!values.enabled}
									label={Liferay.Language.get('frequency')}
									name="frequency"
								>
									{FREQUENCY_KEYS.map((frequency) => (
										<Form.Select.Item
											key={frequency}
											value={frequency}
										>
											{FREQUENCIES[frequency]}
										</Form.Select.Item>
									))}
								</Form.Select>
							</Form.GroupItem>
						</Form.Group>
					</Modal.Body>
					<Modal.Footer>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={onCancel}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="button-root"
							disabled={isSubmitting || !isValid}
							displayType="primary"
							type="submit"
						>
							{isSubmitting && <Loading align={Align.Left} />}

							{Liferay.Language.get('save')}
						</ClayButton>
					</Modal.Footer>
				</Form.Form>
			)}
		</Form>
	</Modal>
);

export default EditEmailReportsModal;
