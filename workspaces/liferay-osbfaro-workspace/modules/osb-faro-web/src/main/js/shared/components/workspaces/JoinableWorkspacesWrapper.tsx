import React from 'react';

interface IJoinableWorkspacesWrapperProps
	extends React.HTMLAttributes<HTMLElement> {
	title: string;
	details: string;
}

const JoinableWorkspacesWrapper: React.FC<IJoinableWorkspacesWrapperProps> = ({
	children,
	details,
	title,
}) => (
	<div className="join-container mt-4">
		<div className="title-container">
			<h2 className="title">{title}</h2>
			<div className="details-container">{details}</div>
		</div>
		{children}
	</div>
);

export default JoinableWorkspacesWrapper;
