import Body from './Body';
import DocumentTitle from 'shared/components/DocumentTitle';
import Main from './Main';
import React from 'react';
import Sidebar from './Sidebar';
import Title from './Title';
import Toolbar from './Toolbar';

interface IBaseEditPageProps {
	children?: React.ReactNode;
	className?: string;
	documentTitle: string;
}

/**
 * Layout shared by every editor page. Compose it from Toolbar, the page
 * header, and Body, which holds an optional Sidebar and the Main content.
 */
export const BaseEditPage: React.FC<IBaseEditPageProps> & {
	Body: typeof Body;
	Main: typeof Main;
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
BaseEditPage.Main = Main;
BaseEditPage.Sidebar = Sidebar;
BaseEditPage.Title = Title;
BaseEditPage.Toolbar = Toolbar;

export default BaseEditPage;
