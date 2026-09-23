import Alert, {AlertTypes} from 'shared/components/Alert';
import React from 'react';
import {applyTimeZone, formatRelativeTime} from 'shared/util/date';
import {sub} from 'shared/util/lang';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const TIME_ZONE_COUNTRY_REGEX = /\([^)]+.*/;

interface ITimeZoneAlertProps extends React.HTMLAttributes<HTMLElement> {
	modifiedTime: number;
	onClose: () => void;
	stripe: boolean;
}

const TimeZoneAlert: React.FC<ITimeZoneAlertProps> = ({
	modifiedTime,
	onClose,
	stripe,
}) => {
	const {displayTimeZone, timeZoneId} = useTimeZone();

	return (
		<Alert
			iconSymbol="exclamation-full"
			onClose={onClose}
			stripe={stripe}
			title={Liferay.Language.get('info')}
			type={AlertTypes.Info}
		>
			{sub(
				Liferay.Language.get(
					'workspace-timezone-has-changed-to-x-as-of-x.-please-allow-1-2-days-for-the-data-to-adjust-to-this-new-setting.'
				),
				[
					displayTimeZone.replace(TIME_ZONE_COUNTRY_REGEX, ''),
					formatRelativeTime(applyTimeZone(modifiedTime, timeZoneId)),
				]
			)}
		</Alert>
	);
};

export default TimeZoneAlert;
