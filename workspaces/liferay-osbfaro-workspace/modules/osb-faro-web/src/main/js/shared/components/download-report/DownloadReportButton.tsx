import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import Loading, {Align} from 'shared/components/Loading';
import React from 'react';
import {useMutationObserver} from './utils';

interface IDownloadReportButton {
	bordered?: boolean;
	disabled: boolean;
	loading?: boolean;
	onClick: () => void;
}

export const DownloadReportButton: React.FC<IDownloadReportButton> = ({
	bordered = false,
	disabled,
	loading = false,
	onClick,
}) => {
	const {loadingCount} = useMutationObserver();

	return (
		<ClayButton
			borderless={!bordered}
			disabled={disabled || loading || loadingCount > 0}
			displayType="secondary"
			onClick={onClick}
			size="sm"
		>
			<ClayIcon className="mr-2" symbol="download" />

			{Liferay.Language.get('download-reports')}

			{(loading || loadingCount > 0) && <Loading align={Align.Right} />}
		</ClayButton>
	);
};
