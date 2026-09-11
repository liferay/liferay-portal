import ClayButton from '@clayui/button';
import Form from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import Modal from '../modal';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {connect, ConnectedProps} from 'react-redux';
import {
	Frequency,
	FREQUENCIES,
	FREQUENCY_KEYS,
} from 'settings/channels/components/EmailReports';
import {Modal as ModalTypes} from 'shared/types';

export interface INotificationCheckbox {
	label: string;
	name: string;
}

export type NotificationSettings = {
	emailFrequency: Frequency;
	[key: string]: boolean | Frequency;
};

const connector = connect(null, {addAlert});

type PropsFromRedux = ConnectedProps<typeof connector>;

interface INotificationSettingsModalProps extends PropsFromRedux {
	checkboxes: INotificationCheckbox[];
	description: string;
	initialValues: NotificationSettings;
	onClose: ModalTypes.close;
	title: string;
}

const NotificationSettingsModal: React.FC<INotificationSettingsModalProps> = ({
	addAlert,
	checkboxes,
	description,
	initialValues,
	onClose,
	title,
}) => (
	<Modal>
		<Modal.Header onClose={onClose} title={title} />

		<Form<NotificationSettings>
			initialValues={initialValues}
			onSubmit={() => {
				onClose();

				addAlert({
					alertType: Alert.Types.Success,
					message: Liferay.Language.get(
						'notification-settings-were-saved'
					),
				});
			}}
		>
			{({handleSubmit, isSubmitting, values}) => (
				<Form.Form onSubmit={handleSubmit}>
					<Modal.Body>
						<p className="text-secondary">{description}</p>

						<div className="font-weight-bold mb-2">
							{Liferay.Language.get('email')}
						</div>

						<Form.Group>
							{checkboxes.map(({label, name}) => (
								<Form.GroupItem key={name}>
									<Form.Checkbox label={label} name={name} />
								</Form.GroupItem>
							))}
						</Form.Group>

						<Form.Group>
							<Form.GroupItem>
								<Form.Select
									disabled={
										!checkboxes.some(
											({name}) => values[name]
										)
									}
									label={Liferay.Language.get(
										'email-frequency'
									)}
									name="emailFrequency"
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
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="button-root"
							disabled={isSubmitting}
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

export default connector(NotificationSettingsModal);
