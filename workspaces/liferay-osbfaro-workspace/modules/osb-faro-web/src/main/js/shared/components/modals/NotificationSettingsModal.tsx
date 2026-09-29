import ClayButton from '@clayui/button';
import Form from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import Modal from '../modal';
import React, {useEffect, useState} from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {connect, ConnectedProps} from 'react-redux';
import {
	Frequency,
	FREQUENCIES,
	FREQUENCY_KEYS,
} from 'settings/channels/components/EmailReports';
import {Modal as ModalTypes} from 'shared/types';
import {pickBy} from 'lodash';

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
	defaultValues: NotificationSettings;
	description: string;
	fetchSavedValues: () => Promise<Partial<NotificationSettings> | undefined>;
	onClose: ModalTypes.close;
	onSave: (values: NotificationSettings) => Promise<unknown>;
	title: string;
}

const NotificationSettingsModal: React.FC<INotificationSettingsModalProps> = ({
	addAlert,
	checkboxes,
	defaultValues,
	description,
	fetchSavedValues,
	onClose,
	onSave,
	title,
}) => {
	const [initialValues, setInitialValues] =
		useState<NotificationSettings | null>(null);

	useEffect(() => {
		let active = true;

		fetchSavedValues()
			.then((savedValues) => {
				if (active) {
					setInitialValues({
						...defaultValues,
						...pickBy(savedValues, (value) => value != null),
					});
				}
			})
			.catch((e: Error) => {
				console.error(e); // eslint-disable-line no-console

				if (active) {
					setInitialValues(defaultValues);
				}
			});

		return () => {
			active = false;
		};
	}, []);

	if (!initialValues) {
		return (
			<Modal>
				<Modal.Header onClose={onClose} title={title} />

				<Modal.Body>
					<Loading />
				</Modal.Body>
			</Modal>
		);
	}

	return (
		<Modal>
			<Modal.Header onClose={onClose} title={title} />

			<Form<NotificationSettings>
				initialValues={initialValues}
				onSubmit={(values) =>
					onSave(values)
						.then(() => {
							onClose();

							addAlert({
								alertType: Alert.Types.Success,
								message: Liferay.Language.get(
									'notification-settings-were-saved'
								),
							});
						})
						.catch((e: Error) => {
							console.error(e); // eslint-disable-line no-console

							addAlert({
								alertType: Alert.Types.Error,
								message: Liferay.Language.get('error'),
								timeout: false,
							});
						})
				}
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
										<Form.Checkbox
											label={label}
											name={name}
										/>
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
};

export default connector(NotificationSettingsModal);
