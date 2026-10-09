import Body from './Body';
import DocumentTitle from 'shared/components/DocumentTitle';
import React from 'react';
import Sidebar from './Sidebar';
import Title from './Title';
import Toolbar from './Toolbar';

interface IBaseEditPageProps {
	children?: React.ReactNode;
	className?: string;
	documentTitle: string;
}

export const BaseEditPage: React.FC<IBaseEditPageProps> & {
	Body: typeof Body;
	Sidebar: typeof Sidebar;
	Title: typeof Title;
	Toolbar: typeof Toolbar;
} = ({children, className, documentTitle}) => (
	<div className={className}>
		<DocumentTitle title={documentTitle} />

		{children}
	</div>
);

BaseEditPage.Body = Body;
BaseEditPage.Sidebar = Sidebar;
BaseEditPage.Title = Title;
BaseEditPage.Toolbar = Toolbar;

export default BaseEditPage;
