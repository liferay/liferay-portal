import ClayIcon from 'shared/components/Icon';
import React from 'react';

interface SummaryAlertIProps extends React.HTMLAttributes<HTMLElement> {
	symbol?: string;
}

export const SummaryAlert: React.FC<SummaryAlertIProps> = ({
	children,
	symbol,
}) => (
	<div className="analytics-summary-card-alert w-100 p-4">
		{symbol ? (
			<>
				<ClayIcon symbol={symbol} />

				<div>{children}</div>
			</>
		) : (
			children
		)}
	</div>
);
