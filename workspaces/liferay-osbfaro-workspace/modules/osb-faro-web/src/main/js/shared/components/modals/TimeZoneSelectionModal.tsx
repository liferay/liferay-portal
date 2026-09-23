import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import Form from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import Modal from 'shared/components/modal';
import React, {useRef, useState} from 'react';
import TimeZonePicker from 'shared/components/form/TimeZonePicker';
import {connect, ConnectedProps} from 'react-redux';
import {
	formatDateToTimeZone,
	formatUTCDate,
	getDateNow,
	TIME_FORMAT,
} from 'shared/util/date';
import {Modal as ModalType} from 'shared/types';
import {RootState} from 'shared/store';
import {TimeZone} from 'shared/util/records';

const connector = connect((store: RootState, {groupId}: {groupId: string}) => ({
	timeZone: new TimeZone(
		store.getIn(['projects', groupId, 'data', 'timeZone'])
	),
}));

type PropsFromRedux = ConnectedProps<typeof connector>;

interface ITimeZoneSelectionModal extends PropsFromRedux {
	groupId: string;
	notificationId: string;
	onClose: ModalType.close;
}

const TimeZoneSelectionModal: React.FC<ITimeZoneSelectionModal> = ({
	groupId,
	notificationId,
	onClose,
	timeZone,
}) => {
	const _formRef = useRef<any>(null);
	const [currentTime, setCurrentTime] = useState(
		formatUTCDate(getDateNow(), TIME_FORMAT)
	);

	const onSubmit = (): void => {
		const {timeZoneId} = _formRef.current.values;

		API.projects.patchTimeZone(groupId, timeZoneId).then(handleClose);
	};

	const handleClose = (): void => {
		API.notifications.readNotification(groupId, notificationId);

		onClose();
	};

	return (
		<Modal>
			<Modal.Header title={Liferay.Language.get('workspace-timezone')} />

			<Form
				initialValues={{
					timeZoneId: timeZone.timeZoneId,
				}}
				innerRef={_formRef as any}
				onSubmit={onSubmit}
			>
				{({
					handleSubmit,
					isSubmitting,
					isValid,
					setFieldTouched,
					setFieldValue,
					values: {timeZoneId = 'UTC'},
				}) => {
					setCurrentTime(
						formatDateToTimeZone(
							getDateNow(),
							TIME_FORMAT,
							timeZoneId
						)
					);

					return (
						<Form.Form onSubmit={handleSubmit}>
							<Modal.Body>
								<div className="mb-4">
									{Liferay.Language.get(
										'your-workspace-now-supports-custom-timezones.-setting-timezones-will-only-impact-future-data.-expect-spiked-or-flat-data-for-1-2-days-following-a-change'
									)}
								</div>

								<div className="picker-root-container">
									<div className="time-zone-spaced-select">
										<TimeZonePicker
											fieldName="timeZoneId"
											initialTimeZone={timeZone}
											onCountryChange={() => {
												setCurrentTime(
													formatUTCDate(
														getDateNow(),
														TIME_FORMAT
													)
												);
											}}
											setFieldTouched={setFieldTouched}
											setFieldValue={setFieldValue}
										/>
									</div>

									<span className="current-time-display">
										{Liferay.Language.get(
											'current-time-colon'
										)}
									</span>
									<span className="current-time-value ml-4">
										{currentTime}
									</span>
								</div>
							</Modal.Body>

							<Modal.Footer>
								<ClayButton
									className="button-root"
									displayType="secondary"
									onClick={handleClose}
								>
									{Liferay.Language.get('do-this-later')}
								</ClayButton>

								<ClayButton
									className="button-root"
									disabled={!isValid}
									displayType="primary"
									type="submit"
								>
									{isSubmitting && (
										<Loading align={Align.Left} />
									)}

									{Liferay.Language.get('set-timezone')}
								</ClayButton>
							</Modal.Footer>
						</Form.Form>
					);
				}}
			</Form>
		</Modal>
	);
};

export default connector(TimeZoneSelectionModal);
