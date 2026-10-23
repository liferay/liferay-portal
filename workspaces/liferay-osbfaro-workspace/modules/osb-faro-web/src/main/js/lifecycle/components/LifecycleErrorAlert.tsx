import ClayAlert from '@clayui/alert';
import React, {useEffect, useRef} from 'react';
import {getPluralMessage} from 'shared/util/lang';

interface ILifecycleErrorAlertProps {
	alertCount: number;
	messages: string[];
	onClose: () => void;
}

const LifecycleErrorAlert: React.FC<ILifecycleErrorAlertProps> = ({
	alertCount,
	messages,
	onClose,
}) => {
	const alertRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		if (alertCount) {
			alertRef.current?.focus();
		}
	}, [alertCount]);

	if (!messages.length) {
		return null;
	}

	return (
		<div className="mb-4" ref={alertRef} tabIndex={-1}>
			<ClayAlert
				displayType="danger"
				onClose={onClose}
				title={
					getPluralMessage(
						Liferay.Language.get(
							'review-1-error-in-this-configuration'
						),
						Liferay.Language.get(
							'review-x-errors-in-this-configuration'
						),
						messages.length,
						true
					) as string
				}
			>
				<ul className="mb-0 mt-1">
					{messages.map((message) => (
						<li key={message}>{message}</li>
					))}
				</ul>
			</ClayAlert>
		</div>
	);
};

export default LifecycleErrorAlert;
