import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import Form from 'shared/components/form';
import Loading, {Align} from 'shared/components/Loading';
import React, {useRef, useState} from 'react';
import TimeZonePicker from 'shared/components/form/TimeZonePicker';
import {connect, ConnectedProps} from 'react-redux';
import {
	formatDateToTimeZone,
	formatUTCDate,
	getDateNow,
	TIME_FORMAT,
} from 'shared/util/date';
import {RootState} from 'shared/store';
import {TimeZone} from 'shared/util/records';
import type {Observer} from '@clayui/modal/lib/types';
import worldMapURL from '../../../../images/backgrounds/world_map.svg?url';

const connector = connect((store: RootState, {groupId}: {groupId: string}) => ({
	timeZone: new TimeZone(
		store.getIn(['projects', groupId, 'data', 'timeZone'])
	),
}));

type PropsFromRedux = ConnectedProps<typeof connector>;

interface ITimeZoneSelectionModal extends PropsFromRedux {
	groupId: string;
	notificationId: string;
	observer: Observer;
	onClose: () => void;
}

const TimeZoneSelectionModal: React.FC<ITimeZoneSelectionModal> = ({
	groupId,
	notificationId,
	observer,
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
		<ClayModal disableAutoClose observer={observer}>
			<ClayModal.Header withTitle={false}>
				<ClayModal.Title>
					{Liferay.Language.get('workspace-timezone')}
				</ClayModal.Title>
			</ClayModal.Header>

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
							<ClayModal.Body>
								<div className="mb-4">
									{Liferay.Language.get(
										'your-workspace-now-supports-custom-timezones.-setting-timezones-will-only-impact-future-data.-expect-spiked-or-flat-data-for-1-2-days-following-a-change'
									)}
								</div>

								<div
									className="mt-4 mx-4"
									style={{
										backgroundImage: `url(${worldMapURL})`,
										backgroundPositionX: 'center',
										backgroundRepeat: 'no-repeat',
										minHeight: '15em',
									}}
								>
									<div className="pt-5">
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

									<span className="h4 font-weight-semi-bold text-secondary">
										{Liferay.Language.get(
											'current-time-colon'
										)}
									</span>
									<span className="font-weight-bold h2 ml-4 text-secondary">
										{currentTime}
									</span>
								</div>
							</ClayModal.Body>

							<ClayModal.Footer
								last={
									<ClayButton.Group spaced>
										<ClayButton
											className="button-root"
											displayType="secondary"
											onClick={handleClose}
										>
											{Liferay.Language.get(
												'do-this-later'
											)}
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

											{Liferay.Language.get(
												'set-timezone'
											)}
										</ClayButton>
									</ClayButton.Group>
								}
							/>
						</Form.Form>
					);
				}}
			</Form>
		</ClayModal>
	);
};

export default connector(TimeZoneSelectionModal);
