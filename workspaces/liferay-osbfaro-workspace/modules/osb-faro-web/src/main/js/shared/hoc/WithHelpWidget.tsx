import HelpWidget from 'shared/components/HelpWidget';
import React from 'react';

interface IWrappedComponentProps {
	currentUserId: string;
	faroSubscriptionIMap: Map<string, any>;
	groupId: string;
	serverLocation: string;
	workspaceName: string;
}

const withHelpWidget =
	(WrappedComponent: React.ComponentType<IWrappedComponentProps>) =>
	(props: IWrappedComponentProps) => (
		<>
			<WrappedComponent {...props} />

			<HelpWidget groupId={props.groupId} />
		</>
	);

export default withHelpWidget;
